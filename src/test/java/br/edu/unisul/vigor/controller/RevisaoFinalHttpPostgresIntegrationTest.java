package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.Funcionario;
import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.repository.FuncionarioRepository;
import br.edu.unisul.vigor.service.MembroService;
import br.edu.unisul.vigor.validation.CpfUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.unbescape.html.HtmlEscape;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class RevisaoFinalHttpPostgresIntegrationTest {

    @LocalServerPort
    private int porta;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MembroService membroService;

    private final String identificador = UUID.randomUUID().toString();
    private final String senha = "Senha-teste-" + identificador;
    private Funcionario funcionario;
    private Membro membro;
    private FuncionarioHttpTestClient cliente;

    @BeforeEach
    void prepararSessaoEMembro() throws Exception {
        funcionario = funcionarioRepository.saveAndFlush(new Funcionario(
                "Revisora " + identificador, "Administradora", "e02-" + identificador,
                passwordEncoder.encode(senha)));
        Membro candidato = new Membro();
        candidato.setNome("Membro E02 " + identificador);
        candidato.setCpf(cpfDisponivel());
        candidato.setTelefone("48999990000");
        candidato.setEndereco("Rua de teste E02");
        membro = membroService.cadastrar(candidato);
        cliente = new FuncionarioHttpTestClient(porta);
        cliente.autenticar(funcionario.getLogin(), senha);
    }

    @AfterEach
    void removerSomenteDadosDaExecucao() {
        if (cliente != null) {
            cliente.close();
        }
        if (membro != null) {
            jdbcTemplate.update("DELETE FROM pagamentos WHERE membro_id = ?", membro.getId());
            jdbcTemplate.update("DELETE FROM historico_atividades WHERE membro_id = ?", membro.getId());
            jdbcTemplate.update("DELETE FROM membros WHERE id = ?", membro.getId());
        }
        if (funcionario != null) {
            jdbcTemplate.update("DELETE FROM funcionarios WHERE id = ?", funcionario.getId());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"/membros", "/membros/novo", "/treinos", "/pagamentos", "/pagamentos/novo", "/atividades"})
    void exigeLoginNasRotasReais(String rota) throws Exception {
        try (FuncionarioHttpTestClient anonimo = new FuncionarioHttpTestClient(porta)) {
            var resposta = anonimo.get(rota);
            assertEquals(302, resposta.statusCode());
            assertEquals("/login", URI.create(resposta.headers().firstValue("Location").orElseThrow()).getPath());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void rejeitaLoginInexistenteOuSenhaIncorretaSemCriarSessao(boolean loginExistente) throws Exception {
        try (FuncionarioHttpTestClient tentativa = new FuncionarioHttpTestClient(porta)) {
            String login = loginExistente ? funcionario.getLogin() : "inexistente-" + identificador;
            IllegalStateException falha = assertThrows(IllegalStateException.class,
                    () -> tentativa.autenticar(login, "senha-incorreta"));
            assertTrue(falha.getMessage().contains("/login?erro"));
            var mensagem = tentativa.get("/login?erro");
            assertTrue(mensagem.body().contains("Login ou senha inválidos."));
            assertFalse(mensagem.body().contains(senha));
            assertEquals(302, tentativa.get("/atividades").statusCode());
        }
    }

    @Test
    void logoutEncerraSessaoAutenticada() throws Exception {
        assertEquals(200, cliente.get("/atividades").statusCode());
        assertEquals(302, cliente.post("/logout", "").statusCode());
        var resposta = cliente.get("/atividades");
        assertEquals(302, resposta.statusCode());
        assertEquals("/login", URI.create(resposta.headers().firstValue("Location").orElseThrow()).getPath());
    }

    @Test
    void pagamentoHttpAtribuiPagoConsultaEBloqueiaExclusaoDoMembro() throws Exception {
        assertEquals(302, cliente.post("/pagamentos",
                formularioPagamento("123.45", "2026-11-23") + "&status=CANCELADO").statusCode());

        assertEquals("PAGO", jdbcTemplate.queryForObject(
                "SELECT status FROM pagamentos WHERE membro_id = ?", String.class, membro.getId()));
        assertEquals(1, quantidadePagamentos());
        var consulta = cliente.get("/pagamentos?membroId=" + membro.getId());
        assertEquals(200, consulta.statusCode());
        assertTrue(HtmlEscape.unescapeHtml(consulta.body()).contains("123,45"));

        assertEquals(302, cliente.post("/membros/" + membro.getId() + "/excluir", "").statusCode());
        assertTrue(HtmlEscape.unescapeHtml(cliente.get("/membros").body()).contains(
                "existem pagamentos vinculados"));
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM membros WHERE id = ?", Integer.class, membro.getId()));
        assertEquals(1, quantidadePagamentos());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "''|2026-11-23", "abc|2026-11-23", "0|2026-11-23", "-1|2026-11-23",
            "10000000000|2026-11-23", "1.234|2026-11-23",
            "10|''", "10|2026-02-30"
    }, delimiter = '|', emptyValue = "")
    void pagamentoInvalidoPeloHttpNaoPersiste(String valor, String data) throws Exception {
        var resposta = cliente.post("/pagamentos", formularioPagamento(valor, data));
        assertEquals(200, resposta.statusCode(), resposta::body);
        assertTrue(resposta.body().contains("Registrar pagamento"), resposta::body);
        assertTrue(resposta.body().contains("class=\"erro-campo\""), resposta::body);
        assertEquals(0, quantidadePagamentos());
    }

    @Test
    void pagamentoComMembroInexistenteNaoPersiste() throws Exception {
        long inexistente = idInexistente();
        var resposta = cliente.post("/pagamentos",
                "membroId=" + inexistente + "&valor=10&dataPagamento=2026-11-23");
        assertEquals(200, resposta.statusCode());
        assertTrue(resposta.body().contains("Selecione um membro existente."));
        assertEquals(0, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pagamentos WHERE membro_id = ?", Integer.class, inexistente));
    }

    @Test
    void historicoHttpMostraResponsavelEFiltraPeriodoSemResultados() throws Exception {
        String atividade = "Atividade E02 " + identificador;
        jdbcTemplate.update("INSERT INTO historico_atividades "
                        + "(membro_id, funcionario_responsavel_id, atividade, data_atividade, tempo_execucao_minutos) "
                        + "VALUES (?, ?, ?, DATE '2026-11-23', 30)",
                membro.getId(), funcionario.getId(), atividade);
        var consulta = cliente.get("/atividades?membroId=" + membro.getId());
        assertEquals(200, consulta.statusCode());
        String html = HtmlEscape.unescapeHtml(consulta.body());
        assertTrue(html.contains(atividade));
        assertTrue(html.contains(funcionario.getNome()));
        assertTrue(html.contains("23/11/2026"));
        assertTrue(html.contains("30 min"));

        var semResultados = cliente.get("/atividades?membroId=" + membro.getId()
                + "&dataInicial=2026-11-24");
        assertEquals(200, semResultados.statusCode());
        assertFalse(semResultados.body().contains(atividade));
        assertTrue(semResultados.body().contains("Nenhuma atividade encontrada"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "0", "-1", "9223372036854775808"})
    void referenciaInvalidaNaoExibeRegistrosNosPagamentosEHistorico(String referencia) throws Exception {
        cliente.post("/pagamentos", formularioPagamento("123.45", "2026-11-23"));
        String atividade = "Atividade E02 " + identificador;
        jdbcTemplate.update("INSERT INTO historico_atividades "
                        + "(membro_id, funcionario_responsavel_id, atividade, data_atividade, tempo_execucao_minutos) "
                        + "VALUES (?, ?, ?, CURRENT_DATE, 30)",
                membro.getId(), funcionario.getId(), atividade);

        var pagamentos = cliente.get("/pagamentos?membroId=" + referencia);
        assertEquals(200, pagamentos.statusCode());
        assertTrue(pagamentos.body().contains("Selecione um membro válido."));
        assertFalse(HtmlEscape.unescapeHtml(pagamentos.body()).contains("123,45"));
        var historico = cliente.get("/atividades?membroId=" + referencia);
        assertEquals(200, historico.statusCode());
        assertTrue(historico.body().contains("Informe um membro e um período válidos."));
        assertFalse(historico.body().contains(atividade));
    }

    private String formularioPagamento(String valor, String data) {
        return "membroId=" + membro.getId()
                + "&valor=" + URLEncoder.encode(valor, StandardCharsets.UTF_8)
                + "&dataPagamento=" + URLEncoder.encode(data, StandardCharsets.UTF_8);
    }

    private int quantidadePagamentos() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pagamentos WHERE membro_id = ?", Integer.class, membro.getId());
    }

    private long idInexistente() {
        return jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 0) + 1 FROM membros", Long.class);
    }

    private String cpfDisponivel() {
        for (int tentativa = 0; tentativa < 100; tentativa++) {
            String base = String.format("%09d",
                    ThreadLocalRandom.current().nextInt(100_000_000, 1_000_000_000));
            int primeiroDigito = calcularDigito(base, 10);
            int segundoDigito = calcularDigito(base + primeiroDigito, 11);
            String cpf = base + primeiroDigito + segundoDigito;
            if (CpfUtils.ehValido(cpf)
                    && jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM membros WHERE cpf = ?", Integer.class, cpf) == 0) {
                return cpf;
            }
        }
        throw new IllegalStateException("Não foi possível gerar CPF de teste disponível.");
    }

    private int calcularDigito(String base, int pesoInicial) {
        int soma = 0;
        for (int indice = 0; indice < base.length(); indice++) {
            soma += (base.charAt(indice) - '0') * (pesoInicial - indice);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

}

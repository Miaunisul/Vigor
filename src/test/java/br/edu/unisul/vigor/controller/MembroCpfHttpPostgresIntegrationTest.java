package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.model.Funcionario;
import br.edu.unisul.vigor.repository.FuncionarioRepository;
import br.edu.unisul.vigor.service.MembroService;
import br.edu.unisul.vigor.validation.CpfUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.unbescape.html.HtmlEscape;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class MembroCpfHttpPostgresIntegrationTest {

    private final String nomeTeste = "Membro teste CPF HTTP " + UUID.randomUUID();

    @LocalServerPort
    private int porta;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MembroService membroService;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private FuncionarioHttpTestClient cliente;
    private Long funcionarioTesteId;

    @BeforeEach
    void autenticarFuncionarioDeTeste() throws IOException, InterruptedException {
        String login = "http-membro-" + UUID.randomUUID();
        String senha = "Senha-http-" + UUID.randomUUID();
        Funcionario funcionario = funcionarioRepository.saveAndFlush(new Funcionario(
                "Funcionário de teste HTTP", "Administradora", login,
                passwordEncoder.encode(senha)));
        funcionarioTesteId = funcionario.getId();

        cliente = new FuncionarioHttpTestClient(porta);
        cliente.autenticar(login, senha);
    }

    @AfterEach
    void removerMembrosDoTeste() {
        jdbcTemplate.update("DELETE FROM membros WHERE nome LIKE ?", nomeTeste + "%");
        if (funcionarioTesteId != null) {
            jdbcTemplate.update("DELETE FROM funcionarios WHERE id = ?", funcionarioTesteId);
        }
        if (cliente != null) {
            cliente.close();
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void cadastraCpfValidoEPersisteSomenteDigitos(boolean pontuado)
            throws IOException, InterruptedException {
        String cpf = cpfDisponivel();
        String cpfInformado = pontuado ? formatarCpf(cpf) : cpf;
        HttpResponse<String> resposta = cadastrar(nomeTeste, cpfInformado);

        assertRedirecionaParaMembros(resposta);
        assertEquals(List.of(new MembroPersistido(nomeTeste, cpf)), consultarMembrosDoTeste());
    }

    @ParameterizedTest
    @ValueSource(strings = {"52998224724", "529.982.247-24", "00000000000", "11111111111"})
    void rejeitaCpfInvalidoSemPersistir(String cpf) throws IOException, InterruptedException {
        HttpResponse<String> resposta = cadastrar(nomeTeste, cpf);

        assertCadastroRejeitado(resposta, "Informe um CPF válido com dígitos verificadores corretos.");
        assertTrue(consultarMembrosDoTeste().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void rejeitaCpfDuplicadoAposNormalizacaoEPreservaOriginal(boolean pontuado)
            throws IOException, InterruptedException {
        String cpf = cpfDisponivel();
        String cpfDuplicado = pontuado ? formatarCpf(cpf) : cpf;
        assertRedirecionaParaMembros(cadastrar(nomeTeste, cpf));

        HttpResponse<String> resposta = cadastrar(nomeTeste + " duplicado", cpfDuplicado);

        assertCadastroRejeitado(resposta, "Já existe um membro cadastrado com este CPF.");
        assertEquals(List.of(new MembroPersistido(nomeTeste, cpf)), consultarMembrosDoTeste());
    }

    @Test
    void abreEdicaoComDadosAtuaisDoMembro() throws IOException, InterruptedException {
        Membro membro = membroService.cadastrar(novoMembro(" edição", cpfDisponivel()));

        HttpResponse<String> resposta = get("/membros/" + membro.getId() + "/editar");

        assertEquals(200, resposta.statusCode(), resposta::body);
        assertTrue(resposta.body().contains("Editar membro"), resposta::body);
        assertTrue(resposta.body().contains(
                "value=\"" + HtmlEscape.escapeHtml5(membro.getNome()) + "\""), resposta::body);
        assertTrue(resposta.body().contains("value=\"" + membro.getCpf() + "\""), resposta::body);
        assertTrue(resposta.body().contains("ID do membro"), resposta::body);
        assertTrue(resposta.body().contains("Data de cadastro"), resposta::body);
    }

    @Test
    void atualizaMembroPorHttpPreservaIdentidadeEListaExibeDadosPersistidos()
            throws IOException, InterruptedException {
        Membro membro = membroService.cadastrar(novoMembro(" antes da edição", cpfDisponivel()));
        Long idOriginal = membro.getId();
        LocalDate dataCadastroOriginal = LocalDate.of(2001, 2, 3);
        jdbcTemplate.update("UPDATE membros SET data_cadastro = ? WHERE id = ?",
                Date.valueOf(dataCadastroOriginal), idOriginal);

        String cpfComPontuacao = formatarCpf(membro.getCpf());
        String formulario = formulario("Nome após edição", cpfComPontuacao,
                "(48) 99999-5678", "Rua Nova, 45")
                + "&id=999999&dataCadastro=2000-01-01";
        HttpResponse<String> resposta = post(
                "/membros/" + idOriginal + "/editar", formulario);

        assertRedirecionaParaMembros(resposta);
        MembroPersistidoAtualizado persistido = jdbcTemplate.queryForObject(
                "SELECT id, nome, cpf, telefone, endereco, data_cadastro FROM membros WHERE id = ?",
                (linha, indice) -> new MembroPersistidoAtualizado(
                        linha.getLong("id"), linha.getString("nome"), linha.getString("cpf"),
                        linha.getString("telefone"), linha.getString("endereco"),
                        linha.getDate("data_cadastro").toLocalDate()),
                idOriginal);
        assertEquals(new MembroPersistidoAtualizado(idOriginal, "Nome após edição",
                membro.getCpf(), "(48) 99999-5678", "Rua Nova, 45", dataCadastroOriginal),
                persistido);

        HttpResponse<String> lista = get("/membros");
        assertEquals(200, lista.statusCode(), lista::body);
        assertTrue(lista.body().contains("Nome após edição"), lista::body);

        HttpResponse<String> detalhes = get(
        "/membros?acao=detalhes&membroId=" + idOriginal);
        assertEquals(200, detalhes.statusCode(), detalhes::body);
        assertTrue(detalhes.body().contains("Rua Nova, 45"), detalhes::body);
    }

    @Test
    void rejeitaCpfDuplicadoNaEdicaoSemAlterarORegistro()
            throws IOException, InterruptedException {
        Membro donoCpf = membroService.cadastrar(novoMembro(" dono do cpf", cpfDisponivel()));
        Membro editado = membroService.cadastrar(novoMembro(" membro editado", cpfDisponivel()));
        HttpResponse<String> resposta = post(
                "/membros/" + editado.getId() + "/editar",
                formulario("Nome que não deve gravar", formatarCpf(donoCpf.getCpf()),
                        "Telefone alterado", "Endereço alterado"));

        assertCadastroRejeitado(resposta, "Já existe um membro cadastrado com este CPF.");
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM membros WHERE id = ? AND nome = ? AND cpf = ? "
                        + "AND telefone = ? AND endereco = ?",
                Integer.class, editado.getId(), nomeTeste + " membro editado", editado.getCpf(),
                "(48) 99999-0000", "Rua de teste, 1"));
    }

    @Test
    void rejeitaCpfInvalidoNaEdicaoSemAlterarORegistro()
            throws IOException, InterruptedException {
        Membro membro = membroService.cadastrar(novoMembro(" cpf inválido", cpfDisponivel()));
        HttpResponse<String> resposta = post(
                "/membros/" + membro.getId() + "/editar",
                formulario("Nome que não deve gravar", "52998224726",
                        "Telefone alterado", "Endereço alterado"));

        assertCadastroRejeitado(resposta, "Informe um CPF válido com dígitos verificadores corretos.");
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM membros WHERE id = ? AND nome = ? AND cpf = ? "
                        + "AND telefone = ? AND endereco = ?",
                Integer.class, membro.getId(), nomeTeste + " cpf inválido", membro.getCpf(),
                "(48) 99999-0000", "Rua de teste, 1"));
    }

    private HttpResponse<String> cadastrar(String nome, String cpf)
            throws IOException, InterruptedException {
        return post("/membros", formulario(nome, cpf, "48999990000", "Rua de teste"));
    }

    private HttpResponse<String> post(String caminho, String formulario)
            throws IOException, InterruptedException {
        return cliente.post(caminho, formulario);
    }

    private HttpResponse<String> get(String caminho) throws IOException, InterruptedException {
        return cliente.get(caminho);
    }

    private String formulario(String nome, String cpf, String telefone, String endereco) {
        return "nome=" + codificar(nome)
                + "&cpf=" + codificar(cpf)
                + "&telefone=" + codificar(telefone)
                + "&endereco=" + codificar(endereco);
    }

    private String codificar(String valor) {
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }

    private Membro novoMembro(String sufixo, String cpf) {
        Membro membro = new Membro();
        membro.setNome(nomeTeste + sufixo);
        membro.setCpf(cpf);
        membro.setTelefone("(48) 99999-0000");
        membro.setEndereco("Rua de teste, 1");
        return membro;
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

    private String formatarCpf(String cpf) {
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    private void assertRedirecionaParaMembros(HttpResponse<String> resposta) {
        assertEquals(302, resposta.statusCode(), resposta::body);
        String destino = resposta.headers().firstValue("Location").orElseThrow();
        assertEquals("/membros", URI.create(destino).getPath().split(";jsessionid=", 2)[0]);
    }

    private void assertCadastroRejeitado(HttpResponse<String> resposta, String mensagem) {
        assertEquals(200, resposta.statusCode(), resposta::body);
        assertTrue(resposta.body().contains(mensagem), resposta::body);
    }

    private List<MembroPersistido> consultarMembrosDoTeste() {
        return jdbcTemplate.query("SELECT nome, cpf FROM membros WHERE nome LIKE ?",
                (linha, indice) -> new MembroPersistido(
                        linha.getString("nome"), linha.getString("cpf")),
                nomeTeste + "%");
    }

    private record MembroPersistido(String nome, String cpf) {
    }

    private record MembroPersistidoAtualizado(
            Long id,
            String nome,
            String cpf,
            String telefone,
            String endereco,
            LocalDate dataCadastro) {
    }
}

package br.edu.unisul.vigor.repository;

import br.edu.unisul.vigor.dto.RegistroCargaHistorico;
import br.edu.unisul.vigor.model.Funcionario;
import br.edu.unisul.vigor.model.HistoricoAtividade;
import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.service.CargaHistoricoAtividadeService;
import br.edu.unisul.vigor.service.HistoricoAtividadeService;
import br.edu.unisul.vigor.validation.CpfUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifica o procedimento técnico de carga do histórico no PostgreSQL (CA28 e CA29).
 *
 * <p>Cada teste cria seu próprio membro e funcionário, sem IDs fixos, e registra uma
 * atividade válida antes do cenário. Nos cenários inválidos, o estado do banco é lido
 * antes e depois da tentativa para comprovar que nada foi incluído e que o registro
 * válido existente permaneceu inalterado. A transação de cada teste é desfeita ao final.
 */
@SpringBootTest
@Transactional
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class CargaHistoricoAtividadePostgresIntegrationTest {

    private static final String ATIVIDADE_VALIDA = "Avaliação física da carga técnica";
    private static final String DATA_VALIDA = "2026-11-20";
    private static final String TEMPO_VALIDO = "40";

    private static final String MENSAGEM_ATIVIDADE =
            "A atividade é obrigatória e não pode ficar vazia.";
    private static final String MENSAGEM_TEMPO_POSITIVO =
            "O tempo de execução deve ser positivo.";

    @Autowired
    private CargaHistoricoAtividadeService cargaService;

    @Autowired
    private HistoricoAtividadeService historicoService;

    @Autowired
    private HistoricoAtividadeRepository historicoRepository;

    @Autowired
    private MembroRepository membroRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    private Membro membro;
    private Funcionario funcionario;
    private HistoricoAtividade registroExistente;

    @BeforeEach
    void prepararDados() {
        membro = criarMembro();
        funcionario = criarFuncionario();
        registroExistente = cargaService.carregar(new RegistroCargaHistorico(
                membro.getId().toString(), funcionario.getId().toString(),
                "Registro válido existente", "2026-11-19", "25"));
        historicoRepository.flush();
    }

    // CA28 — registro técnico válido

    @Test
    void cargaValidaPersisteAtividadeComIdGeradoEVinculosCorretos() {
        long totalAntes = contarAtividades();

        HistoricoAtividade salva = cargaService.carregar(registroValido());
        historicoRepository.flush();
        entityManager.clear();

        assertNotNull(salva.getId());
        assertNotEquals(registroExistente.getId(), salva.getId());
        assertEquals(totalAntes + 1, contarAtividades());

        Map<String, Object> gravado = lerRegistro(salva.getId());
        assertEquals(membro.getId(), ((Number) gravado.get("membro_id")).longValue());
        assertEquals(funcionario.getId(),
                ((Number) gravado.get("funcionario_responsavel_id")).longValue());
        assertEquals(ATIVIDADE_VALIDA, gravado.get("atividade"));
        assertEquals(DATA_VALIDA, gravado.get("data_atividade"));
        assertEquals(40, ((Number) gravado.get("tempo_execucao_minutos")).intValue());
    }

    @Test
    void cargaValidaFicaDisponivelNoHistoricoDoMembro() {
        HistoricoAtividade salva = cargaService.carregar(registroValido());
        historicoRepository.flush();
        entityManager.clear();

        List<HistoricoAtividade> historico = historicoService.consultarPorMembro(membro.getId());

        assertEquals(Set.of(registroExistente.getId(), salva.getId()), ids(historico));
        HistoricoAtividade consultada = historico.stream()
                .filter(item -> item.getId().equals(salva.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(membro.getId(), consultada.getMembro().getId());
        assertEquals(funcionario.getId(), consultada.getFuncionarioResponsavel().getId());
        assertEquals(ATIVIDADE_VALIDA, consultada.getAtividade());
    }

    // CA29 — rejeição de dados inválidos, um cenário por teste

    @Test
    void rejeitaCargaSemMembro() {
        verificarRejeicao(new RegistroCargaHistorico(null, funcionarioId(),
                        ATIVIDADE_VALIDA, DATA_VALIDA, TEMPO_VALIDO),
                IllegalArgumentException.class, "O membro é obrigatório.");
    }

    @Test
    void rejeitaCargaComMembroInexistente() {
        verificarRejeicao(new RegistroCargaHistorico(idInexistente("membros"), funcionarioId(),
                        ATIVIDADE_VALIDA, DATA_VALIDA, TEMPO_VALIDO),
                IllegalArgumentException.class, "Membro não encontrado.");
    }

    @Test
    void rejeitaCargaSemFuncionarioResponsavel() {
        verificarRejeicao(new RegistroCargaHistorico(membroId(), null,
                        ATIVIDADE_VALIDA, DATA_VALIDA, TEMPO_VALIDO),
                IllegalArgumentException.class, "O funcionário responsável é obrigatório.");
    }

    @Test
    void rejeitaCargaComFuncionarioInexistente() {
        verificarRejeicao(new RegistroCargaHistorico(membroId(), idInexistente("funcionarios"),
                        ATIVIDADE_VALIDA, DATA_VALIDA, TEMPO_VALIDO),
                IllegalArgumentException.class, "Funcionário responsável não encontrado.");
    }

    @Test
    void rejeitaCargaSemNomeDaAtividade() {
        verificarRejeicao(registroComAtividade(null),
                ConstraintViolationException.class, MENSAGEM_ATIVIDADE);
    }

    @Test
    void rejeitaCargaComNomeDaAtividadeVazio() {
        verificarRejeicao(registroComAtividade(""),
                ConstraintViolationException.class, MENSAGEM_ATIVIDADE);
    }

    @Test
    void rejeitaCargaComNomeDaAtividadeSomenteComEspacos() {
        verificarRejeicao(registroComAtividade("   "),
                ConstraintViolationException.class, MENSAGEM_ATIVIDADE);
    }

    @Test
    void rejeitaCargaSemData() {
        verificarRejeicao(registroComData(null),
                ConstraintViolationException.class, "A data da atividade é obrigatória.");
    }

    @Test
    void rejeitaCargaComDataInvalida() {
        verificarRejeicao(registroComData("2026-02-30"),
                IllegalArgumentException.class,
                "A data da atividade é inválida. "
                        + "Informe uma data existente no formato AAAA-MM-DD.");
    }

    @Test
    void rejeitaCargaSemTempoDeExecucao() {
        verificarRejeicao(registroComTempo(null),
                ConstraintViolationException.class, "O tempo de execução é obrigatório.");
    }

    @Test
    void rejeitaCargaComTempoNaoInteiro() {
        verificarRejeicao(registroComTempo("30.5"),
                IllegalArgumentException.class,
                "O tempo de execução deve ser um número inteiro de minutos.");
    }

    @Test
    void rejeitaCargaComTempoIgualAZero() {
        verificarRejeicao(registroComTempo("0"),
                ConstraintViolationException.class, MENSAGEM_TEMPO_POSITIVO);
    }

    @Test
    void rejeitaCargaComTempoNegativo() {
        verificarRejeicao(registroComTempo("-5"),
                ConstraintViolationException.class, MENSAGEM_TEMPO_POSITIVO);
    }

    private void verificarRejeicao(RegistroCargaHistorico registroInvalido,
            Class<? extends RuntimeException> tipoEsperado, String mensagemEsperada) {
        long totalAntes = contarAtividades();
        long doMembroAntes = contarAtividadesDoMembro();
        Map<String, Object> existenteAntes = lerRegistro(registroExistente.getId());

        RuntimeException erro =
                assertThrows(tipoEsperado, () -> cargaService.carregar(registroInvalido));

        assertEquals(Set.of(mensagemEsperada), mensagens(erro));
        assertEquals(totalAntes, contarAtividades(),
                "Nenhum registro inválido pode ser incluído.");
        assertEquals(doMembroAntes, contarAtividadesDoMembro());
        assertEquals(existenteAntes, lerRegistro(registroExistente.getId()),
                "O registro válido existente deve permanecer inalterado.");
    }

    private static Set<String> mensagens(RuntimeException erro) {
        if (erro instanceof ConstraintViolationException violacao) {
            return violacao.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toSet());
        }
        return Set.of(erro.getMessage());
    }

    private RegistroCargaHistorico registroValido() {
        return new RegistroCargaHistorico(membroId(), funcionarioId(),
                ATIVIDADE_VALIDA, DATA_VALIDA, TEMPO_VALIDO);
    }

    private RegistroCargaHistorico registroComAtividade(String atividade) {
        return new RegistroCargaHistorico(membroId(), funcionarioId(),
                atividade, DATA_VALIDA, TEMPO_VALIDO);
    }

    private RegistroCargaHistorico registroComData(String data) {
        return new RegistroCargaHistorico(membroId(), funcionarioId(),
                ATIVIDADE_VALIDA, data, TEMPO_VALIDO);
    }

    private RegistroCargaHistorico registroComTempo(String tempo) {
        return new RegistroCargaHistorico(membroId(), funcionarioId(),
                ATIVIDADE_VALIDA, DATA_VALIDA, tempo);
    }

    private String membroId() {
        return membro.getId().toString();
    }

    private String funcionarioId() {
        return funcionario.getId().toString();
    }

    private String idInexistente(String tabela) {
        Long proximo = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(id), 0) + 1 FROM " + tabela, Long.class);
        return String.valueOf(proximo);
    }

    private long contarAtividades() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM historico_atividades", Long.class);
    }

    private long contarAtividadesDoMembro() {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM historico_atividades WHERE membro_id = ?",
                Long.class, membro.getId());
    }

    private Map<String, Object> lerRegistro(Long id) {
        return jdbcTemplate.queryForMap(
                "SELECT membro_id, funcionario_responsavel_id, atividade, "
                        + "to_char(data_atividade, 'YYYY-MM-DD') AS data_atividade, "
                        + "tempo_execucao_minutos "
                        + "FROM historico_atividades WHERE id = ?",
                id);
    }

    private Set<Long> ids(List<HistoricoAtividade> atividades) {
        return atividades.stream().map(HistoricoAtividade::getId).collect(Collectors.toSet());
    }

    private Membro criarMembro() {
        Membro novo = new Membro();
        novo.setNome("Membro da carga técnica");
        novo.setCpf(cpfDisponivel());
        novo.setTelefone("(48) 99999-0000");
        novo.setEndereco("Rua de teste, 1");
        return membroRepository.saveAndFlush(novo);
    }

    private Funcionario criarFuncionario() {
        return funcionarioRepository.saveAndFlush(new Funcionario(
                "Funcionário da carga técnica",
                "Teste",
                "carga-tecnica-" + UUID.randomUUID(),
                passwordEncoder.encode(UUID.randomUUID().toString())));
    }

    private String cpfDisponivel() {
        for (int tentativa = 0; tentativa < 100; tentativa++) {
            String base = String.format("%09d",
                    ThreadLocalRandom.current().nextInt(100_000_000, 1_000_000_000));
            int primeiroDigito = calcularDigito(base, 10);
            int segundoDigito = calcularDigito(base + primeiroDigito, 11);
            String cpf = base + primeiroDigito + segundoDigito;
            if (CpfUtils.ehValido(cpf) && !membroRepository.existsByCpf(cpf)) {
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

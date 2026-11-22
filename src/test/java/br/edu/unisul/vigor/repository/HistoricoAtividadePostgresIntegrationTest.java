package br.edu.unisul.vigor.repository;

import br.edu.unisul.vigor.model.Funcionario;
import br.edu.unisul.vigor.model.HistoricoAtividade;
import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.service.HistoricoAtividadeService;
import br.edu.unisul.vigor.validation.CpfUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class HistoricoAtividadePostgresIntegrationTest {

    @Autowired
    private HistoricoAtividadeRepository historicoRepository;

    @Autowired
    private MembroRepository membroRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private HistoricoAtividadeService historicoService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void persisteAtividadeComMembroEFuncionarioEConsultaPorAmbos() {
        Membro membro = membroParaTeste();
        Funcionario funcionario = funcionarioParaTeste();
        HistoricoAtividade atividade = atividadeValida();

        HistoricoAtividade salva = historicoService.registrar(
                atividade, membro.getId(), funcionario.getId());
        List<HistoricoAtividade> porMembro = historicoService.consultarPorMembro(membro.getId());
        List<HistoricoAtividade> porFuncionario =
                historicoService.consultarPorFuncionario(funcionario.getId());

        assertNotNull(salva.getId());
        assertEquals(membro.getId(), salva.getMembro().getId());
        assertEquals(funcionario.getId(), salva.getFuncionarioResponsavel().getId());
        assertTrue(porMembro.stream().anyMatch(item -> item.getId().equals(salva.getId())));
        assertTrue(porFuncionario.stream().anyMatch(item -> item.getId().equals(salva.getId())));
    }

    @Test
    void servicoRejeitaDadosInvalidosSemPersistirAtividade() {
        Membro membro = membroParaTeste();
        Funcionario funcionario = funcionarioParaTeste();
        long quantidadeAntes = historicoRepository.findByMembro_Id(membro.getId()).size();
        HistoricoAtividade atividade = atividadeValida();
        atividade.setTempoExecucaoMinutos(0);

        assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> historicoService.registrar(atividade, membro.getId(), funcionario.getId()));

        assertEquals(quantidadeAntes, historicoRepository.findByMembro_Id(membro.getId()).size());
    }

    @Test
    void checkDoBancoRejeitaAtividadeVazia() {
        Membro membro = membroParaTeste();
        Funcionario funcionario = funcionarioParaTeste();

        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO historico_atividades "
                        + "(membro_id, funcionario_responsavel_id, atividade, data_atividade, "
                        + "tempo_execucao_minutos) VALUES (?, ?, ?, ?, ?)",
                membro.getId(), funcionario.getId(), "   ", LocalDate.now(), 10));
    }

    @Test
    void checkDoBancoRejeitaTempoNaoPositivo() {
        Membro membro = membroParaTeste();
        Funcionario funcionario = funcionarioParaTeste();

        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO historico_atividades "
                        + "(membro_id, funcionario_responsavel_id, atividade, data_atividade, "
                        + "tempo_execucao_minutos) VALUES (?, ?, ?, ?, ?)",
                membro.getId(), funcionario.getId(), "Atividade de teste", LocalDate.now(), 0));
    }

    @Test
    void chaveEstrangeiraRejeitaMembroInexistente() {
        Funcionario funcionario = funcionarioParaTeste();
        assertThrows(DataIntegrityViolationException.class, () -> inserir(-1L,
                funcionario.getId(), "Atividade de teste", 10));
    }

    @Test
    void chaveEstrangeiraRejeitaFuncionarioInexistente() {
        Membro membro = membroParaTeste();
        assertThrows(DataIntegrityViolationException.class, () -> inserir(membro.getId(),
                -1L, "Atividade de teste", 10));
    }

    @Test
    void chaveEstrangeiraTambemExigeFuncionarioResponsavel() {
        Membro membro = membroParaTeste();
        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO historico_atividades "
                        + "(membro_id, funcionario_responsavel_id, atividade, data_atividade, "
                        + "tempo_execucao_minutos) VALUES (?, NULL, ?, ?, ?)",
                membro.getId(), "Atividade de teste", LocalDate.now(), 10));
    }

    @Test
    void consultaPorMembroNaoRetornaAtividadesDeOutroMembro() {
        Membro primeiro = criarMembroTransitorio();
        Membro segundo = criarMembroTransitorio();
        Funcionario funcionario = funcionarioParaTeste();
        HistoricoAtividade primeiraAtividade = historicoService.registrar(
                atividadeValida(), primeiro.getId(), funcionario.getId());
        HistoricoAtividade segundaAtividade = historicoService.registrar(
                atividadeValida(), primeiro.getId(), funcionario.getId());
        HistoricoAtividade atividadeDoSegundo = historicoService.registrar(
                atividadeValida(), segundo.getId(), funcionario.getId());
        historicoRepository.flush();
        entityManager.clear();

        List<HistoricoAtividade> doPrimeiro = historicoService.consultarPorMembro(primeiro.getId());
        List<HistoricoAtividade> doSegundo = historicoService.consultarPorMembro(segundo.getId());

        assertEquals(Set.of(primeiraAtividade.getId(), segundaAtividade.getId()),
                idsDasAtividades(doPrimeiro));
        assertEquals(Set.of(atividadeDoSegundo.getId()), idsDasAtividades(doSegundo));
        assertTrue(doPrimeiro.stream()
                .allMatch(atividade -> atividade.getMembro().getId().equals(primeiro.getId())));
        assertTrue(doSegundo.stream()
                .allMatch(atividade -> atividade.getMembro().getId().equals(segundo.getId())));
        assertTrue(doPrimeiro.stream().allMatch(atividade ->
                atividade.getFuncionarioResponsavel().getId().equals(funcionario.getId())));
    }

    @Test
    void consultaFiltradaIsolaMembroCombinaCpfEPeriodoEOrdenaDatasIguais() {
        Membro ana = criarMembroTransitorio();
        ana.setNome("Ana Beatriz Integração");
        membroRepository.saveAndFlush(ana);
        Membro bruno = criarMembroTransitorio();
        bruno.setNome("Bruno Integração");
        membroRepository.saveAndFlush(bruno);
        Membro semAtividades = criarMembroTransitorio();
        semAtividades.setNome("Membro sem atividades");
        membroRepository.saveAndFlush(semAtividades);
        Funcionario funcionario = funcionarioParaTeste();

        HistoricoAtividade antiga = historicoService.registrar(
                atividadeValida(LocalDate.of(2026, 11, 9), "Esteira"),
                ana.getId(), funcionario.getId());
        HistoricoAtividade primeiraNaData = historicoService.registrar(
                atividadeValida(LocalDate.of(2026, 11, 10), "Treino de força"),
                ana.getId(), funcionario.getId());
        HistoricoAtividade segundaNaData = historicoService.registrar(
                atividadeValida(LocalDate.of(2026, 11, 10), "Bicicleta ergométrica"),
                ana.getId(), funcionario.getId());
        historicoService.registrar(
                atividadeValida(LocalDate.of(2026, 11, 10), "Atividade de Bruno"),
                bruno.getId(), funcionario.getId());
        historicoRepository.flush();
        entityManager.clear();

        List<HistoricoAtividade> porMembro = historicoService.consultar(
                ana.getId(), "Ana", null, null);
        List<HistoricoAtividade> porCpfEPeriodo = historicoService.consultar(
                ana.getId(),
                ana.getCpf(),
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 10));
        List<HistoricoAtividade> desdeDataInicial = historicoService.consultar(
                ana.getId(), "", LocalDate.of(2026, 11, 10), null);
        List<HistoricoAtividade> ateDataFinal = historicoService.consultar(
                ana.getId(), "", null, LocalDate.of(2026, 11, 9));
        List<HistoricoAtividade> semAtividadesEncontradas = historicoService.consultar(
                semAtividades.getId(), "", null, null);

        assertEquals(Set.of(antiga.getId(), primeiraNaData.getId(), segundaNaData.getId()),
                idsDasAtividades(porMembro));
        assertTrue(porMembro.stream()
                .allMatch(item -> item.getMembro().getId().equals(ana.getId())));
        assertEquals(List.of(segundaNaData.getId(), primeiraNaData.getId()),
                porCpfEPeriodo.stream().map(HistoricoAtividade::getId).toList());
        assertEquals(List.of(segundaNaData.getId(), primeiraNaData.getId()),
                desdeDataInicial.stream().map(HistoricoAtividade::getId).toList());
        assertEquals(List.of(antiga.getId()),
                ateDataFinal.stream().map(HistoricoAtividade::getId).toList());
        assertTrue(semAtividadesEncontradas.isEmpty());
        assertTrue(porCpfEPeriodo.stream().allMatch(item ->
                item.getFuncionarioResponsavel().getNome().equals(funcionario.getNome())));
    }

    @Test
    void consultaAtividadePersistidaRetornaDescricaoDataETempoGravados() {
        Membro membro = criarMembroTransitorio();
        Funcionario funcionario = funcionarioParaTeste();
        HistoricoAtividade salva = historicoService.registrar(
                atividadeValida(), membro.getId(), funcionario.getId());
        historicoRepository.flush();
        entityManager.clear();

        HistoricoAtividade consultada =
                historicoRepository.findById(salva.getId()).orElseThrow();

        assertNotSame(salva, consultada);
        assertEquals(membro.getId(), consultada.getMembro().getId());
        assertEquals(funcionario.getId(), consultada.getFuncionarioResponsavel().getId());
        assertEquals("Acompanhamento de treino", consultada.getAtividade());
        assertEquals(LocalDate.of(2026, 11, 10), consultada.getDataAtividade());
        assertEquals(30, consultada.getTempoExecucaoMinutos());
    }

    @Test
    void checkDoBancoRejeitaTempoNegativo() {
        Membro membro = membroParaTeste();
        Funcionario funcionario = funcionarioParaTeste();

        assertThrows(DataIntegrityViolationException.class, () -> inserir(membro.getId(),
                funcionario.getId(), "Atividade de teste", -1));
    }

    private Set<Long> idsDasAtividades(List<HistoricoAtividade> atividades) {
        return atividades.stream().map(HistoricoAtividade::getId).collect(Collectors.toSet());
    }

    private void inserir(long membroId, long funcionarioId, String atividade, int tempo) {
        jdbcTemplate.update(
                "INSERT INTO historico_atividades "
                        + "(membro_id, funcionario_responsavel_id, atividade, data_atividade, "
                        + "tempo_execucao_minutos) VALUES (?, ?, ?, ?, ?)",
                membroId, funcionarioId, atividade, LocalDate.now(), tempo);
    }

    private Membro membroParaTeste() {
        return membroRepository.findAll().stream()
                .findFirst()
                .orElseGet(this::criarMembroTransitorio);
    }

    private Membro criarMembroTransitorio() {
        Membro membro = new Membro();
        membro.setNome("Membro de integração");
        membro.setCpf(cpfDisponivel());
        membro.setTelefone("(48) 99999-0000");
        membro.setEndereco("Rua de teste, 1");
        return membroRepository.saveAndFlush(membro);
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

    private Funcionario funcionarioParaTeste() {
        return funcionarioRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> funcionarioRepository.saveAndFlush(
                        new Funcionario("Funcionário de integração", "Teste", "teste-integracao",
                                passwordEncoder.encode(java.util.UUID.randomUUID().toString()))));
    }

    private HistoricoAtividade atividadeValida() {
        return atividadeValida(LocalDate.of(2026, 11, 10), "Acompanhamento de treino");
    }

    private HistoricoAtividade atividadeValida(LocalDate data, String descricao) {
        HistoricoAtividade atividade = new HistoricoAtividade();
        atividade.setAtividade(descricao);
        atividade.setDataAtividade(data);
        atividade.setTempoExecucaoMinutos(30);
        return atividade;
    }
}

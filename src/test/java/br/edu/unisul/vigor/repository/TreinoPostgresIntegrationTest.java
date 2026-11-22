package br.edu.unisul.vigor.repository;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.model.Treino;
import br.edu.unisul.vigor.service.TreinoService;
import br.edu.unisul.vigor.validation.CpfUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
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
class TreinoPostgresIntegrationTest {

    @Autowired
    private TreinoRepository treinoRepository;

    @Autowired
    private MembroRepository membroRepository;

    @Autowired
    private TreinoService treinoService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void persisteTreinoVinculadoEConsultaPeloMembro() {
        Membro membro = membroParaTeste();
        Treino treino = treinoValido();

        Treino salvo = treinoService.cadastrar(treino, membro.getId());
        List<Treino> consultados = treinoService.consultarPorMembro(membro.getId());

        assertNotNull(salvo.getId());
        assertEquals(membro.getId(), salvo.getMembro().getId());
        assertTrue(consultados.stream().anyMatch(item -> item.getId().equals(salvo.getId())));
    }

    @Test
    void atualizaTreinoExistenteSemAlterarSeuIdentificador() {
        Membro membro = membroParaTeste();
        Treino salvo = treinoService.cadastrar(treinoValido(), membro.getId());
        Treino dadosAtualizados = treinoValido();
        dadosAtualizados.setTipo("Treino atualizado");
        dadosAtualizados.setDescricao("Descrição atualizada");
        dadosAtualizados.setDuracaoMinutos(50);
        dadosAtualizados.setDataInicio(LocalDate.of(2026, 2, 12));

        treinoService.atualizar(salvo.getId(), dadosAtualizados, membro.getId());
        entityManager.flush();
        entityManager.clear();

        Treino consultado = treinoRepository.findById(salvo.getId()).orElseThrow();
        assertEquals(salvo.getId(), consultado.getId());
        assertEquals("Treino atualizado", consultado.getTipo());
        assertEquals("Descrição atualizada", consultado.getDescricao());
        assertEquals(50, consultado.getDuracaoMinutos());
        assertEquals(LocalDate.of(2026, 2, 12), consultado.getDataInicio());
    }

    @Test
    void excluiTreinoExistente() {
        Membro membro = membroParaTeste();
        Treino salvo = treinoService.cadastrar(treinoValido(), membro.getId());
        Long treinoId = salvo.getId();

        treinoService.excluir(treinoId);

        assertTrue(treinoRepository.findById(treinoId).isEmpty());
    }

    @Test
    void servicoRejeitaDuracaoInvalidaSemPersistirTreino() {
        Membro membro = membroParaTeste();
        long quantidadeAntes = treinoRepository.findByMembro_Id(membro.getId()).size();
        Treino treino = treinoValido();
        treino.setDuracaoMinutos(0);

        assertThrows(ConstraintViolationException.class,
                () -> treinoService.cadastrar(treino, membro.getId()));

        assertEquals(quantidadeAntes,
                treinoRepository.findByMembro_Id(membro.getId()).size());
    }

    @Test
    void chaveEstrangeiraRejeitaMembroInexistente() {
        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO treinos (membro_id, tipo, descricao, duracao_minutos, data_inicio) "
                        + "VALUES (?, ?, ?, ?, ?)",
                -1L, "Musculação", "Treino de teste", 45, LocalDate.now()));
    }

    @Test
    void checkDoBancoRejeitaDuracaoNaoPositiva() {
        Membro membro = membroParaTeste();

        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO treinos (membro_id, tipo, descricao, duracao_minutos, data_inicio) "
                        + "VALUES (?, ?, ?, ?, ?)",
                membro.getId(), "Musculação", "Treino de teste", 0, LocalDate.now()));
    }

    @Test
    void checkDoBancoRejeitaTipoVazio() {
        Membro membro = membroParaTeste();

        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO treinos (membro_id, tipo, descricao, duracao_minutos, data_inicio) "
                        + "VALUES (?, ?, ?, ?, ?)",
                membro.getId(), "   ", "Treino de teste", 45, LocalDate.now()));
    }

    @Test
    void consultaTreinoPorIdRetornaOsDadosPersistidos() {
        Membro membro = membroParaTeste();
        Treino salvo = treinoService.cadastrar(treinoValido(), membro.getId());
        treinoRepository.flush();
        entityManager.clear();

        Treino consultado = treinoRepository.findById(salvo.getId()).orElseThrow();

        assertNotSame(salvo, consultado);
        assertEquals(salvo.getId(), consultado.getId());
        assertEquals(membro.getId(), consultado.getMembro().getId());
        assertEquals("Musculação", consultado.getTipo());
        assertEquals("Treino de força", consultado.getDescricao());
        assertEquals(45, consultado.getDuracaoMinutos());
        assertEquals(LocalDate.of(2026, 1, 10), consultado.getDataInicio());
    }

    @Test
    void consultaTreinoPorIdInexistenteRetornaVazio() {
        // Os IDs gerados pela coluna IDENTITY são positivos; -1 nunca existe na tabela.
        assertTrue(treinoRepository.findById(-1L).isEmpty());
    }

    @Test
    void consultaPorMembroNaoRetornaTreinosDeOutroMembro() {
        Membro primeiro = criarMembroTransitorio();
        Membro segundo = criarMembroTransitorio();
        Treino primeiroTreino = treinoService.cadastrar(treinoValido(), primeiro.getId());
        Treino segundoTreino = treinoService.cadastrar(treinoValido(), primeiro.getId());
        Treino treinoDoSegundo = treinoService.cadastrar(treinoValido(), segundo.getId());
        treinoRepository.flush();
        entityManager.clear();

        List<Treino> doPrimeiro = treinoService.consultarPorMembro(primeiro.getId());
        List<Treino> doSegundo = treinoService.consultarPorMembro(segundo.getId());

        assertEquals(Set.of(primeiroTreino.getId(), segundoTreino.getId()),
                idsDosTreinos(doPrimeiro));
        assertEquals(Set.of(treinoDoSegundo.getId()), idsDosTreinos(doSegundo));
        assertTrue(doPrimeiro.stream()
                .allMatch(treino -> treino.getMembro().getId().equals(primeiro.getId())));
        assertTrue(doSegundo.stream()
                .allMatch(treino -> treino.getMembro().getId().equals(segundo.getId())));
    }

    @Test
    void consultaPorMembroSemTreinosRetornaListaVaziaMesmoComTreinoDeOutroMembro() {
        Membro membroComTreino = criarMembroTransitorio();
        Membro membroSemTreinos = criarMembroTransitorio();
        Treino treinoDoPrimeiro = treinoService.cadastrar(
                treinoValido(), membroComTreino.getId());
        treinoRepository.flush();
        entityManager.clear();

        List<Treino> treinosDoPrimeiro =
                treinoService.consultarPorMembro(membroComTreino.getId());
        List<Treino> treinosDoSegundo =
                treinoService.consultarPorMembro(membroSemTreinos.getId());

        assertEquals(Set.of(treinoDoPrimeiro.getId()), idsDosTreinos(treinosDoPrimeiro));
        assertTrue(treinosDoSegundo.isEmpty());
    }

    @Test
    void checkDoBancoRejeitaDescricaoVazia() {
        Membro membro = membroParaTeste();

        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO treinos (membro_id, tipo, descricao, duracao_minutos, data_inicio) "
                        + "VALUES (?, ?, ?, ?, ?)",
                membro.getId(), "Musculação", "   ", 45, LocalDate.now()));
    }

    @Test
    void bancoExigeDataDeInicio() {
        Membro membro = membroParaTeste();

        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO treinos (membro_id, tipo, descricao, duracao_minutos, data_inicio) "
                        + "VALUES (?, ?, ?, ?, NULL)",
                membro.getId(), "Musculação", "Treino de teste", 45));
    }

    private Set<Long> idsDosTreinos(List<Treino> treinos) {
        return treinos.stream().map(Treino::getId).collect(Collectors.toSet());
    }

    private Membro membroParaTeste() {
        return membroRepository.findAll().stream()
                .findFirst()
                .orElseGet(this::criarMembroTransitorio);
    }

    private Membro criarMembroTransitorio() {
        String cpf = cpfDisponivel();
        Membro membro = new Membro();
        membro.setNome("Membro de integração");
        membro.setCpf(cpf);
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

    private Treino treinoValido() {
        Treino treino = new Treino();
        treino.setTipo("Musculação");
        treino.setDescricao("Treino de força");
        treino.setDuracaoMinutos(45);
        treino.setDataInicio(LocalDate.of(2026, 1, 10));
        return treino;
    }
}

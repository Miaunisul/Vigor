package br.edu.unisul.vigor.repository;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.model.Treino;
import br.edu.unisul.vigor.service.TreinoService;
import br.edu.unisul.vigor.validation.CpfUtils;
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
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.flyway.enabled=false")
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

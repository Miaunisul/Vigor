package br.edu.unisul.vigor.repository;

import br.edu.unisul.vigor.model.Funcionario;
import br.edu.unisul.vigor.model.HistoricoAtividade;
import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.model.Pagamento;
import br.edu.unisul.vigor.model.Treino;
import br.edu.unisul.vigor.service.MembroService;
import br.edu.unisul.vigor.validation.CpfUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.flyway.enabled=false")
@Transactional
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class MembroExclusaoPostgresIntegrationTest {

    @Autowired
    private MembroRepository membroRepository;

    @Autowired
    private TreinoRepository treinoRepository;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private HistoricoAtividadeRepository historicoRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private MembroService membroService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void servicoExcluiMembroSemRegistrosRelacionados() {
        Membro membro = criarMembro();

        membroService.excluir(membro.getId());

        assertFalse(membroRepository.existsById(membro.getId()));
    }

    @Test
    void servicoBloqueiaExclusaoDeMembroComTreino() {
        Membro membro = criarMembro();
        criarTreino(membro);

        assertThrows(IllegalStateException.class, () -> membroService.excluir(membro.getId()));

        assertTrue(membroRepository.existsById(membro.getId()));
    }

    @Test
    void servicoBloqueiaExclusaoDeMembroComPagamento() {
        Membro membro = criarMembro();
        criarPagamento(membro);

        assertThrows(IllegalStateException.class, () -> membroService.excluir(membro.getId()));

        assertTrue(membroRepository.existsById(membro.getId()));
    }

    @Test
    void servicoBloqueiaExclusaoDeMembroComHistorico() {
        Membro membro = criarMembro();
        criarHistorico(membro);

        assertThrows(IllegalStateException.class, () -> membroService.excluir(membro.getId()));

        assertTrue(membroRepository.existsById(membro.getId()));
    }

    @Test
    void fkDeTreinoBloqueiaExclusaoDiretaNoBanco() {
        Membro membro = criarMembro();
        criarTreino(membro);

        assertThrows(DataIntegrityViolationException.class,
                () -> jdbcTemplate.update("DELETE FROM membros WHERE id = ?", membro.getId()));
    }

    @Test
    void fkDePagamentoBloqueiaExclusaoDiretaNoBanco() {
        Membro membro = criarMembro();
        criarPagamento(membro);

        assertThrows(DataIntegrityViolationException.class,
                () -> jdbcTemplate.update("DELETE FROM membros WHERE id = ?", membro.getId()));
    }

    @Test
    void fkDeHistoricoBloqueiaExclusaoDiretaNoBanco() {
        Membro membro = criarMembro();
        criarHistorico(membro);

        assertThrows(DataIntegrityViolationException.class,
                () -> jdbcTemplate.update("DELETE FROM membros WHERE id = ?", membro.getId()));
    }

    private Membro criarMembro() {
        Membro membro = new Membro();
        membro.setNome("Membro de teste de exclusão");
        membro.setCpf(cpfDisponivel());
        membro.setTelefone("(48) 99999-0000");
        membro.setEndereco("Rua de teste, 1");
        return membroRepository.saveAndFlush(membro);
    }

    private void criarTreino(Membro membro) {
        Treino treino = new Treino();
        treino.setMembro(membro);
        treino.setTipo("Musculação");
        treino.setDescricao("Treino de teste");
        treino.setDuracaoMinutos(30);
        treino.setDataInicio(LocalDate.now());
        treinoRepository.saveAndFlush(treino);
    }

    private void criarPagamento(Membro membro) {
        Pagamento pagamento = new Pagamento();
        pagamento.setMembro(membro);
        pagamento.setValor(new BigDecimal("50.00"));
        pagamento.setDataPagamento(LocalDate.now());
        pagamento.setStatus("PAGO");
        pagamentoRepository.saveAndFlush(pagamento);
    }

    private void criarHistorico(Membro membro) {
        HistoricoAtividade atividade = new HistoricoAtividade();
        atividade.setMembro(membro);
        atividade.setFuncionarioResponsavel(funcionarioParaTeste());
        atividade.setAtividade("Atividade de teste");
        atividade.setDataAtividade(LocalDate.now());
        atividade.setTempoExecucaoMinutos(10);
        historicoRepository.saveAndFlush(atividade);
    }

    private Funcionario funcionarioParaTeste() {
        return funcionarioRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> funcionarioRepository.saveAndFlush(new Funcionario(
                        "Funcionário de teste",
                        "Teste",
                        "delete-it-" + UUID.randomUUID(),
                        passwordEncoder.encode("senha-temporaria-de-teste"))));
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

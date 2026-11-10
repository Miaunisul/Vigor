package br.edu.unisul.vigor.repository;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.service.MembroService;
import br.edu.unisul.vigor.validation.CpfUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.flyway.enabled=false")
@Transactional
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class MembroCpfPostgresIntegrationTest {

    private static final String UNIQUE_CPF_CONSTRAINT = "uk_membros_cpf";
    private static final String DUPLICATE_ATTEMPT_SAVEPOINT = "before_duplicate_cpf";

    @Autowired
    private MembroRepository membroRepository;

    @Autowired
    private MembroService membroService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void postgresConstraintRejectsSecondMemberWithSameCpf() {
        String cpf = cpfDisponivel();
        Membro primeiro = membroService.cadastrar(novoMembro(cpf));
        membroRepository.flush();

        assertNotNull(primeiro.getId());
        jdbcTemplate.execute("SAVEPOINT " + DUPLICATE_ATTEMPT_SAVEPOINT);

        DataIntegrityViolationException excecao = assertThrows(
                DataIntegrityViolationException.class,
                () -> jdbcTemplate.update(
                        "INSERT INTO membros (nome, cpf, telefone, endereco, data_cadastro) "
                                + "VALUES (?, ?, ?, ?, ?)",
                        "Segundo membro de teste", cpf, "(48) 99999-1111",
                        "Rua de teste, 2", LocalDate.now()));

        jdbcTemplate.execute("ROLLBACK TO SAVEPOINT " + DUPLICATE_ATTEMPT_SAVEPOINT);
        jdbcTemplate.execute("RELEASE SAVEPOINT " + DUPLICATE_ATTEMPT_SAVEPOINT);

        SQLException causaSql = encontrarSQLException(excecao);
        assertNotNull(causaSql);
        assertEquals("23505", causaSql.getSQLState());
        assertTrue(causaSql.getMessage().contains(UNIQUE_CPF_CONSTRAINT));
        assertEquals(1L, contarRegistros(cpf));
    }

    @Test
    void servicoNormalizaPontuacaoERejeitaCpfDuplicadoMantendoUmRegistro() {
        String cpf = cpfDisponivel();
        membroService.cadastrar(novoMembro(cpf));

        String cpfPontuado = cpf.substring(0, 3) + "."
                + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-"
                + cpf.substring(9);

        assertThrows(IllegalArgumentException.class,
                () -> membroService.cadastrar(novoMembro(cpfPontuado)));

        assertEquals(cpf, membroRepository.findByCpf(cpf).orElseThrow().getCpf());
        assertEquals(1L, contarRegistros(cpf));
    }

    private long contarRegistros(String cpf) {
        Long total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM membros WHERE cpf = ?", Long.class, cpf);
        return total == null ? 0 : total;
    }

    private SQLException encontrarSQLException(Throwable excecao) {
        for (Throwable causa = excecao; causa != null; causa = causa.getCause()) {
            if (causa instanceof SQLException sql) {
                return sql;
            }
        }
        return null;
    }

    private Membro novoMembro(String cpf) {
        Membro membro = new Membro();
        membro.setNome("Membro de integração CPF");
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

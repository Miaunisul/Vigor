package br.edu.unisul.vigor.repository;

import br.edu.unisul.vigor.model.Funcionario;
import br.edu.unisul.vigor.service.FuncionarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class FuncionarioPostgresIntegrationTest {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void conectaAoPostgreSQL() {
        String versao = jdbcTemplate.queryForObject("SELECT version()", String.class);

        assertTrue(versao.startsWith("PostgreSQL"));
    }

    @Test
    void persisteHashBCryptEAutenticaLoginIgnorandoCaixaEEspacosExternos() {
        String loginBase = "vigor-it-" + UUID.randomUUID();
        String senha = "Senha-de-integracao-123";
        Funcionario funcionario = novoFuncionario(
                "  " + loginBase + "  ", passwordEncoder.encode(senha));

        Funcionario salvo = funcionarioRepository.saveAndFlush(funcionario);

        assertNotNull(salvo.getId());
        assertNotEquals(senha, salvo.getSenhaHash());
        assertTrue(passwordEncoder.matches(senha, salvo.getSenhaHash()));
        assertTrue(funcionarioService.autenticar(
                loginBase.toUpperCase(Locale.ROOT), senha));
        assertFalse(funcionarioService.autenticar(
                loginBase.toUpperCase(Locale.ROOT), "senha-incorreta"));
    }

    @Test
    void indiceUnicoRejeitaLoginDuplicadoComCaixaOuEspacosDiferentes() {
        String loginBase = "vigor-it-" + UUID.randomUUID();
        funcionarioRepository.saveAndFlush(novoFuncionario(
                "  " + loginBase + "  ", passwordEncoder.encode("senha-um")));

        Funcionario duplicado = novoFuncionario(
                loginBase.toUpperCase(Locale.ROOT), passwordEncoder.encode("senha-dois"));

        assertThrows(DataIntegrityViolationException.class,
                () -> funcionarioRepository.saveAndFlush(duplicado));
    }

    private Funcionario novoFuncionario(String login, String senhaHash) {
        return new Funcionario("Funcionário de Integração", "Teste", login, senhaHash);
    }
}

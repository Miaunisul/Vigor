package br.edu.unisul.vigor.repository;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.service.MembroService;
import br.edu.unisul.vigor.validation.CpfUtils;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class MembroAtualizacaoPostgresIntegrationTest {

    private final String prefixoTeste = "Membro atualização " + UUID.randomUUID();

    @Autowired
    private MembroService membroService;

    @Autowired
    private MembroRepository membroRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void removerDadosDoTeste() {
        jdbcTemplate.update("DELETE FROM membros WHERE nome LIKE ?", prefixoTeste + "%");
    }

    @Test
    void atualizaCamposComCpfProprioEPreservaIdEDataCadastroNoPostgres() {
        Membro original = membroService.cadastrar(novoMembro(" original", cpfDisponivel()));
        Long idOriginal = original.getId();
        LocalDate dataCadastroOriginal = LocalDate.of(2001, 2, 3);
        jdbcTemplate.update("UPDATE membros SET data_cadastro = ? WHERE id = ?",
                dataCadastroOriginal, idOriginal);
        entityManager.clear();

        Membro dados = novoMembro(" atualizado", formatarCpf(original.getCpf()));
        dados.setTelefone("(48) 99999-1234");
        dados.setEndereco("Rua atualizada, 2");
        Membro atualizado = membroService.atualizar(idOriginal, dados);

        assertEquals(idOriginal, atualizado.getId());
        assertEquals(dataCadastroOriginal, atualizado.getDataCadastro());
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM membros WHERE id = ? AND nome = ? AND cpf = ? "
                        + "AND telefone = ? AND endereco = ? AND data_cadastro = ?",
                Integer.class, idOriginal, prefixoTeste + " atualizado", original.getCpf(),
                "(48) 99999-1234", "Rua atualizada, 2", dataCadastroOriginal));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void atualizaCpfDisponivelComOuSemPontuacaoEGravaSomenteDigitos(boolean pontuado) {
        Membro original = membroService.cadastrar(novoMembro(" original", cpfDisponivel()));
        String novoCpf = cpfDisponivel();
        String cpfInformado = pontuado ? formatarCpf(novoCpf) : novoCpf;
        Membro dados = novoMembro(" cpf atualizado", cpfInformado);

        membroService.atualizar(original.getId(), dados);

        assertEquals(novoCpf, jdbcTemplate.queryForObject(
                "SELECT cpf FROM membros WHERE id = ?", String.class, original.getId()));
        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM membros WHERE cpf = ?", Integer.class, novoCpf));
    }

    @Test
    void rejeitaCpfInvalidoSemAlterarRegistroPersistido() {
        Membro original = membroService.cadastrar(novoMembro(" original", cpfDisponivel()));
        Membro dados = novoMembro(" não deve gravar", "52998224726");

        assertThrows(ConstraintViolationException.class,
                () -> membroService.atualizar(original.getId(), dados));

        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM membros WHERE id = ? AND nome = ? AND cpf = ? "
                        + "AND telefone = ? AND endereco = ?",
                Integer.class, original.getId(), prefixoTeste + " original", original.getCpf(),
                "(48) 99999-0000", "Rua de teste, 1"));
    }

    @Test
    void rejeitaCpfDeOutroMembroComFormatoDiferenteESemAlterarDadosOriginais() {
        Membro donoCpf = membroService.cadastrar(novoMembro(" dono do cpf", cpfDisponivel()));
        Membro membroEditado = membroService.cadastrar(novoMembro(" membro editado", cpfDisponivel()));
        String cpfDuplicadoPontuado = formatarCpf(donoCpf.getCpf());
        Membro dados = novoMembro(" não deve gravar", cpfDuplicadoPontuado);

        assertThrows(IllegalArgumentException.class,
                () -> membroService.atualizar(membroEditado.getId(), dados));

        assertEquals(1, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM membros WHERE id = ? AND nome = ? AND cpf = ? "
                        + "AND telefone = ? AND endereco = ?",
                Integer.class, membroEditado.getId(), prefixoTeste + " membro editado",
                membroEditado.getCpf(), "(48) 99999-0000", "Rua de teste, 1"));
        assertNotNull(membroRepository.findById(donoCpf.getId()).orElse(null));
    }

    private Membro novoMembro(String sufixo, String cpf) {
        Membro membro = new Membro();
        membro.setNome(prefixoTeste + sufixo);
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

    private String formatarCpf(String cpf) {
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }
}

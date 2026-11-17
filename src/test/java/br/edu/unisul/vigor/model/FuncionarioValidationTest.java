package br.edu.unisul.vigor.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FuncionarioValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void criarValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void fecharValidator() {
        validatorFactory.close();
    }

    @Test
    void exigeNomeCargoLoginESenhaHashNaoVazios() {
        Funcionario funcionario = new Funcionario(" ", "", "   ", " ");

        Set<String> camposInvalidos = validator.validate(funcionario).stream()
                .map(violacao -> violacao.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(camposInvalidos.contains("nome"));
        assertTrue(camposInvalidos.contains("cargo"));
        assertTrue(camposInvalidos.contains("login"));
        assertTrue(camposInvalidos.contains("senhaHash"));
    }

    @Test
    void rejeitaNomeAcimaDoLimiteDaColuna() {
        Funcionario funcionario = new Funcionario(
                "N".repeat(151), "Atendimento", "mille", "hash-de-teste");

        assertTrue(validator.validate(funcionario).stream()
                .anyMatch(violacao -> violacao.getPropertyPath().toString().equals("nome")));
    }

    @Test
    void disponibilizaOsDadosDoFuncionario() {
        Funcionario funcionario = new Funcionario(
                "Millena Ferreira", "Atendimento", "mille", "hash-de-teste");

        assertEquals("Millena Ferreira", funcionario.getNome());
        assertEquals("Atendimento", funcionario.getCargo());
        assertEquals("mille", funcionario.getLogin());
        assertEquals("hash-de-teste", funcionario.getSenhaHash());
    }
}

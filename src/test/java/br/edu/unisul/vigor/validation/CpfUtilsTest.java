package br.edu.unisul.vigor.validation;

import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpfUtilsTest {

    private static final ValidatorFactory VALIDATOR_FACTORY =
            Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    @Test
    void deveManterCpfSemPontuacao() {
        assertEquals("52998224725", CpfUtils.normalizar("52998224725"));
    }

    @Test
    void deveRemoverPontosEHifen() {
        assertEquals("52998224725", CpfUtils.normalizar("529.982.247-25"));
    }

    @Test
    void deveRemoverEspacosExternosAntesDaPontuacao() {
        assertEquals("52998224725", CpfUtils.normalizar("  529.982.247-25  "));
    }

    @Test
    void devePreservarEspacosInternosParaQueSejamRejeitadosPelaValidacao() {
        assertEquals("529 98224725", CpfUtils.normalizar("529 98224725"));
    }

    @Test
    void devePreservarValorNulo() {
        assertNull(CpfUtils.normalizar(null));
    }

    @Test
    void deveValidarCpfComDigitosVerificadoresCorretos() {
        assertTrue(CpfUtils.ehValido("52998224725"));
    }

    @Test
    void deveValidarCpfFormatado() {
        assertTrue(CpfUtils.ehValido("529.982.247-25"));
    }

    @Test
    void deveRejeitarCpfComDigitosVerificadoresIncorretos() {
        assertFalse(CpfUtils.ehValido("52998224726"));
    }

    @Test
    void deveRejeitarSequenciasDeDigitosRepetidos() {
        assertFalse(CpfUtils.ehValido("11111111111"));
    }

    @Test
    void deveRejeitarCaracteresNaoNumericosEQuantidadeIncorreta() {
        assertFalse(CpfUtils.ehValido("52998224A25"));
        assertFalse(CpfUtils.ehValido("5299822472"));
    }

    @Test
    void anotacaoDeveAceitarCpfValidoFormatado() {
        assertTrue(VALIDATOR.validate(new CpfDeTeste("529.982.247-25")).isEmpty());
    }

    @Test
    void anotacaoDeveRejeitarCpfInvalido() {
        assertFalse(VALIDATOR.validate(new CpfDeTeste("52998224726")).isEmpty());
    }

    private record CpfDeTeste(@CpfValido String cpf) {
    }
}

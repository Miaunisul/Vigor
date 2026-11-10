package br.edu.unisul.vigor.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CpfUtilsTest {

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
}

package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.dto.RegistroCargaHistorico;
import br.edu.unisul.vigor.model.HistoricoAtividade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CargaHistoricoAtividadeServiceTest {

    private HistoricoAtividadeService historicoService;
    private CargaHistoricoAtividadeService cargaService;

    @BeforeEach
    void configurar() {
        historicoService = mock(HistoricoAtividadeService.class);
        cargaService = new CargaHistoricoAtividadeService(historicoService);
    }

    @Test
    void converteOsValoresTextuaisEDelegaAoRegistroDoHistorico() {
        HistoricoAtividade salva = new HistoricoAtividade();
        when(historicoService.registrar(any(HistoricoAtividade.class), eq(10L), eq(20L)))
                .thenReturn(salva);

        HistoricoAtividade resultado = cargaService.carregar(new RegistroCargaHistorico(
                " 10 ", "20", "Treino de força", " 2026-11-10 ", " 45 "));

        assertSame(salva, resultado);
        ArgumentCaptor<HistoricoAtividade> captor =
                ArgumentCaptor.forClass(HistoricoAtividade.class);
        verify(historicoService).registrar(captor.capture(), eq(10L), eq(20L));
        assertEquals("Treino de força", captor.getValue().getAtividade());
        assertEquals(LocalDate.of(2026, 11, 10), captor.getValue().getDataAtividade());
        assertEquals(45, captor.getValue().getTempoExecucaoMinutos());
    }

    @Test
    void valoresAusentesSeguemParaAsValidacoesDoRegistro() {
        cargaService.carregar(new RegistroCargaHistorico(null, "   ", null, "", "  "));

        ArgumentCaptor<HistoricoAtividade> captor =
                ArgumentCaptor.forClass(HistoricoAtividade.class);
        verify(historicoService).registrar(captor.capture(), isNull(), isNull());
        assertNull(captor.getValue().getAtividade());
        assertNull(captor.getValue().getDataAtividade());
        assertNull(captor.getValue().getTempoExecucaoMinutos());
    }

    @Test
    void rejeitaCargaSemDados() {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> cargaService.carregar(null));

        assertEquals("Os dados da carga são obrigatórios.", erro.getMessage());
        verifyNoInteractions(historicoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"2026-02-30", "2026-13-01", "10/11/2026", "amanhã"})
    void rejeitaDataInvalidaAntesDeRegistrar(String data) {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> cargaService.carregar(registro("10", "20", data, "30")));

        assertEquals("A data da atividade é inválida. "
                + "Informe uma data existente no formato AAAA-MM-DD.", erro.getMessage());
        verifyNoInteractions(historicoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"30.5", "30,5", "trinta", "1e2"})
    void rejeitaTempoNaoInteiroAntesDeRegistrar(String tempo) {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> cargaService.carregar(registro("10", "20", "2026-11-10", tempo)));

        assertEquals("O tempo de execução deve ser um número inteiro de minutos.",
                erro.getMessage());
        verifyNoInteractions(historicoService);
    }

    @Test
    void rejeitaReferenciaDeMembroNaoNumerica() {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> cargaService.carregar(registro("abc", "20", "2026-11-10", "30")));

        assertEquals("A referência do membro deve ser um número inteiro.", erro.getMessage());
        verifyNoInteractions(historicoService);
    }

    @Test
    void rejeitaReferenciaDeFuncionarioNaoNumerica() {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> cargaService.carregar(registro("10", "xyz", "2026-11-10", "30")));

        assertEquals("A referência do funcionário responsável deve ser um número inteiro.",
                erro.getMessage());
        verifyNoInteractions(historicoService);
    }

    private RegistroCargaHistorico registro(
            String membroId, String funcionarioId, String data, String tempo) {
        return new RegistroCargaHistorico(
                membroId, funcionarioId, "Acompanhamento de treino", data, tempo);
    }
}

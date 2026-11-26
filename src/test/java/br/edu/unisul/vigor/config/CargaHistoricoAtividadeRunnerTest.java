package br.edu.unisul.vigor.config;

import br.edu.unisul.vigor.dto.RegistroCargaHistorico;
import br.edu.unisul.vigor.model.HistoricoAtividade;
import br.edu.unisul.vigor.service.CargaHistoricoAtividadeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CargaHistoricoAtividadeRunnerTest {

    private CargaHistoricoAtividadeService cargaService;
    private CargaHistoricoAtividadeRunner runner;

    @BeforeEach
    void configurar() {
        cargaService = mock(CargaHistoricoAtividadeService.class);
        MockEnvironment environment = new MockEnvironment()
                .withProperty("carga.membro.id", "10")
                .withProperty("carga.funcionario.id", "20")
                .withProperty("carga.atividade", "Treino de força")
                .withProperty("carga.data.atividade", "2026-11-10")
                .withProperty("carga.tempo.execucao.minutos", "45");
        runner = new CargaHistoricoAtividadeRunner(cargaService, environment);
    }

    @Test
    void enviaOsValoresConfiguradosAoProcedimentoDeCarga() {
        when(cargaService.carregar(any(RegistroCargaHistorico.class)))
                .thenReturn(new HistoricoAtividade());

        runner.run(null);

        verify(cargaService).carregar(new RegistroCargaHistorico(
                "10", "20", "Treino de força", "2026-11-10", "45"));
    }

    @Test
    void interrompeAExecucaoQuandoACargaERejeitada() {
        when(cargaService.carregar(any(RegistroCargaHistorico.class)))
                .thenThrow(new IllegalArgumentException("Membro não encontrado."));

        IllegalArgumentException erro =
                assertThrows(IllegalArgumentException.class, () -> runner.run(null));

        assertEquals("Membro não encontrado.", erro.getMessage());
    }

    @Test
    void ficaAtivoSomenteNoPerfilDeCarga() {
        Profile perfil = CargaHistoricoAtividadeRunner.class.getAnnotation(Profile.class);

        assertArrayEquals(new String[] {"carga-historico"}, perfil.value());
    }
}

package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.Funcionario;
import br.edu.unisul.vigor.model.HistoricoAtividade;
import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.service.HistoricoAtividadeService;
import br.edu.unisul.vigor.service.MembroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class AtividadeTemplateRenderingTest {

    private HistoricoAtividadeService historicoService;
    private MembroService membroService;
    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        historicoService = mock(HistoricoAtividadeService.class);
        membroService = mock(MembroService.class);

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setCharacterEncoding("UTF-8");

        mockMvc = standaloneSetup(new AtividadeController(historicoService, membroService))
                .setViewResolvers(viewResolver)
                .build();
        when(membroService.listarTodos()).thenReturn(List.of(membro("Ana Beatriz Costa", 10L)));
        when(historicoService.consultar(null, "", null, null)).thenReturn(List.of());
    }

    @Test
    void renderizaHistoricoComResponsavelFiltrosEAtividadesAtivoNaSidebar() throws Exception {
        when(historicoService.consultar(null, "", null, null))
                .thenReturn(List.of(atividade("Esteira", "Ana Beatriz Costa")));

        mockMvc.perform(get("/atividades"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/css/atividades.css")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Consulte o histórico")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Buscar membro por nome ou CPF")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Data inicial")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Data final")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Esteira")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("10/11/2026")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Camila Martins")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"/atividades\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("aria-current=\"page\"")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("Registrar atividade"))));
    }

    @Test
    void preservaFiltrosEExibeMensagemQuandoNaoHaAtividades() throws Exception {
        mockMvc.perform(get("/atividades")
                        .param("membroId", "10")
                        .param("q", "Ana")
                        .param("dataInicial", "2026-11-01")
                        .param("dataFinal", "2026-11-30"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Nenhuma atividade encontrada para os filtros informados.")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("value=\"Ana\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("value=\"2026-11-01\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("value=\"2026-11-30\"")));

        verify(historicoService).consultar(
                10L, "Ana", LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30));
    }

    @Test
    void rejeitaIntervaloInvertidoEDataMalformadaSemConsultarHistorico() throws Exception {
        mockMvc.perform(get("/atividades")
                        .param("dataInicial", "2026-11-21")
                        .param("dataFinal", "2026-11-20"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "A data inicial não pode ser posterior à data final.")));

        mockMvc.perform(get("/atividades").param("dataInicial", "data-invalida"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Informe um membro e um período válidos.")));

        verifyNoInteractions(historicoService);
    }

    private Membro membro(String nome, Long id) {
        Membro membro = new Membro();
        membro.setNome(nome);
        ReflectionTestUtils.setField(membro, "id", id);
        return membro;
    }

    private HistoricoAtividade atividade(String nomeAtividade, String nomeMembro) {
        HistoricoAtividade atividade = new HistoricoAtividade();
        atividade.setAtividade(nomeAtividade);
        atividade.setDataAtividade(LocalDate.of(2026, 11, 10));
        atividade.setTempoExecucaoMinutos(30);
        atividade.setMembro(membro(nomeMembro, 10L));
        atividade.setFuncionarioResponsavel(
                new Funcionario("Camila Martins", "Administradora", "camila", "hash"));
        return atividade;
    }
}

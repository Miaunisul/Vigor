package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.model.Treino;
import br.edu.unisul.vigor.service.MembroService;
import br.edu.unisul.vigor.service.TreinoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class TreinoTemplateRenderingTest {

    private TreinoService treinoService;
    private MembroService membroService;
    private LocalValidatorFactoryBean validator;
    private MockMvc mockMvc;
    private Membro membro;

    @BeforeEach
    void configurar() throws ReflectiveOperationException {
        treinoService = mock(TreinoService.class);
        membroService = mock(MembroService.class);
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

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

        mockMvc = standaloneSetup(new TreinoController(treinoService, membroService))
                .setValidator(validator)
                .setViewResolvers(viewResolver)
                .build();
        membro = membroComId(42L, "Membro Teste & Família");
        when(membroService.listarTodos()).thenReturn(List.of(membro));
    }

    @AfterEach
    void liberarValidator() {
        validator.destroy();
    }

    @Test
    void renderizaFormularioComMembrosETodosOsCamposObrigatorios() throws Exception {
        mockMvc.perform(get("/treinos/novo"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cadastrar treino")))
                .andExpect(content().string(containsString("Membro Teste &amp; Família")))
                .andExpect(content().string(containsString("name=\"membroId\"")))
                .andExpect(content().string(containsString("name=\"tipo\"")))
                .andExpect(content().string(containsString("name=\"descricao\"")))
                .andExpect(content().string(containsString("name=\"duracaoMinutos\"")))
                .andExpect(content().string(containsString("name=\"dataInicio\"")))
                .andExpect(content().string(containsString("/css/membros.css")))
                .andExpect(content().string(containsString("required")));

        verifyNoInteractions(treinoService);
    }

    @Test
    void renderizaListaDoMembroSelecionadoESeusTreinos() throws Exception {
        Treino treino = treinoValido(membro);
        when(treinoService.consultarPorMembro(42L)).thenReturn(List.of(treino));

        mockMvc.perform(get("/treinos").param("membroId", "42"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Treinos cadastrados")))
                .andExpect(content().string(containsString("Musculação")))
                .andExpect(content().string(containsString("Treino de força")))
                .andExpect(content().string(containsString("45")))
                .andExpect(content().string(containsString("2026-11-20")))
                .andExpect(content().string(containsString("method=\"get\"")))
                .andExpect(content().string(containsString(
                        "onchange=\"this.form.requestSubmit()\"")))
                .andExpect(content().string(containsString("Consultar treinos")));
    }

    @Test
    void renderizaTrocaParaMembroSemTreinosESelecionaOMembroConsultado() throws Exception {
        Membro segundoMembro = membroComId(84L, "Membro sem treinos");
        Treino treinoDoPrimeiro = treinoValido(membro);
        when(membroService.listarTodos()).thenReturn(List.of(membro, segundoMembro));
        when(treinoService.consultarPorMembro(42L)).thenReturn(List.of(treinoDoPrimeiro));
        when(treinoService.consultarPorMembro(84L)).thenReturn(List.of());

        String listaDoPrimeiro = mockMvc.perform(get("/treinos").param("membroId", "42"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Treino de força")))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertMembroSelecionado(listaDoPrimeiro, 42L);

        String listaDoSegundo = mockMvc.perform(get("/treinos").param("membroId", "84"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "Nenhum treino cadastrado para este membro")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        containsString("Treino de força"))))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertMembroSelecionado(listaDoSegundo, 84L);
    }

    @Test
    void renderizaListaSemMembroSemExibirTodosOsTreinos() throws Exception {
        mockMvc.perform(get("/treinos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Selecione um membro")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        containsString("<table"))));

        verifyNoInteractions(treinoService);
    }

    @Test
    void renderizaMensagensJuntoDosCamposInvalidos() throws Exception {
        mockMvc.perform(post("/treinos")
                        .param("tipo", "   ")
                        .param("descricao", "Descrição preservada")
                        .param("duracaoMinutos", "0")
                        .param("dataInicio", "20/11/2026"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Selecione um membro cadastrado.")))
                .andExpect(content().string(containsString("O tipo do treino é obrigatório.")))
                .andExpect(content().string(containsString(
                        "A duração deve ser um número inteiro positivo de minutos.")))
                .andExpect(content().string(containsString("Informe uma data válida.")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        containsString("Failed to convert"))))
                .andExpect(content().string(containsString("field-input--invalid")))
                .andExpect(content().string(containsString(
                        "Descri&ccedil;&atilde;o preservada")));

        verifyNoInteractions(treinoService);
    }

    @Test
    void renderizaMensagemClaraQuandoDuracaoNaoForInteira() throws Exception {
        mockMvc.perform(post("/treinos")
                        .param("membroId", "42")
                        .param("tipo", "Musculação")
                        .param("descricao", "Descrição do treino")
                        .param("duracaoMinutos", "quarenta")
                        .param("dataInicio", "2026-11-20"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "Informe a duração como um número inteiro de minutos.")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        containsString("Failed to convert"))));

        verifyNoInteractions(treinoService);
    }

    private Treino treinoValido(Membro associado) {
        Treino treino = new Treino();
        treino.setMembro(associado);
        treino.setTipo("Musculação");
        treino.setDescricao("Treino de força");
        treino.setDuracaoMinutos(45);
        treino.setDataInicio(LocalDate.of(2026, 11, 20));
        return treino;
    }

    private Membro membroComId(Long id, String nome) throws ReflectiveOperationException {
        Membro membro = new Membro();
        membro.setNome(nome);
        Field campoId = Membro.class.getDeclaredField("id");
        campoId.setAccessible(true);
        campoId.set(membro, id);
        return membro;
    }

    private void assertMembroSelecionado(String html, Long membroId) {
        int indiceValor = html.indexOf("value=\"" + membroId + "\"");
        assertTrue(indiceValor >= 0, html);
        int inicioOpcao = html.lastIndexOf("<option", indiceValor);
        int fimOpcao = html.indexOf('>', indiceValor);
        assertTrue(inicioOpcao >= 0 && fimOpcao > indiceValor, html);
        assertTrue(html.substring(inicioOpcao, fimOpcao).contains("selected"), html);
    }
}

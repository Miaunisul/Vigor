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
        membro = membroComId(42L, "Ana Beatriz Costa");
        membro.setCpf("52998224725");
        when(membroService.listarTodos()).thenReturn(List.of(membro));
        when(treinoService.listarTodos()).thenReturn(List.of());
    }

    @AfterEach
    void liberarValidator() {
        validator.destroy();
    }

    @Test
    void renderizaListaComSidebarBuscaFiltroEEstados() throws Exception {
        Treino treino = treinoValido();
        setTreinoId(treino, 1L);
        when(treinoService.consultarPorMembro(42L)).thenReturn(List.of(treino));

        mockMvc.perform(get("/treinos").param("membroId", "42"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/css/treinos.css")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "/images/vigor-logo-branca.png")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Gestão da academia")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Treinos")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ana Beatriz Costa")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Treino A — membros superiores")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("45 min")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("20/11/2026")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Buscar membro por nome ou CPF")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Selecione um membro")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("Todos os membros"))))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ver detalhes")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Excluir")));
    }

    @Test
    void naoExibeTabelaNemConsultaTreinosSemMembroSelecionado() throws Exception {
        mockMvc.perform(get("/treinos").param("q", "Ana"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Selecione um membro para consultar os treinos.")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("aria-label=\"Treinos cadastrados\""))))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("Todos os membros"))));

        verifyNoInteractions(treinoService);
    }

    @Test
    void diferenciaReferenciaInvalidaDeMembroInexistente() throws Exception {
        mockMvc.perform(get("/treinos").param("membroId", "abc").param("q", "Ana"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "A referência do membro é inválida.")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Referência inválida: abc")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("value=\"Ana\"")));

        when(treinoService.consultarPorMembro(404L))
                .thenThrow(new IllegalArgumentException("Membro não encontrado."));
        mockMvc.perform(get("/treinos").param("membroId", "404"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Membro não encontrado.")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Membro não encontrado: 404")));
    }

    @Test
    void mostraEstadoVazioParaMembroExistenteSemTreinos() throws Exception {
        when(treinoService.consultarPorMembro(42L)).thenReturn(List.of());

        mockMvc.perform(get("/treinos").param("membroId", "42"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Este membro ainda não possui treinos cadastrados.")));
    }

    @Test
    void renderizaCadastroComoEstadoDaTelaPrincipal() throws Exception {
        mockMvc.perform(get("/treinos").param("acao", "novo"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Novo treino")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Dados do treino")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"membroId\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"tipo\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"descricao\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"duracaoMinutos\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"dataInicio\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Treino A — membros superiores")));

        verifyNoInteractions(treinoService);
    }

    @Test
    void renderizaDetalhesComAcoesDeEdicaoEExclusao() throws Exception {
        Treino treino = treinoValido();
        setTreinoId(treino, 1L);
        when(treinoService.buscarPorId(1L)).thenReturn(treino);

        mockMvc.perform(get("/treinos").param("acao", "detalhes").param("treinoId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Detalhes do treino")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Registro TR-001")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Membro")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("45 minutos")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Editar treino")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Excluir treino")));
    }

    @Test
    void renderizaEdicaoComDadosPreenchidosEIdSomenteLeitura() throws Exception {
        Treino treino = treinoValido();
        setTreinoId(treino, 1L);
        when(treinoService.buscarPorId(1L)).thenReturn(treino);

        mockMvc.perform(get("/treinos").param("acao", "editar").param("treinoId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Editar treino")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("value=\"TR-001\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("value=\"Treino A — membros superiores\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("value=\"45\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Salvar treino")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("readonly")));
    }

    @Test
    void renderizaErrosDeValidacaoNoMesmoFormularioEPreservaDados() throws Exception {
        mockMvc.perform(post("/treinos")
                        .param("acao", "novo")
                        .param("membroId", "42")
                        .param("tipo", " ")
                        .param("descricao", "Descrição preservada")
                        .param("duracaoMinutos", "0")
                        .param("dataInicio", "20/11/2026"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Novo treino")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("O tipo do treino é obrigatório.")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "A duração deve ser um número inteiro positivo de minutos.")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Informe uma data válida.")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Descri&ccedil;&atilde;o preservada")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("campo-invalido")));

        verifyNoInteractions(treinoService);
    }

    private Treino treinoValido() {
        Treino treino = new Treino();
        treino.setMembro(membro);
        treino.setTipo("Treino A — membros superiores");
        treino.setDescricao("Treino voltado ao fortalecimento dos membros superiores.");
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

    private void setTreinoId(Treino treino, Long id) throws ReflectiveOperationException {
        Field campoId = Treino.class.getDeclaredField("id");
        campoId.setAccessible(true);
        campoId.set(treino, id);
    }
}

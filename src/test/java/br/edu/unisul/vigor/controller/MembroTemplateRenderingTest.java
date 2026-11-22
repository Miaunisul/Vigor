package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.model.Funcionario;
import br.edu.unisul.vigor.model.HistoricoAtividade;
import br.edu.unisul.vigor.model.Pagamento;
import br.edu.unisul.vigor.model.Treino;
import br.edu.unisul.vigor.service.HistoricoAtividadeService;
import br.edu.unisul.vigor.service.MembroService;
import br.edu.unisul.vigor.service.PagamentoService;
import br.edu.unisul.vigor.service.TreinoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.unbescape.html.HtmlEscape;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class MembroTemplateRenderingTest {

    private MembroService membroService;
    private TreinoService treinoService;
    private PagamentoService pagamentoService;
    private HistoricoAtividadeService historicoAtividadeService;
    private LocalValidatorFactoryBean validator;
    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        membroService = mock(MembroService.class);
        treinoService = mock(TreinoService.class);
        pagamentoService = mock(PagamentoService.class);
        historicoAtividadeService = mock(HistoricoAtividadeService.class);
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
        mockMvc = standaloneSetup(new MembroController(
                        membroService,
                        treinoService,
                        pagamentoService,
                        historicoAtividadeService))
                .setValidator(validator)
                .setViewResolvers(viewResolver)
                .build();
    }

    @AfterEach
    void liberarValidator() {
        validator.destroy();
    }

    @Test
    void renderizaListaNoLayoutCompartilhadoComCpfMascarado() throws Exception {
        Membro membro = membroValido("Membro da lista");
        setId(membro, 42L);
        when(membroService.listarTodos()).thenReturn(List.of(membro));

        mockMvc.perform(get("/membros"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Membros | VIGOR")))
                .andExpect(content().string(containsString("Gestão da academia /")))
                .andExpect(content().string(containsString("aria-current=\"page\"")))
                .andExpect(content().string(containsString("Membro da lista")))
                .andExpect(content().string(containsString("MB-042")))
                .andExpect(content().string(containsString("***.***.***-25")))
                .andExpect(content().string(containsString("Ver detalhes")))
                .andExpect(content().string(containsString("/css/membros-modulo.css")))
                .andExpect(content().string(containsString("/membros?acao=detalhes")))
                .andExpect(content().string(containsString("/membros/42/editar")));

        assertNotNull(membro.getDataCadastro());
    }

    @Test
    void sidebarNavegaParaModulosComRotasEIdentificaTreinosComoLink() throws Exception {
        String html = mockMvc.perform(get("/membros"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertTrue(html.contains("href=\"/membros\""));
        assertTrue(html.contains("href=\"/treinos\""));
        assertTrue(html.contains("href=\"/pagamentos\""));
        assertFalse(html.contains("title=\"A navegação será integrada com o módulo de treinos\""));
        assertTrue(html.contains("Atividades"));
        assertTrue(html.contains("aria-disabled=\"true\""));
    }

    @Test
    void renderizaFormularioDeCadastroComoEstadoDaTelaPrincipal() throws Exception {
        mockMvc.perform(get("/membros/novo"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Novo membro")))
                .andExpect(content().string(containsString("action=\"/membros\"")))
                .andExpect(content().string(containsString("Dados do membro")))
                .andExpect(content().string(containsString("Nome completo *")))
                .andExpect(content().string(containsString("Endereço *")))
                .andExpect(content().string(containsString("Data de cadastro")))
                .andExpect(content().string(containsString("Salvar membro")));

        verifyNoInteractions(membroService);
    }

    @Test
    void renderizaFormularioDeEdicaoComDadosEAcaoDoMembro() throws Exception {
        String nomeAtual = "Membro teste CPF HTTP 123e4567-e89b-12d3-a456-426614174000 edição";
        Membro membro = membroValido(nomeAtual);
        setId(membro, 42L);
        when(membroService.buscarPorId(42L)).thenReturn(membro);

        mockMvc.perform(get("/membros/{id}/editar", 42L))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Editar membro")))
                .andExpect(content().string(containsString(
                        "value=\"" + HtmlEscape.escapeHtml5(nomeAtual) + "\"")))
                .andExpect(content().string(containsString("value=\"52998224725\"")))
                .andExpect(content().string(containsString("action=\"/membros/42/editar\"")))
                .andExpect(content().string(containsString("MB-042")))
                .andExpect(content().string(containsString("Data de cadastro")))
                .andExpect(content().string(containsString("/css/membros-modulo.css")));
    }

    @Test
    void renderizaDetalhesComoEstadoDaTelaPrincipal() throws Exception {
        Membro membro = membroValido("Membro para detalhes");
        membro.setEndereco("Rua das Palmeiras, 85 - São Paulo - SP");
        setId(membro, 42L);
        when(membroService.buscarPorId(42L)).thenReturn(membro);

        Treino treino = new Treino();
        treino.setMembro(membro);
        treino.setTipo("Treino A — membros superiores");
        treino.setDuracaoMinutos(45);
        treino.setDataInicio(LocalDate.of(2026, 10, 5));
        when(treinoService.consultarPorMembro(42L)).thenReturn(List.of(treino));

        Pagamento pagamento = new Pagamento();
        pagamento.setMembro(membro);
        pagamento.setValor(new BigDecimal("150.00"));
        pagamento.setDataPagamento(LocalDate.of(2026, 10, 5));
        pagamento.setStatus("PAGO");
        when(pagamentoService.consultarPorMembro(42L)).thenReturn(List.of(pagamento));

        HistoricoAtividade atividade = new HistoricoAtividade();
        atividade.setMembro(membro);
        atividade.setAtividade("Esteira");
        atividade.setDataAtividade(LocalDate.of(2026, 10, 10));
        atividade.setTempoExecucaoMinutos(30);
        atividade.setFuncionarioResponsavel(
                new Funcionario("Camila Martins", "Administradora", "camila", "hash"));
        when(historicoAtividadeService.consultarPorMembro(42L))
                .thenReturn(List.of(atividade));

        mockMvc.perform(get("/membros")
                        .param("acao", "detalhes")
                        .param("membroId", "42"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Detalhes do membro")))
                .andExpect(content().string(containsString("Membro para detalhes")))
                .andExpect(content().string(containsString("***.***.***-25")))
                .andExpect(content().string(containsString("Rua das Palmeiras, 85 - São Paulo - SP")))
                .andExpect(content().string(containsString("Informações pessoais")))
                .andExpect(content().string(containsString("Cadastro MB-042")))
                .andExpect(content().string(containsString("Treinos vinculados")))
                .andExpect(content().string(containsString("Treino A — membros superiores")))
                .andExpect(content().string(containsString("45 min")))
                .andExpect(content().string(containsString("Pagamentos")))
                .andExpect(content().string(containsString("R$ 150,00")))
                .andExpect(content().string(containsString(">Pago</span>")))
                .andExpect(content().string(containsString("Atividades realizadas")))
                .andExpect(content().string(containsString("Esteira")))
                .andExpect(content().string(containsString("30 min")))
                .andExpect(content().string(containsString("Camila Martins")))
                .andExpect(content().string(containsString("/membros/42/editar")))
                .andExpect(content().string(containsString("/membros/42/excluir")))
                .andExpect(content().string(containsString("Excluir membro")));

        verify(treinoService).consultarPorMembro(42L);
        verify(pagamentoService).consultarPorMembro(42L);
        verify(historicoAtividadeService).consultarPorMembro(42L);
    }

    @Test
    void renderizaMensagensQuandoMembroNaoTemRegistrosVinculados() throws Exception {
        Membro membro = membroValido("Membro sem histórico");
        setId(membro, 42L);
        when(membroService.buscarPorId(42L)).thenReturn(membro);
        when(treinoService.consultarPorMembro(42L)).thenReturn(List.of());
        when(pagamentoService.consultarPorMembro(42L)).thenReturn(List.of());
        when(historicoAtividadeService.consultarPorMembro(42L)).thenReturn(List.of());

        mockMvc.perform(get("/membros")
                        .param("acao", "detalhes")
                        .param("membroId", "42"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nenhum treino vinculado a este membro.")))
                .andExpect(content().string(containsString("Nenhum pagamento registrado para este membro.")))
                .andExpect(content().string(containsString("Nenhuma atividade registrada para este membro.")));

        verify(treinoService).consultarPorMembro(42L);
        verify(pagamentoService).consultarPorMembro(42L);
        verify(historicoAtividadeService).consultarPorMembro(42L);
    }

    @Test
    void renderizaErrosDeValidacaoJuntoAoCampoCpf() throws Exception {
        mockMvc.perform(post("/membros/{id}/editar", 42L)
                        .param("nome", "Nome enviado")
                        .param("cpf", "529.982.247-26")
                        .param("telefone", "Telefone enviado")
                        .param("endereco", "Endereco enviado"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Informe um CPF válido")))
                .andExpect(content().string(containsString("value=\"529.982.247-26\"")))
                .andExpect(content().string(containsString("value=\"MB-042\"")))
                .andExpect(content().string(containsString("campo-invalido")))
                .andExpect(content().string(containsString("Editar membro")));

        verifyNoInteractions(membroService);
    }

    private Membro membroValido(String nome) {
        Membro membro = new Membro();
        membro.setNome(nome);
        membro.setCpf("52998224725");
        membro.setTelefone("(48) 99999-0000");
        membro.setEndereco("Rua da lista");
        return membro;
    }

    private void setId(Membro membro, Long id) throws ReflectiveOperationException {
        var idField = Membro.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(membro, id);
    }
}

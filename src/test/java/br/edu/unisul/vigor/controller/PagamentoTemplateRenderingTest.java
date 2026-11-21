package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.model.Pagamento;
import br.edu.unisul.vigor.service.MembroService;
import br.edu.unisul.vigor.service.PagamentoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class PagamentoTemplateRenderingTest {

    private PagamentoService pagamentoService;
    private MembroService membroService;
    private LocalValidatorFactoryBean validator;
    private MockMvc mockMvc;
    private Membro membro;

    @BeforeEach
    void configurar() {
        pagamentoService = mock(PagamentoService.class);
        membroService = mock(MembroService.class);

        membro = new Membro();
        ReflectionTestUtils.setField(membro, "id", 42L);
        membro.setNome("Membro Teste D05");

        when(membroService.listarTodos()).thenReturn(List.of(membro));

        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        ClassLoaderTemplateResolver templateResolver =
                new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setCharacterEncoding("UTF-8");

        mockMvc = standaloneSetup(
                new PagamentoController(pagamentoService, membroService))
                .setValidator(validator)
                .setViewResolvers(viewResolver)
                .build();
    }

    @AfterEach
    void liberarValidator() {
        validator.destroy();
    }

    @Test
    void renderizaFormularioComMembroStatusSomenteLeituraEIdentidadeVisual()
            throws Exception {
        mockMvc.perform(get("/pagamentos/novo"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("Registrar pagamento")))
                .andExpect(content().string(
                        containsString("/css/pagamentos.css")))
                .andExpect(content().string(
                        containsString("/images/vigor-logo-branca.png")))
                .andExpect(xpath(
                        "//form[@action='/pagamentos' and @method='post']")
                        .exists())
                .andExpect(xpath(
                        "//select[@name='membroId']/option[@value='42']")
                        .exists())
                .andExpect(xpath("//input[@name='valor']").exists())
                .andExpect(xpath("//input[@name='dataPagamento']").exists())
                .andExpect(xpath(
                        "//input[@id='statusExibido' and @readonly and @value='Pago']")
                        .exists())
                .andExpect(xpath("//input[@name='status']").doesNotExist())
                .andExpect(xpath("//button[@type='submit' and @disabled]")
                        .doesNotExist());

        verifyNoInteractions(pagamentoService);
    }

    @Test
    void desabilitaRegistroQuandoNaoExistemMembros() throws Exception {
        when(membroService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/pagamentos/novo"))
                .andExpect(status().isOk())
                .andExpect(xpath("//button[@type='submit' and @disabled]")
                        .exists())
                .andExpect(xpath("//a[@href='/membros/novo']").exists());

        verifyNoInteractions(pagamentoService);
    }

    @Test
    void renderizaListagemSemPagamentos() throws Exception {
        when(pagamentoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("Ainda não existem pagamentos registrados.")))
                .andExpect(xpath("//a[@href='/pagamentos/novo']").exists())
                .andExpect(xpath("//select[@name='membroId']").exists());
    }

    @Test
    void renderizaPagamentoComValorDataEStatusFormatados() throws Exception {
        when(pagamentoService.listarTodos())
                .thenReturn(List.of(pagamentoValido()));

        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Membro Teste D05")))
                .andExpect(content().string(containsString("R$ 150,00")))
                .andExpect(content().string(containsString("21/11/2026")))
                .andExpect(xpath("//tbody/tr/td[4]//span").string("Pago"));
    }

    @Test
    void preservaSelecaoDoMembroNaConsultaSemResultados() throws Exception {
        when(pagamentoService.consultarPorMembro(42L))
                .thenReturn(List.of());

        mockMvc.perform(get("/pagamentos").param("membroId", "42"))
                .andExpect(status().isOk())
                .andExpect(xpath(
                        "//select[@name='membroId']/option[@value='42' and @selected]")
                        .exists())
                .andExpect(xpath("//tbody/tr/td[@colspan='4']").exists());
    }

    @Test
    void renderizaErroDeValorPreservandoMembroEData() throws Exception {
        mockMvc.perform(post("/pagamentos")
                        .param("membroId", "42")
                        .param("valor", "0")
                        .param("dataPagamento", "2026-11-21"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("O valor deve ser maior que zero.")))
                .andExpect(xpath(
                        "//input[@name='valor' and @aria-invalid='true']")
                        .exists())
                .andExpect(xpath(
                        "//select[@name='membroId']/option[@value='42' and @selected]")
                        .exists())
                .andExpect(xpath(
                        "//input[@name='dataPagamento' and @value='2026-11-21']")
                        .exists())
                .andExpect(xpath("//*[@role='alert']").exists());

        verifyNoInteractions(pagamentoService);
    }

    @Test
    void renderizaMensagemDeSucessoNaListagem() throws Exception {
        when(pagamentoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/pagamentos")
                        .flashAttr("sucesso", "Pagamento registrado com sucesso."))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("Pagamento registrado com sucesso.")))
                .andExpect(xpath("//*[@role='status']").exists());
    }

    @Test
    void escapaNomeDoMembroSemRenderizarScript() throws Exception {
        membro.setNome("<script>alert('teste')</script>");
        when(pagamentoService.listarTodos())
                .thenReturn(List.of(pagamentoValido()));

        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        not(containsString("<script>alert('teste')</script>"))))
                .andExpect(xpath("//tbody/tr/td[1]")
                        .string("<script>alert('teste')</script>"));
    }

    private Pagamento pagamentoValido() {
        Pagamento pagamento = new Pagamento();
        pagamento.setMembro(membro);
        pagamento.setValor(new BigDecimal("150.00"));
        pagamento.setDataPagamento(LocalDate.of(2026, 11, 21));
        pagamento.setStatus("PAGO");
        return pagamento;
    }
}
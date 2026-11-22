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
import org.unbescape.html.HtmlEscape;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
        String html = mockMvc.perform(get("/pagamentos/novo"))
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertTrue(html.contains("Registrar pagamento"));
        assertTrue(html.contains("/css/pagamentos.css"));
        assertTrue(html.contains("/images/vigor-logo-branca.png"));

        assertTrue(possuiTag(html, "form",
                "action=\"/pagamentos\"", "method=\"post\""));
        assertTrue(possuiTag(html, "select", "name=\"membroId\""));
        assertTrue(possuiTag(html, "option", "value=\"42\""));
        assertTrue(html.contains("Membro Teste D05"));
        assertTrue(possuiTag(html, "input", "name=\"valor\""));
        assertTrue(possuiTag(html, "input", "name=\"dataPagamento\""));

        assertTrue(possuiTag(html, "input",
                "id=\"statusExibido\"", "readonly", "value=\"Pago\""));
        assertFalse(possuiTag(html, "input", "name=\"status\""));
        assertTrue(possuiTag(html, "button", "type=\"submit\""));
        assertFalse(possuiTag(html, "button",
                "type=\"submit\"", "disabled"));

        verifyNoInteractions(pagamentoService);
    }

    @Test
    void desabilitaRegistroQuandoNaoExistemMembros() throws Exception {
        when(membroService.listarTodos()).thenReturn(List.of());

        String html = mockMvc.perform(get("/pagamentos/novo"))
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertTrue(possuiTag(html, "button",
                "type=\"submit\"", "disabled"));
        assertTrue(possuiTag(html, "a", "href=\"/membros/novo\""));

        verifyNoInteractions(pagamentoService);
    }

    @Test
    void renderizaListagemSemPagamentos() throws Exception {
        when(pagamentoService.listarTodos()).thenReturn(List.of());

        String html = mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertTrue(html.contains("Ainda não existem pagamentos registrados."));
        assertTrue(possuiTag(html, "a", "href=\"/pagamentos/novo\""));
        assertTrue(possuiTag(html, "select", "name=\"membroId\""));
    }

    @Test
    void renderizaPagamentoComValorDataEStatusFormatados() throws Exception {
        when(pagamentoService.listarTodos())
                .thenReturn(List.of(pagamentoValido()));

        String html = mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertTrue(html.contains("Membro Teste D05"));
        assertTrue(html.contains("R$ 150,00"));
        assertTrue(html.contains("21/11/2026"));
        assertTrue(Pattern.compile("<span\\b[^>]*>\\s*Pago\\s*</span>")
                .matcher(html).find());
    }

    @Test
    void preservaSelecaoDoMembroNaConsultaSemResultados() throws Exception {
        when(pagamentoService.consultarPorMembro(42L))
                .thenReturn(List.of());

        String html = mockMvc.perform(get("/pagamentos")
                        .param("membroId", "42"))
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertTrue(possuiTag(html, "option",
                "value=\"42\"", "selected"));
        assertTrue(possuiTag(html, "td", "colspan=\"4\""));
    }

    @Test
    void renderizaErroDeValorPreservandoMembroEData() throws Exception {
        String html = mockMvc.perform(post("/pagamentos")
                        .param("membroId", "42")
                        .param("valor", "0")
                        .param("dataPagamento", "2026-11-21"))
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertTrue(html.contains("O valor deve ser maior que zero."));
        assertTrue(possuiTag(html, "input",
                "name=\"valor\"", "aria-invalid=\"true\""));
        assertTrue(possuiTag(html, "option",
                "value=\"42\"", "selected"));
        assertTrue(possuiTag(html, "input",
                "name=\"dataPagamento\"", "value=\"2026-11-21\""));
        assertTrue(html.contains("role=\"alert\""));

        verifyNoInteractions(pagamentoService);
    }

    @Test
    void renderizaMensagemDeSucessoNaListagem() throws Exception {
        when(pagamentoService.listarTodos()).thenReturn(List.of());

        String html = mockMvc.perform(get("/pagamentos")
                        .flashAttr("sucesso", "Pagamento registrado com sucesso."))
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertTrue(html.contains("Pagamento registrado com sucesso."));
        assertTrue(html.contains("role=\"status\""));
    }

    @Test
    void escapaNomeDoMembroSemRenderizarScript() throws Exception {
        String nome = "<script>alert('teste')</script>";
        membro.setNome(nome);

        when(pagamentoService.listarTodos())
                .thenReturn(List.of(pagamentoValido()));

        String html = mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertFalse(html.contains(nome),
                "O nome não deve aparecer como uma tag script no HTML.");

        var celulas = Pattern.compile(
                "<td\\b[^>]*>(.*?)</td>",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        ).matcher(html);

        boolean encontrouNomeComoTexto = false;

        while (celulas.find()) {
            String conteudo = celulas.group(1).strip();

            if (nome.equals(HtmlEscape.unescapeHtml(conteudo))) {
                assertFalse(conteudo.contains("<"),
                        "A célula deve conter texto escapado, sem tags HTML.");

                encontrouNomeComoTexto = true;
                break;
            }
        }

        assertTrue(encontrouNomeComoTexto,
                "A tabela deve apresentar o nome completo como texto escapado.");
    }

    /*
     * Verifica atributos na mesma tag, sem depender da ordem
     * em que o Thymeleaf os escreve e sem interpretar HTML como XML.
     */
    private boolean possuiTag(String html, String tag, String... atributos) {
        StringBuilder expressao = new StringBuilder(
                "<" + Pattern.quote(tag) + "\\b");

        for (String atributo : atributos) {
            expressao.append("(?=[^>]*\\s")
                    .append(Pattern.quote(atributo))
                    .append("(?=[\\s=>/]))");
        }

        expressao.append("[^>]*>");

        return Pattern.compile(expressao.toString())
                .matcher(html)
                .find();
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
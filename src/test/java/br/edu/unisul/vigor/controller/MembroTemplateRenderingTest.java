package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.service.MembroService;
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

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class MembroTemplateRenderingTest {

    private MembroService membroService;
    private LocalValidatorFactoryBean validator;
    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
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
        mockMvc = standaloneSetup(new MembroController(membroService))
                .setValidator(validator)
                .setViewResolvers(viewResolver)
                .build();
    }

    @AfterEach
    void liberarValidator() {
        validator.destroy();
    }

    @Test
    void renderizaFormularioDeEdicaoComDadosEAcaoDoMembro() throws Exception {
        String nomeAtual = "Membro teste CPF HTTP 123e4567-e89b-12d3-a456-426614174000 edição";
        Membro membro = membroValido(nomeAtual);
        when(membroService.buscarPorId(42L)).thenReturn(membro);

        mockMvc.perform(get("/membros/{id}/editar", 42L))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Editar membro")))
                .andExpect(content().string(containsString(
                        "value=\"" + HtmlEscape.escapeHtml5(nomeAtual) + "\"")))
                .andExpect(content().string(containsString("value=\"52998224725\"")))
                .andExpect(content().string(containsString("action=\"/membros/42/editar\"")))
                .andExpect(content().string(containsString("ID do membro")))
                .andExpect(content().string(containsString("Data de cadastro")))
                .andExpect(content().string(containsString("/css/membros.css")));
    }

    @Test
    void renderizaErrosDeValidacaoProximosAoCampoCpf() throws Exception {
        mockMvc.perform(post("/membros/{id}/editar", 42L)
                        .param("nome", "Nome enviado")
                        .param("cpf", "529.982.247-26")
                        .param("telefone", "Telefone enviado")
                        .param("endereco", "Endereço enviado"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Informe um CPF válido")))
                .andExpect(content().string(containsString("value=\"529.982.247-26\"")))
                .andExpect(content().string(containsString("field-input--invalid")));

        verifyNoInteractions(membroService);
    }

    @Test
    void renderizaListaComEnderecoEAcaoDeEdicao() throws Exception {
        Membro membro = membroValido("Membro da lista");
        setId(membro, 42L);
        when(membroService.listarTodos()).thenReturn(List.of(membro));

        mockMvc.perform(get("/membros"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Membro da lista")))
                .andExpect(content().string(containsString("Rua da lista")))
                .andExpect(content().string(containsString("/membros/42/editar")));

        assertNotNull(membro.getDataCadastro());
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

package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.config.FuncionarioAuthenticationProvider;
import br.edu.unisul.vigor.config.FuncionarioSecurityConfiguration;
import br.edu.unisul.vigor.config.FuncionarioViewModelAdvice;
import br.edu.unisul.vigor.security.FuncionarioPrincipal;
import br.edu.unisul.vigor.service.FuncionarioService;
import br.edu.unisul.vigor.service.HistoricoAtividadeService;
import br.edu.unisul.vigor.service.MembroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {LoginController.class, AtividadeController.class})
@Import({
        FuncionarioSecurityConfiguration.class,
        FuncionarioAuthenticationProvider.class,
        FuncionarioViewModelAdvice.class
})
class FuncionarioSecurityFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FuncionarioService funcionarioService;

    @MockitoBean
    private HistoricoAtividadeService historicoService;

    @MockitoBean
    private MembroService membroService;

    @BeforeEach
    void prepararConsulta() {
        when(membroService.listarTodos()).thenReturn(List.of());
        when(historicoService.consultar(null, null, null, null)).thenReturn(List.of());
    }

    @Test
    void protegeRotasInternasMasLiberaLoginERecursosEstaticos() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Bem-vindo à Vigor")));

        mockMvc.perform(get("/atividades"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login"));

        mockMvc.perform(get("/membros"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login"));

        mockMvc.perform(get("/treinos/novo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login"));

        mockMvc.perform(get("/css/login.css"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/css"));

        mockMvc.perform(get("/funcionarios/novo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login"));
    }

    @Test
    void loginValidoCriaSessaoAcessaHistoricoELogoutInvalidaSessao() throws Exception {
        when(funcionarioService.autenticarFuncionario("camila", "senha-correta"))
                .thenReturn(Optional.of(new FuncionarioPrincipal(
                        7L, "Camila Martins", "Administradora", "camila")));

        var resultadoLogin = mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("login", "camila")
                        .param("senha", "senha-correta"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/membros"))
                .andReturn();

        MockHttpSession sessao = (MockHttpSession) resultadoLogin.getRequest().getSession(false);
        mockMvc.perform(get("/atividades").session(sessao))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Camila Martins")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Administradora")));

        mockMvc.perform(post("/logout").session(sessao))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/logout").session(sessao).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login?sair"));

        org.junit.jupiter.api.Assertions.assertTrue(sessao.isInvalid());
        mockMvc.perform(get("/atividades"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login"));
        verify(funcionarioService).autenticarFuncionario("camila", "senha-correta");
    }

    @Test
    void credenciaisInvalidasRetornamMensagemGenericaESemCadastroPublico() throws Exception {
        when(funcionarioService.autenticarFuncionario("desconhecido", "senha"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("login", "desconhecido")
                        .param("senha", "senha"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login?erro"));

        mockMvc.perform(get("/login").param("erro", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "Login ou senha inválidos.")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("desconhecido"))));

        mockMvc.perform(post("/funcionarios").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login"));
    }
}

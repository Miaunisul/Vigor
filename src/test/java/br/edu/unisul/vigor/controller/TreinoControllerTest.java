package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.model.Treino;
import br.edu.unisul.vigor.service.MembroService;
import br.edu.unisul.vigor.service.TreinoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class TreinoControllerTest {

    private static final Long MEMBRO_ID = 42L;

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
        mockMvc = standaloneSetup(new TreinoController(treinoService, membroService))
                .setValidator(validator)
                .build();
        membro = membroComId(MEMBRO_ID, "Membro Teste");
        when(membroService.listarTodos()).thenReturn(List.of(membro));
    }

    @AfterEach
    void liberarValidator() {
        validator.destroy();
    }

    @Test
    void listaCarregaMembrosEExigeSelecaoParaConsultarTreinos() throws Exception {
        mockMvc.perform(get("/treinos"))
                .andExpect(status().isOk())
                .andExpect(view().name("treinos/lista"))
                .andExpect(model().attribute("membros", List.of(membro)))
                .andExpect(model().attribute("treinos", List.of()))
                .andExpect(model().attribute("membroSelecionadoId", (Object) null));

        verify(membroService).listarTodos();
        verify(treinoService, never()).consultarPorMembro(any());
    }

    @Test
    void consultaSomenteTreinosDoMembroSelecionado() throws Exception {
        Treino treino = treinoValido();
        when(treinoService.consultarPorMembro(MEMBRO_ID)).thenReturn(List.of(treino));

        mockMvc.perform(get("/treinos").param("membroId", MEMBRO_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("treinos/lista"))
                .andExpect(model().attribute("membroSelecionadoId", MEMBRO_ID))
                .andExpect(model().attribute("treinos", List.of(treino)));

        verify(treinoService).consultarPorMembro(MEMBRO_ID);
    }

    @Test
    void redirecionaComErroAoConsultarMembroInexistente() throws Exception {
        when(treinoService.consultarPorMembro(404L))
                .thenThrow(new IllegalArgumentException("Membro não encontrado."));

        mockMvc.perform(get("/treinos").param("membroId", "404"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/treinos"))
                .andExpect(flash().attribute("erro", "Membro não encontrado."));
    }

    @Test
    void formularioCarregaMembrosParaSelecao() throws Exception {
        mockMvc.perform(get("/treinos/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("treinos/formulario"))
                .andExpect(model().attribute("membros", List.of(membro)))
                .andExpect(model().attribute("treino", hasProperty("membro")));

        verify(membroService).listarTodos();
    }

    @Test
    void cadastraTreinoValidoEVoltaParaListaDoMembro() throws Exception {
        mockMvc.perform(post("/treinos")
                        .param("membroId", MEMBRO_ID.toString())
                        .param("tipo", "Musculação")
                        .param("descricao", "Treino de força")
                        .param("duracaoMinutos", "45")
                        .param("dataInicio", "2026-11-20"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/treinos?membroId=42"))
                .andExpect(flash().attribute("sucesso", "Treino cadastrado com sucesso."));

        verify(treinoService).cadastrar(any(Treino.class), eq(MEMBRO_ID));
    }

    @Test
    void rejeitaCadastroSemMembroEPreservaOsDados() throws Exception {
        mockMvc.perform(post("/treinos")
                        .param("tipo", "Musculação")
                        .param("descricao", "Treino de força")
                        .param("duracaoMinutos", "45")
                        .param("dataInicio", "2026-11-20"))
                .andExpect(status().isOk())
                .andExpect(view().name("treinos/formulario"))
                .andExpect(model().attributeHasFieldErrors("treino", "membro.id"))
                .andExpect(model().attribute("treino", hasProperty("tipo", equalTo("Musculação"))))
                .andExpect(model().attribute("membros", List.of(membro)));

        verifyNoInteractions(treinoService);
    }

    @Test
    void membroInexistenteRetornaFormularioComErroNoCampoDeSelecao() throws Exception {
        when(treinoService.cadastrar(any(Treino.class), eq(404L)))
                .thenThrow(new IllegalArgumentException("Membro não encontrado."));

        mockMvc.perform(post("/treinos")
                        .param("membroId", "404")
                        .param("tipo", "Musculação")
                        .param("descricao", "Treino de força")
                        .param("duracaoMinutos", "45")
                        .param("dataInicio", "2026-11-20"))
                .andExpect(status().isOk())
                .andExpect(view().name("treinos/formulario"))
                .andExpect(model().attributeHasFieldErrors("treino", "membro.id"));

        verify(treinoService).cadastrar(any(Treino.class), eq(404L));
    }

    @ParameterizedTest
    @MethodSource("camposObrigatoriosAusentes")
    void rejeitaCamposObrigatoriosAusentes(String campo, String valor) throws Exception {
        var requisicao = post("/treinos")
                .param("membroId", MEMBRO_ID.toString());
        if (!"tipo".equals(campo)) {
            requisicao.param("tipo", "Musculação");
        }
        if (!"descricao".equals(campo)) {
            requisicao.param("descricao", "Treino de força");
        }
        if (!"duracaoMinutos".equals(campo)) {
            requisicao.param("duracaoMinutos", "45");
        }
        if (!"dataInicio".equals(campo)) {
            requisicao.param("dataInicio", "2026-11-20");
        }
        if (valor != null) {
            requisicao.param(campo, valor);
        }

        mockMvc.perform(requisicao)
                .andExpect(status().isOk())
                .andExpect(view().name("treinos/formulario"))
                .andExpect(model().attributeHasFieldErrors("treino", campo));

        verifyNoInteractions(treinoService);
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> camposObrigatoriosAusentes() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of("tipo", "   "),
                org.junit.jupiter.params.provider.Arguments.of("descricao", "   "),
                org.junit.jupiter.params.provider.Arguments.of("duracaoMinutos", null),
                org.junit.jupiter.params.provider.Arguments.of("dataInicio", null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1"})
    void rejeitaDuracaoZeroOuNegativa(String duracao) throws Exception {
        mockMvc.perform(post("/treinos")
                        .param("membroId", MEMBRO_ID.toString())
                        .param("tipo", "Musculação")
                        .param("descricao", "Treino de força")
                        .param("duracaoMinutos", duracao)
                        .param("dataInicio", "2026-11-20"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("treino", "duracaoMinutos"));

        verifyNoInteractions(treinoService);
    }

    @Test
    void rejeitaDuracaoQueNaoSejaInteira() throws Exception {
        mockMvc.perform(post("/treinos")
                        .param("membroId", MEMBRO_ID.toString())
                        .param("tipo", "Musculação")
                        .param("descricao", "Treino de força")
                        .param("duracaoMinutos", "quarenta")
                        .param("dataInicio", "2026-11-20"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("treino", "duracaoMinutos"));

        verifyNoInteractions(treinoService);
    }

    @Test
    void rejeitaDataDeInicioInvalida() throws Exception {
        mockMvc.perform(post("/treinos")
                        .param("membroId", MEMBRO_ID.toString())
                        .param("tipo", "Musculação")
                        .param("descricao", "Treino de força")
                        .param("duracaoMinutos", "45")
                        .param("dataInicio", "20/11/2026"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("treino", "dataInicio"));

        verifyNoInteractions(treinoService);
    }

    private Treino treinoValido() {
        Treino treino = new Treino();
        treino.setTipo("Musculação");
        treino.setDescricao("Treino de força");
        treino.setDuracaoMinutos(45);
        treino.setDataInicio(LocalDate.of(2026, 11, 20));
        treino.setMembro(membro);
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
}

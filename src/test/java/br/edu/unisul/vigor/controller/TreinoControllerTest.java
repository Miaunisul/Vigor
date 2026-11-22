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

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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
    private static final Long TREINO_ID = 9L;

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
        membro = membroComId(MEMBRO_ID, "Ana Beatriz Costa");
        membro.setCpf("52998224725");
        when(membroService.listarTodos()).thenReturn(List.of(membro));
        when(treinoService.listarTodos()).thenReturn(List.of());
    }

    @AfterEach
    void liberarValidator() {
        validator.destroy();
    }

    @Test
    void telaPrincipalCarregaListaCompletaEControles() throws Exception {
        mockMvc.perform(get("/treinos"))
                .andExpect(status().isOk())
                .andExpect(view().name("treinos/lista"))
                .andExpect(model().attribute("visualizacao", "lista"))
                .andExpect(model().attribute("moduloAtivo", "treinos"))
                .andExpect(model().attribute("membros", List.of(membro)))
                .andExpect(model().attribute("treinos", List.of()))
                .andExpect(model().attribute("totalTreinos", 0));

        verify(treinoService).listarTodos();
    }

    @Test
    void filtraListaPorMembroENomeOuCpf() throws Exception {
        Membro bruno = membroComId(84L, "Bruno Almeida");
        bruno.setCpf("39053344705");
        Treino treinoAna = treinoValido(membro);
        Treino treinoBruno = treinoValido(bruno);
        when(membroService.listarTodos()).thenReturn(List.of(membro, bruno));
        when(treinoService.listarTodos()).thenReturn(List.of(treinoAna, treinoBruno));

        mockMvc.perform(get("/treinos").param("q", "529.982"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("treinos", List.of(treinoAna)))
                .andExpect(model().attribute("totalTreinos", 1));

        mockMvc.perform(get("/treinos").param("membroId", "84"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("treinos", List.of(treinoBruno)))
                .andExpect(model().attribute("membroSelecionadoId", 84L));
    }

    @Test
    void detalhesEEdicaoSaoEstadosDaMesmaTela() throws Exception {
        Treino treino = treinoValido(membro);
        setTreinoId(treino, TREINO_ID);
        when(treinoService.buscarPorId(TREINO_ID)).thenReturn(treino);

        mockMvc.perform(get("/treinos").param("acao", "detalhes")
                        .param("treinoId", TREINO_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("treinos/lista"))
                .andExpect(model().attribute("visualizacao", "detalhes"))
                .andExpect(model().attribute("treino", treino));

        mockMvc.perform(get("/treinos").param("acao", "editar")
                        .param("treinoId", TREINO_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("treinos/lista"))
                .andExpect(model().attribute("visualizacao", "editar"))
                .andExpect(model().attribute("treino", treino));
    }

    @Test
    void redirecionaQuandoTreinoSolicitadoNaoExiste() throws Exception {
        when(treinoService.buscarPorId(404L))
                .thenThrow(new IllegalArgumentException("Treino não encontrado."));

        mockMvc.perform(get("/treinos").param("acao", "detalhes").param("treinoId", "404"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/treinos"))
                .andExpect(flash().attribute("erro", "Treino não encontrado."));
    }

    @Test
    void antigoEnderecoDeCadastroAbreEstadoNovoNaTelaPrincipal() throws Exception {
        mockMvc.perform(get("/treinos/novo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/treinos?acao=novo"));
    }

    @Test
    void cadastraTreinoValido() throws Exception {
        mockMvc.perform(post("/treinos")
                        .param("acao", "novo")
                        .param("membroId", MEMBRO_ID.toString())
                        .param("tipo", "Treino A — membros superiores")
                        .param("descricao", "Treino de força")
                        .param("duracaoMinutos", "45")
                        .param("dataInicio", "2026-11-20"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/treinos?membroId=" + MEMBRO_ID))
                .andExpect(flash().attribute("sucesso", "Treino cadastrado com sucesso."));

        verify(treinoService).cadastrar(any(Treino.class), eq(MEMBRO_ID));
    }

    @Test
    void atualizaTreinoSemCriarOutraTelaOuRota() throws Exception {
        mockMvc.perform(post("/treinos")
                        .param("acao", "editar")
                        .param("treinoId", TREINO_ID.toString())
                        .param("membroId", MEMBRO_ID.toString())
                        .param("tipo", "Treino B — membros inferiores")
                        .param("descricao", "Treino atualizado")
                        .param("duracaoMinutos", "50")
                        .param("dataInicio", "2026-11-21"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/treinos?membroId=" + MEMBRO_ID))
                .andExpect(flash().attribute("sucesso", "Treino atualizado com sucesso."));

        verify(treinoService).atualizar(eq(TREINO_ID), any(Treino.class), eq(MEMBRO_ID));
    }

    @Test
    void excluiTreinoPeloPostDaTelaPrincipal() throws Exception {
        mockMvc.perform(post("/treinos")
                        .param("acao", "excluir")
                        .param("treinoId", TREINO_ID.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/treinos"))
                .andExpect(flash().attribute("sucesso", "Treino excluído com sucesso."));

        verify(treinoService).excluir(TREINO_ID);
    }

    @Test
    void preservaFormularioEValidacoesQuandoDadosSaoInvalidos() throws Exception {
        mockMvc.perform(post("/treinos")
                        .param("acao", "novo")
                        .param("membroId", MEMBRO_ID.toString())
                        .param("tipo", " ")
                        .param("descricao", "Descrição preservada")
                        .param("duracaoMinutos", "0")
                        .param("dataInicio", "20/11/2026"))
                .andExpect(status().isOk())
                .andExpect(view().name("treinos/lista"))
                .andExpect(model().attribute("visualizacao", "novo"))
                .andExpect(model().attributeHasFieldErrors("treino", "tipo"))
                .andExpect(model().attributeHasFieldErrors("treino", "duracaoMinutos"))
                .andExpect(model().attributeHasFieldErrors("treino", "dataInicio"))
                .andExpect(model().attribute("treino", org.hamcrest.Matchers.hasProperty(
                        "descricao", org.hamcrest.Matchers.equalTo("Descrição preservada"))));

        verifyNoInteractions(treinoService);
    }

    @Test
    void informaDuracaoNaoInteiraSemMensagemTecnica() throws Exception {
        mockMvc.perform(post("/treinos")
                        .param("acao", "novo")
                        .param("membroId", MEMBRO_ID.toString())
                        .param("tipo", "Treino A")
                        .param("descricao", "Treino de força")
                        .param("duracaoMinutos", "quarenta")
                        .param("dataInicio", "2026-11-20"))
                .andExpect(status().isOk())
                .andExpect(view().name("treinos/lista"))
                .andExpect(model().attributeHasFieldErrors("treino", "duracaoMinutos"));

        verifyNoInteractions(treinoService);
    }

    private Treino treinoValido(Membro associado) {
        Treino treino = new Treino();
        treino.setMembro(associado);
        treino.setTipo("Treino A — membros superiores");
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

    private void setTreinoId(Treino treino, Long id) throws ReflectiveOperationException {
        Field campoId = Treino.class.getDeclaredField("id");
        campoId.setAccessible(true);
        campoId.set(treino, id);
    }
}

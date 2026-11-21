package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.model.Pagamento;
import br.edu.unisul.vigor.service.MembroService;
import br.edu.unisul.vigor.service.PagamentoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class PagamentoControllerTest {

    private PagamentoService pagamentoService;
    private MembroService membroService;
    private LocalValidatorFactoryBean validator;
    private MockMvc mockMvc;
    private List<Membro> membros;

    @BeforeEach
    void configurar() {
        pagamentoService = mock(PagamentoService.class);
        membroService = mock(MembroService.class);

        Membro membro = new Membro();
        membro.setNome("Membro Teste D05");
        membros = List.of(membro);

        when(membroService.listarTodos()).thenReturn(membros);

        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = standaloneSetup(
                new PagamentoController(pagamentoService, membroService))
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void liberarValidator() {
        validator.destroy();
    }

    @Test
    void deveAbrirFormularioComMembrosDisponiveis() throws Exception {
        mockMvc.perform(get("/pagamentos/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagamentos/formulario"))
                .andExpect(model().attributeExists("pagamentoForm"))
                .andExpect(model().attribute("membros", membros));

        verifyNoInteractions(pagamentoService);
    }

    @Test
    void deveListarTodosOsPagamentosSemFiltro() throws Exception {
        List<Pagamento> pagamentos = List.of(new Pagamento());
        when(pagamentoService.listarTodos()).thenReturn(pagamentos);

        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagamentos/lista"))
                .andExpect(model().attribute("pagamentos", pagamentos))
                .andExpect(model().attribute("membros", membros));

        verify(pagamentoService).listarTodos();
        verify(pagamentoService, never()).consultarPorMembro(any());
    }

    @Test
    void deveListarTodosQuandoFiltroEstaVazio() throws Exception {
        when(pagamentoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/pagamentos").param("membroId", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("pagamentos/lista"))
                .andExpect(model().attribute("pagamentos", List.of()));

        verify(pagamentoService).listarTodos();
    }

    @Test
    void deveConsultarPagamentosDoMembroSelecionado() throws Exception {
        List<Pagamento> pagamentos = List.of(new Pagamento());
        when(pagamentoService.consultarPorMembro(42L))
                .thenReturn(pagamentos);

        mockMvc.perform(get("/pagamentos").param("membroId", "42"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagamentos/lista"))
                .andExpect(model().attribute("pagamentos", pagamentos))
                .andExpect(model().attribute("membroSelecionado", 42L));

        verify(pagamentoService).consultarPorMembro(42L);
        verify(pagamentoService, never()).listarTodos();
    }

    @Test
    void deveRetornarListaVaziaParaMembroSemPagamentos() throws Exception {
        when(pagamentoService.consultarPorMembro(42L))
                .thenReturn(List.of());

        mockMvc.perform(get("/pagamentos").param("membroId", "42"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("pagamentos", List.of()))
                .andExpect(model().attribute("membroSelecionado", 42L))
                .andExpect(model().attributeDoesNotExist("erro"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "0", "-1", "1.5", "999999999999999999999"})
    void deveRejeitarFiltroInvalidoSemConsultarPagamentos(String membroId)
            throws Exception {
        mockMvc.perform(get("/pagamentos").param("membroId", membroId))
                .andExpect(status().isOk())
                .andExpect(view().name("pagamentos/lista"))
                .andExpect(model().attributeExists("erro"))
                .andExpect(model().attribute("pagamentos", List.of()));

        verifyNoInteractions(pagamentoService);
    }

    @Test
    void deveInformarMembroInexistenteNaConsulta() throws Exception {
        when(pagamentoService.consultarPorMembro(404L))
                .thenThrow(new IllegalArgumentException("Membro não encontrado."));

        mockMvc.perform(get("/pagamentos").param("membroId", "404"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagamentos/lista"))
                .andExpect(model().attributeExists("erro"))
                .andExpect(model().attribute("pagamentos", List.of()));

        verify(pagamentoService, never()).listarTodos();
    }

    @Test
    void deveRegistrarPagamentoComStatusPagoSemReceberStatusDoFormulario()
            throws Exception {
        mockMvc.perform(post("/pagamentos")
                        .param("membroId", "42")
                        .param("valor", "150.00")
                        .param("dataPagamento", "2026-11-21"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pagamentos"))
                .andExpect(flash().attribute(
                        "sucesso", "Pagamento registrado com sucesso."));

        ArgumentCaptor<Pagamento> captor =
                ArgumentCaptor.forClass(Pagamento.class);

        verify(pagamentoService).registrar(captor.capture(), eq(42L));

        Pagamento pagamento = captor.getValue();
        assertEquals(new BigDecimal("150.00"), pagamento.getValor());
        assertEquals(LocalDate.of(2026, 11, 21), pagamento.getDataPagamento());
        assertEquals("PAGO", pagamento.getStatus());
        assertNull(pagamento.getId());
    }

    @Test
    void deveIgnorarTentativaDeEnviarStatusEIdPeloFormulario() throws Exception {
        mockMvc.perform(post("/pagamentos")
                        .param("membroId", "42")
                        .param("valor", "150.00")
                        .param("dataPagamento", "2026-11-21")
                        .param("status", "PEND")
                        .param("id", "999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pagamentos"));

        ArgumentCaptor<Pagamento> captor =
                ArgumentCaptor.forClass(Pagamento.class);

        verify(pagamentoService).registrar(captor.capture(), eq(42L));
        assertEquals("PAGO", captor.getValue().getStatus());
        assertNull(captor.getValue().getId());
    }

    @Test
    void deveRejeitarFormularioSemCamposObrigatorios() throws Exception {
        mockMvc.perform(post("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagamentos/formulario"))
                .andExpect(model().attributeHasFieldErrors(
                        "pagamentoForm", "membroId", "valor", "dataPagamento"))
                .andExpect(model().attribute("membros", membros));

        verifyNoInteractions(pagamentoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "", "abc", "0", "-1.00", "1.001", "10000000000.00"
    })
    void deveRejeitarValorInvalidoPreservandoOsOutrosCampos(String valor)
            throws Exception {
        mockMvc.perform(post("/pagamentos")
                        .param("membroId", "42")
                        .param("valor", valor)
                        .param("dataPagamento", "2026-11-21"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagamentos/formulario"))
                .andExpect(model().attributeHasFieldErrors(
                        "pagamentoForm", "valor"))
                .andExpect(model().attribute("pagamentoForm",
                        hasProperty("membroId", equalTo(42L))))
                .andExpect(model().attribute("pagamentoForm",
                        hasProperty("dataPagamento",
                                equalTo(LocalDate.of(2026, 11, 21)))))
                .andExpect(model().attribute("membros", membros));

        verifyNoInteractions(pagamentoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abc", "0", "-1", "1.5"})
    void deveRejeitarMembroInvalidoNoRegistro(String membroId)
            throws Exception {
        mockMvc.perform(post("/pagamentos")
                        .param("membroId", membroId)
                        .param("valor", "150.00")
                        .param("dataPagamento", "2026-11-21"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagamentos/formulario"))
                .andExpect(model().attributeHasFieldErrors(
                        "pagamentoForm", "membroId"));

        verifyNoInteractions(pagamentoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abc", "2026-13-10"})
    void deveRejeitarDataAusenteOuInvalida(String data) throws Exception {
        mockMvc.perform(post("/pagamentos")
                        .param("membroId", "42")
                        .param("valor", "150.00")
                        .param("dataPagamento", data))
                .andExpect(status().isOk())
                .andExpect(view().name("pagamentos/formulario"))
                .andExpect(model().attributeHasFieldErrors(
                        "pagamentoForm", "dataPagamento"));

        verifyNoInteractions(pagamentoService);
    }

    @Test
    void deveAssociarMembroInexistenteAoCampoDoFormulario() throws Exception {
        when(pagamentoService.registrar(any(Pagamento.class), eq(404L)))
                .thenThrow(new IllegalArgumentException("Membro não encontrado."));

        mockMvc.perform(post("/pagamentos")
                        .param("membroId", "404")
                        .param("valor", "150.00")
                        .param("dataPagamento", "2026-11-21"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagamentos/formulario"))
                .andExpect(model().attributeHasFieldErrors(
                        "pagamentoForm", "membroId"))
                .andExpect(model().attribute("membros", membros))
                .andExpect(model().attribute("pagamentoForm",
                        hasProperty("valor", equalTo(new BigDecimal("150.00")))));
    }
}
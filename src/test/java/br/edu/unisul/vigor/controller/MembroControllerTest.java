package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.repository.HistoricoAtividadeRepository;
import br.edu.unisul.vigor.repository.MembroRepository;
import br.edu.unisul.vigor.repository.PagamentoRepository;
import br.edu.unisul.vigor.repository.TreinoRepository;
import br.edu.unisul.vigor.service.MembroService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class MembroControllerTest {

    private MembroService membroService;
    private LocalValidatorFactoryBean validator;
    private MockMvc mockMvc;

    @BeforeEach
    void configurar() {
        membroService = mock(MembroService.class);
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = standaloneSetup(new MembroController(membroService))
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void liberarValidator() {
        validator.destroy();
    }

    @Test
    void deveExibirDetalhesDentroDaTelaDeMembrosUsandoConsultaExistente()
            throws Exception {
        Membro membro = membroValido("52998224725");
        setId(membro, 42L);
        when(membroService.buscarPorId(42L)).thenReturn(membro);

        mockMvc.perform(get("/membros")
                        .param("acao", "detalhes")
                        .param("membroId", "42"))
                .andExpect(status().isOk())
                .andExpect(view().name("membros/lista"))
                .andExpect(model().attribute("estado", "detalhes"))
                .andExpect(model().attribute("membro", membro));

        verify(membroService).buscarPorId(42L);
    }

    @Test
    void deveFiltrarListaPorCpfSemCriarConsultaDeServicoNova() throws Exception {
        Membro correspondente = membroValido("52998224725");
        correspondente.setNome("Ana Beatriz Costa");
        setId(correspondente, 1L);
        Membro outro = membroValido("12345678909");
        outro.setNome("Bruno Almeida");
        setId(outro, 2L);
        when(membroService.listarTodos()).thenReturn(List.of(correspondente, outro));

        mockMvc.perform(get("/membros").param("q", "529.982.247-25"))
                .andExpect(status().isOk())
                .andExpect(view().name("membros/lista"))
                .andExpect(model().attribute("totalMembros", 1))
                .andExpect(model().attribute("membrosPagina", hasSize(1)))
                .andExpect(model().attribute("membrosPagina", org.hamcrest.Matchers.hasItem(correspondente)));

        verify(membroService).listarTodos();
    }

    @Test
    void devePaginarListaDosMembrosEmDezRegistros() throws Exception {
        List<Membro> membros = new ArrayList<>();
        for (int indice = 1; indice <= 12; indice++) {
            Membro membro = membroValido("52998224725");
            membro.setNome("Membro " + indice);
            setId(membro, (long) indice);
            membros.add(membro);
        }
        when(membroService.listarTodos()).thenReturn(membros);

        mockMvc.perform(get("/membros").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("pagina", 1))
                .andExpect(model().attribute("totalMembros", 12))
                .andExpect(model().attribute("inicioLista", 11))
                .andExpect(model().attribute("fimLista", 12))
                .andExpect(model().attribute("membrosPagina", hasSize(2)));
    }

    @Test
    void devePreservarCamposInformadosAoRetornarFormularioComErroDeValidacao()
            throws Exception {
        mockMvc.perform(post("/membros")
                        .param("nome", "  Membro Teste  ")
                        .param("cpf", "529.982.247-26")
                        .param("telefone", "  (48) 99999-0000  ")
                        .param("endereco", "  Rua A, 10  "))
                .andExpect(status().isOk())
                .andExpect(view().name("membros/lista"))
                .andExpect(model().attribute("estado", "novo"))
                .andExpect(model().attributeHasFieldErrors("membro", "cpf"))
                .andExpect(model().attribute("membro",
                        hasProperty("nome", equalTo("Membro Teste"))))
                .andExpect(model().attribute("membro",
                        hasProperty("cpf", equalTo("529.982.247-26"))))
                .andExpect(model().attribute("membro",
                        hasProperty("telefone", equalTo("(48) 99999-0000"))))
                .andExpect(model().attribute("membro",
                        hasProperty("endereco", equalTo("Rua A, 10"))));

        verifyNoInteractions(membroService);
    }

    @Test
    void deveMapearConstraintViolationDoServicoParaErroDeCampoSemPerderDados()
            throws Exception {
        Membro membroInvalido = membroValido("52998224726");
        ConstraintViolationException excecao = new ConstraintViolationException(
                validator.validate(membroInvalido));
        when(membroService.cadastrar(org.mockito.ArgumentMatchers.any(Membro.class)))
                .thenThrow(excecao);

        mockMvc.perform(post("/membros")
                        .param("nome", "Membro Teste")
                        .param("cpf", "529.982.247-25")
                        .param("telefone", "(48) 99999-0000")
                        .param("endereco", "Rua A, 10"))
                .andExpect(status().isOk())
                .andExpect(view().name("membros/lista"))
                .andExpect(model().attributeHasFieldErrors("membro", "cpf"))
                .andExpect(model().attribute("membro",
                        hasProperty("cpf", equalTo("529.982.247-25"))));

        verify(membroService).cadastrar(org.mockito.ArgumentMatchers.any(Membro.class));
    }

    @Test
    void deveRetornarFormularioQuandoServicoRejeitaCpfDuplicado()
            throws Exception {
        when(membroService.cadastrar(org.mockito.ArgumentMatchers.any(Membro.class)))
                .thenThrow(new IllegalArgumentException(
                        "Já existe um membro cadastrado com este CPF."));

        mockMvc.perform(post("/membros")
                        .param("nome", "Membro Teste")
                        .param("cpf", "529.982.247-25")
                        .param("telefone", "(48) 99999-0000")
                        .param("endereco", "Rua A, 10"))
                .andExpect(status().isOk())
                .andExpect(view().name("membros/lista"))
                .andExpect(model().attributeHasFieldErrors("membro", "cpf"))
                .andExpect(model().attribute("membro",
                        hasProperty("cpf", equalTo("529.982.247-25"))));

        verify(membroService).cadastrar(org.mockito.ArgumentMatchers.any(Membro.class));
    }

    @Test
    void deveAssociarErroDaConstraintCpfQuandoRepositorioFalhaAoSalvar()
            throws Exception {
        MembroRepository repository = mock(MembroRepository.class);
        MembroService serviceReal = new MembroService(
                repository,
                mock(TreinoRepository.class),
                mock(PagamentoRepository.class),
                mock(HistoricoAtividadeRepository.class),
                validator);
        MockMvc mvcComServicoReal = standaloneSetup(new MembroController(serviceReal))
                .setValidator(validator)
                .build();
        when(repository.existsByCpf("52998224725")).thenReturn(false);
        SQLException causaSql = new SQLException(
                "duplicate key violates unique constraint \"uk_membros_cpf\"",
                "23505");
        when(repository.save(org.mockito.ArgumentMatchers.any(Membro.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "could not execute statement", causaSql));

        mvcComServicoReal.perform(post("/membros")
                        .param("nome", "Membro Teste")
                        .param("cpf", "529.982.247-25")
                        .param("telefone", "(48) 99999-0000")
                        .param("endereco", "Rua A, 10"))
                .andExpect(status().isOk())
                .andExpect(view().name("membros/lista"))
                .andExpect(model().attributeHasFieldErrors("membro", "cpf"))
                .andExpect(model().attribute("membro",
                        hasProperty("cpf", equalTo("529.982.247-25"))));

        verify(repository).existsByCpf("52998224725");
        verify(repository).save(org.mockito.ArgumentMatchers.any(Membro.class));
    }

    @Test
    void deveRedirecionarComSucessoAoExcluirMembroSemVinculos() throws Exception {
        mockMvc.perform(post("/membros/{id}/excluir", 42L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/membros"))
                .andExpect(flash().attribute("sucesso", "Membro excluído com sucesso."));

        verify(membroService).excluir(42L);
    }

    @Test
    void deveRedirecionarComMotivoQuandoExclusaoEstaBloqueada() throws Exception {
        String mensagem = "Não é possível excluir o membro: existem treinos vinculados.";
        org.mockito.Mockito.doThrow(new IllegalStateException(mensagem))
                .when(membroService).excluir(42L);

        mockMvc.perform(post("/membros/{id}/excluir", 42L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/membros"))
                .andExpect(flash().attribute("erro", mensagem));

        verify(membroService).excluir(42L);
    }

    @Test
    void deveExibirFormularioDeEdicaoComDadosAtuaisDoMembro() throws Exception {
        Membro membro = membroValido("52998224725");
        membro.setNome("Nome atual");
        when(membroService.buscarPorId(42L)).thenReturn(membro);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/membros/{id}/editar", 42L))
                .andExpect(status().isOk())
                .andExpect(view().name("membros/lista"))
                .andExpect(model().attribute("editando", true))
                .andExpect(model().attribute("formAction", "/membros/42/editar"))
                .andExpect(model().attribute("membroId", 42L))
                .andExpect(model().attribute("membro",
                        hasProperty("nome", equalTo("Nome atual"))))
                .andExpect(model().attribute("membro",
                        hasProperty("cpf", equalTo("52998224725"))));

        verify(membroService).buscarPorId(42L);
    }

    @Test
    void deveRedirecionarComMensagemAoAbrirEdicaoDeMembroInexistente() throws Exception {
        when(membroService.buscarPorId(404L))
                .thenThrow(new IllegalArgumentException("Membro não encontrado."));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/membros/{id}/editar", 404L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/membros"))
                .andExpect(flash().attribute("erro", "Membro não encontrado."));
    }

    @Test
    void deveAtualizarMembroRedirecionarEIgnorarIdEDataEnviadosNoFormulario()
            throws Exception {
        when(membroService.atualizar(eq(42L), any(Membro.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(1));

        mockMvc.perform(post("/membros/{id}/editar", 42L)
                        .param("nome", "Nome atualizado")
                        .param("cpf", "529.982.247-25")
                        .param("telefone", "(48) 99999-1111")
                        .param("endereco", "Rua Atualizada, 20")
                        .param("id", "999")
                        .param("dataCadastro", "2000-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/membros"))
                .andExpect(flash().attribute("sucesso", "Membro atualizado com sucesso!"));

        ArgumentCaptor<Membro> captor = ArgumentCaptor.forClass(Membro.class);
        verify(membroService).atualizar(eq(42L), captor.capture());
        Membro dados = captor.getValue();
        assertEquals("Nome atualizado", dados.getNome());
        assertEquals("529.982.247-25", dados.getCpf());
        assertEquals("(48) 99999-1111", dados.getTelefone());
        assertEquals("Rua Atualizada, 20", dados.getEndereco());
        assertNull(dados.getId());
        assertEquals(LocalDate.now(), dados.getDataCadastro());
    }

    @Test
    void deveRetornarFormularioDeEdicaoComErroDeValidacaoPreservandoValores()
            throws Exception {
        mockMvc.perform(post("/membros/{id}/editar", 42L)
                        .param("nome", "Nome enviado")
                        .param("cpf", "529.982.247-26")
                        .param("telefone", "Telefone enviado")
                        .param("endereco", "Endereço enviado"))
                .andExpect(status().isOk())
                .andExpect(view().name("membros/lista"))
                .andExpect(model().attribute("editando", true))
                .andExpect(model().attribute("formAction", "/membros/42/editar"))
                .andExpect(model().attributeHasFieldErrors("membro", "cpf"))
                .andExpect(model().attribute("membro",
                        hasProperty("cpf", equalTo("529.982.247-26"))))
                .andExpect(model().attribute("membro",
                        hasProperty("nome", equalTo("Nome enviado"))));

        verifyNoInteractions(membroService);
    }

    @Test
    void deveAssociarCpfDuplicadoAoCampoNaEdicao() throws Exception {
        when(membroService.atualizar(eq(42L), any(Membro.class)))
                .thenThrow(new IllegalArgumentException(
                        "Já existe um membro cadastrado com este CPF."));

        mockMvc.perform(post("/membros/{id}/editar", 42L)
                        .param("nome", "Nome enviado")
                        .param("cpf", "529.982.247-25")
                        .param("telefone", "Telefone enviado")
                        .param("endereco", "Endereço enviado"))
                .andExpect(status().isOk())
                .andExpect(view().name("membros/lista"))
                .andExpect(model().attributeHasFieldErrors("membro", "cpf"))
                .andExpect(model().attribute("membro",
                        hasProperty("cpf", equalTo("529.982.247-25"))));
    }

    @Test
    void deveAssociarViolacaoDaConstraintCpfAoCampoDuranteEdicao() throws Exception {
        SQLException causaSql = new SQLException(
                "duplicate key violates unique constraint \"uk_membros_cpf\"", "23505");
        when(membroService.atualizar(eq(42L), any(Membro.class)))
                .thenThrow(new DataIntegrityViolationException("could not execute statement", causaSql));

        mockMvc.perform(post("/membros/{id}/editar", 42L)
                        .param("nome", "Nome enviado")
                        .param("cpf", "529.982.247-25")
                        .param("telefone", "Telefone enviado")
                        .param("endereco", "Endereço enviado"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("membro", "cpf"));
    }

    @Test
    void devePropagarViolacaoDeOutraConstraintSemClassificarComoCpfDuplicado() {
        DataIntegrityViolationException excecao = new DataIntegrityViolationException(
                "integrity failure",
                new SQLException(
                        "duplicate key violates unique constraint \"uk_outra_constraint\"",
                        "23505"));
        when(membroService.cadastrar(org.mockito.ArgumentMatchers.any(Membro.class)))
                .thenThrow(excecao);
        Membro membro = membroValido("52998224725");
        BeanPropertyBindingResult resultado =
                new BeanPropertyBindingResult(membro, "membro");

        DataIntegrityViolationException propagada = assertThrows(
                DataIntegrityViolationException.class,
                () -> new MembroController(membroService).cadastrar(
                        membro, resultado, new ExtendedModelMap(),
                        new RedirectAttributesModelMap()));

        assertSame(excecao, propagada);
    }

    private Membro membroValido(String cpf) {
        Membro membro = new Membro();
        membro.setNome("Membro Teste");
        membro.setCpf(cpf);
        membro.setTelefone("(48) 99999-0000");
        membro.setEndereco("Rua A, 10");
        return membro;
    }

    private void setId(Membro membro, Long id) throws ReflectiveOperationException {
        var idField = Membro.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(membro, id);
    }
}

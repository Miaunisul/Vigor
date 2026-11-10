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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.sql.SQLException;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
    void devePreservarCamposInformadosAoRetornarFormularioComErroDeValidacao()
            throws Exception {
        mockMvc.perform(post("/membros")
                        .param("nome", "  Membro Teste  ")
                        .param("cpf", "529.982.247-26")
                        .param("telefone", "  (48) 99999-0000  ")
                        .param("endereco", "  Rua A, 10  "))
                .andExpect(status().isOk())
                .andExpect(view().name("membros/formulario"))
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
                .andExpect(view().name("membros/formulario"))
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
                .andExpect(view().name("membros/formulario"))
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
                .andExpect(view().name("membros/formulario"))
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
                        membro, resultado, new RedirectAttributesModelMap()));

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
}

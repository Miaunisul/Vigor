package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.model.Funcionario;
import br.edu.unisul.vigor.model.HistoricoAtividade;
import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.repository.FuncionarioRepository;
import br.edu.unisul.vigor.repository.HistoricoAtividadeRepository;
import br.edu.unisul.vigor.repository.MembroRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class HistoricoAtividadeServiceTest {

    private static final ValidatorFactory VALIDATOR_FACTORY =
            Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    private HistoricoAtividadeRepository historicoRepository;
    private MembroRepository membroRepository;
    private FuncionarioRepository funcionarioRepository;
    private HistoricoAtividadeService historicoService;

    @AfterAll
    static void liberarValidator() {
        VALIDATOR_FACTORY.close();
    }

    @BeforeEach
    void configurar() {
        historicoRepository = mock(HistoricoAtividadeRepository.class);
        membroRepository = mock(MembroRepository.class);
        funcionarioRepository = mock(FuncionarioRepository.class);
        historicoService = new HistoricoAtividadeService(
                historicoRepository, membroRepository, funcionarioRepository, VALIDATOR);
    }

    @Test
    void registraAtividadeComMembroEFuncionarioResponsavelExistentes() {
        Membro membro = new Membro();
        Funcionario funcionario = new Funcionario("Nome", "Cargo", "login", "hash");
        HistoricoAtividade atividade = atividadeValida();
        when(membroRepository.findById(10L)).thenReturn(Optional.of(membro));
        when(funcionarioRepository.findById(20L)).thenReturn(Optional.of(funcionario));
        when(historicoRepository.save(atividade)).thenReturn(atividade);

        HistoricoAtividade resultado = historicoService.registrar(atividade, 10L, 20L);

        assertSame(atividade, resultado);
        assertSame(membro, resultado.getMembro());
        assertSame(funcionario, resultado.getFuncionarioResponsavel());
        verify(historicoRepository).save(atividade);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void rejeitaAtividadeVazia(String descricao) {
        rejeitarAtividadeInvalida(atividade -> atividade.setAtividade(descricao));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rejeitaTempoDeExecucaoNaoPositivo(int tempo) {
        rejeitarAtividadeInvalida(atividade -> atividade.setTempoExecucaoMinutos(tempo));
    }

    @Test
    void rejeitaAtividadeNula() {
        rejeitarAtividadeInvalida(atividade -> atividade.setAtividade(null));
    }

    @Test
    void rejeitaDataNula() {
        rejeitarAtividadeInvalida(atividade -> atividade.setDataAtividade(null));
    }

    @Test
    void rejeitaTempoNulo() {
        rejeitarAtividadeInvalida(atividade -> atividade.setTempoExecucaoMinutos(null));
    }

    @Test
    void rejeitaRegistroSemMembro() {
        assertThrows(IllegalArgumentException.class,
                () -> historicoService.registrar(atividadeValida(), null, 20L));
        verifyNoInteractions(membroRepository, funcionarioRepository, historicoRepository);
    }

    @Test
    void rejeitaRegistroSemFuncionarioResponsavel() {
        assertThrows(IllegalArgumentException.class,
                () -> historicoService.registrar(atividadeValida(), 10L, null));
        verifyNoInteractions(membroRepository, funcionarioRepository, historicoRepository);
    }

    @Test
    void rejeitaRegistroQuandoMembroNaoExiste() {
        when(membroRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> historicoService.registrar(atividadeValida(), 404L, 20L));

        verifyNoInteractions(funcionarioRepository);
        verify(historicoRepository, never()).save(any(HistoricoAtividade.class));
    }

    @Test
    void rejeitaRegistroQuandoFuncionarioNaoExiste() {
        when(membroRepository.findById(10L)).thenReturn(Optional.of(new Membro()));
        when(funcionarioRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> historicoService.registrar(atividadeValida(), 10L, 404L));

        verify(historicoRepository, never()).save(any(HistoricoAtividade.class));
    }

    @Test
    void consultaAtividadesPorMembroExistente() {
        List<HistoricoAtividade> esperadas = List.of(atividadeValida());
        when(membroRepository.existsById(10L)).thenReturn(true);
        when(historicoRepository.findByMembro_Id(10L)).thenReturn(esperadas);

        assertSame(esperadas, historicoService.consultarPorMembro(10L));
        verify(historicoRepository).findByMembro_Id(10L);
    }

    @Test
    void rejeitaConsultaPorMembroInexistente() {
        when(membroRepository.existsById(404L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> historicoService.consultarPorMembro(404L));

        verify(historicoRepository, never()).findByMembro_Id(any());
    }

    @Test
    void consultaAtividadesPorFuncionarioExistente() {
        List<HistoricoAtividade> esperadas = List.of(atividadeValida());
        when(funcionarioRepository.existsById(20L)).thenReturn(true);
        when(historicoRepository.findByFuncionarioResponsavel_Id(20L)).thenReturn(esperadas);

        assertSame(esperadas, historicoService.consultarPorFuncionario(20L));
        verify(historicoRepository).findByFuncionarioResponsavel_Id(20L);
    }

    @Test
    void rejeitaConsultaPorFuncionarioInexistente() {
        when(funcionarioRepository.existsById(404L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> historicoService.consultarPorFuncionario(404L));

        verify(historicoRepository, never()).findByFuncionarioResponsavel_Id(any());
    }

    private void rejeitarAtividadeInvalida(
            java.util.function.Consumer<HistoricoAtividade> alteracao) {
        HistoricoAtividade atividade = atividadeValida();
        alteracao.accept(atividade);
        when(membroRepository.findById(10L)).thenReturn(Optional.of(new Membro()));
        when(funcionarioRepository.findById(20L))
                .thenReturn(Optional.of(new Funcionario("Nome", "Cargo", "login", "hash")));

        assertThrows(ConstraintViolationException.class,
                () -> historicoService.registrar(atividade, 10L, 20L));

        verify(historicoRepository, never()).save(any(HistoricoAtividade.class));
    }

    private HistoricoAtividade atividadeValida() {
        HistoricoAtividade atividade = new HistoricoAtividade();
        atividade.setAtividade("Acompanhamento de treino");
        atividade.setDataAtividade(LocalDate.of(2026, 11, 10));
        atividade.setTempoExecucaoMinutos(30);
        return atividade;
    }
}

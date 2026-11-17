package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.model.Treino;
import br.edu.unisul.vigor.repository.MembroRepository;
import br.edu.unisul.vigor.repository.TreinoRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TreinoServiceTest {

    private static final ValidatorFactory VALIDATOR_FACTORY =
            Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    private TreinoRepository treinoRepository;
    private MembroRepository membroRepository;
    private TreinoService treinoService;

    @AfterAll
    static void liberarValidator() {
        VALIDATOR_FACTORY.close();
    }

    @BeforeEach
    void configurar() {
        treinoRepository = mock(TreinoRepository.class);
        membroRepository = mock(MembroRepository.class);
        treinoService = new TreinoService(treinoRepository, membroRepository, VALIDATOR);
    }

    @Test
    void cadastraTreinoVinculadoAMembroExistente() {
        Membro membro = new Membro();
        Treino treino = treinoValido();
        treino.setTipo("  Musculação  ");
        treino.setDescricao("  Treino de força  ");
        when(membroRepository.findById(42L)).thenReturn(Optional.of(membro));
        when(treinoRepository.save(treino)).thenReturn(treino);

        Treino resultado = treinoService.cadastrar(treino, 42L);

        assertSame(treino, resultado);
        assertSame(membro, resultado.getMembro());
        assertEquals("Musculação", resultado.getTipo());
        assertEquals("Treino de força", resultado.getDescricao());
        verify(treinoRepository).save(treino);
    }

    @ParameterizedTest
    @MethodSource("dadosInvalidos")
    void rejeitaDadosInvalidosSemPersistir(String campo, String valor, Integer duracao,
                                            LocalDate dataInicio) {
        Treino treino = treinoValido();
        if ("tipo".equals(campo)) {
            treino.setTipo(valor);
        } else if ("descricao".equals(campo)) {
            treino.setDescricao(valor);
        }
        if (duracao != null || "duracaoMinutos".equals(campo)) {
            treino.setDuracaoMinutos(duracao);
        }
        if ("dataInicio".equals(campo)) {
            treino.setDataInicio(dataInicio);
        }
        when(membroRepository.findById(42L)).thenReturn(Optional.of(new Membro()));

        assertThrows(ConstraintViolationException.class,
                () -> treinoService.cadastrar(treino, 42L));

        verify(treinoRepository, never()).save(any(Treino.class));
    }

    static Stream<Arguments> dadosInvalidos() {
        return Stream.of(
                Arguments.of("tipo", "", 45, LocalDate.of(2026, 1, 10)),
                Arguments.of("tipo", "   ", 45, LocalDate.of(2026, 1, 10)),
                Arguments.of("descricao", "", 45, LocalDate.of(2026, 1, 10)),
                Arguments.of("descricao", "   ", 45, LocalDate.of(2026, 1, 10)),
                Arguments.of("duracaoMinutos", null, null, LocalDate.of(2026, 1, 10)),
                Arguments.of("duracaoMinutos", null, 0, LocalDate.of(2026, 1, 10)),
                Arguments.of("duracaoMinutos", null, -1, LocalDate.of(2026, 1, 10)),
                Arguments.of("dataInicio", null, 45, null)
        );
    }

    @Test
    void rejeitaCadastroSemMembro() {
        assertThrows(IllegalArgumentException.class,
                () -> treinoService.cadastrar(treinoValido(), null));

        verifyNoInteractions(membroRepository, treinoRepository);
    }

    @Test
    void rejeitaCadastroQuandoMembroNaoExiste() {
        when(membroRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> treinoService.cadastrar(treinoValido(), 404L));

        verify(treinoRepository, never()).save(any(Treino.class));
    }

    @Test
    void consultaTreinosDoMembroSelecionado() {
        Membro membro = new Membro();
        Treino treino = treinoValido();
        List<Treino> esperados = List.of(treino);
        when(membroRepository.existsById(42L)).thenReturn(true);
        when(treinoRepository.findByMembro_Id(42L)).thenReturn(esperados);

        List<Treino> resultado = treinoService.consultarPorMembro(42L);

        assertSame(esperados, resultado);
        verify(treinoRepository).findByMembro_Id(42L);
    }

    @Test
    void rejeitaConsultaDeMembroInexistente() {
        when(membroRepository.existsById(404L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> treinoService.consultarPorMembro(404L));

        verify(treinoRepository, never()).findByMembro_Id(any());
    }

    private Treino treinoValido() {
        Treino treino = new Treino();
        treino.setTipo("Musculação");
        treino.setDescricao("Treino de força");
        treino.setDuracaoMinutos(45);
        treino.setDataInicio(LocalDate.of(2026, 1, 10));
        return treino;
    }
}

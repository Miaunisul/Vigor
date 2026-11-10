package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.model.Pagamento;
import br.edu.unisul.vigor.repository.MembroRepository;
import br.edu.unisul.vigor.repository.PagamentoRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PagamentoServiceTest {

    private static final ValidatorFactory VALIDATOR_FACTORY =
            Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    private PagamentoRepository pagamentoRepository;
    private MembroRepository membroRepository;
    private PagamentoService pagamentoService;

    @AfterAll
    static void liberarValidator() {
        VALIDATOR_FACTORY.close();
    }

    @BeforeEach
    void configurar() {
        pagamentoRepository = mock(PagamentoRepository.class);
        membroRepository = mock(MembroRepository.class);
        pagamentoService = new PagamentoService(pagamentoRepository, membroRepository, VALIDATOR);
    }

    @Test
    void registraPagamentoPagoVinculadoAMembroExistente() {
        Membro membro = new Membro();
        Pagamento pagamento = pagamentoValido();
        when(membroRepository.findById(42L)).thenReturn(Optional.of(membro));
        when(pagamentoRepository.save(pagamento)).thenReturn(pagamento);

        Pagamento resultado = pagamentoService.registrar(pagamento, 42L);

        assertSame(pagamento, resultado);
        assertSame(membro, resultado.getMembro());
        verify(pagamentoRepository).save(pagamento);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.00", "-1.00", "1.001"})
    void rejeitaValorNaoPositivoOuComMaisDeDuasCasas(String valor) {
        rejeitarPagamentoInvalido(pagamento -> pagamento.setValor(new BigDecimal(valor)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PEND", "Pago", "", "   "})
    void rejeitaStatusDiferenteDePago(String status) {
        rejeitarPagamentoInvalido(pagamento -> pagamento.setStatus(status));
    }

    @Test
    void rejeitaValorNulo() {
        rejeitarPagamentoInvalido(pagamento -> pagamento.setValor(null));
    }

    @Test
    void rejeitaDataNula() {
        rejeitarPagamentoInvalido(pagamento -> pagamento.setDataPagamento(null));
    }

    @Test
    void rejeitaPagamentoNulo() {
        assertThrows(IllegalArgumentException.class, () -> pagamentoService.registrar(null, 42L));
        verifyNoInteractions(membroRepository, pagamentoRepository);
    }

    @Test
    void rejeitaRegistroSemMembro() {
        assertThrows(IllegalArgumentException.class,
                () -> pagamentoService.registrar(pagamentoValido(), null));
        verifyNoInteractions(membroRepository, pagamentoRepository);
    }

    @Test
    void rejeitaRegistroQuandoMembroNaoExiste() {
        when(membroRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> pagamentoService.registrar(pagamentoValido(), 404L));

        verify(pagamentoRepository, never()).save(any(Pagamento.class));
    }

    @Test
    void consultaPagamentosDoMembro() {
        List<Pagamento> esperados = List.of(pagamentoValido());
        when(membroRepository.existsById(42L)).thenReturn(true);
        when(pagamentoRepository.findByMembro_Id(42L)).thenReturn(esperados);

        List<Pagamento> resultado = pagamentoService.consultarPorMembro(42L);

        assertSame(esperados, resultado);
        verify(pagamentoRepository).findByMembro_Id(42L);
    }

    @Test
    void rejeitaConsultaDeMembroInexistente() {
        when(membroRepository.existsById(404L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> pagamentoService.consultarPorMembro(404L));

        verify(pagamentoRepository, never()).findByMembro_Id(any());
    }

    private void rejeitarPagamentoInvalido(java.util.function.Consumer<Pagamento> alteracao) {
        Pagamento pagamento = pagamentoValido();
        alteracao.accept(pagamento);
        when(membroRepository.findById(42L)).thenReturn(Optional.of(new Membro()));

        assertThrows(ConstraintViolationException.class,
                () -> pagamentoService.registrar(pagamento, 42L));

        verify(pagamentoRepository, never()).save(any(Pagamento.class));
    }

    private Pagamento pagamentoValido() {
        Pagamento pagamento = new Pagamento();
        pagamento.setValor(new BigDecimal("125.50"));
        pagamento.setDataPagamento(LocalDate.of(2026, 11, 10));
        pagamento.setStatus("PAGO");
        return pagamento;
    }
}

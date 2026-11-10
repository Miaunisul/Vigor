
package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.repository.HistoricoAtividadeRepository;
import br.edu.unisul.vigor.repository.MembroRepository;
import br.edu.unisul.vigor.repository.PagamentoRepository;
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
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MembroServiceTest {

    private static final ValidatorFactory VALIDATOR_FACTORY =
            Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    private MembroRepository membroRepository;
    private TreinoRepository treinoRepository;
    private PagamentoRepository pagamentoRepository;
    private HistoricoAtividadeRepository historicoAtividadeRepository;
    private MembroService membroService;

    @AfterAll
    static void liberarValidator() {
        VALIDATOR_FACTORY.close();
    }

    @BeforeEach
    void configurar() {
        membroRepository = mock(MembroRepository.class);
        treinoRepository = mock(TreinoRepository.class);
        pagamentoRepository = mock(PagamentoRepository.class);
        historicoAtividadeRepository = mock(HistoricoAtividadeRepository.class);
        membroService = new MembroService(membroRepository, treinoRepository,
                pagamentoRepository, historicoAtividadeRepository, VALIDATOR);
    }

    @Test
    void deveCadastrarMembroQuandoCpfAindaNaoExiste() {
        Membro membro = membroValido("52998224725");

        when(membroRepository.existsByCpf("52998224725"))
                .thenReturn(false);

        when(membroRepository.save(membro))
                .thenReturn(membro);

        Membro resultado = membroService.cadastrar(membro);

        assertNotNull(resultado);
        assertEquals("Membro Teste", resultado.getNome());
        verify(membroRepository).save(membro);
    }

    @Test
    void deveRejeitarCadastroQuandoCpfJaExiste() {
        Membro membro = membroValido("52998224725");
        membro.setNome("Outro Membro");

        when(membroRepository.existsByCpf("52998224725"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> membroService.cadastrar(membro)
        );

        verify(membroRepository, never()).save(any(Membro.class));
    }

    @Test
    void deveNormalizarCpfComPontuacaoAntesDeConsultarDuplicidadeESalvar() {
        Membro membro = membroValido(" 529.982.247-25 ");
        membro.setNome("  Membro Teste  ");
        membro.setTelefone("  (48) 99999-0000  ");
        membro.setEndereco("  Rua A, 10  ");
        when(membroRepository.existsByCpf("52998224725")).thenReturn(false);
        when(membroRepository.save(membro)).thenReturn(membro);

        membroService.cadastrar(membro);

        assertEquals("52998224725", membro.getCpf());
        assertEquals("Membro Teste", membro.getNome());
        assertEquals("(48) 99999-0000", membro.getTelefone());
        assertEquals("Rua A, 10", membro.getEndereco());
        verify(membroRepository).existsByCpf("52998224725");
        verify(membroRepository).save(membro);
    }

    @Test
    void deveRejeitarCpfComDigitosVerificadoresInvalidosAntesDePersistir() {
        Membro membro = membroValido("52998224726");

        assertThrows(ConstraintViolationException.class,
                () -> membroService.cadastrar(membro));

        verifyNoInteractions(membroRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "52998224A25",
            "5299822472"
    })
    void deveRejeitarCpfComLetrasOuQuantidadeIncorretaDeDigitos(String cpf) {
        Membro membro = membroValido(cpf);

        assertThrows(ConstraintViolationException.class,
                () -> membroService.cadastrar(membro));

        verifyNoInteractions(membroRepository);
    }

    @Test
    void deveRejeitarCpfComNumerosRepetidosAntesDePersistir() {
        Membro membro = membroValido("11111111111");

        assertThrows(ConstraintViolationException.class,
                () -> membroService.cadastrar(membro));

        verifyNoInteractions(membroRepository);
    }

    @Test
    void deveRejeitarDuplicidadeMesmoQuandoCpfDuplicadoTemPontuacao() {
        Membro membro = membroValido("529.982.247-25");
        when(membroRepository.existsByCpf("52998224725")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> membroService.cadastrar(membro));

        verify(membroRepository).existsByCpf("52998224725");
        verify(membroRepository, never()).save(any(Membro.class));
        assertEquals("529.982.247-25", membro.getCpf());
    }

    @ParameterizedTest
    @MethodSource("camposObrigatoriosInvalidos")
    void deveRejeitarCamposObrigatoriosVaziosOuComEspacos(String campo, String valor) {
        Membro membro = membroValido("52998224725");
        definirCampo(membro, campo, valor);

        assertThrows(ConstraintViolationException.class,
                () -> membroService.cadastrar(membro));

        verifyNoInteractions(membroRepository);
    }

    static Stream<Arguments> camposObrigatoriosInvalidos() {
        return Stream.of(
                Arguments.of("nome", ""),
                Arguments.of("nome", "   "),
                Arguments.of("cpf", ""),
                Arguments.of("cpf", "   "),
                Arguments.of("telefone", ""),
                Arguments.of("telefone", "   "),
                Arguments.of("endereco", ""),
                Arguments.of("endereco", "   ")
        );
    }

    @ParameterizedTest
    @MethodSource("camposAcimaDoLimite")
    void deveRejeitarCamposAcimaDosLimites(String campo, int tamanho) {
        Membro membro = membroValido("52998224725");
        definirCampo(membro, campo, "a".repeat(tamanho));

        assertThrows(ConstraintViolationException.class,
                () -> membroService.cadastrar(membro));

        verifyNoInteractions(membroRepository);
    }

    @Test
    void deveExcluirMembroSemRegistrosRelacionados() {
        Membro membro = membroValido("52998224725");
        when(membroRepository.findById(42L)).thenReturn(java.util.Optional.of(membro));

        membroService.excluir(42L);

        verify(treinoRepository).existsByMembro_Id(42L);
        verify(pagamentoRepository).existsByMembro_Id(42L);
        verify(historicoAtividadeRepository).existsByMembro_Id(42L);
        verify(membroRepository).delete(membro);
        verify(membroRepository).flush();
    }

    @Test
    void deveBloquearExclusaoDeMembroComTreino() {
        Membro membro = membroValido("52998224725");
        when(membroRepository.findById(42L)).thenReturn(java.util.Optional.of(membro));
        when(treinoRepository.existsByMembro_Id(42L)).thenReturn(true);

        IllegalStateException excecao = assertThrows(IllegalStateException.class,
                () -> membroService.excluir(42L));

        assertTrue(excecao.getMessage().contains("treinos"));
        verify(membroRepository, never()).delete(any(Membro.class));
        verifyNoInteractions(pagamentoRepository, historicoAtividadeRepository);
    }

    @Test
    void deveBloquearExclusaoDeMembroComPagamento() {
        Membro membro = membroValido("52998224725");
        when(membroRepository.findById(42L)).thenReturn(java.util.Optional.of(membro));
        when(pagamentoRepository.existsByMembro_Id(42L)).thenReturn(true);

        IllegalStateException excecao = assertThrows(IllegalStateException.class,
                () -> membroService.excluir(42L));

        assertTrue(excecao.getMessage().contains("pagamentos"));
        verify(membroRepository, never()).delete(any(Membro.class));
        verifyNoInteractions(historicoAtividadeRepository);
    }

    @Test
    void deveBloquearExclusaoDeMembroComHistorico() {
        Membro membro = membroValido("52998224725");
        when(membroRepository.findById(42L)).thenReturn(java.util.Optional.of(membro));
        when(historicoAtividadeRepository.existsByMembro_Id(42L)).thenReturn(true);

        IllegalStateException excecao = assertThrows(IllegalStateException.class,
                () -> membroService.excluir(42L));

        assertTrue(excecao.getMessage().contains("histórico"));
        verify(membroRepository, never()).delete(any(Membro.class));
    }

    @Test
    void deveRejeitarExclusaoDeMembroInexistente() {
        when(membroRepository.findById(404L)).thenReturn(java.util.Optional.empty());

        IllegalArgumentException excecao = assertThrows(IllegalArgumentException.class,
                () -> membroService.excluir(404L));

        assertEquals("Membro não encontrado.", excecao.getMessage());
        verifyNoInteractions(treinoRepository, pagamentoRepository, historicoAtividadeRepository);
        verify(membroRepository, never()).delete(any(Membro.class));
    }

    static Stream<Arguments> camposAcimaDoLimite() {
        return Stream.of(
                Arguments.of("nome", 151),
                Arguments.of("telefone", 21),
                Arguments.of("endereco", 256)
        );
    }

    private void definirCampo(Membro membro, String campo, String valor) {
        switch (campo) {
            case "nome" -> membro.setNome(valor);
            case "cpf" -> membro.setCpf(valor);
            case "telefone" -> membro.setTelefone(valor);
            case "endereco" -> membro.setEndereco(valor);
            default -> throw new IllegalArgumentException("Campo desconhecido: " + campo);
        }
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

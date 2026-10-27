
package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.repository.MembroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MembroServiceTest {

    private MembroRepository membroRepository;
    private MembroService membroService;

    @BeforeEach
    void configurar() {
        membroRepository = mock(MembroRepository.class);
        membroService = new MembroService(membroRepository);
    }

    @Test
    void deveCadastrarMembroQuandoCpfAindaNaoExiste() {
        Membro membro = new Membro();
        membro.setNome("Membro Teste");
        membro.setCpf("12345678900");

        when(membroRepository.existsByCpf("12345678900"))
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
        Membro membro = new Membro();
        membro.setNome("Outro Membro");
        membro.setCpf("12345678900");

        when(membroRepository.existsByCpf("12345678900"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> membroService.cadastrar(membro)
        );

        verify(membroRepository, never()).save(any(Membro.class));
    }
}

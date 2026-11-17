package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.model.Funcionario;
import br.edu.unisul.vigor.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class FuncionarioServiceTest {

    private FuncionarioRepository funcionarioRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private FuncionarioService funcionarioService;

    @BeforeEach
    void configurar() {
        funcionarioRepository = mock(FuncionarioRepository.class);
        passwordEncoder = new BCryptPasswordEncoder(4);
        funcionarioService = new FuncionarioService(funcionarioRepository, passwordEncoder);
    }

    @Test
    void autenticaComLoginNormalizadoESenhaBCryptValida() {
        String senha = "Senha-de-teste-123";
        Funcionario funcionario = funcionarioComSenha(passwordEncoder.encode(senha));
        when(funcionarioRepository.findByLoginNormalizado("Mille"))
                .thenReturn(Optional.of(funcionario));

        assertTrue(funcionarioService.autenticar("  Mille  ", senha));
        verify(funcionarioRepository).findByLoginNormalizado("Mille");
    }

    @Test
    void rejeitaSenhaIncorreta() {
        Funcionario funcionario = funcionarioComSenha(passwordEncoder.encode("senha-correta"));
        when(funcionarioRepository.findByLoginNormalizado("mille"))
                .thenReturn(Optional.of(funcionario));

        assertFalse(funcionarioService.autenticar("mille", "senha-incorreta"));
    }

    @Test
    void rejeitaLoginInexistente() {
        when(funcionarioRepository.findByLoginNormalizado("inexistente"))
                .thenReturn(Optional.empty());

        assertFalse(funcionarioService.autenticar("inexistente", "qualquer-senha"));
    }

    @Test
    void rejeitaCredenciaisEmBrancoSemConsultarRepositorio() {
        assertFalse(funcionarioService.autenticar("   ", "senha"));
        assertFalse(funcionarioService.autenticar("mille", "   "));
        assertFalse(funcionarioService.autenticar(null, "senha"));
        assertFalse(funcionarioService.autenticar("mille", null));

        verifyNoInteractions(funcionarioRepository);
    }

    @Test
    void falhaFechadoQuandoHashArmazenadoNaoTemFormatoValido() {
        Funcionario funcionario = funcionarioComSenha("valor-invalido");
        when(funcionarioRepository.findByLoginNormalizado("mille"))
                .thenReturn(Optional.of(funcionario));

        assertFalse(funcionarioService.autenticar("mille", "senha"));
    }

    private Funcionario funcionarioComSenha(String senhaHash) {
        return new Funcionario("Funcionária Teste", "Atendimento", "Mille", senhaHash);
    }
}

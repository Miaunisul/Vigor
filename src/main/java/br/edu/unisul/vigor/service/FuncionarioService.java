package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.model.Funcionario;
import br.edu.unisul.vigor.repository.FuncionarioRepository;
import br.edu.unisul.vigor.security.FuncionarioPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioService(
            FuncionarioRepository funcionarioRepository,
            PasswordEncoder passwordEncoder) {
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public boolean autenticar(String login, String senha) {
        return autenticarFuncionario(login, senha).isPresent();
    }

    @Transactional(readOnly = true)
    public Optional<FuncionarioPrincipal> autenticarFuncionario(String login, String senha) {
        if (login == null || login.isBlank() || senha == null || senha.isBlank()) {
            return Optional.empty();
        }

        String loginNormalizado = normalizarLogin(login);
        return funcionarioRepository.findByLoginNormalizado(loginNormalizado)
                .filter(funcionario -> senhaCorresponde(senha, funcionario.getSenhaHash()))
                .map(this::criarPrincipal);
    }

    private FuncionarioPrincipal criarPrincipal(Funcionario funcionario) {
        return new FuncionarioPrincipal(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getCargo(),
                funcionario.getLogin());
    }

    static String normalizarLogin(String login) {
        int inicio = 0;
        int fim = login.length();

        while (inicio < fim && login.charAt(inicio) == ' ') {
            inicio++;
        }
        while (fim > inicio && login.charAt(fim - 1) == ' ') {
            fim--;
        }

        return login.substring(inicio, fim);
    }

    private boolean senhaCorresponde(String senha, String senhaHash) {
        if (senhaHash == null || senhaHash.isBlank()) {
            return false;
        }
        try {
            return passwordEncoder.matches(senha, senhaHash);
        } catch (IllegalArgumentException hashInvalido) {
            return false;
        }
    }
}

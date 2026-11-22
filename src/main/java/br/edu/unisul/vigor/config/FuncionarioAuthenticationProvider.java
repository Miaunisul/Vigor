package br.edu.unisul.vigor.config;

import br.edu.unisul.vigor.security.FuncionarioPrincipal;
import br.edu.unisul.vigor.service.FuncionarioService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FuncionarioAuthenticationProvider implements AuthenticationProvider {

    private static final String CREDENCIAIS_INVALIDAS = "Login ou senha inválidos.";

    private final FuncionarioService funcionarioService;

    public FuncionarioAuthenticationProvider(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String login = authentication.getName();
        Object credencial = authentication.getCredentials();
        String senha = credencial instanceof String valor ? valor : null;

        FuncionarioPrincipal funcionario = funcionarioService
                .autenticarFuncionario(login, senha)
                .orElseThrow(() -> new BadCredentialsException(CREDENCIAIS_INVALIDAS));

        UsernamePasswordAuthenticationToken autenticado =
                UsernamePasswordAuthenticationToken.authenticated(
                        funcionario,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_FUNCIONARIO")));
        autenticado.setDetails(authentication.getDetails());
        return autenticado;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

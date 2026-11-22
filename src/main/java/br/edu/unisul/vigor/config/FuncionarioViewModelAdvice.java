package br.edu.unisul.vigor.config;

import br.edu.unisul.vigor.security.FuncionarioPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class FuncionarioViewModelAdvice {

    @ModelAttribute("funcionarioAutenticado")
    public FuncionarioPrincipal funcionarioAutenticado() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao != null
                && autenticacao.isAuthenticated()
                && autenticacao.getPrincipal() instanceof FuncionarioPrincipal funcionario) {
            return funcionario;
        }
        return null;
    }
}

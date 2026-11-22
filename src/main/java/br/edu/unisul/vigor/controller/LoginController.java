package br.edu.unisul.vigor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String erro,
            @RequestParam(required = false) String sair,
            Model model) {
        if (erro != null) {
            model.addAttribute("erro", "Login ou senha inválidos.");
        } else if (sair != null) {
            model.addAttribute("mensagem", "Sessão encerrada com segurança.");
        }
        return "login";
    }
}

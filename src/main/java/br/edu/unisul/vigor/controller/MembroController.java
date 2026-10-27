
package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.service.MembroService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/membros")
public class MembroController {

    private final MembroService membroService;

    public MembroController(MembroService membroService) {
        this.membroService = membroService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("membros", membroService.listarTodos());
        return "membros/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("membro", new Membro());
        return "membros/formulario";
    }

    @PostMapping
    public String cadastrar(
            @Valid @ModelAttribute("membro") Membro membro,
            BindingResult resultado,
            RedirectAttributes atributos) {

        if (resultado.hasErrors()) {
            return "membros/formulario";
        }

        try {
            membroService.cadastrar(membro);
            atributos.addFlashAttribute(
                "sucesso", "Membro cadastrado com sucesso!"
            );
            return "redirect:/membros";
        } catch (IllegalArgumentException e) {
            resultado.rejectValue("cpf", "cpf.duplicado", e.getMessage());
            return "membros/formulario";
        }
    }
}

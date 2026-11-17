
package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.service.MembroService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Controller
@RequestMapping("/membros")
public class MembroController {

    private static final String CONSTRAINT_CPF_UNICO = "uk_membros_cpf";

    private final MembroService membroService;

    public MembroController(MembroService membroService) {
        this.membroService = membroService;
    }

    @InitBinder("membro")
    public void normalizarCamposTextuais(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("membros", membroService.listarTodos());
        return "membros/lista";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes atributos) {
        try {
            membroService.excluir(id);
            atributos.addFlashAttribute("sucesso", "Membro excluído com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            atributos.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/membros";
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

        String cpfInformado = membro.getCpf();
        try {
            membroService.cadastrar(membro);
            atributos.addFlashAttribute(
                "sucesso", "Membro cadastrado com sucesso!"
            );
            return "redirect:/membros";
        } catch (IllegalArgumentException e) {
            membro.setCpf(cpfInformado);
            rejeitarCpf(resultado, e.getMessage());
            return "membros/formulario";
        } catch (ConstraintViolationException e) {
            membro.setCpf(cpfInformado);
            adicionarViolacoes(resultado, e.getConstraintViolations());
            return "membros/formulario";
        } catch (DataIntegrityViolationException e) {
            if (!ehViolacaoDaConstraintCpf(e)) {
                throw e;
            }

            membro.setCpf(cpfInformado);
            rejeitarCpf(resultado,
                    "Já existe um membro cadastrado com este CPF.");
            return "membros/formulario";
        }
    }

    private void adicionarViolacoes(
            BindingResult resultado,
            Set<ConstraintViolation<?>> violacoes) {
        for (ConstraintViolation<?> violacao : violacoes) {
            String campo = violacao.getPropertyPath().toString();
            boolean mensagemJaExibida = resultado.getFieldErrors(campo).stream()
                    .anyMatch(erro -> Objects.equals(
                            erro.getDefaultMessage(), violacao.getMessage()));
            if (!mensagemJaExibida) {
                resultado.rejectValue(campo, "membro.invalido", violacao.getMessage());
            }
        }
    }

    private void rejeitarCpf(BindingResult resultado, String mensagem) {
        boolean mensagemJaExibida = resultado.getFieldErrors("cpf").stream()
                .anyMatch(erro -> Objects.equals(erro.getDefaultMessage(), mensagem));
        if (!mensagemJaExibida) {
            resultado.rejectValue("cpf", "cpf.duplicado", mensagem);
        }
    }

    private boolean ehViolacaoDaConstraintCpf(Throwable excecao) {
        for (Throwable causa = excecao; causa != null; causa = causa.getCause()) {
            if (causa instanceof org.hibernate.exception.ConstraintViolationException violacaoHibernate
                    && CONSTRAINT_CPF_UNICO.equalsIgnoreCase(
                            normalizarNomeConstraint(violacaoHibernate.getConstraintName()))) {
                return true;
            }

            if (causa instanceof SQLException sql
                    && "23505".equals(sql.getSQLState())
                    && sql.getMessage() != null
                    && sql.getMessage().toLowerCase(Locale.ROOT)
                            .contains(CONSTRAINT_CPF_UNICO)) {
                return true;
            }
        }
        return false;
    }

    private String normalizarNomeConstraint(String nome) {
        return nome == null ? null : nome.replace("\"", "").replace("`", "");
    }
}

package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.model.Treino;
import br.edu.unisul.vigor.service.MembroService;
import br.edu.unisul.vigor.service.TreinoService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultBindingErrorProcessor;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
@RequestMapping("/treinos")
public class TreinoController {

    private static final String MENSAGEM_MEMBRO_OBRIGATORIO =
            "Selecione um membro cadastrado.";
    private static final String MENSAGEM_MEMBRO_NAO_ENCONTRADO =
            "Membro não encontrado.";

    private final TreinoService treinoService;
    private final MembroService membroService;

    public TreinoController(TreinoService treinoService, MembroService membroService) {
        this.treinoService = treinoService;
        this.membroService = membroService;
    }

    @ModelAttribute("treino")
    public Treino novoTreino() {
        Treino treino = new Treino();
        treino.setMembro(new Membro());
        return treino;
    }

    @InitBinder("treino")
    public void configurarFormulario(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        binder.registerCustomEditor(Integer.class, "duracaoMinutos", new PropertyEditorSupport() {
            @Override
            public void setAsText(String valor) {
                if (valor == null || valor.isBlank()) {
                    setValue(null);
                    return;
                }
                try {
                    setValue(Integer.valueOf(valor.strip()));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Informe a duração como um número inteiro de minutos.", e);
                }
            }
        });
        binder.registerCustomEditor(LocalDate.class, "dataInicio", new PropertyEditorSupport() {
            @Override
            public void setAsText(String valor) {
                if (valor == null || valor.isBlank()) {
                    setValue(null);
                    return;
                }
                try {
                    setValue(LocalDate.parse(valor.strip()));
                } catch (RuntimeException e) {
                    throw new IllegalArgumentException("Informe uma data válida.", e);
                }
            }
        });
        binder.setBindingErrorProcessor(new DefaultBindingErrorProcessor() {
            @Override
            public void processPropertyAccessException(
                    PropertyAccessException excecao,
                    BindingResult resultado) {
                String campo = excecao.getPropertyChangeEvent().getPropertyName();
                String mensagem = switch (campo) {
                    case "duracaoMinutos" ->
                            "Informe a duração como um número inteiro de minutos.";
                    case "dataInicio" -> "Informe uma data válida.";
                    default -> null;
                };
                if (mensagem == null) {
                    super.processPropertyAccessException(excecao, resultado);
                    return;
                }

                String[] codigos = resultado.resolveMessageCodes("typeMismatch", campo);
                resultado.addError(new FieldError(
                        resultado.getObjectName(),
                        campo,
                        excecao.getPropertyChangeEvent().getNewValue(),
                        true,
                        codigos,
                        null,
                        mensagem));
            }
        });
        binder.setAllowedFields("tipo", "descricao", "duracaoMinutos", "dataInicio");
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) Long membroId,
            Model model,
            RedirectAttributes atributos) {
        model.addAttribute("membros", membroService.listarTodos());
        model.addAttribute("membroSelecionadoId", membroId);
        model.addAttribute("treinos", List.of());

        if (membroId != null) {
            try {
                model.addAttribute("treinos", treinoService.consultarPorMembro(membroId));
            } catch (IllegalArgumentException e) {
                if (!MENSAGEM_MEMBRO_NAO_ENCONTRADO.equals(e.getMessage())) {
                    throw e;
                }
                atributos.addFlashAttribute("erro", e.getMessage());
                return "redirect:/treinos";
            }
        }

        return "treinos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        prepararFormulario(model, null);
        return "treinos/formulario";
    }

    @PostMapping
    public String cadastrar(
            @Valid @ModelAttribute("treino") Treino treino,
            BindingResult resultado,
            @RequestParam(value = "membroId", required = false) String membroIdInformado,
            Model model,
            RedirectAttributes atributos) {
        if (treino.getMembro() == null) {
            treino.setMembro(new Membro());
        }
        Long membroId = interpretarMembroId(membroIdInformado, resultado);

        if (resultado.hasErrors()) {
            prepararFormulario(model, membroIdInformado);
            return "treinos/formulario";
        }

        try {
            treinoService.cadastrar(treino, membroId);
            atributos.addFlashAttribute("sucesso", "Treino cadastrado com sucesso.");
            return "redirect:/treinos?membroId=" + membroId;
        } catch (ConstraintViolationException e) {
            adicionarViolacoes(resultado, e.getConstraintViolations());
        } catch (IllegalArgumentException e) {
            if (MENSAGEM_MEMBRO_OBRIGATORIO.equals(e.getMessage())
                    || MENSAGEM_MEMBRO_NAO_ENCONTRADO.equals(e.getMessage())) {
                rejeitarMembro(resultado, e.getMessage());
            } else {
                throw e;
            }
        }

        prepararFormulario(model, membroIdInformado);
        return "treinos/formulario";
    }

    private void prepararFormulario(Model model, String membroIdSelecionado) {
        model.addAttribute("membros", membroService.listarTodos());
        model.addAttribute("membroIdSelecionado", membroIdSelecionado);
    }

    private Long interpretarMembroId(String valor, BindingResult resultado) {
        if (valor == null || valor.isBlank()) {
            rejeitarMembro(resultado, MENSAGEM_MEMBRO_OBRIGATORIO);
            return null;
        }
        try {
            long id = Long.parseLong(valor.strip());
            if (id <= 0) {
                rejeitarMembro(resultado, "Selecione um membro válido.");
                return null;
            }
            return id;
        } catch (NumberFormatException e) {
            rejeitarMembro(resultado, "Selecione um membro válido.");
            return null;
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
                resultado.rejectValue(campo, "treino.invalido", violacao.getMessage());
            }
        }
    }

    private void rejeitarMembro(BindingResult resultado, String mensagem) {
        boolean mensagemJaExibida = resultado.getFieldErrors("membro.id").stream()
                .anyMatch(erro -> Objects.equals(erro.getDefaultMessage(), mensagem));
        if (!mensagemJaExibida) {
            resultado.rejectValue("membro.id", "treino.membro.invalido", mensagem);
        }
    }
}

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
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Controller
@RequestMapping("/treinos")
public class TreinoController {

    private static final String MENSAGEM_MEMBRO_OBRIGATORIO =
            "Selecione um membro cadastrado.";
    private static final String MENSAGEM_MEMBRO_NAO_ENCONTRADO =
            "Membro não encontrado.";
    private static final String MENSAGEM_TREINO_NAO_ENCONTRADO =
            "Treino não encontrado.";
    private static final int TAMANHO_PAGINA = 10;

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
    public String tela(
            @RequestParam(required = false) String acao,
            @RequestParam(required = false) Long treinoId,
            @RequestParam(required = false) Long membroId,
            @RequestParam(required = false, name = "q") String busca,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            RedirectAttributes atributos) {
        prepararDadosBase(model, busca, membroId);

        String visualizacao = normalizarAcao(acao);
        if ("detalhes".equals(visualizacao) || "editar".equals(visualizacao)) {
            if (treinoId == null || treinoId <= 0) {
                atributos.addFlashAttribute("erro", MENSAGEM_TREINO_NAO_ENCONTRADO);
                return "redirect:/treinos";
            }
            try {
                Treino treino = treinoService.buscarPorId(treinoId);
                model.addAttribute("treino", treino);
                model.addAttribute("treinoId", treinoId);
                model.addAttribute("membroSelecionadoId", treino.getMembro().getId());
                model.addAttribute("membroIdFormulario", treino.getMembro().getId().toString());
                model.addAttribute("visualizacao", visualizacao);
                return "treinos/lista";
            } catch (IllegalArgumentException e) {
                if (!MENSAGEM_TREINO_NAO_ENCONTRADO.equals(e.getMessage())) {
                    throw e;
                }
                atributos.addFlashAttribute("erro", e.getMessage());
                return "redirect:/treinos";
            }
        }

        if ("novo".equals(visualizacao)) {
            model.addAttribute("visualizacao", visualizacao);
            model.addAttribute("membroIdFormulario", membroId == null ? "" : membroId.toString());
            return "treinos/lista";
        }

        return montarLista(model, busca, membroId, page, atributos);
    }

    @GetMapping("/novo")
    public String novo() {
        return "redirect:/treinos?acao=novo";
    }

    @PostMapping
    public String salvar(
            @Valid @ModelAttribute("treino") Treino treino,
            BindingResult resultado,
            @RequestParam(value = "acao", defaultValue = "novo") String acao,
            @RequestParam(value = "treinoId", required = false) Long treinoId,
            @RequestParam(value = "membroId", required = false) String membroIdInformado,
            Model model,
            RedirectAttributes atributos) {
        if (treino.getMembro() == null) {
            treino.setMembro(new Membro());
        }
        Long membroId = interpretarMembroId(membroIdInformado, resultado);
        boolean editando = "editar".equals(acao);

        if (editando && (treinoId == null || treinoId <= 0)) {
            resultado.reject("treino.naoEncontrado", MENSAGEM_TREINO_NAO_ENCONTRADO);
        }
        if (resultado.hasErrors()) {
            prepararFormulario(model, treino, editando ? "editar" : "novo",
                    treinoId, membroIdInformado);
            return "treinos/lista";
        }

        try {
            if (editando) {
                treinoService.atualizar(treinoId, treino, membroId);
                atributos.addFlashAttribute("sucesso", "Treino atualizado com sucesso.");
            } else {
                treinoService.cadastrar(treino, membroId);
                atributos.addFlashAttribute("sucesso", "Treino cadastrado com sucesso.");
            }
            return "redirect:/treinos";
        } catch (ConstraintViolationException e) {
            adicionarViolacoes(resultado, e.getConstraintViolations());
        } catch (IllegalArgumentException e) {
            if (MENSAGEM_MEMBRO_OBRIGATORIO.equals(e.getMessage())
                    || MENSAGEM_MEMBRO_NAO_ENCONTRADO.equals(e.getMessage())) {
                rejeitarMembro(resultado, e.getMessage());
            } else if (MENSAGEM_TREINO_NAO_ENCONTRADO.equals(e.getMessage())) {
                resultado.reject("treino.naoEncontrado", e.getMessage());
            } else {
                throw e;
            }
        }

        prepararFormulario(model, treino, editando ? "editar" : "novo",
                treinoId, membroIdInformado);
        return "treinos/lista";
    }

    @PostMapping(params = "acao=excluir")
    public String excluir(
            @RequestParam Long treinoId,
            RedirectAttributes atributos) {
        try {
            treinoService.excluir(treinoId);
            atributos.addFlashAttribute("sucesso", "Treino excluído com sucesso.");
        } catch (IllegalArgumentException e) {
            if (!MENSAGEM_TREINO_NAO_ENCONTRADO.equals(e.getMessage())) {
                throw e;
            }
            atributos.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/treinos";
    }

    private String montarLista(
            Model model,
            String busca,
            Long membroId,
            int paginaSolicitada,
            RedirectAttributes atributos) {
        if (membroId != null) {
            try {
                membroService.buscarPorId(membroId);
            } catch (IllegalArgumentException e) {
                if (!MENSAGEM_MEMBRO_NAO_ENCONTRADO.equals(e.getMessage())) {
                    throw e;
                }
                atributos.addFlashAttribute("erro", e.getMessage());
                return "redirect:/treinos";
            }
        }

        String termo = busca == null ? "" : busca.strip();
        String termoMinusculo = termo.toLowerCase(Locale.ROOT);
        String cpfTermo = termo.replaceAll("\\D", "");
        List<Treino> filtrados = treinoService.listarTodos().stream()
                .filter(treino -> membroId == null
                        || treino.getMembro().getId().equals(membroId))
                .filter(treino -> termo.isBlank()
                        || treino.getMembro().getNome().toLowerCase(Locale.ROOT)
                                .contains(termoMinusculo)
                        || (!cpfTermo.isBlank() && treino.getMembro().getCpf() != null
                                && treino.getMembro().getCpf().contains(cpfTermo)))
                .toList();

        int total = filtrados.size();
        int totalPaginas = Math.max(1, (int) Math.ceil((double) total / TAMANHO_PAGINA));
        int pagina = Math.max(0, Math.min(paginaSolicitada, totalPaginas - 1));
        int inicioIndice = pagina * TAMANHO_PAGINA;
        int fimIndice = Math.min(total, inicioIndice + TAMANHO_PAGINA);

        model.addAttribute("treinos", filtrados.subList(inicioIndice, fimIndice));
        model.addAttribute("visualizacao", "lista");
        model.addAttribute("busca", termo);
        model.addAttribute("membroSelecionadoId", membroId);
        model.addAttribute("pagina", pagina);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("totalTreinos", total);
        model.addAttribute("inicioLista", total == 0 ? 0 : inicioIndice + 1);
        model.addAttribute("fimLista", fimIndice);
        return "treinos/lista";
    }

    private void prepararDadosBase(Model model, String busca, Long membroId) {
        model.addAttribute("membros", membroService.listarTodos());
        model.addAttribute("busca", busca == null ? "" : busca);
        model.addAttribute("membroSelecionadoId", membroId);
        model.addAttribute("visualizacao", "lista");
        model.addAttribute("moduloAtivo", "treinos");
    }

    private void prepararFormulario(
            Model model,
            Treino treino,
            String visualizacao,
            Long treinoId,
            String membroIdSelecionado) {
        prepararDadosBase(model, "", null);
        model.addAttribute("treino", treino);
        model.addAttribute("visualizacao", visualizacao);
        model.addAttribute("treinoId", treinoId);
        model.addAttribute("membroIdFormulario", membroIdSelecionado == null ? "" : membroIdSelecionado);
    }

    private String normalizarAcao(String acao) {
        if ("novo".equals(acao) || "editar".equals(acao) || "detalhes".equals(acao)) {
            return acao;
        }
        return "lista";
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


package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.service.HistoricoAtividadeService;
import br.edu.unisul.vigor.service.MembroService;
import br.edu.unisul.vigor.service.PagamentoService;
import br.edu.unisul.vigor.service.TreinoService;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

@Controller
@RequestMapping("/membros")
public class MembroController {

    private static final String CONSTRAINT_CPF_UNICO = "uk_membros_cpf";
    private static final String MENSAGEM_CPF_DUPLICADO =
            "Já existe um membro cadastrado com este CPF.";
    private static final String MENSAGEM_MEMBRO_NAO_ENCONTRADO =
            "Membro não encontrado.";

    private final MembroService membroService;
    private final TreinoService treinoService;
    private final PagamentoService pagamentoService;
    private final HistoricoAtividadeService historicoAtividadeService;

    public MembroController(
            MembroService membroService,
            TreinoService treinoService,
            PagamentoService pagamentoService,
            HistoricoAtividadeService historicoAtividadeService) {
        this.membroService = membroService;
        this.treinoService = treinoService;
        this.pagamentoService = pagamentoService;
        this.historicoAtividadeService = historicoAtividadeService;
    }

    @ModelAttribute
    public void configurarFormularioPadrao(Model model) {
        model.addAttribute("editando", false);
        model.addAttribute("formAction", "/membros");
        model.addAttribute("estado", "lista");
        model.addAttribute("moduloAtivo", "membros");
    }

    @InitBinder("membro")
    public void normalizarCamposTextuais(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        binder.setAllowedFields("nome", "cpf", "telefone", "endereco");
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) String acao,
            @RequestParam(required = false) Long membroId,
            @RequestParam(required = false, name = "q") String busca,
            @RequestParam(defaultValue = "0") int page,
            Model model,
            RedirectAttributes atributos) {
        if ("detalhes".equals(acao)) {
            return exibirDetalhes(membroId, model, atributos);
        }

        montarLista(model, busca, membroId, page);
        return "membros/lista";
    }

    private String exibirDetalhes(
            Long membroId,
            Model model,
            RedirectAttributes atributos) {
        if (membroId == null || membroId <= 0) {
            atributos.addFlashAttribute("erro", MENSAGEM_MEMBRO_NAO_ENCONTRADO);
            return "redirect:/membros";
        }
        try {
            Membro membro = membroService.buscarPorId(membroId);
            model.addAttribute("membro", membro);
            model.addAttribute("treinos", treinoService.consultarPorMembro(membroId));
            model.addAttribute("pagamentos", pagamentoService.consultarPorMembro(membroId));
            model.addAttribute(
                    "atividades",
                    historicoAtividadeService.consultarPorMembro(membroId));
            model.addAttribute("estado", "detalhes");
            return "membros/lista";
        } catch (IllegalArgumentException e) {
            if (!MENSAGEM_MEMBRO_NAO_ENCONTRADO.equals(e.getMessage())) {
                throw e;
            }
            atributos.addFlashAttribute("erro", e.getMessage());
            return "redirect:/membros";
        }
    }

    private void montarLista(Model model, String busca, Long membroId, int paginaSolicitada) {
        List<Membro> membros = membroService.listarTodos();
        String termo = busca == null ? "" : busca.strip();
        String termoMinusculo = termo.toLowerCase(Locale.ROOT);
        String cpfTermo = termo.replaceAll("\\D", "");

        List<Membro> filtrados = membros.stream()
                .filter(membro -> membroId == null
                        || Objects.equals(membro.getId(), membroId))
                .filter(membro -> termo.isBlank()
                        || (membro.getNome() != null
                                && membro.getNome().toLowerCase(Locale.ROOT)
                                        .contains(termoMinusculo))
                        || (!cpfTermo.isBlank() && membro.getCpf() != null
                                && membro.getCpf().contains(cpfTermo)))
                .toList();

        int total = filtrados.size();
        int totalPaginas = Math.max(1, (int) Math.ceil((double) total / 10));
        int pagina = Math.max(0, Math.min(paginaSolicitada, totalPaginas - 1));
        int inicioIndice = pagina * 10;
        int fimIndice = Math.min(total, inicioIndice + 10);

        model.addAttribute("membros", membros);
        model.addAttribute("membrosPagina", filtrados.subList(inicioIndice, fimIndice));
        model.addAttribute("estado", "lista");
        model.addAttribute("busca", termo);
        model.addAttribute("membroSelecionadoId", membroId);
        model.addAttribute("pagina", pagina);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("totalMembros", total);
        model.addAttribute("inicioLista", total == 0 ? 0 : inicioIndice + 1);
        model.addAttribute("fimLista", fimIndice);
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
        model.addAttribute("estado", "novo");
        return "membros/lista";
    }

    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes atributos) {
        try {
            model.addAttribute("membro", membroService.buscarPorId(id));
            configurarFormularioEdicao(model, id);
            return "membros/lista";
        } catch (IllegalArgumentException e) {
            if (!MENSAGEM_MEMBRO_NAO_ENCONTRADO.equals(e.getMessage())) {
                throw e;
            }
            atributos.addFlashAttribute("erro", e.getMessage());
            return "redirect:/membros";
        }
    }

    @PostMapping
    public String cadastrar(
            @Valid @ModelAttribute("membro") Membro membro,
            BindingResult resultado,
            Model model,
            RedirectAttributes atributos) {

        if (resultado.hasErrors()) {
            model.addAttribute("estado", "novo");
            return "membros/lista";
        }

        String cpfInformado = membro.getCpf();
        try {
            membroService.cadastrar(membro);
            atributos.addFlashAttribute(
                "sucesso", "Membro cadastrado com sucesso!"
            );
            return "redirect:/membros";
        } catch (IllegalArgumentException e) {
            if (!ehCpfDuplicado(e)) {
                throw e;
            }
            membro.setCpf(cpfInformado);
            rejeitarCpf(resultado, e.getMessage());
            model.addAttribute("estado", "novo");
            return "membros/lista";
        } catch (ConstraintViolationException e) {
            membro.setCpf(cpfInformado);
            adicionarViolacoes(resultado, e.getConstraintViolations());
            model.addAttribute("estado", "novo");
            return "membros/lista";
        } catch (DataIntegrityViolationException e) {
            if (!ehViolacaoDaConstraintCpf(e)) {
                throw e;
            }

            membro.setCpf(cpfInformado);
            rejeitarCpf(resultado,
                    "Já existe um membro cadastrado com este CPF.");
            model.addAttribute("estado", "novo");
            return "membros/lista";
        }
    }

    @PostMapping("/{id}/editar")
    public String atualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("membro") Membro membro,
            BindingResult resultado,
            Model model,
            RedirectAttributes atributos) {
        configurarFormularioEdicao(model, id);

        if (resultado.hasErrors()) {
            return "membros/lista";
        }

        String cpfInformado = membro.getCpf();
        try {
            membroService.atualizar(id, membro);
            atributos.addFlashAttribute(
                    "sucesso", "Membro atualizado com sucesso!");
            return "redirect:/membros";
        } catch (ConstraintViolationException e) {
            membro.setCpf(cpfInformado);
            adicionarViolacoes(resultado, e.getConstraintViolations());
        } catch (IllegalArgumentException e) {
            if (ehCpfDuplicado(e)) {
                membro.setCpf(cpfInformado);
                rejeitarCpf(resultado, e.getMessage());
            } else if (MENSAGEM_MEMBRO_NAO_ENCONTRADO.equals(e.getMessage())) {
                atributos.addFlashAttribute("erro", e.getMessage());
                return "redirect:/membros";
            } else {
                throw e;
            }
        } catch (DataIntegrityViolationException e) {
            if (!ehViolacaoDaConstraintCpf(e)) {
                throw e;
            }

            membro.setCpf(cpfInformado);
            rejeitarCpf(resultado, MENSAGEM_CPF_DUPLICADO);
        }
        return "membros/lista";
    }

    private void configurarFormularioEdicao(Model model, Long id) {
        model.addAttribute("editando", true);
        model.addAttribute("formAction", "/membros/" + id + "/editar");
        model.addAttribute("membroId", id);
        model.addAttribute("estado", "editar");
    }

    private boolean ehCpfDuplicado(IllegalArgumentException excecao) {
        return MENSAGEM_CPF_DUPLICADO.equals(excecao.getMessage());
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

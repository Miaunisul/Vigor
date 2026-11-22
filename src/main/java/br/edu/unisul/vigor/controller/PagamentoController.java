package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.dto.PagamentoForm;
import br.edu.unisul.vigor.model.Pagamento;
import br.edu.unisul.vigor.service.MembroService;
import br.edu.unisul.vigor.service.PagamentoService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/pagamentos")
public class PagamentoController {

    private static final String FORMULARIO = "pagamentos/formulario";
    private static final String LISTAGEM = "pagamentos/lista";
    private static final String STATUS_PAGO = "PAGO";

    private final PagamentoService pagamentoService;
    private final MembroService membroService;

    public PagamentoController(
            PagamentoService pagamentoService,
            MembroService membroService) {
        this.pagamentoService = pagamentoService;
        this.membroService = membroService;
    }

    @InitBinder("pagamentoForm")
    public void configurarCamposPermitidos(WebDataBinder binder) {
        binder.setAllowedFields("membroId", "valor", "dataPagamento");
    }

    @GetMapping
    public String listar(
            @RequestParam(name = "membroId", required = false)
            String membroInformado,
            Model model) {

        carregarMembros(model);
        model.addAttribute("membroSelecionado", null);

        if (membroInformado == null || membroInformado.isBlank()) {
            model.addAttribute("pagamentos", pagamentoService.listarTodos());
            return LISTAGEM;
        }

        Long membroId;
        try {
            membroId = Long.valueOf(membroInformado.strip());
        } catch (NumberFormatException e) {
            return apresentarErroConsulta(model, "Selecione um membro válido.");
        }

        if (membroId <= 0) {
            return apresentarErroConsulta(model, "Selecione um membro válido.");
        }

        try {
            model.addAttribute(
                    "pagamentos",
                    pagamentoService.consultarPorMembro(membroId));
            model.addAttribute("membroSelecionado", membroId);
        } catch (IllegalArgumentException e) {
            if (!"Membro não encontrado.".equals(e.getMessage())) {
                throw e;
            }
            return apresentarErroConsulta(
                    model,
                    "O membro selecionado não foi encontrado.");
        }

        return LISTAGEM;
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pagamentoForm", new PagamentoForm());
        carregarMembros(model);
        return FORMULARIO;
    }

    @PostMapping
    public String registrar(
            @Valid @ModelAttribute("pagamentoForm") PagamentoForm formulario,
            BindingResult resultado,
            Model model,
            RedirectAttributes atributos) {

        if (resultado.hasErrors()) {
            carregarMembros(model);
            return FORMULARIO;
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setValor(formulario.getValor());
        pagamento.setDataPagamento(formulario.getDataPagamento());
        pagamento.setStatus(STATUS_PAGO);

        try {
            pagamentoService.registrar(pagamento, formulario.getMembroId());
        } catch (ConstraintViolationException e) {
            adicionarViolacoes(resultado, e);
            carregarMembros(model);
            return FORMULARIO;
        } catch (IllegalArgumentException e) {
            if (!"Membro não encontrado.".equals(e.getMessage())
                    && !"O membro é obrigatório.".equals(e.getMessage())) {
                throw e;
            }

            resultado.rejectValue(
                    "membroId",
                    "pagamento.membro.invalido",
                    "Selecione um membro existente.");

            carregarMembros(model);
            return FORMULARIO;
        }

        atributos.addFlashAttribute(
                "sucesso",
                "Pagamento registrado com sucesso.");

        return "redirect:/pagamentos";
    }

    private void carregarMembros(Model model) {
        model.addAttribute("membros", membroService.listarTodos());
    }

    private String apresentarErroConsulta(Model model, String mensagem) {
        model.addAttribute("pagamentos", List.of());
        model.addAttribute("erro", mensagem);
        return LISTAGEM;
    }

    private void adicionarViolacoes(
            BindingResult resultado,
            ConstraintViolationException excecao) {

        for (ConstraintViolation<?> violacao : excecao.getConstraintViolations()) {
            String campo = violacao.getPropertyPath().toString();

            switch (campo) {
                case "valor", "dataPagamento" ->
                        resultado.rejectValue(
                                campo,
                                "pagamento.invalido",
                                violacao.getMessage());

                case "membro" ->
                        resultado.rejectValue(
                                "membroId",
                                "pagamento.membro.invalido",
                                violacao.getMessage());

                default ->
                        resultado.reject(
                                "pagamento.invalido",
                                "Não foi possível registrar o pagamento. Confira os dados.");
            }
        }
    }
}
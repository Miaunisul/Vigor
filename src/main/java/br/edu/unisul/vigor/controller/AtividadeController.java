package br.edu.unisul.vigor.controller;

import br.edu.unisul.vigor.model.HistoricoAtividade;
import br.edu.unisul.vigor.service.HistoricoAtividadeService;
import br.edu.unisul.vigor.service.MembroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
public class AtividadeController {

    private final HistoricoAtividadeService historicoAtividadeService;
    private final MembroService membroService;

    public AtividadeController(
            HistoricoAtividadeService historicoAtividadeService,
            MembroService membroService) {
        this.historicoAtividadeService = historicoAtividadeService;
        this.membroService = membroService;
    }

    @GetMapping("/atividades")
    public String listar(
            @RequestParam(required = false) String membroId,
            @RequestParam(required = false, name = "q") String busca,
            @RequestParam(required = false) String dataInicial,
            @RequestParam(required = false) String dataFinal,
            Model model) {
        List<HistoricoAtividade> atividades = List.of();
        String erro = null;
        Long membroSelecionadoId = null;
        LocalDate inicio = null;
        LocalDate fim = null;
        String termoBusca = busca == null ? "" : busca.strip();

        try {
            membroSelecionadoId = interpretarId(membroId);
            inicio = interpretarData(dataInicial);
            fim = interpretarData(dataFinal);
            if (inicio != null && fim != null && inicio.isAfter(fim)) {
                erro = "A data inicial não pode ser posterior à data final.";
            } else {
                atividades = historicoAtividadeService.consultar(
                        membroSelecionadoId, termoBusca, inicio, fim);
            }
        } catch (DateTimeParseException | NumberFormatException excecao) {
            erro = "Informe um membro e um período válidos.";
        } catch (IllegalArgumentException excecao) {
            erro = "Membro não encontrado.".equals(excecao.getMessage())
                    ? excecao.getMessage()
                    : "Não foi possível aplicar os filtros informados.";
        }

        model.addAttribute("moduloAtivo", "atividades");
        model.addAttribute("membros", membroService.listarTodos());
        model.addAttribute("atividades", atividades);
        model.addAttribute("membroSelecionadoId", membroSelecionadoId);
        model.addAttribute("busca", busca == null ? "" : busca);
        model.addAttribute("dataInicial", dataInicial == null ? "" : dataInicial);
        model.addAttribute("dataFinal", dataFinal == null ? "" : dataFinal);
        model.addAttribute("erro", erro);
        return "atividades/lista";
    }

    private Long interpretarId(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        long id = Long.parseLong(valor);
        if (id <= 0) {
            throw new NumberFormatException("ID deve ser positivo.");
        }
        return id;
    }

    private LocalDate interpretarData(String valor) {
        return valor == null || valor.isBlank() ? null : LocalDate.parse(valor);
    }
}

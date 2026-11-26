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

    private static final String MENSAGEM_MEMBRO_OBRIGATORIO =
            "Selecione um membro para consultar o histórico de atividades.";
    private static final String MENSAGEM_REFERENCIA_MEMBRO_INVALIDA =
            "A referência do membro é inválida. Selecione um membro cadastrado.";
    private static final String MENSAGEM_MEMBRO_NAO_ENCONTRADO = "Membro não encontrado.";

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
        boolean consultaExecutada = false;
        boolean membroSemAtividades = false;
        boolean membroEncontrado = false;
        boolean referenciaMembroValida = false;

        if (membroId == null || membroId.isBlank()) {
            erro = MENSAGEM_MEMBRO_OBRIGATORIO;
        } else {
            try {
                membroSelecionadoId = interpretarId(membroId);
                referenciaMembroValida = true;
            } catch (NumberFormatException excecao) {
                erro = MENSAGEM_REFERENCIA_MEMBRO_INVALIDA;
            }
        }

        try {
            inicio = interpretarData(dataInicial);
            fim = interpretarData(dataFinal);
        } catch (DateTimeParseException excecao) {
            if (erro == null) {
                erro = "Informe um período válido.";
            }
        }

        if (erro == null && inicio != null && fim != null && inicio.isAfter(fim)) {
            erro = "A data inicial não pode ser posterior à data final.";
        }

        if (erro == null) {
            try {
                atividades = historicoAtividadeService.consultar(
                        membroSelecionadoId, termoBusca, inicio, fim);
                consultaExecutada = true;
                membroEncontrado = true;
                membroSemAtividades = atividades.isEmpty()
                        && historicoAtividadeService.consultarPorMembro(membroSelecionadoId).isEmpty();
            } catch (IllegalArgumentException excecao) {
                erro = MENSAGEM_MEMBRO_NAO_ENCONTRADO.equals(excecao.getMessage())
                        ? excecao.getMessage()
                        : "Não foi possível aplicar os filtros informados.";
            }
        }

        model.addAttribute("moduloAtivo", "atividades");
        model.addAttribute("membros", membroService.listarTodos());
        model.addAttribute("atividades", atividades);
        model.addAttribute("membroSelecionadoId", membroSelecionadoId);
        model.addAttribute("membroIdInformado", membroId == null ? "" : membroId);
        model.addAttribute("referenciaMembroValida", referenciaMembroValida);
        model.addAttribute("referenciaMembroEncontrada", membroEncontrado);
        model.addAttribute("consultaExecutada", consultaExecutada);
        model.addAttribute("membroSemAtividades", membroSemAtividades);
        model.addAttribute("busca", busca == null ? "" : busca);
        model.addAttribute("dataInicial", dataInicial == null ? "" : dataInicial);
        model.addAttribute("dataFinal", dataFinal == null ? "" : dataFinal);
        model.addAttribute("erro", erro);
        return "atividades/lista";
    }

    private Long interpretarId(String valor) {
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

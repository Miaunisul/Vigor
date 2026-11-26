package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.dto.RegistroCargaHistorico;
import br.edu.unisul.vigor.model.HistoricoAtividade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Procedimento técnico de carga do histórico de atividades (CA28 e CA29).
 *
 * <p>Converte os valores textuais recebidos, rejeita data inválida e tempo não inteiro
 * antes de qualquer gravação e delega o registro ao {@link HistoricoAtividadeService},
 * que confere a existência do membro e do funcionário responsável, aplica as validações
 * da entidade e persiste a atividade por JPA.
 */
@Service
public class CargaHistoricoAtividadeService {

    private static final String MENSAGEM_DADOS_AUSENTES = "Os dados da carga são obrigatórios.";
    private static final String MENSAGEM_MEMBRO_INVALIDO =
            "A referência do membro deve ser um número inteiro.";
    private static final String MENSAGEM_FUNCIONARIO_INVALIDO =
            "A referência do funcionário responsável deve ser um número inteiro.";
    private static final String MENSAGEM_DATA_INVALIDA =
            "A data da atividade é inválida. Informe uma data existente no formato AAAA-MM-DD.";
    private static final String MENSAGEM_TEMPO_NAO_INTEIRO =
            "O tempo de execução deve ser um número inteiro de minutos.";

    private final HistoricoAtividadeService historicoAtividadeService;

    public CargaHistoricoAtividadeService(HistoricoAtividadeService historicoAtividadeService) {
        this.historicoAtividadeService = historicoAtividadeService;
    }

    @Transactional
    public HistoricoAtividade carregar(RegistroCargaHistorico registro) {
        if (registro == null) {
            throw new IllegalArgumentException(MENSAGEM_DADOS_AUSENTES);
        }

        Long membroId = converterReferencia(registro.membroId(), MENSAGEM_MEMBRO_INVALIDO);
        Long funcionarioResponsavelId = converterReferencia(
                registro.funcionarioResponsavelId(), MENSAGEM_FUNCIONARIO_INVALIDO);

        HistoricoAtividade atividade = new HistoricoAtividade();
        atividade.setAtividade(registro.atividade());
        atividade.setDataAtividade(converterData(registro.dataAtividade()));
        atividade.setTempoExecucaoMinutos(converterTempo(registro.tempoExecucaoMinutos()));

        return historicoAtividadeService.registrar(atividade, membroId, funcionarioResponsavelId);
    }

    private static Long converterReferencia(String valor, String mensagemErro) {
        if (estaAusente(valor)) {
            return null;
        }
        try {
            return Long.valueOf(valor.strip());
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException(mensagemErro, erro);
        }
    }

    private static LocalDate converterData(String valor) {
        if (estaAusente(valor)) {
            return null;
        }
        try {
            // ISO_LOCAL_DATE usa resolução estrita: datas como 2026-02-30 são rejeitadas.
            return LocalDate.parse(valor.strip(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException erro) {
            throw new IllegalArgumentException(MENSAGEM_DATA_INVALIDA, erro);
        }
    }

    private static Integer converterTempo(String valor) {
        if (estaAusente(valor)) {
            return null;
        }
        try {
            return Integer.valueOf(valor.strip());
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException(MENSAGEM_TEMPO_NAO_INTEIRO, erro);
        }
    }

    private static boolean estaAusente(String valor) {
        return valor == null || valor.isBlank();
    }
}

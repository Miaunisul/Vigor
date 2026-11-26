package br.edu.unisul.vigor.dto;

/**
 * Dados de uma atividade recebidos pelo procedimento técnico de carga do histórico.
 *
 * <p>Os valores chegam como texto, como em uma planilha ou em variáveis de ambiente,
 * e são convertidos e validados antes de qualquer gravação. A carga técnica não
 * representa um cadastro manual de atividades pela interface (CA13).
 */
public record RegistroCargaHistorico(
        String membroId,
        String funcionarioResponsavelId,
        String atividade,
        String dataAtividade,
        String tempoExecucaoMinutos) {
}

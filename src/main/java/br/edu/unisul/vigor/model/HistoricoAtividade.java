package br.edu.unisul.vigor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Entity
@Table(name = "historico_atividades")
public class HistoricoAtividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O membro é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "membro_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_historico_atividades_membros"))
    private Membro membro;

    @NotNull(message = "O funcionário responsável é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "funcionario_responsavel_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_historico_atividades_funcionarios"))
    private Funcionario funcionarioResponsavel;

    @NotBlank(message = "A atividade é obrigatória e não pode ficar vazia.")
    @Column(nullable = false, columnDefinition = "text")
    private String atividade;

    @NotNull(message = "A data da atividade é obrigatória.")
    @Column(name = "data_atividade", nullable = false)
    private LocalDate dataAtividade;

    @NotNull(message = "O tempo de execução é obrigatório.")
    @Positive(message = "O tempo de execução deve ser positivo.")
    @Column(name = "tempo_execucao_minutos", nullable = false)
    private Integer tempoExecucaoMinutos;

    public HistoricoAtividade() {
    }

    public Long getId() {
        return id;
    }

    public Membro getMembro() {
        return membro;
    }

    public void setMembro(Membro membro) {
        this.membro = membro;
    }

    public Funcionario getFuncionarioResponsavel() {
        return funcionarioResponsavel;
    }

    public void setFuncionarioResponsavel(Funcionario funcionarioResponsavel) {
        this.funcionarioResponsavel = funcionarioResponsavel;
    }

    public String getAtividade() {
        return atividade;
    }

    public void setAtividade(String atividade) {
        this.atividade = atividade;
    }

    public LocalDate getDataAtividade() {
        return dataAtividade;
    }

    public void setDataAtividade(LocalDate dataAtividade) {
        this.dataAtividade = dataAtividade;
    }

    public Integer getTempoExecucaoMinutos() {
        return tempoExecucaoMinutos;
    }

    public void setTempoExecucaoMinutos(Integer tempoExecucaoMinutos) {
        this.tempoExecucaoMinutos = tempoExecucaoMinutos;
    }
}

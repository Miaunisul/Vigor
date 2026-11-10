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
@Table(name = "treinos")
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O membro é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "membro_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_treinos_membros"))
    private Membro membro;

    @NotBlank(message = "O tipo do treino é obrigatório.")
    @Column(nullable = false, columnDefinition = "text")
    private String tipo;

    @NotBlank(message = "A descrição do treino é obrigatória.")
    @Column(nullable = false, columnDefinition = "text")
    private String descricao;

    @NotNull(message = "A duração do treino é obrigatória.")
    @Positive(message = "A duração deve ser um número inteiro positivo de minutos.")
    @Column(name = "duracao_minutos", nullable = false)
    private Integer duracaoMinutos;

    @NotNull(message = "A data de início do treino é obrigatória.")
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    public Treino() {
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }
}

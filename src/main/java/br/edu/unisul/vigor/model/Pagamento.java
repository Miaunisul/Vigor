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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O membro é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "membro_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_pagamentos_membros"))
    private Membro membro;

    @NotNull(message = "O valor do pagamento é obrigatório.")
    @DecimalMin(value = "0.00", inclusive = false,
            message = "O valor do pagamento deve ser maior que zero.")
    @Digits(integer = 10, fraction = 2,
            message = "O valor deve possuir no máximo duas casas decimais.")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "A data do pagamento é obrigatória.")
    @Column(name = "data_pagamento", nullable = false)
    private LocalDate dataPagamento;

    @NotBlank(message = "O status do pagamento é obrigatório.")
    @Pattern(regexp = "PAGO", message = "O status do pagamento deve ser PAGO.")
    @Column(nullable = false, length = 4)
    private String status;

    public Pagamento() {
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

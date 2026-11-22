package br.edu.unisul.vigor.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagamentoForm {

    @NotNull(message = "Selecione um membro.")
    @Positive(message = "Selecione um membro válido.")
    private Long membroId;

    @NotNull(message = "Informe o valor do pagamento.")
    @DecimalMin(
            value = "0.00",
            inclusive = false,
            message = "O valor deve ser maior que zero.")
    @Digits(
            integer = 10,
            fraction = 2,
            message = "Use no máximo 10 dígitos inteiros e duas casas decimais.")
    private BigDecimal valor;

    @NotNull(message = "Informe a data do pagamento.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataPagamento;

    public Long getMembroId() {
        return membroId;
    }

    public void setMembroId(Long membroId) {
        this.membroId = membroId;
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
}
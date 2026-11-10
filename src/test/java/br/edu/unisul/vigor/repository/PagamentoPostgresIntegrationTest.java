package br.edu.unisul.vigor.repository;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.model.Pagamento;
import br.edu.unisul.vigor.service.PagamentoService;
import br.edu.unisul.vigor.validation.CpfUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.flyway.enabled=false")
@Transactional
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class PagamentoPostgresIntegrationTest {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private MembroRepository membroRepository;

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void persistePagamentoEConsultaVinculoComOMembro() {
        Membro membro = membroParaTeste();
        Pagamento pagamento = pagamentoValido();

        Pagamento salvo = pagamentoService.registrar(pagamento, membro.getId());
        List<Pagamento> consultados = pagamentoService.consultarPorMembro(membro.getId());

        assertNotNull(salvo.getId());
        assertEquals(membro.getId(), salvo.getMembro().getId());
        assertTrue(consultados.stream().anyMatch(item -> item.getId().equals(salvo.getId())));
    }

    @Test
    void servicoRejeitaValorInvalidoSemPersistirPagamento() {
        Membro membro = membroParaTeste();
        long quantidadeAntes = pagamentoRepository.findByMembro_Id(membro.getId()).size();
        Pagamento pagamento = pagamentoValido();
        pagamento.setValor(BigDecimal.ZERO);

        assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> pagamentoService.registrar(pagamento, membro.getId()));

        assertEquals(quantidadeAntes, pagamentoRepository.findByMembro_Id(membro.getId()).size());
    }

    @Test
    void servicoRejeitaStatusInvalidoSemPersistirPagamento() {
        Membro membro = membroParaTeste();
        Pagamento pagamento = pagamentoValido();
        pagamento.setStatus("PEND");

        assertThrows(jakarta.validation.ConstraintViolationException.class,
                () -> pagamentoService.registrar(pagamento, membro.getId()));
        assertEquals(0, pagamentoRepository.findByMembro_Id(membro.getId()).size());
    }

    @Test
    void constraintDoBancoRejeitaValorNaoPositivo() {
        Membro membro = membroParaTeste();

        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO pagamentos (membro_id, valor, data_pagamento, status) "
                        + "VALUES (?, ?, ?, ?)",
                membro.getId(), BigDecimal.ZERO, LocalDate.now(), "PAGO"));
    }

    @Test
    void constraintDoBancoRejeitaStatusDiferenteDePago() {
        Membro membro = membroParaTeste();

        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO pagamentos (membro_id, valor, data_pagamento, status) "
                        + "VALUES (?, ?, ?, ?)",
                membro.getId(), new BigDecimal("10.00"), LocalDate.now(), "PEND"));
    }

    @Test
    void chaveEstrangeiraRejeitaMembroInexistente() {
        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "INSERT INTO pagamentos (membro_id, valor, data_pagamento, status) "
                        + "VALUES (?, ?, ?, ?)",
                -1L, new BigDecimal("10.00"), LocalDate.now(), "PAGO"));
    }

    private Membro membroParaTeste() {
        return membroRepository.findAll().stream()
                .findFirst()
                .orElseGet(this::criarMembroTransitorio);
    }

    private Membro criarMembroTransitorio() {
        Membro membro = new Membro();
        membro.setNome("Membro de integração");
        membro.setCpf(cpfDisponivel());
        membro.setTelefone("(48) 99999-0000");
        membro.setEndereco("Rua de teste, 1");
        return membroRepository.saveAndFlush(membro);
    }

    private String cpfDisponivel() {
        for (int tentativa = 0; tentativa < 100; tentativa++) {
            String base = String.format("%09d",
                    ThreadLocalRandom.current().nextInt(100_000_000, 1_000_000_000));
            int primeiroDigito = calcularDigito(base, 10);
            int segundoDigito = calcularDigito(base + primeiroDigito, 11);
            String cpf = base + primeiroDigito + segundoDigito;
            if (CpfUtils.ehValido(cpf) && !membroRepository.existsByCpf(cpf)) {
                return cpf;
            }
        }
        throw new IllegalStateException("Não foi possível gerar CPF de teste disponível.");
    }

    private int calcularDigito(String base, int pesoInicial) {
        int soma = 0;
        for (int indice = 0; indice < base.length(); indice++) {
            soma += (base.charAt(indice) - '0') * (pesoInicial - indice);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private Pagamento pagamentoValido() {
        Pagamento pagamento = new Pagamento();
        pagamento.setValor(new BigDecimal("125.50"));
        pagamento.setDataPagamento(LocalDate.of(2026, 11, 10));
        pagamento.setStatus("PAGO");
        return pagamento;
    }
}

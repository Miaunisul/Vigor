
package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.repository.HistoricoAtividadeRepository;
import br.edu.unisul.vigor.repository.MembroRepository;
import br.edu.unisul.vigor.repository.PagamentoRepository;
import br.edu.unisul.vigor.repository.TreinoRepository;
import br.edu.unisul.vigor.validation.CpfUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class MembroService {

    private final MembroRepository membroRepository;
    private final TreinoRepository treinoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final HistoricoAtividadeRepository historicoAtividadeRepository;
    private final Validator validator;

    private static final Set<String> CONSTRAINTS_VINCULO_MEMBRO = Set.of(
            "fk_treinos_membros",
            "fk_pagamentos_membros",
            "fk_historico_atividades_membros");

    public MembroService(
            MembroRepository membroRepository,
            TreinoRepository treinoRepository,
            PagamentoRepository pagamentoRepository,
            HistoricoAtividadeRepository historicoAtividadeRepository,
            Validator validator) {
        this.membroRepository = membroRepository;
        this.treinoRepository = treinoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.historicoAtividadeRepository = historicoAtividadeRepository;
        this.validator = validator;
    }

    @Transactional
    public Membro cadastrar(Membro membro) {
        membro.setNome(normalizarTexto(membro.getNome()));
        membro.setTelefone(normalizarTexto(membro.getTelefone()));
        membro.setEndereco(normalizarTexto(membro.getEndereco()));
        String cpfNormalizado = CpfUtils.normalizar(membro.getCpf());

        Set<ConstraintViolation<Membro>> violacoes = validator.validate(membro);
        if (!violacoes.isEmpty()) {
            throw new ConstraintViolationException(violacoes);
        }

        if (membroRepository.existsByCpf(cpfNormalizado)) {
            throw new IllegalArgumentException(
                "Já existe um membro cadastrado com este CPF."
            );
        }

        membro.setCpf(cpfNormalizado);
        return membroRepository.save(membro);
    }

    private String normalizarTexto(String valor) {
        return valor == null ? null : valor.strip();
    }

    @Transactional(readOnly = true)
    public List<Membro> listarTodos() {
        return membroRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Membro buscarPorId(Long id) {
        return membroRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                "Membro não encontrado."
            ));
    }

    @Transactional
    public void excluir(Long id) {
        Membro membro = membroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Membro não encontrado."));

        if (treinoRepository.existsByMembro_Id(id)) {
            throw new IllegalStateException(
                    "Não é possível excluir o membro: existem treinos vinculados.");
        }
        if (pagamentoRepository.existsByMembro_Id(id)) {
            throw new IllegalStateException(
                    "Não é possível excluir o membro: existem pagamentos vinculados.");
        }
        if (historicoAtividadeRepository.existsByMembro_Id(id)) {
            throw new IllegalStateException(
                    "Não é possível excluir o membro: existem atividades no histórico.");
        }

        try {
            membroRepository.delete(membro);
            membroRepository.flush();
        } catch (DataIntegrityViolationException excecao) {
            if (!ehViolacaoDeVinculo(excecao)) {
                throw excecao;
            }
            throw new IllegalStateException(
                    "Não é possível excluir o membro porque existem registros vinculados.",
                    excecao);
        }
    }

    private boolean ehViolacaoDeVinculo(Throwable excecao) {
        for (Throwable causa = excecao; causa != null; causa = causa.getCause()) {
            if (causa instanceof org.hibernate.exception.ConstraintViolationException violacao
                    && violacao.getConstraintName() != null
                    && CONSTRAINTS_VINCULO_MEMBRO.contains(
                            violacao.getConstraintName().replace("\"", "").replace("`", ""))) {
                return true;
            }
            if (causa instanceof SQLException sql
                    && "23503".equals(sql.getSQLState())
                    && sql.getMessage() != null
                    && CONSTRAINTS_VINCULO_MEMBRO.stream().anyMatch(
                            nome -> sql.getMessage().toLowerCase(Locale.ROOT).contains(nome))) {
                return true;
            }
        }
        return false;
    }
}

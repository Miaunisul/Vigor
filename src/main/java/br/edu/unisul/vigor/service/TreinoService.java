package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.model.Treino;
import br.edu.unisul.vigor.repository.MembroRepository;
import br.edu.unisul.vigor.repository.TreinoRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class TreinoService {

    private final TreinoRepository treinoRepository;
    private final MembroRepository membroRepository;
    private final Validator validator;

    public TreinoService(
            TreinoRepository treinoRepository,
            MembroRepository membroRepository,
            Validator validator) {
        this.treinoRepository = treinoRepository;
        this.membroRepository = membroRepository;
        this.validator = validator;
    }

    @Transactional
    public Treino cadastrar(Treino treino, Long membroId) {
        if (treino == null) {
            throw new IllegalArgumentException("Os dados do treino são obrigatórios.");
        }
        if (membroId == null) {
            throw new IllegalArgumentException("O membro é obrigatório.");
        }

        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new IllegalArgumentException("Membro não encontrado."));

        treino.setMembro(membro);
        treino.setTipo(normalizarTexto(treino.getTipo()));
        treino.setDescricao(normalizarTexto(treino.getDescricao()));

        Set<ConstraintViolation<Treino>> violacoes = validator.validate(treino);
        if (!violacoes.isEmpty()) {
            throw new ConstraintViolationException(violacoes);
        }

        return treinoRepository.save(treino);
    }

    @Transactional(readOnly = true)
    public List<Treino> consultarPorMembro(Long membroId) {
        if (membroId == null) {
            throw new IllegalArgumentException("O membro é obrigatório.");
        }
        if (!membroRepository.existsById(membroId)) {
            throw new IllegalArgumentException("Membro não encontrado.");
        }

        return treinoRepository.findByMembro_Id(membroId);
    }

    @Transactional(readOnly = true)
    public List<Treino> listarTodos() {
        return treinoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Treino buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Identificador do treino obrigatório.");
        }
        return treinoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Treino não encontrado."));
    }

    @Transactional
    public Treino atualizar(Long id, Treino dadosAtualizados, Long membroId) {
        if (dadosAtualizados == null) {
            throw new IllegalArgumentException("Os dados do treino são obrigatórios.");
        }
        if (membroId == null) {
            throw new IllegalArgumentException("O membro é obrigatório.");
        }

        Treino original = buscarPorId(id);
        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new IllegalArgumentException("Membro não encontrado."));

        original.setMembro(membro);
        original.setTipo(normalizarTexto(dadosAtualizados.getTipo()));
        original.setDescricao(normalizarTexto(dadosAtualizados.getDescricao()));
        original.setDuracaoMinutos(dadosAtualizados.getDuracaoMinutos());
        original.setDataInicio(dadosAtualizados.getDataInicio());

        Set<ConstraintViolation<Treino>> violacoes = validator.validate(original);
        if (!violacoes.isEmpty()) {
            throw new ConstraintViolationException(violacoes);
        }

        return treinoRepository.saveAndFlush(original);
    }


    private String normalizarTexto(String valor) {
        return valor == null ? null : valor.strip();
    }
}

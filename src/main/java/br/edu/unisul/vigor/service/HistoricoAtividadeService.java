package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.model.Funcionario;
import br.edu.unisul.vigor.model.HistoricoAtividade;
import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.repository.FuncionarioRepository;
import br.edu.unisul.vigor.repository.HistoricoAtividadeRepository;
import br.edu.unisul.vigor.repository.MembroRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class HistoricoAtividadeService {

    private final HistoricoAtividadeRepository historicoAtividadeRepository;
    private final MembroRepository membroRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final Validator validator;

    public HistoricoAtividadeService(
            HistoricoAtividadeRepository historicoAtividadeRepository,
            MembroRepository membroRepository,
            FuncionarioRepository funcionarioRepository,
            Validator validator) {
        this.historicoAtividadeRepository = historicoAtividadeRepository;
        this.membroRepository = membroRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.validator = validator;
    }

    @Transactional
    public HistoricoAtividade registrar(
            HistoricoAtividade historicoAtividade, Long membroId, Long funcionarioResponsavelId) {
        if (historicoAtividade == null) {
            throw new IllegalArgumentException("Os dados da atividade são obrigatórios.");
        }
        if (membroId == null) {
            throw new IllegalArgumentException("O membro é obrigatório.");
        }
        if (funcionarioResponsavelId == null) {
            throw new IllegalArgumentException("O funcionário responsável é obrigatório.");
        }

        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new IllegalArgumentException("Membro não encontrado."));
        Funcionario funcionario = funcionarioRepository.findById(funcionarioResponsavelId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Funcionário responsável não encontrado."));

        historicoAtividade.setMembro(membro);
        historicoAtividade.setFuncionarioResponsavel(funcionario);

        Set<ConstraintViolation<HistoricoAtividade>> violacoes =
                validator.validate(historicoAtividade);
        if (!violacoes.isEmpty()) {
            throw new ConstraintViolationException(violacoes);
        }

        return historicoAtividadeRepository.save(historicoAtividade);
    }

    @Transactional(readOnly = true)
    public List<HistoricoAtividade> consultarPorMembro(Long membroId) {
        if (membroId == null) {
            throw new IllegalArgumentException("O membro é obrigatório.");
        }
        if (!membroRepository.existsById(membroId)) {
            throw new IllegalArgumentException("Membro não encontrado.");
        }
        return historicoAtividadeRepository.findByMembro_Id(membroId);
    }

    @Transactional(readOnly = true)
    public List<HistoricoAtividade> consultarPorFuncionario(Long funcionarioId) {
        if (funcionarioId == null) {
            throw new IllegalArgumentException("O funcionário é obrigatório.");
        }
        if (!funcionarioRepository.existsById(funcionarioId)) {
            throw new IllegalArgumentException("Funcionário não encontrado.");
        }
        return historicoAtividadeRepository.findByFuncionarioResponsavel_Id(funcionarioId);
    }
}

package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.model.Funcionario;
import br.edu.unisul.vigor.model.HistoricoAtividade;
import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.repository.FuncionarioRepository;
import br.edu.unisul.vigor.repository.HistoricoAtividadeRepository;
import br.edu.unisul.vigor.repository.MembroRepository;
import br.edu.unisul.vigor.validation.CpfUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
        return historicoAtividadeRepository
                .findByMembro_IdOrderByDataAtividadeDescIdDesc(membroId);
    }

    @Transactional(readOnly = true)
    public List<HistoricoAtividade> consultar(
            Long membroId, String busca, LocalDate dataInicial, LocalDate dataFinal) {
        if (membroId != null && (membroId <= 0 || !membroRepository.existsById(membroId))) {
            throw new IllegalArgumentException("Membro não encontrado.");
        }
        if (dataInicial != null && dataFinal != null && dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException(
                    "A data inicial não pode ser posterior à data final.");
        }

        String termo = busca == null ? "" : busca.strip();
        String cpfNormalizado = CpfUtils.normalizar(termo);
        String cpf = cpfNormalizado == null ? "" : cpfNormalizado.replaceAll("\\D", "");
        String termoMinusculo = termo.toLowerCase(Locale.ROOT);
        Specification<HistoricoAtividade> filtros = (root, query, construtor) -> {
            List<Predicate> predicados = new ArrayList<>();
            Join<HistoricoAtividade, Membro> membro = null;
            if (membroId != null || !termoMinusculo.isEmpty()) {
                membro = root.join("membro", JoinType.INNER);
            }

            if (membroId != null) {
                predicados.add(construtor.equal(membro.get("id"), membroId));
            }
            if (!termoMinusculo.isEmpty()) {
                List<Predicate> correspondencias = new ArrayList<>();
                correspondencias.add(construtor.like(
                        construtor.lower(membro.<String>get("nome")),
                        "%" + termoMinusculo + "%"));
                if (!cpf.isEmpty()) {
                    correspondencias.add(construtor.like(
                            membro.<String>get("cpf"), "%" + cpf + "%"));
                }
                predicados.add(construtor.or(correspondencias.toArray(Predicate[]::new)));
            }
            if (dataInicial != null) {
                predicados.add(construtor.greaterThanOrEqualTo(
                        root.<LocalDate>get("dataAtividade"), dataInicial));
            }
            if (dataFinal != null) {
                predicados.add(construtor.lessThanOrEqualTo(
                        root.<LocalDate>get("dataAtividade"), dataFinal));
            }

            query.orderBy(
                    construtor.desc(root.get("dataAtividade")),
                    construtor.desc(root.get("id")));
            return construtor.and(predicados.toArray(Predicate[]::new));
        };
        return historicoAtividadeRepository.findAll(filtros);
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

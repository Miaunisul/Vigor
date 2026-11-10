package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.model.Pagamento;
import br.edu.unisul.vigor.repository.MembroRepository;
import br.edu.unisul.vigor.repository.PagamentoRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final MembroRepository membroRepository;
    private final Validator validator;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            MembroRepository membroRepository,
            Validator validator) {
        this.pagamentoRepository = pagamentoRepository;
        this.membroRepository = membroRepository;
        this.validator = validator;
    }

    @Transactional
    public Pagamento registrar(Pagamento pagamento, Long membroId) {
        if (pagamento == null) {
            throw new IllegalArgumentException("Os dados do pagamento são obrigatórios.");
        }
        if (membroId == null) {
            throw new IllegalArgumentException("O membro é obrigatório.");
        }

        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new IllegalArgumentException("Membro não encontrado."));

        pagamento.setMembro(membro);
        Set<ConstraintViolation<Pagamento>> violacoes = validator.validate(pagamento);
        if (!violacoes.isEmpty()) {
            throw new ConstraintViolationException(violacoes);
        }

        return pagamentoRepository.save(pagamento);
    }

    @Transactional(readOnly = true)
    public List<Pagamento> consultarPorMembro(Long membroId) {
        if (membroId == null) {
            throw new IllegalArgumentException("O membro é obrigatório.");
        }
        if (!membroRepository.existsById(membroId)) {
            throw new IllegalArgumentException("Membro não encontrado.");
        }

        return pagamentoRepository.findByMembro_Id(membroId);
    }
}

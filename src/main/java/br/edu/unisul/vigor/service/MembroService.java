
package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.repository.MembroRepository;
import br.edu.unisul.vigor.validation.CpfUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class MembroService {

    private final MembroRepository membroRepository;
    private final Validator validator;

    public MembroService(MembroRepository membroRepository, Validator validator) {
        this.membroRepository = membroRepository;
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
}

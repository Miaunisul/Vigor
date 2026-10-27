
package br.edu.unisul.vigor.service;

import br.edu.unisul.vigor.model.Membro;
import br.edu.unisul.vigor.repository.MembroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MembroService {

    private final MembroRepository membroRepository;

    public MembroService(MembroRepository membroRepository) {
        this.membroRepository = membroRepository;
    }

    @Transactional
    public Membro cadastrar(Membro membro) {
        if (membroRepository.existsByCpf(membro.getCpf())) {
            throw new IllegalArgumentException(
                "Já existe um membro cadastrado com este CPF."
            );
        }

        return membroRepository.save(membro);
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

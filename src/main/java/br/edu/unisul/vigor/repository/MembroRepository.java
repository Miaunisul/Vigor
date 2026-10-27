
package br.edu.unisul.vigor.repository;

import br.edu.unisul.vigor.model.Membro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembroRepository extends JpaRepository<Membro, Long> {

    Optional<Membro> findByCpf(String cpf);

    boolean existsByCpf(String cpf);
}

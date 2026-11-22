package br.edu.unisul.vigor.repository;

import br.edu.unisul.vigor.model.Treino;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TreinoRepository extends JpaRepository<Treino, Long> {

    @EntityGraph(attributePaths = "membro")
    @Override
    List<Treino> findAll();

    @EntityGraph(attributePaths = "membro")
    @Override
    Optional<Treino> findById(Long id);

    @EntityGraph(attributePaths = "membro")
    List<Treino> findByMembro_Id(Long membroId);

    boolean existsByMembro_Id(Long membroId);
}

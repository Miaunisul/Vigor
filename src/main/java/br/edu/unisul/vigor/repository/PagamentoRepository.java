package br.edu.unisul.vigor.repository;

import br.edu.unisul.vigor.model.Pagamento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    @EntityGraph(attributePaths = "membro")
    List<Pagamento> findByMembro_Id(Long membroId);

    boolean existsByMembro_Id(Long membroId);
}

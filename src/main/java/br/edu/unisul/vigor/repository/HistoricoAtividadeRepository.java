package br.edu.unisul.vigor.repository;

import br.edu.unisul.vigor.model.HistoricoAtividade;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoAtividadeRepository extends JpaRepository<HistoricoAtividade, Long> {

    @EntityGraph(attributePaths = {"membro", "funcionarioResponsavel"})
    List<HistoricoAtividade> findByMembro_Id(Long membroId);

    @EntityGraph(attributePaths = {"membro", "funcionarioResponsavel"})
    List<HistoricoAtividade> findByFuncionarioResponsavel_Id(Long funcionarioId);
}

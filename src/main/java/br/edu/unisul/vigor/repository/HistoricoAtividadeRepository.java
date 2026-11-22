package br.edu.unisul.vigor.repository;

import br.edu.unisul.vigor.model.HistoricoAtividade;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface HistoricoAtividadeRepository extends
        JpaRepository<HistoricoAtividade, Long>, JpaSpecificationExecutor<HistoricoAtividade> {

    @EntityGraph(attributePaths = {"membro", "funcionarioResponsavel"})
    List<HistoricoAtividade> findByMembro_Id(Long membroId);

    @EntityGraph(attributePaths = {"membro", "funcionarioResponsavel"})
    List<HistoricoAtividade> findByMembro_IdOrderByDataAtividadeDescIdDesc(Long membroId);

    @EntityGraph(attributePaths = {"membro", "funcionarioResponsavel"})
    List<HistoricoAtividade> findByFuncionarioResponsavel_Id(Long funcionarioId);

    @Override
    @EntityGraph(attributePaths = {"membro", "funcionarioResponsavel"})
    List<HistoricoAtividade> findAll(Specification<HistoricoAtividade> specification);

    boolean existsByMembro_Id(Long membroId);
}

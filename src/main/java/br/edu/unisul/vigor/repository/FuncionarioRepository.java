package br.edu.unisul.vigor.repository;

import br.edu.unisul.vigor.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    @Query(value = """
            SELECT *
            FROM funcionarios
            WHERE lower(btrim(login)) = lower(btrim(:login))
            """, nativeQuery = true)
    Optional<Funcionario> findByLoginNormalizado(@Param("login") String login);
}

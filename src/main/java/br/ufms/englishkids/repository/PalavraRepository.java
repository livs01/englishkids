package br.ufms.englishkids.repository;

import br.ufms.englishkids.model.Palavra;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PalavraRepository extends JpaRepository<Palavra, Long> {

    boolean existsByTemaId(Long temaId);

    boolean existsByInglesIgnoreCaseAndTemaId(String ingles, Long temaId);

    boolean existsByInglesIgnoreCaseAndTemaIdAndIdNot(String ingles, Long temaId, Long id);

    @EntityGraph(attributePaths = "tema")
    List<Palavra> findByTemaIdOrderByInglesAsc(Long temaId);

    /**
     * Busca paginada por palavra (inglês ou português) ou nome do tema (inglês ou português),
     * sem diferenciar maiúsculas/minúsculas. Termo vazio retorna tudo.
     * O @EntityGraph carrega o tema na mesma consulta (evita N+1).
     */
    @EntityGraph(attributePaths = "tema")
    @Query("""
            select p from Palavra p
            where lower(p.ingles) like lower(concat('%', :termo, '%'))
               or lower(p.portugues) like lower(concat('%', :termo, '%'))
               or lower(p.tema.nomeIngles) like lower(concat('%', :termo, '%'))
               or lower(p.tema.nomePortugues) like lower(concat('%', :termo, '%'))
            """)
    Page<Palavra> buscar(@Param("termo") String termo, Pageable pageable);
}

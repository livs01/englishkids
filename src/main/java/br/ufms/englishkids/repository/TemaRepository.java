package br.ufms.englishkids.repository;

import br.ufms.englishkids.model.Tema;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemaRepository extends JpaRepository<Tema, Long> {

    List<Tema> findAllByOrderByNomeInglesAsc();

    boolean existsByNomeInglesIgnoreCase(String nomeIngles);

    boolean existsByNomeInglesIgnoreCaseAndIdNot(String nomeIngles, Long id);
}

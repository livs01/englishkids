package br.ufms.englishkids.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.ufms.englishkids.model.Nivel;
import br.ufms.englishkids.model.Palavra;
import br.ufms.englishkids.model.Tema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/** Usa H2 em ARQUIVO (arquivo próprio, isolado dos demais testes) e dispensa o banco embutido em memória. */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:file:./target/test-data/englishkids-repo-test")
class PalavraRepositoryTest {

    @Autowired
    private PalavraRepository palavraRepository;

    @Autowired
    private TemaRepository temaRepository;

    private Tema animais;

    @BeforeEach
    void setUp() {
        palavraRepository.deleteAll();
        temaRepository.deleteAll();

        animais = temaRepository.save(new Tema("Animals", "Animais", "🐾", null));
        Tema cores = temaRepository.save(new Tema("Colors", "Cores", "🎨", null));

        palavraRepository.save(palavra("Dog", "cachorro", animais));
        palavraRepository.save(palavra("Red", "vermelho", cores));
    }

    private static Palavra palavra(String ingles, String portugues, Tema tema) {
        Palavra p = new Palavra();
        p.setIngles(ingles);
        p.setPortugues(portugues);
        p.setNivel(Nivel.INICIANTE);
        p.setTema(tema);
        return p;
    }

    @Test
    @DisplayName("buscar: termo vazio retorna todas as palavras")
    void buscarTodas() {
        Page<Palavra> pagina = palavraRepository.buscar("", PageRequest.of(0, 10));
        assertEquals(2, pagina.getTotalElements());
    }

    @Test
    @DisplayName("buscar: filtra pela palavra em inglês sem diferenciar maiúsculas")
    void buscarPorIngles() {
        Page<Palavra> pagina = palavraRepository.buscar("DOG", PageRequest.of(0, 10));
        assertEquals(1, pagina.getTotalElements());
        assertEquals("Dog", pagina.getContent().get(0).getIngles());
    }

    @Test
    @DisplayName("buscar: filtra pela tradução em português")
    void buscarPorPortugues() {
        Page<Palavra> pagina = palavraRepository.buscar("vermelh", PageRequest.of(0, 10));
        assertEquals(1, pagina.getTotalElements());
        assertEquals("Red", pagina.getContent().get(0).getIngles());
    }

    @Test
    @DisplayName("buscar: filtra pelo nome do tema (português)")
    void buscarPorTema() {
        Page<Palavra> pagina = palavraRepository.buscar("animais", PageRequest.of(0, 10));
        assertEquals(1, pagina.getTotalElements());
    }

    @Test
    @DisplayName("existsBy...: unicidade da palavra dentro do tema")
    void unicidadeNoTema() {
        Palavra dog = palavraRepository.findByTemaIdOrderByInglesAsc(animais.getId()).get(0);

        assertTrue(palavraRepository.existsByInglesIgnoreCaseAndTemaId("dog", animais.getId()));
        assertFalse(palavraRepository.existsByInglesIgnoreCaseAndTemaId("cat", animais.getId()));
        assertFalse(palavraRepository.existsByInglesIgnoreCaseAndTemaIdAndIdNot("dog", animais.getId(), dog.getId()));
        assertTrue(palavraRepository.existsByTemaId(animais.getId()));
    }
}

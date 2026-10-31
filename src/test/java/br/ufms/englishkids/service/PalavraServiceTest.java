package br.ufms.englishkids.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.ufms.englishkids.dto.PalavraForm;
import br.ufms.englishkids.dto.PalavraResumo;
import br.ufms.englishkids.exception.RecursoNaoEncontradoException;
import br.ufms.englishkids.exception.RegraNegocioException;
import br.ufms.englishkids.model.Nivel;
import br.ufms.englishkids.model.Palavra;
import br.ufms.englishkids.model.Tema;
import br.ufms.englishkids.repository.PalavraRepository;
import br.ufms.englishkids.repository.TemaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PalavraServiceTest {

    @Mock
    private PalavraRepository palavraRepository;

    @Mock
    private TemaRepository temaRepository;

    @InjectMocks
    private PalavraService service;

    private Tema tema;

    @BeforeEach
    void setUp() {
        tema = new Tema("Animals", "Animais", "🐾", null);
        ReflectionTestUtils.setField(tema, "id", 1L);
    }

    private PalavraForm formValido(String ingles) {
        PalavraForm f = new PalavraForm();
        f.setIngles(ingles);
        f.setPortugues(" cachorro ");
        f.setPronuncia("dóg");
        f.setExemplo("  ");
        f.setNivel(Nivel.INICIANTE);
        f.setTemaId(1L);
        return f;
    }

    @Test
    @DisplayName("criar: salva a palavra associada ao tema, apara textos e anula opcionais vazios")
    void criarComSucesso() {
        when(palavraRepository.existsByInglesIgnoreCaseAndTemaId("Dog", 1L)).thenReturn(false);
        when(temaRepository.findById(1L)).thenReturn(Optional.of(tema));
        when(palavraRepository.save(any(Palavra.class))).thenAnswer(inv -> inv.getArgument(0));

        PalavraResumo resultado = service.criar(formValido("  Dog "));

        assertEquals("Dog", resultado.ingles());
        assertEquals("cachorro", resultado.portugues());
        assertEquals(null, resultado.exemplo());
        assertEquals("Animals", resultado.temaNomeIngles());
        assertEquals(Nivel.INICIANTE, resultado.nivel());
    }

    @Test
    @DisplayName("criar: palavra repetida no mesmo tema lança RegraNegocioException")
    void criarDuplicada() {
        when(palavraRepository.existsByInglesIgnoreCaseAndTemaId("Dog", 1L)).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> service.criar(formValido("Dog")));
        verify(palavraRepository, never()).save(any());
    }

    @Test
    @DisplayName("criar: tema inexistente lança RecursoNaoEncontradoException")
    void criarTemaInexistente() {
        when(palavraRepository.existsByInglesIgnoreCaseAndTemaId("Dog", 1L)).thenReturn(false);
        when(temaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.criar(formValido("Dog")));
        verify(palavraRepository, never()).save(any());
    }

    @Test
    @DisplayName("atualizar: palavra repetida em outro registro do mesmo tema é rejeitada")
    void atualizarDuplicada() {
        Palavra existente = new Palavra();
        ReflectionTestUtils.setField(existente, "id", 10L);
        when(palavraRepository.findById(10L)).thenReturn(Optional.of(existente));
        when(palavraRepository.existsByInglesIgnoreCaseAndTemaIdAndIdNot("Dog", 1L, 10L)).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> service.atualizar(10L, formValido("Dog")));
        verify(palavraRepository, never()).save(any());
    }

    @Test
    @DisplayName("excluir: palavra inexistente lança RecursoNaoEncontradoException")
    void excluirInexistente() {
        when(palavraRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.excluir(99L));
    }

    @Test
    @DisplayName("excluir: palavra existente é removida")
    void excluirExistente() {
        Palavra palavra = new Palavra();
        when(palavraRepository.findById(10L)).thenReturn(Optional.of(palavra));

        service.excluir(10L);

        verify(palavraRepository).delete(palavra);
    }

    @Test
    @DisplayName("listarPorTema: tema inexistente lança RecursoNaoEncontradoException")
    void listarPorTemaInexistente() {
        when(temaRepository.existsById(5L)).thenReturn(false);

        assertThrows(RecursoNaoEncontradoException.class, () -> service.listarPorTema(5L));
    }

    @Test
    @DisplayName("buscar: termo nulo vira vazio e o resultado é convertido em DTO")
    void buscarComTermoNulo() {
        Palavra palavra = new Palavra();
        palavra.setIngles("Dog");
        palavra.setPortugues("cachorro");
        palavra.setNivel(Nivel.INICIANTE);
        palavra.setTema(tema);
        Pageable pageable = PageRequest.of(0, 10);
        when(palavraRepository.buscar(eq(""), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(palavra), pageable, 1));

        Page<PalavraResumo> pagina = service.buscar(null, pageable);

        assertEquals(1, pagina.getTotalElements());
        assertEquals("Dog", pagina.getContent().get(0).ingles());
    }
}

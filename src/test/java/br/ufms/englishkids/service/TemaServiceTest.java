package br.ufms.englishkids.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.ufms.englishkids.dto.TemaForm;
import br.ufms.englishkids.dto.TemaResumo;
import br.ufms.englishkids.exception.RecursoNaoEncontradoException;
import br.ufms.englishkids.exception.RegraNegocioException;
import br.ufms.englishkids.model.Tema;
import br.ufms.englishkids.repository.PalavraRepository;
import br.ufms.englishkids.repository.TemaRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TemaServiceTest {

    @Mock
    private TemaRepository temaRepository;

    @Mock
    private PalavraRepository palavraRepository;

    @InjectMocks
    private TemaService service;

    private static TemaForm form(String ingles, String portugues) {
        TemaForm f = new TemaForm();
        f.setNomeIngles(ingles);
        f.setNomePortugues(portugues);
        f.setEmoji("  ");
        f.setDescricao("  ");
        return f;
    }

    private static Tema temaComId(Long id, String nome) {
        Tema t = new Tema(nome, "Nome PT", null, null);
        ReflectionTestUtils.setField(t, "id", id);
        return t;
    }

    @Test
    @DisplayName("criar: apara os nomes e converte campos opcionais vazios em nulo")
    void criarSalva() {
        when(temaRepository.existsByNomeInglesIgnoreCase("Animals")).thenReturn(false);
        when(temaRepository.save(any(Tema.class))).thenAnswer(inv -> inv.getArgument(0));

        TemaResumo resultado = service.criar(form("  Animals ", " Animais "));

        assertEquals("Animals", resultado.nomeIngles());
        assertEquals("Animais", resultado.nomePortugues());
        assertEquals(null, resultado.emoji());
        assertEquals(null, resultado.descricao());
    }

    @Test
    @DisplayName("criar: nome em inglês duplicado lança RegraNegocioException")
    void criarDuplicado() {
        when(temaRepository.existsByNomeInglesIgnoreCase("Animals")).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> service.criar(form("Animals", "Animais")));
        verify(temaRepository, never()).save(any());
    }

    @Test
    @DisplayName("atualizar: nome pertencente a outro tema é rejeitado")
    void atualizarDuplicado() {
        when(temaRepository.findById(1L)).thenReturn(Optional.of(temaComId(1L, "Colors")));
        when(temaRepository.existsByNomeInglesIgnoreCaseAndIdNot("Animals", 1L)).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> service.atualizar(1L, form("Animals", "Animais")));
        verify(temaRepository, never()).save(any());
    }

    @Test
    @DisplayName("buscarPorId: inexistente lança RecursoNaoEncontradoException")
    void buscarInexistente() {
        when(temaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    @DisplayName("excluir: tema com palavras associadas é bloqueado")
    void excluirComPalavras() {
        Tema tema = temaComId(1L, "Animals");
        when(temaRepository.findById(1L)).thenReturn(Optional.of(tema));
        when(palavraRepository.existsByTemaId(1L)).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> service.excluir(1L));
        verify(temaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("excluir: tema sem palavras é removido")
    void excluirSemPalavras() {
        Tema tema = temaComId(1L, "Animals");
        when(temaRepository.findById(1L)).thenReturn(Optional.of(tema));
        when(palavraRepository.existsByTemaId(1L)).thenReturn(false);

        service.excluir(1L);

        verify(temaRepository).delete(tema);
    }
}

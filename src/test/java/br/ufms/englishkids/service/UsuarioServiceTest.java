package br.ufms.englishkids.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.ufms.englishkids.dto.UsuarioForm;
import br.ufms.englishkids.dto.UsuarioResumo;
import br.ufms.englishkids.exception.RecursoNaoEncontradoException;
import br.ufms.englishkids.exception.RegraNegocioException;
import br.ufms.englishkids.model.Perfil;
import br.ufms.englishkids.model.Usuario;
import br.ufms.englishkids.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    private static UsuarioForm form(String username) {
        UsuarioForm f = new UsuarioForm();
        f.setNome(" Maria ");
        f.setUsername(username);
        f.setSenha("segredo1");
        f.setConfirmacaoSenha("segredo1");
        f.setPerfil(Perfil.USER);
        return f;
    }

    private static Usuario usuario(Long id, String username, Perfil perfil) {
        Usuario u = new Usuario("Fulano", username, "hash", perfil);
        ReflectionTestUtils.setField(u, "id", id);
        return u;
    }

    @Test
    @DisplayName("criar: grava a senha como hash e apara o nome")
    void criarComSucesso() {
        when(usuarioRepository.existsByUsername("maria")).thenReturn(false);
        when(passwordEncoder.encode("segredo1")).thenReturn("HASH");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResumo resultado = service.criar(form("maria"));

        assertEquals("Maria", resultado.nome());
        assertEquals("maria", resultado.username());
        assertEquals(Perfil.USER, resultado.perfil());
        verify(passwordEncoder).encode("segredo1");
    }

    @Test
    @DisplayName("criar: login duplicado lança RegraNegocioException")
    void criarLoginDuplicado() {
        when(usuarioRepository.existsByUsername("maria")).thenReturn(true);

        assertThrows(RegraNegocioException.class, () -> service.criar(form("maria")));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("alternarAtivo: não permite alterar a própria conta")
    void alternarAtivoPropriaConta() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, "admin", Perfil.ADMIN)));

        assertThrows(RegraNegocioException.class, () -> service.alternarAtivo(1L, "admin"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("alternarAtivo: desativa outro usuário")
    void alternarAtivoOutroUsuario() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario(2L, "user", Perfil.USER)));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResumo resultado = service.alternarAtivo(2L, "admin");

        assertFalse(resultado.ativo());
    }

    @Test
    @DisplayName("alterarPerfil: não permite alterar o próprio perfil")
    void alterarPerfilProprio() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L, "admin", Perfil.ADMIN)));

        assertThrows(RegraNegocioException.class, () -> service.alterarPerfil(1L, Perfil.USER, "admin"));
    }

    @Test
    @DisplayName("alterarPerfil: promove outro usuário a administrador")
    void alterarPerfilOutroUsuario() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario(2L, "user", Perfil.USER)));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResumo resultado = service.alterarPerfil(2L, Perfil.ADMIN, "admin");

        assertEquals(Perfil.ADMIN, resultado.perfil());
    }

    @Test
    @DisplayName("alternarAtivo: usuário inexistente lança RecursoNaoEncontradoException")
    void usuarioInexistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> service.alternarAtivo(99L, "admin"));
    }
}

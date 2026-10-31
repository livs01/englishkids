package br.ufms.englishkids.service;

import br.ufms.englishkids.dto.UsuarioForm;
import br.ufms.englishkids.dto.UsuarioResumo;
import br.ufms.englishkids.exception.RecursoNaoEncontradoException;
import br.ufms.englishkids.exception.RegraNegocioException;
import br.ufms.englishkids.model.Perfil;
import br.ufms.englishkids.model.Usuario;
import br.ufms.englishkids.repository.UsuarioRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Gestão (moderação) de usuários pelo administrador. */
@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResumo> listar() {
        return usuarioRepository.findAll(Sort.by("username")).stream()
                .map(UsuarioResumo::from)
                .toList();
    }

    @Transactional
    public UsuarioResumo criar(UsuarioForm form) {
        String username = form.getUsername().trim();
        if (usuarioRepository.existsByUsername(username)) {
            throw new RegraNegocioException("Já existe um usuário com o login \"" + username + "\".");
        }
        Usuario usuario = new Usuario(
                form.getNome().trim(),
                username,
                passwordEncoder.encode(form.getSenha()),
                form.getPerfil());
        return UsuarioResumo.from(usuarioRepository.save(usuario));
    }

    /** Ativa/desativa a conta. Um administrador não pode alterar a própria conta. */
    @Transactional
    public UsuarioResumo alternarAtivo(Long id, String usernameLogado) {
        Usuario usuario = obter(id);
        if (usuario.getUsername().equals(usernameLogado)) {
            throw new RegraNegocioException("Você não pode ativar ou desativar a própria conta.");
        }
        usuario.setAtivo(!usuario.isAtivo());
        return UsuarioResumo.from(usuarioRepository.save(usuario));
    }

    /** Altera o perfil. Um administrador não pode alterar o próprio perfil. */
    @Transactional
    public UsuarioResumo alterarPerfil(Long id, Perfil perfil, String usernameLogado) {
        Usuario usuario = obter(id);
        if (usuario.getUsername().equals(usernameLogado)) {
            throw new RegraNegocioException("Você não pode alterar o próprio perfil.");
        }
        usuario.setPerfil(perfil);
        return UsuarioResumo.from(usuarioRepository.save(usuario));
    }

    private Usuario obter(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }
}

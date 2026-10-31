package br.ufms.englishkids.config;

import br.ufms.englishkids.model.Perfil;
import br.ufms.englishkids.model.Usuario;
import br.ufms.englishkids.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Cria os usuários iniciais (admin e user) caso ainda não existam. */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner seedUsuarios(UsuarioRepository repo,
                                   PasswordEncoder encoder,
                                   @Value("${app.seed.enabled:false}") boolean enabled,
                                   @Value("${app.seed.admin-password:admin123}") String adminPwd,
                                   @Value("${app.seed.user-password:user123}") String userPwd) {
        return args -> {
            if (!enabled) {
                return;
            }
            criarSeNaoExistir(repo, encoder, "Administrador", "admin", adminPwd, Perfil.ADMIN);
            criarSeNaoExistir(repo, encoder, "Usuário Comum", "user", userPwd, Perfil.USER);
        };
    }

    private void criarSeNaoExistir(UsuarioRepository repo, PasswordEncoder encoder,
                                   String nome, String username, String senha, Perfil perfil) {
        if (!repo.existsByUsername(username)) {
            repo.save(new Usuario(nome, username, encoder.encode(senha), perfil));
            log.info("Usuário inicial criado: {} ({})", username, perfil);
        }
    }
}

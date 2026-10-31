package br.ufms.englishkids.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Anônimo é redirecionado para /login")
    void anonimoRedirecionaParaLogin() throws Exception {
        mvc.perform(get("/"))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Página de login é pública")
    void loginEPublico() throws Exception {
        mvc.perform(get("/login")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Usuário comum acessa a home")
    void usuarioComumAcessaHome() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Usuário comum NÃO acessa /admin")
    void usuarioComumNaoAcessaAdmin() throws Exception {
        mvc.perform(get("/admin")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Login com credenciais válidas do admin redireciona para a home")
    void loginAdminValido() throws Exception {
        mvc.perform(post("/login").param("username", "admin").param("password", "admin123").with(csrf()))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("Login com senha errada volta para /login?error")
    void loginInvalido() throws Exception {
        mvc.perform(post("/login").param("username", "admin").param("password", "errada").with(csrf()))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/login?error"));
    }
}

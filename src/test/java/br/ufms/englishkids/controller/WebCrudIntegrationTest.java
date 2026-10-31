package br.ufms.englishkids.controller;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import br.ufms.englishkids.model.Tema;
import br.ufms.englishkids.repository.PalavraRepository;
import br.ufms.englishkids.repository.TemaRepository;
import br.ufms.englishkids.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Testes de integração da camada web: renderizam os templates reais, exercitam as regras
 * de perfil (USER x ADMIN) e as validações. Cada teste é revertido ao final (@Transactional).
 * Dependem dos dados de exemplo criados pelo ConteudoSeeder (profile "test").
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class WebCrudIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PalavraRepository palavraRepository;

    @Autowired
    private TemaRepository temaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /** Tema "Animals" (primeiro em ordem alfabética nos dados de exemplo). */
    private Tema temaAnimals() {
        return temaRepository.findAllByOrderByNomeInglesAsc().get(0);
    }

    private MockHttpServletRequestBuilder palavraValida(String ingles) {
        return post("/palavras")
                .param("ingles", ingles)
                .param("portugues", "cavalo")
                .param("nivel", "INICIANTE")
                .param("temaId", String.valueOf(temaAnimals().getId()))
                .with(csrf());
    }

    // ------------------------- Usuário comum -------------------------

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER lista palavras e vê o vocabulário de exemplo")
    void usuarioComumListaPalavras() throws Exception {
        mvc.perform(get("/palavras"))
           .andExpect(status().isOk())
           .andExpect(view().name("palavras/lista"))
           .andExpect(content().string(containsString("Dog")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER não vê botões de escrita na listagem")
    void usuarioComumNaoVeBotoesDeEscrita() throws Exception {
        String html = mvc.perform(get("/palavras")).andReturn().getResponse().getContentAsString();
        assertTrue(!html.contains("Nova palavra"), "Botão 'Nova palavra' não deve aparecer para USER");
        assertTrue(!html.contains("/editar"), "Links de edição não devem aparecer para USER");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER vê a lista de temas para praticar")
    void usuarioComumVeTemasParaPraticar() throws Exception {
        mvc.perform(get("/praticar"))
           .andExpect(status().isOk())
           .andExpect(view().name("praticar/temas"))
           .andExpect(content().string(containsString("Animals")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER estuda os cartões de um tema (tradução e botão de áudio)")
    void usuarioComumEstudaCartoes() throws Exception {
        mvc.perform(get("/praticar/" + temaAnimals().getId()))
           .andExpect(status().isOk())
           .andExpect(view().name("praticar/cartoes"))
           .andExpect(content().string(containsString("cachorro")))
           .andExpect(content().string(containsString("data-falar")))
           .andExpect(content().string(containsString("lang=\"en\"")));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER é barrado ao abrir o formulário de nova palavra")
    void usuarioComumNaoAbreFormulario() throws Exception {
        mvc.perform(get("/palavras/novo")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER é barrado ao tentar criar palavra (POST)")
    void usuarioComumNaoCria() throws Exception {
        mvc.perform(palavraValida("Horse")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER é barrado ao tentar excluir palavra (POST)")
    void usuarioComumNaoExclui() throws Exception {
        Long id = palavraRepository.findAll().get(0).getId();
        mvc.perform(post("/palavras/" + id + "/excluir").with(csrf())).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("USER não acessa o painel de administração")
    void usuarioComumNaoAcessaAdmin() throws Exception {
        mvc.perform(get("/admin")).andExpect(status().isForbidden());
        mvc.perform(get("/admin/usuarios")).andExpect(status().isForbidden());
    }

    // ------------------------- Administrador -------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN abre o formulário de nova palavra")
    void adminAbreFormulario() throws Exception {
        mvc.perform(get("/palavras/novo"))
           .andExpect(status().isOk())
           .andExpect(view().name("palavras/form"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN: formulário inválido volta com erros de validação")
    void adminFormularioInvalido() throws Exception {
        mvc.perform(post("/palavras").with(csrf()))
           .andExpect(status().isOk())
           .andExpect(view().name("palavras/form"))
           .andExpect(model().attributeHasFieldErrors("palavraForm", "ingles", "portugues", "nivel", "temaId"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN cria palavra e é redirecionado para a lista")
    void adminCriaPalavra() throws Exception {
        mvc.perform(palavraValida("Horse"))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/palavras"))
           .andExpect(flash().attributeExists("sucesso"));

        assertTrue(palavraRepository.existsByInglesIgnoreCaseAndTemaId("Horse", temaAnimals().getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN: palavra repetida no mesmo tema volta ao formulário com erro")
    void adminPalavraDuplicada() throws Exception {
        mvc.perform(palavraValida("Dog"))
           .andExpect(status().isOk())
           .andExpect(view().name("palavras/form"))
           .andExpect(model().attributeHasErrors("palavraForm"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN exclui uma palavra")
    void adminExcluiPalavra() throws Exception {
        Long id = palavraRepository.findAll().get(0).getId();
        mvc.perform(post("/palavras/" + id + "/excluir").with(csrf()))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/palavras"));

        assertTrue(palavraRepository.findById(id).isEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN: excluir tema com palavras é bloqueado com mensagem de erro")
    void adminNaoExcluiTemaComPalavras() throws Exception {
        mvc.perform(post("/temas/" + temaAnimals().getId() + "/excluir").with(csrf()))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/temas"))
           .andExpect(flash().attributeExists("erro"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN cria tema")
    void adminCriaTema() throws Exception {
        mvc.perform(post("/temas")
                   .param("nomeIngles", "Toys")
                   .param("nomePortugues", "Brinquedos")
                   .param("emoji", "🧸")
                   .with(csrf()))
           .andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrl("/temas"));

        assertTrue(temaRepository.existsByNomeInglesIgnoreCase("Toys"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("ADMIN acessa o painel e a lista de usuários")
    void adminAcessaPainel() throws Exception {
        mvc.perform(get("/admin")).andExpect(status().isOk()).andExpect(view().name("admin/painel"));
        mvc.perform(get("/admin/usuarios"))
           .andExpect(status().isOk())
           .andExpect(content().string(containsString("admin")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("ADMIN não consegue desativar a própria conta")
    void adminNaoDesativaASiMesmo() throws Exception {
        Long id = usuarioRepository.findByUsername("admin").orElseThrow().getId();
        mvc.perform(post("/admin/usuarios/" + id + "/ativo").with(csrf()))
           .andExpect(status().is3xxRedirection())
           .andExpect(flash().attributeExists("erro"));
    }

    // ------------------------- Erros -------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Editar palavra inexistente retorna 404 com a página de erro")
    void palavraInexistente() throws Exception {
        mvc.perform(get("/palavras/999999/editar"))
           .andExpect(status().isNotFound())
           .andExpect(view().name("error"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Praticar tema inexistente retorna 404")
    void temaInexistente() throws Exception {
        mvc.perform(get("/praticar/999999")).andExpect(status().isNotFound());
    }
}

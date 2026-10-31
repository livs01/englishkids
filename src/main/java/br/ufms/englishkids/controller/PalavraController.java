package br.ufms.englishkids.controller;

import br.ufms.englishkids.config.Permissoes;
import br.ufms.englishkids.dto.PalavraForm;
import br.ufms.englishkids.dto.PalavraResumo;
import br.ufms.englishkids.exception.RegraNegocioException;
import br.ufms.englishkids.model.Nivel;
import br.ufms.englishkids.service.PalavraService;
import br.ufms.englishkids.service.TemaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** CRUD de Palavras com busca e paginação. Leitura: qualquer usuário autenticado. Escrita: somente ADMIN. */
@Controller
@RequestMapping("/palavras")
public class PalavraController {

    private static final int TAMANHO_PAGINA = 10;
    private static final String VIEW_FORM = "palavras/form";
    private static final String REDIRECT_LISTA = "redirect:/palavras";

    private final PalavraService palavraService;
    private final TemaService temaService;

    public PalavraController(PalavraService palavraService, TemaService temaService) {
        this.palavraService = palavraService;
        this.temaService = temaService;
    }

    @ModelAttribute("secao")
    public String secao() {
        return "palavras";
    }

    @GetMapping
    public String listar(@RequestParam(name = "q", defaultValue = "") String q,
                         @RequestParam(name = "page", defaultValue = "0") int page,
                         Model model) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), TAMANHO_PAGINA, Sort.by("ingles").ascending());
        Page<PalavraResumo> pagina = palavraService.buscar(q, pageable);
        model.addAttribute("pagina", pagina);
        model.addAttribute("q", q);
        return "palavras/lista";
    }

    @GetMapping("/novo")
    @PreAuthorize(Permissoes.SOMENTE_ADMIN)
    public String novo(Model model) {
        PalavraForm form = new PalavraForm();
        form.setNivel(Nivel.INICIANTE);
        model.addAttribute("palavraForm", form);
        carregarOpcoes(model);
        return VIEW_FORM;
    }

    @PostMapping
    @PreAuthorize(Permissoes.SOMENTE_ADMIN)
    public String criar(@Valid @ModelAttribute("palavraForm") PalavraForm form,
                        BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                palavraService.criar(form);
                redirect.addFlashAttribute("sucesso", "Palavra cadastrada com sucesso.");
                return REDIRECT_LISTA;
            } catch (RegraNegocioException e) {
                result.reject("regraNegocio", e.getMessage());
            }
        }
        carregarOpcoes(model);
        return VIEW_FORM;
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize(Permissoes.SOMENTE_ADMIN)
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("palavraForm", PalavraForm.from(palavraService.buscarPorId(id)));
        model.addAttribute("itemId", id);
        carregarOpcoes(model);
        return VIEW_FORM;
    }

    @PostMapping("/{id}")
    @PreAuthorize(Permissoes.SOMENTE_ADMIN)
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("palavraForm") PalavraForm form,
                            BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                palavraService.atualizar(id, form);
                redirect.addFlashAttribute("sucesso", "Palavra atualizada com sucesso.");
                return REDIRECT_LISTA;
            } catch (RegraNegocioException e) {
                result.reject("regraNegocio", e.getMessage());
            }
        }
        model.addAttribute("itemId", id);
        carregarOpcoes(model);
        return VIEW_FORM;
    }

    @GetMapping("/{id}/excluir")
    @PreAuthorize(Permissoes.SOMENTE_ADMIN)
    public String confirmarExclusao(@PathVariable Long id, Model model) {
        PalavraResumo palavra = palavraService.buscarPorId(id);
        model.addAttribute("tituloPagina", "Excluir palavra");
        model.addAttribute("mensagem", "Deseja realmente excluir a palavra \"" + palavra.ingles() + "\"?");
        model.addAttribute("acao", "/palavras/" + id + "/excluir");
        model.addAttribute("voltar", "/palavras");
        return "confirmar-exclusao";
    }

    @PostMapping("/{id}/excluir")
    @PreAuthorize(Permissoes.SOMENTE_ADMIN)
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        palavraService.excluir(id);
        redirect.addFlashAttribute("sucesso", "Palavra excluída com sucesso.");
        return REDIRECT_LISTA;
    }

    private void carregarOpcoes(Model model) {
        model.addAttribute("temas", temaService.listar());
        model.addAttribute("niveis", Nivel.values());
    }
}

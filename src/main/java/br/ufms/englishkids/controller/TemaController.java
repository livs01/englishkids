package br.ufms.englishkids.controller;

import br.ufms.englishkids.config.Permissoes;
import br.ufms.englishkids.dto.TemaForm;
import br.ufms.englishkids.dto.TemaResumo;
import br.ufms.englishkids.exception.RegraNegocioException;
import br.ufms.englishkids.service.TemaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** CRUD de Temas. Leitura: qualquer usuário autenticado. Escrita: somente ADMIN. */
@Controller
@RequestMapping("/temas")
public class TemaController {

    private static final String VIEW_FORM = "temas/form";
    private static final String REDIRECT_LISTA = "redirect:/temas";

    private final TemaService temaService;

    public TemaController(TemaService temaService) {
        this.temaService = temaService;
    }

    @ModelAttribute("secao")
    public String secao() {
        return "temas";
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("temas", temaService.listar());
        return "temas/lista";
    }

    @GetMapping("/novo")
    @PreAuthorize(Permissoes.SOMENTE_ADMIN)
    public String novo(Model model) {
        model.addAttribute("temaForm", new TemaForm());
        return VIEW_FORM;
    }

    @PostMapping
    @PreAuthorize(Permissoes.SOMENTE_ADMIN)
    public String criar(@Valid @ModelAttribute("temaForm") TemaForm form,
                        BindingResult result, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                temaService.criar(form);
                redirect.addFlashAttribute("sucesso", "Tema cadastrado com sucesso.");
                return REDIRECT_LISTA;
            } catch (RegraNegocioException e) {
                result.reject("regraNegocio", e.getMessage());
            }
        }
        return VIEW_FORM;
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize(Permissoes.SOMENTE_ADMIN)
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("temaForm", TemaForm.from(temaService.buscarPorId(id)));
        model.addAttribute("itemId", id);
        return VIEW_FORM;
    }

    @PostMapping("/{id}")
    @PreAuthorize(Permissoes.SOMENTE_ADMIN)
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("temaForm") TemaForm form,
                            BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                temaService.atualizar(id, form);
                redirect.addFlashAttribute("sucesso", "Tema atualizado com sucesso.");
                return REDIRECT_LISTA;
            } catch (RegraNegocioException e) {
                result.reject("regraNegocio", e.getMessage());
            }
        }
        model.addAttribute("itemId", id);
        return VIEW_FORM;
    }

    @GetMapping("/{id}/excluir")
    @PreAuthorize(Permissoes.SOMENTE_ADMIN)
    public String confirmarExclusao(@PathVariable Long id, Model model) {
        TemaResumo tema = temaService.buscarPorId(id);
        model.addAttribute("tituloPagina", "Excluir tema");
        model.addAttribute("mensagem", "Deseja realmente excluir o tema \"" + tema.nomeIngles() + "\"?");
        model.addAttribute("acao", "/temas/" + id + "/excluir");
        model.addAttribute("voltar", "/temas");
        return "confirmar-exclusao";
    }

    @PostMapping("/{id}/excluir")
    @PreAuthorize(Permissoes.SOMENTE_ADMIN)
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            temaService.excluir(id);
            redirect.addFlashAttribute("sucesso", "Tema excluído com sucesso.");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return REDIRECT_LISTA;
    }
}

package br.ufms.englishkids.controller;

import br.ufms.englishkids.config.Permissoes;
import br.ufms.englishkids.dto.UsuarioForm;
import br.ufms.englishkids.dto.UsuarioResumo;
import br.ufms.englishkids.exception.RegraNegocioException;
import br.ufms.englishkids.model.Perfil;
import br.ufms.englishkids.service.UsuarioService;
import jakarta.validation.Valid;
import java.security.Principal;
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

/** Moderação de usuários (somente ADMIN). */
@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize(Permissoes.SOMENTE_ADMIN)
public class UsuarioAdminController {

    private static final String VIEW_FORM = "admin/usuario-form";
    private static final String REDIRECT_LISTA = "redirect:/admin/usuarios";

    private final UsuarioService usuarioService;

    public UsuarioAdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @ModelAttribute("secao")
    public String secao() {
        return "admin";
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "admin/usuarios";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        UsuarioForm form = new UsuarioForm();
        form.setPerfil(Perfil.USER);
        model.addAttribute("usuarioForm", form);
        model.addAttribute("perfis", Perfil.values());
        return VIEW_FORM;
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("usuarioForm") UsuarioForm form,
                        BindingResult result, Model model, RedirectAttributes redirect) {
        if (form.getSenha() != null && form.getConfirmacaoSenha() != null
                && !form.getSenha().equals(form.getConfirmacaoSenha())) {
            result.rejectValue("confirmacaoSenha", "senha.divergente", "As senhas não conferem.");
        }
        if (!result.hasErrors()) {
            try {
                UsuarioResumo criado = usuarioService.criar(form);
                redirect.addFlashAttribute("sucesso", "Usuário \"" + criado.username() + "\" cadastrado com sucesso.");
                return REDIRECT_LISTA;
            } catch (RegraNegocioException e) {
                result.reject("regraNegocio", e.getMessage());
            }
        }
        model.addAttribute("perfis", Perfil.values());
        return VIEW_FORM;
    }

    @PostMapping("/{id}/ativo")
    public String alternarAtivo(@PathVariable Long id, Principal principal, RedirectAttributes redirect) {
        try {
            UsuarioResumo u = usuarioService.alternarAtivo(id, principal.getName());
            redirect.addFlashAttribute("sucesso",
                    "Usuário \"" + u.username() + "\" " + (u.ativo() ? "ativado" : "desativado") + " com sucesso.");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return REDIRECT_LISTA;
    }

    @PostMapping("/{id}/perfil")
    public String alterarPerfil(@PathVariable Long id, @RequestParam Perfil perfil,
                                Principal principal, RedirectAttributes redirect) {
        try {
            UsuarioResumo u = usuarioService.alterarPerfil(id, perfil, principal.getName());
            redirect.addFlashAttribute("sucesso",
                    "Perfil de \"" + u.username() + "\" alterado para " + u.perfil().getRotulo() + ".");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return REDIRECT_LISTA;
    }
}

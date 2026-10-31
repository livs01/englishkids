package br.ufms.englishkids.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/** Painel administrativo. O acesso a /admin/** é restrito a ROLE_ADMIN em SecurityConfig. */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @ModelAttribute("secao")
    public String secao() {
        return "admin";
    }

    @GetMapping
    public String painel() {
        return "admin/painel";
    }
}

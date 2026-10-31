package br.ufms.englishkids.controller;

import br.ufms.englishkids.service.PalavraService;
import br.ufms.englishkids.service.TemaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/** Área de estudo (somente leitura): cartões de vocabulário por tema. Disponível para qualquer usuário autenticado. */
@Controller
@RequestMapping("/praticar")
public class PraticarController {

    private final TemaService temaService;
    private final PalavraService palavraService;

    public PraticarController(TemaService temaService, PalavraService palavraService) {
        this.temaService = temaService;
        this.palavraService = palavraService;
    }

    @ModelAttribute("secao")
    public String secao() {
        return "praticar";
    }

    @GetMapping
    public String temas(Model model) {
        model.addAttribute("temas", temaService.listar());
        return "praticar/temas";
    }

    @GetMapping("/{temaId}")
    public String cartoes(@PathVariable Long temaId, Model model) {
        model.addAttribute("tema", temaService.buscarPorId(temaId));
        model.addAttribute("palavras", palavraService.listarPorTema(temaId));
        return "praticar/cartoes";
    }
}

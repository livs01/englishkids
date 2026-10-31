package br.ufms.englishkids.service;

import br.ufms.englishkids.dto.PalavraForm;
import br.ufms.englishkids.dto.PalavraResumo;
import br.ufms.englishkids.exception.RecursoNaoEncontradoException;
import br.ufms.englishkids.exception.RegraNegocioException;
import br.ufms.englishkids.model.Palavra;
import br.ufms.englishkids.model.Tema;
import br.ufms.englishkids.repository.PalavraRepository;
import br.ufms.englishkids.repository.TemaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Regras de negócio de Palavra. */
@Service
@Transactional(readOnly = true)
public class PalavraService {

    private final PalavraRepository palavraRepository;
    private final TemaRepository temaRepository;

    public PalavraService(PalavraRepository palavraRepository, TemaRepository temaRepository) {
        this.palavraRepository = palavraRepository;
        this.temaRepository = temaRepository;
    }

    /** Busca paginada por palavra ou tema. Termo vazio lista todas. */
    public Page<PalavraResumo> buscar(String termo, Pageable pageable) {
        String termoLimpo = termo == null ? "" : termo.trim();
        return palavraRepository.buscar(termoLimpo, pageable).map(PalavraResumo::from);
    }

    public PalavraResumo buscarPorId(Long id) {
        return PalavraResumo.from(obter(id));
    }

    /** Todas as palavras de um tema, em ordem alfabética (usado nos cartões de estudo). */
    public List<PalavraResumo> listarPorTema(Long temaId) {
        if (!temaRepository.existsById(temaId)) {
            throw new RecursoNaoEncontradoException("Tema", temaId);
        }
        return palavraRepository.findByTemaIdOrderByInglesAsc(temaId).stream()
                .map(PalavraResumo::from)
                .toList();
    }

    @Transactional
    public PalavraResumo criar(PalavraForm form) {
        String ingles = form.getIngles().trim();
        if (palavraRepository.existsByInglesIgnoreCaseAndTemaId(ingles, form.getTemaId())) {
            throw new RegraNegocioException("A palavra \"" + ingles + "\" já está cadastrada neste tema.");
        }
        Palavra palavra = new Palavra();
        preencher(palavra, form, ingles);
        return PalavraResumo.from(palavraRepository.save(palavra));
    }

    @Transactional
    public PalavraResumo atualizar(Long id, PalavraForm form) {
        Palavra palavra = obter(id);
        String ingles = form.getIngles().trim();
        if (palavraRepository.existsByInglesIgnoreCaseAndTemaIdAndIdNot(ingles, form.getTemaId(), id)) {
            throw new RegraNegocioException("A palavra \"" + ingles + "\" já está cadastrada neste tema.");
        }
        preencher(palavra, form, ingles);
        return PalavraResumo.from(palavraRepository.save(palavra));
    }

    @Transactional
    public void excluir(Long id) {
        palavraRepository.delete(obter(id));
    }

    private Palavra obter(Long id) {
        return palavraRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Palavra", id));
    }

    private void preencher(Palavra palavra, PalavraForm form, String ingles) {
        Tema tema = temaRepository.findById(form.getTemaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tema", form.getTemaId()));

        palavra.setIngles(ingles);
        palavra.setPortugues(form.getPortugues().trim());
        palavra.setPronuncia(vazioParaNulo(form.getPronuncia()));
        palavra.setExemplo(vazioParaNulo(form.getExemplo()));
        palavra.setEmoji(vazioParaNulo(form.getEmoji()));
        palavra.setNivel(form.getNivel());
        palavra.setTema(tema);
    }

    private static String vazioParaNulo(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}

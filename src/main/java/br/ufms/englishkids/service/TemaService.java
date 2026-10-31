package br.ufms.englishkids.service;

import br.ufms.englishkids.dto.TemaForm;
import br.ufms.englishkids.dto.TemaResumo;
import br.ufms.englishkids.exception.RecursoNaoEncontradoException;
import br.ufms.englishkids.exception.RegraNegocioException;
import br.ufms.englishkids.model.Tema;
import br.ufms.englishkids.repository.PalavraRepository;
import br.ufms.englishkids.repository.TemaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Regras de negócio de Tema. */
@Service
@Transactional(readOnly = true)
public class TemaService {

    private final TemaRepository temaRepository;
    private final PalavraRepository palavraRepository;

    public TemaService(TemaRepository temaRepository, PalavraRepository palavraRepository) {
        this.temaRepository = temaRepository;
        this.palavraRepository = palavraRepository;
    }

    public List<TemaResumo> listar() {
        return temaRepository.findAllByOrderByNomeInglesAsc().stream()
                .map(TemaResumo::from)
                .toList();
    }

    public TemaResumo buscarPorId(Long id) {
        return TemaResumo.from(obter(id));
    }

    @Transactional
    public TemaResumo criar(TemaForm form) {
        String nomeIngles = form.getNomeIngles().trim();
        if (temaRepository.existsByNomeInglesIgnoreCase(nomeIngles)) {
            throw new RegraNegocioException("Já existe um tema chamado \"" + nomeIngles + "\".");
        }
        Tema tema = new Tema();
        preencher(tema, form, nomeIngles);
        return TemaResumo.from(temaRepository.save(tema));
    }

    @Transactional
    public TemaResumo atualizar(Long id, TemaForm form) {
        Tema tema = obter(id);
        String nomeIngles = form.getNomeIngles().trim();
        if (temaRepository.existsByNomeInglesIgnoreCaseAndIdNot(nomeIngles, id)) {
            throw new RegraNegocioException("Já existe um tema chamado \"" + nomeIngles + "\".");
        }
        preencher(tema, form, nomeIngles);
        return TemaResumo.from(temaRepository.save(tema));
    }

    @Transactional
    public void excluir(Long id) {
        Tema tema = obter(id);
        if (palavraRepository.existsByTemaId(id)) {
            throw new RegraNegocioException(
                    "Não é possível excluir o tema \"" + tema.getNomeIngles() + "\" pois existem palavras associadas a ele.");
        }
        temaRepository.delete(tema);
    }

    private Tema obter(Long id) {
        return temaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tema", id));
    }

    private static void preencher(Tema tema, TemaForm form, String nomeIngles) {
        tema.setNomeIngles(nomeIngles);
        tema.setNomePortugues(form.getNomePortugues().trim());
        tema.setEmoji(vazioParaNulo(form.getEmoji()));
        tema.setDescricao(vazioParaNulo(form.getDescricao()));
    }

    private static String vazioParaNulo(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}

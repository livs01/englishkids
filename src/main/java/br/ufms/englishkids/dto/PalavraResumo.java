package br.ufms.englishkids.dto;

import br.ufms.englishkids.model.Nivel;
import br.ufms.englishkids.model.Palavra;

/** Representação de leitura de Palavra, já com os dados do tema resolvidos. */
public record PalavraResumo(
        Long id,
        String ingles,
        String portugues,
        String pronuncia,
        String exemplo,
        String emoji,
        Nivel nivel,
        Long temaId,
        String temaNomeIngles,
        String temaNomePortugues,
        String temaEmoji) {

    public static PalavraResumo from(Palavra p) {
        return new PalavraResumo(
                p.getId(),
                p.getIngles(),
                p.getPortugues(),
                p.getPronuncia(),
                p.getExemplo(),
                p.getEmoji(),
                p.getNivel(),
                p.getTema().getId(),
                p.getTema().getNomeIngles(),
                p.getTema().getNomePortugues(),
                p.getTema().getEmoji());
    }
}

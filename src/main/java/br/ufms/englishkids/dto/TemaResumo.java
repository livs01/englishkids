package br.ufms.englishkids.dto;

import br.ufms.englishkids.model.Tema;

/** Representação de leitura de Tema. */
public record TemaResumo(Long id, String nomeIngles, String nomePortugues, String emoji, String descricao) {

    public static TemaResumo from(Tema t) {
        return new TemaResumo(t.getId(), t.getNomeIngles(), t.getNomePortugues(), t.getEmoji(), t.getDescricao());
    }
}

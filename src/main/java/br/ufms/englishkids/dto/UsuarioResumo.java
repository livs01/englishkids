package br.ufms.englishkids.dto;

import br.ufms.englishkids.model.Perfil;
import br.ufms.englishkids.model.Usuario;

/** Representação de leitura de Usuario (nunca expõe a senha). */
public record UsuarioResumo(Long id, String nome, String username, Perfil perfil, boolean ativo) {

    public static UsuarioResumo from(Usuario u) {
        return new UsuarioResumo(u.getId(), u.getNome(), u.getUsername(), u.getPerfil(), u.isAtivo());
    }
}

package br.ufms.englishkids.model;

/**
 * Perfis de acesso da aplicação.
 * USER: apenas listagem/visualização. ADMIN: acesso total.
 * O Spring Security expõe estes valores como ROLE_USER / ROLE_ADMIN.
 * No banco é gravado o nome do enum (EnumType.STRING); o rótulo é só para exibição.
 */
public enum Perfil {
    USER("Usuário comum"),
    ADMIN("Administrador");

    private final String rotulo;

    Perfil(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}

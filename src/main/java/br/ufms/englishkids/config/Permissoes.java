package br.ufms.englishkids.config;

/** Expressões de autorização reutilizadas nas anotações @PreAuthorize. */
public final class Permissoes {

    public static final String SOMENTE_ADMIN = "hasRole('ADMIN')";

    private Permissoes() {
    }
}

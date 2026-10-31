package br.ufms.englishkids.model;

/** Nível de dificuldade de uma palavra. No banco é gravado o nome do enum; o rótulo é só para exibição. */
public enum Nivel {
    INICIANTE("Iniciante"),
    INTERMEDIARIO("Intermediário"),
    AVANCADO("Avançado");

    private final String rotulo;

    Nivel(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}

package br.ufms.englishkids.exception;

/** Lançada quando uma regra de negócio é violada (ex.: ISBN duplicado). */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}

package br.ufms.englishkids.exception;

/** Lançada quando um registro solicitado não existe (mapeada para HTTP 404 na camada web). */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " com id " + id + " não foi encontrado(a).");
    }
}

package br.ufms.englishkids.controller;

import br.ufms.englishkids.exception.RecursoNaoEncontradoException;
import br.ufms.englishkids.exception.RegraNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Converte exceções de domínio em páginas de erro amigáveis e com o status HTTP correto.
 * AccessDeniedException NÃO é tratada aqui: o Spring Security a encaminha para /acesso-negado.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String naoEncontrado(RecursoNaoEncontradoException ex, Model model) {
        model.addAttribute("tituloErro", "Registro não encontrado");
        model.addAttribute("mensagem", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(RegraNegocioException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String regraNegocio(RegraNegocioException ex, Model model) {
        model.addAttribute("tituloErro", "Operação não permitida");
        model.addAttribute("mensagem", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String parametroInvalido(Model model) {
        model.addAttribute("tituloErro", "Requisição inválida");
        model.addAttribute("mensagem", "Um dos parâmetros informados na URL é inválido.");
        return "error";
    }
}

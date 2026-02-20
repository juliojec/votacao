package br.com.cooperativismo.votacao.exception;

import br.com.cooperativismo.votacao.controller.dto.StatusUserEnum;
import br.com.cooperativismo.votacao.controller.dto.StatusUserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<String> handleNotFound(RecursoNaoEncontradoException ex) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(SessaoJaEncerradaException.class)
    public ResponseEntity<String> handleSessaoEncerrada(SessaoJaEncerradaException ex) {
        log.warn("Sessão encerrada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }

    @ExceptionHandler(AssociadoJaVotouException.class)
    public ResponseEntity<String> handleJaVotou(AssociadoJaVotouException ex) {
        log.warn("Voto duplicado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(AssociadoNaoAutorizadoException.class)
    public ResponseEntity<String> handleNaoAutorizado(AssociadoNaoAutorizadoException ex) {
        log.warn("Não autorizado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(CpfNaoValidoException.class)
    public ResponseEntity<StatusUserResponse> handleCpfNaoValido(CpfNaoValidoException ex) {
        log.warn("CPF não válido: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new StatusUserResponse(StatusUserEnum.UNABLE_TO_VOTE));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Erro de validação: {}", ex.getFieldError() != null ? ex.getFieldError().getDefaultMessage() : "Campos inválidos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados inválidos");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        log.error("Erro inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro interno. Tente novamente mais tarde.");
    }
}

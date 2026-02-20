package br.com.cooperativismo.votacao.exception;

public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String recurso, Object id) {
        super("%s não encontrado: %s".formatted(recurso, id));
    }

    public RecursoNaoEncontradoException(String cpf) {
        super("cpf não encontrado: %s".formatted(cpf));
    }
}
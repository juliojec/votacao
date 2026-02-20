package br.com.cooperativismo.votacao.exception;

public class CpfNaoValidoException extends RuntimeException {
    public CpfNaoValidoException(String cpf) {
        super("cpf não válido: %s".formatted(cpf));
    }
}
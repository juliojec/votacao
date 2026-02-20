package br.com.cooperativismo.votacao.exception;

public class AssociadoNaoAutorizadoException extends RuntimeException {
    public AssociadoNaoAutorizadoException(String cpfAssociado) {
        super("Associado de cpf: '%s' não está autorizado a votar (UNABLE_TO_VOTE)".formatted(cpfAssociado));
    }
}
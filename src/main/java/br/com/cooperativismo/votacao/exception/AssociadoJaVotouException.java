package br.com.cooperativismo.votacao.exception;

public class AssociadoJaVotouException extends RuntimeException {
    public AssociadoJaVotouException(String cpfAssociado, String pautaTitulo) {
        super("Associado de cpf: '%s' já votou na pauta '%s'".formatted(cpfAssociado, pautaTitulo));
    }
}

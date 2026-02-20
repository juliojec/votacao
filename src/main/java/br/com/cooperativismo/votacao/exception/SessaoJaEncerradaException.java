package br.com.cooperativismo.votacao.exception;

import java.util.UUID;

public class SessaoJaEncerradaException extends RuntimeException {
    public SessaoJaEncerradaException(UUID sessaoId) {
        super("Sessão de votação já encerrada ou expirada: " + sessaoId);
    }
}
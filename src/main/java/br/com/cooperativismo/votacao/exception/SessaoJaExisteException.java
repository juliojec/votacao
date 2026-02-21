package br.com.cooperativismo.votacao.exception;

import java.util.UUID;

public class SessaoJaExisteException extends RuntimeException {
    public SessaoJaExisteException(UUID pautaId) {
        super("Já existe uma sessão de votação para a pauta: " + pautaId);
    }
}
package br.com.cooperativismo.votacao.messaging;

import java.util.UUID;

public record VotacaoResultadoEvent(
        UUID sessaoId,
        UUID pautaId,
        String pautaTitulo,
        long votosSim,
        long votosNao,
        String resultado
) {}

package br.com.cooperativismo.votacao.domain.model;

import java.util.UUID;

public record ResultadoVotacao(
    Pauta pauta,
    UUID sessaoId,
    StatusSessao statusSessao,
    long votosSim,
    long votosNao,
    String resultado
) {}
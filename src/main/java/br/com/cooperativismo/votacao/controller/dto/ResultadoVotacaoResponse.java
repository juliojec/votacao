package br.com.cooperativismo.votacao.controller.dto;

import br.com.cooperativismo.votacao.domain.model.ResultadoVotacao;

import java.util.UUID;

public record ResultadoVotacaoResponse(
        UUID pautaId,
        String pautaTitulo,
        UUID sessaoId,
        long votosSim,
        long votosNao,
        long totalVotos,
        String resultado
) {
    public static ResultadoVotacaoResponse from(ResultadoVotacao resultado) {
        return new ResultadoVotacaoResponse(
            resultado.pauta().getId(),
            resultado.pauta().getTitulo(),
            resultado.sessaoId(),
            resultado.votosSim(),
            resultado.votosNao(),
            resultado.votosSim() + resultado.votosNao(),
            resultado.resultado()
        );
    }
}
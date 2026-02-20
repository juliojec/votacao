package br.com.cooperativismo.votacao.controller.dto;

import br.com.cooperativismo.votacao.domain.model.OpcaoVoto;
import br.com.cooperativismo.votacao.domain.model.Voto;

import java.util.UUID;

public record VotoResponse(
        UUID id,
        UUID sessaoId,
        String cpfAssociado,
        OpcaoVoto opcao,
        String mensagem
) {
}
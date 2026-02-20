package br.com.cooperativismo.votacao.controller.dto;

import br.com.cooperativismo.votacao.client.dto.CpfStatusResponse;

public record StatusUserResponse(StatusUserEnum status) {
    public static StatusUserResponse from(CpfStatusResponse response) {
        return new StatusUserResponse(response.valid() ? StatusUserEnum.ABLE_TO_VOTE : StatusUserEnum.UNABLE_TO_VOTE);
    }
}
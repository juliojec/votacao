package br.com.cooperativismo.votacao.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CriarPautaRequest(
        @NotBlank(message = "Título é obrigatório")
        String titulo,

        @NotBlank(message = "Descrição é obrigatória")
        String descricao
) {}
package br.com.cooperativismo.votacao.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AbrirSessaoRequest(
        @NotNull(message = "ID da pauta é obrigatório")
        UUID pautaId,

        @Min(value = 1, message = "Duração mínima é 1 minuto")
        int duracaoMinutos
) {}
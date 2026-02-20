package br.com.cooperativismo.votacao.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PautaResponse(
        UUID id,
        String titulo,
        String descricao,
        LocalDateTime criadaEm
) {}
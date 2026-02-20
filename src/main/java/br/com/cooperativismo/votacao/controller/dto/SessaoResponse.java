package br.com.cooperativismo.votacao.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessaoResponse(
        UUID id,
        UUID pautaId,
        String pautaTitulo,
        LocalDateTime abertaEm,
        LocalDateTime encerrraEm,
        String status
) {}
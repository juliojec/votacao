package br.com.cooperativismo.votacao.controller.dto;

import br.com.cooperativismo.votacao.domain.model.OpcaoVoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RegistrarVotoRequest(
        @NotNull(message = "ID da sessão é obrigatório")
        UUID sessaoId,

        @NotBlank(message = "CPF é obrigatório")
        String cpf,

        @NotNull(message = "Opção de voto é obrigatória (SIM ou NAO)")
        OpcaoVoto opcao
) {}
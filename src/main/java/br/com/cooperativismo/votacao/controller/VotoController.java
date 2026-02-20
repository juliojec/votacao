package br.com.cooperativismo.votacao.controller;

import br.com.cooperativismo.votacao.controller.dto.RegistrarVotoRequest;
import br.com.cooperativismo.votacao.controller.dto.VotoResponse;
import br.com.cooperativismo.votacao.domain.model.Voto;
import br.com.cooperativismo.votacao.mapper.VotoMapper;
import br.com.cooperativismo.votacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/votos")
@Tag(name = "Votos", description = "Registro de votos nas pautas")
@RequiredArgsConstructor
public class VotoController {

    private final VotoService votoService;
    private final VotoMapper votoMapper;

    @PostMapping
    @Operation(summary = "Registrar voto", description = "Registra o voto de um associado em uma sessão de votação aberta")
    @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou associado já votou")
    @ApiResponse(responseCode = "403", description = "Associado não autorizado a votar")
    @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    @ApiResponse(responseCode = "422", description = "Sessão encerrada ou expirada")
    public ResponseEntity<VotoResponse> registrarVoto(@RequestBody @Valid RegistrarVotoRequest request) {
        Voto voto = votoService.registrarVoto(request.sessaoId(), request.cpf(), request.opcao());
        return ResponseEntity.status(HttpStatus.CREATED).body(votoMapper.toResponse(voto));
    }
}

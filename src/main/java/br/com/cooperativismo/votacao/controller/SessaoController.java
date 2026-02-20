package br.com.cooperativismo.votacao.controller;

import br.com.cooperativismo.votacao.controller.dto.AbrirSessaoRequest;
import br.com.cooperativismo.votacao.controller.dto.SessaoResponse;
import br.com.cooperativismo.votacao.domain.model.SessaoVotacao;
import br.com.cooperativismo.votacao.mapper.SessaoMapper;
import br.com.cooperativismo.votacao.service.SessaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessoes")
@Tag(name = "Sessões", description = "Gerenciamento de sessões de votação")
@RequiredArgsConstructor
public class SessaoController {

    private final SessaoService sessaoService;
    private final SessaoMapper sessaoMapper;

    @GetMapping
    @Operation(summary = "Listar sessões de votação")
    public ResponseEntity<List<SessaoResponse>> listar() {
        List<SessaoResponse> sessoes = sessaoService.listar().stream()
                .map(sessaoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(sessoes);
    }

    @PostMapping
    @Operation(summary = "Abrir sessão de votação", description = "Abre uma sessão de votação para uma pauta. Duração padrão: 1 minuto.")
    @ApiResponse(responseCode = "201", description = "Sessão aberta com sucesso")
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    public ResponseEntity<SessaoResponse> abrir(@RequestBody @Valid AbrirSessaoRequest request) {
        SessaoVotacao sessao = sessaoService.abrir(request.pautaId(), request.duracaoMinutos());
        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoMapper.toResponse(sessao));
    }

}
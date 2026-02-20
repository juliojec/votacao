package br.com.cooperativismo.votacao.controller;

import br.com.cooperativismo.votacao.controller.dto.CriarPautaRequest;
import br.com.cooperativismo.votacao.controller.dto.PautaResponse;
import br.com.cooperativismo.votacao.controller.dto.ResultadoVotacaoResponse;
import br.com.cooperativismo.votacao.domain.model.Pauta;
import br.com.cooperativismo.votacao.mapper.PautaMapper;
import br.com.cooperativismo.votacao.service.PautaService;
import br.com.cooperativismo.votacao.domain.model.ResultadoVotacao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pautas")
@Tag(name = "Pautas", description = "Gerenciamento de pautas")
@RequiredArgsConstructor
public class PautaController {

    private final PautaService pautaService;
    private final PautaMapper pautaMapper;

    @PostMapping
    @Operation(summary = "Cadastrar Pauta")
    @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso!")
    public ResponseEntity<PautaResponse> criar(@RequestBody @Valid CriarPautaRequest request) {
        Pauta pauta = pautaService.criar(request.titulo(), request.descricao());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pautaMapper.toResponse(pauta));
    }

    @GetMapping
    @Operation(summary = "Listar pautas")
    public ResponseEntity<List<PautaResponse>> listar() {
        List<PautaResponse> pautas = pautaService.listar().stream()
                .map(pautaMapper::toResponse)
                .toList();
        return ResponseEntity.ok(pautas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pauta por ID")
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada")
    public ResponseEntity<PautaResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(
                pautaMapper.toResponse(pautaService.buscarPorId(id))
        );
    }

    @GetMapping("/{id}/resultado")
    @Operation(summary = "Consultar resultado da votação")
    @ApiResponse(responseCode = "404", description = "Pauta ou sessão encerrada não encontrada")
    public ResponseEntity<ResultadoVotacaoResponse> resultado(@PathVariable UUID id) {
        ResultadoVotacao resultado = pautaService.buscarResultado(id);
        return ResponseEntity.ok(ResultadoVotacaoResponse.from(resultado));
    }
}
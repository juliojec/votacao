package br.com.cooperativismo.votacao.controller;

import br.com.cooperativismo.votacao.client.dto.CpfStatusResponse;
import br.com.cooperativismo.votacao.controller.dto.StatusUserResponse;
import br.com.cooperativismo.votacao.service.CpfValidacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuários", description = "Validação de CPF para votação")
@RequiredArgsConstructor
public class UserController {

    private final CpfValidacaoService cpfValidacaoService;

    @GetMapping("/{cpf}")
    @Operation(summary = "Verificar se usuário pode votar",
               description = "Valida o CPF é valido via api externa")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "CPF válido - retorna status de votação"),
        @ApiResponse(responseCode = "404", description = "CPF inválido - retorna status de votação")
    })
    public ResponseEntity<StatusUserResponse> verificarUsuarioCpf(@PathVariable String cpf) {
        CpfStatusResponse consultaCpf = cpfValidacaoService.consultarPorUsuario(cpf);
        return ResponseEntity.ok(StatusUserResponse.from(consultaCpf));
    }
}
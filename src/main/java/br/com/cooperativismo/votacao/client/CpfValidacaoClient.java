package br.com.cooperativismo.votacao.client;

import br.com.cooperativismo.votacao.client.dto.CpfStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cpf-validator", url = "${cpf.validacao.base-url}")
public interface CpfValidacaoClient {

    @GetMapping
    CpfStatusResponse validarCpf(
            @RequestParam("token") String token,
            @RequestParam("value") String cpf,
            @RequestParam("type") String type);
}
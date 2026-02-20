package br.com.cooperativismo.votacao.service;

import br.com.cooperativismo.votacao.client.CpfValidacaoClient;
import br.com.cooperativismo.votacao.client.dto.CpfStatusResponse;
import br.com.cooperativismo.votacao.exception.AssociadoNaoAutorizadoException;
import br.com.cooperativismo.votacao.exception.CpfNaoValidoException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CpfValidacaoService {

    private final CpfValidacaoClient cpfValidatorClient;

    @Value("${cpf.validacao.token}")
    private String token;

    private static final String TIPO = "cpf";

    public void validarAptoParaVotar(String cpf) {
        CpfStatusResponse response = consultarCpf(limparCpf(cpf));
        if (!response.valid()) throw new AssociadoNaoAutorizadoException(cpf);
    }

    private CpfStatusResponse consultarCpf(String cpf) {
        try {
            return cpfValidatorClient.validarCpf(token, cpf, TIPO); }
        catch (FeignException.NotFound e) {
            throw new AssociadoNaoAutorizadoException(cpf);
        }
    }

    public CpfStatusResponse consultarPorUsuario(String cpf) {
        CpfStatusResponse cpfStatusResponse = this.consultarCpf(cpf);
        if(!cpfStatusResponse.valid()) throw new CpfNaoValidoException(cpf);
        return cpfStatusResponse;
    }

    private String limparCpf(String cpf) {
        return cpf.replaceAll("[^0-9]", "");
    }
}
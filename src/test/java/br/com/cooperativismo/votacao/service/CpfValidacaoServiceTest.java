package br.com.cooperativismo.votacao.service;

import br.com.cooperativismo.votacao.client.CpfValidacaoClient;
import br.com.cooperativismo.votacao.client.dto.CpfStatusResponse;
import br.com.cooperativismo.votacao.exception.AssociadoNaoAutorizadoException;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CpfValidacaoServiceTest {

    @InjectMocks
    private CpfValidacaoService cpfValidacaoService;

    @Mock
    private CpfValidacaoClient cpfValidatorClient;

    private final String token = "token-teste";
    private final String cpfValido = "05911295903";
    private final String cpfInvalido = "00000000000";

    @BeforeEach
    void setup() {
        cpfValidacaoService = new CpfValidacaoService(cpfValidatorClient);

        try {
            java.lang.reflect.Field field = CpfValidacaoService.class.getDeclaredField("token");
            field.setAccessible(true);
            field.set(cpfValidacaoService, token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void devePassarQuandoCpfValido() {
        when(cpfValidatorClient.validarCpf(token, cpfValido, "cpf"))
                .thenReturn(new CpfStatusResponse(true, "059.112.959-03"));

        assertDoesNotThrow(() -> cpfValidacaoService.validarAptoParaVotar(cpfValido));
        verify(cpfValidatorClient, times(1)).validarCpf(token, cpfValido, "cpf");
    }

    @Test
    void deveLancarExcecaoQuandoCpfInvalido() {
        when(cpfValidatorClient.validarCpf(token, cpfInvalido, "cpf"))
                .thenReturn(new CpfStatusResponse(false, "000.000.000-00"));

        assertThrows(AssociadoNaoAutorizadoException.class,
                () -> cpfValidacaoService.validarAptoParaVotar(cpfInvalido));

        verify(cpfValidatorClient, times(1)).validarCpf(token, cpfInvalido, "cpf");
    }

    @Test
    void deveLancarExcecaoQuandoCpfNaoEncontrado() {
        FeignException.NotFound notFound = new FeignException.NotFound(
                "Not Found", Request.create(Request.HttpMethod.GET, "",
                java.util.Collections.emptyMap(), null, StandardCharsets.UTF_8, new RequestTemplate()), null, null);

        when(cpfValidatorClient.validarCpf(token, cpfInvalido, "cpf")).thenThrow(notFound);

        assertThrows(AssociadoNaoAutorizadoException.class,
                () -> cpfValidacaoService.validarAptoParaVotar(cpfInvalido));

        verify(cpfValidatorClient, times(1)).validarCpf(token, cpfInvalido, "cpf");
    }
}

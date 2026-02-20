package br.com.cooperativismo.votacao.controller;

import br.com.cooperativismo.votacao.client.dto.CpfStatusResponse;
import br.com.cooperativismo.votacao.controller.dto.StatusUserEnum;
import br.com.cooperativismo.votacao.controller.dto.StatusUserResponse;
import br.com.cooperativismo.votacao.service.CpfValidacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private CpfValidacaoService cpfValidacaoService;

    @InjectMocks
    private UserController userController;

    private String cpf;
    private CpfStatusResponse cpfStatusResponse;

    @BeforeEach
    void setUp() {
        cpf = "12345678900";
        cpfStatusResponse = new CpfStatusResponse(true, "123.456.789-00");
    }

    @Test
    void deveRetornarAbleToVoteQuandoCpfValido() {

        when(cpfValidacaoService.consultarPorUsuario(cpf))
                .thenReturn(cpfStatusResponse);

        ResponseEntity<StatusUserResponse> response =
                userController.verificarUsuarioCpf(cpf);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().status())
                .isEqualTo(StatusUserEnum.ABLE_TO_VOTE);

        verify(cpfValidacaoService, times(1))
                .consultarPorUsuario(cpf);
    }

    @Test
    void deveRetornarUnableToVoteQuandoCpfInvalido() {

        CpfStatusResponse cpfInvalido =
                new CpfStatusResponse(false, "123.456.789-00");

        when(cpfValidacaoService.consultarPorUsuario(cpf))
                .thenReturn(cpfInvalido);

        ResponseEntity<StatusUserResponse> response =
                userController.verificarUsuarioCpf(cpf);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().status())
                .isEqualTo(StatusUserEnum.UNABLE_TO_VOTE);

        verify(cpfValidacaoService, times(1))
                .consultarPorUsuario(cpf);
    }

}

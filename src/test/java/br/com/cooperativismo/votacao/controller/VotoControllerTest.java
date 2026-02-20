package br.com.cooperativismo.votacao.controller;

import br.com.cooperativismo.votacao.controller.dto.RegistrarVotoRequest;
import br.com.cooperativismo.votacao.controller.dto.VotoResponse;
import br.com.cooperativismo.votacao.domain.model.OpcaoVoto;
import br.com.cooperativismo.votacao.domain.model.SessaoVotacao;
import br.com.cooperativismo.votacao.domain.model.Voto;
import br.com.cooperativismo.votacao.mapper.VotoMapper;
import br.com.cooperativismo.votacao.service.VotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoControllerTest {

    @Mock
    private VotoService votoService;

    @Mock
    private VotoMapper votoMapper;

    @InjectMocks
    private VotoController votoController;

    private UUID sessaoId;
    private SessaoVotacao sessaoVotacao;
    private Voto voto;
    private VotoResponse votoResponse;

    @BeforeEach
    void setUp() {
        sessaoId = UUID.randomUUID();
        LocalDateTime agora = LocalDateTime.now();

        sessaoVotacao = SessaoVotacao.builder()
                .id(sessaoId)
                .abertaEm(agora)
                .encerrraEm(agora.plusMinutes(1))
                .status(br.com.cooperativismo.votacao.domain.model.StatusSessao.ABERTA)
                .build();

        voto = Voto.builder()
                .id(UUID.randomUUID())
                .sessaoVotacao(sessaoVotacao)
                .cpfAssociado("123")
                .opcao(OpcaoVoto.SIM)
                .criadoEm(agora)
                .build();

        votoResponse = new VotoResponse(
                voto.getId(),
                sessaoId,
                voto.getCpfAssociado(),
                voto.getOpcao(),
                "Voto registrado com sucesso");
    }

    @Test
    void deveRetornarVotoResponseComStatusCreated() {
        RegistrarVotoRequest request = new RegistrarVotoRequest(
                sessaoId,
                "123",
                OpcaoVoto.SIM
        );

        when(votoService.registrarVoto(request.sessaoId(), request.cpf(), request.opcao()))
                .thenReturn(voto);

        when(votoMapper.toResponse(voto)).thenReturn(votoResponse);

        ResponseEntity<VotoResponse> response = votoController.registrarVoto(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isEqualTo(votoResponse);

        verify(votoService, times(1))
                .registrarVoto(request.sessaoId(), request.cpf(), request.opcao());

        verify(votoMapper, times(1)).toResponse(voto);
    }
}

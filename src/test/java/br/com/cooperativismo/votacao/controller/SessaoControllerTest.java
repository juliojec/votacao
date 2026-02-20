package br.com.cooperativismo.votacao.controller;

import br.com.cooperativismo.votacao.controller.dto.AbrirSessaoRequest;
import br.com.cooperativismo.votacao.controller.dto.SessaoResponse;
import br.com.cooperativismo.votacao.domain.model.Pauta;
import br.com.cooperativismo.votacao.domain.model.SessaoVotacao;
import br.com.cooperativismo.votacao.domain.model.StatusSessao;
import br.com.cooperativismo.votacao.mapper.SessaoMapper;
import br.com.cooperativismo.votacao.service.SessaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoControllerTest {

    @Mock
    private SessaoService sessaoService;

    @Mock
    private SessaoMapper sessaoMapper;

    @InjectMocks
    private SessaoController sessaoController;

    private UUID pautaId;
    private Pauta pauta;
    private SessaoVotacao sessao;
    private SessaoResponse sessaoResponse;

    @BeforeEach
    void setUp() {
        pautaId = UUID.randomUUID();
        LocalDateTime agora = LocalDateTime.now();

        pauta = Pauta.builder()
                .id(pautaId)
                .titulo("Nova Pauta")
                .descricao("Descrição da pauta")
                .criadaEm(agora)
                .build();

        sessao = SessaoVotacao.builder()
                .id(UUID.randomUUID())
                .pauta(pauta)
                .abertaEm(agora)
                .encerrraEm(agora.plusMinutes(1))
                .status(StatusSessao.ABERTA)
                .build();

        sessaoResponse = new SessaoResponse(
                sessao.getId(),
                pautaId,
                pauta.getTitulo(),
                sessao.getAbertaEm(),
                sessao.getEncerrraEm(),
                sessao.getStatus().name());
    }

    @Test
    void deveListarSessoes() {
        when(sessaoService.listar()).thenReturn(List.of(sessao));
        when(sessaoMapper.toResponse(sessao)).thenReturn(sessaoResponse);

        ResponseEntity<List<SessaoResponse>> response = sessaoController.listar();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(sessaoResponse);

        verify(sessaoService, times(1)).listar();
        verify(sessaoMapper, times(1)).toResponse(sessao);
    }

    @Test
    void deveRetornarSessaoResponseComStatusCreated() {
        AbrirSessaoRequest request = new AbrirSessaoRequest(pautaId, 1);

        when(sessaoService.abrir(request.pautaId(), request.duracaoMinutos())).thenReturn(sessao);
        when(sessaoMapper.toResponse(sessao)).thenReturn(sessaoResponse);

        ResponseEntity<SessaoResponse> response = sessaoController.abrir(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isEqualTo(sessaoResponse);

        verify(sessaoService, times(1)).abrir(request.pautaId(), request.duracaoMinutos());
        verify(sessaoMapper, times(1)).toResponse(sessao);
    }
}

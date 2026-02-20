package br.com.cooperativismo.votacao.controller;

import br.com.cooperativismo.votacao.controller.dto.CriarPautaRequest;
import br.com.cooperativismo.votacao.controller.dto.PautaResponse;
import br.com.cooperativismo.votacao.controller.dto.ResultadoVotacaoResponse;
import br.com.cooperativismo.votacao.domain.model.Pauta;
import br.com.cooperativismo.votacao.domain.model.ResultadoVotacao;
import br.com.cooperativismo.votacao.mapper.PautaMapper;
import br.com.cooperativismo.votacao.service.PautaService;
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
class PautaControllerTest {

    @Mock
    private PautaService pautaService;

    @Mock
    private PautaMapper pautaMapper;

    @InjectMocks
    private PautaController pautaController;

    private UUID pautaId;
    private Pauta pauta;
    private PautaResponse pautaResponse;
    private ResultadoVotacao resultadoVotacao;

    @BeforeEach
    void setUp() {
        pautaId = UUID.randomUUID();
        LocalDateTime criadaEm = LocalDateTime.now();

        pauta = Pauta.builder()
                .id(pautaId)
                .titulo("Nova Pauta")
                .descricao("Pauta da Assembléia")
                .criadaEm(criadaEm)
                .build();

        pautaResponse = new PautaResponse(
                pautaId,
                pauta.getTitulo(),
                pauta.getDescricao(),
                pauta.getCriadaEm());

        resultadoVotacao = new ResultadoVotacao(
                pauta,
                pautaId,
                10L,
                5L,
                "APROVADA");
    }

    @Test
    void deveRetornarPautaResponse() {
        CriarPautaRequest request = new CriarPautaRequest("Nova Pauta", "Descrição da pauta");

        when(pautaService.criar(request.titulo(), request.descricao())).thenReturn(pauta);
        when(pautaMapper.toResponse(pauta)).thenReturn(pautaResponse);

        ResponseEntity<PautaResponse> response = pautaController.criar(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody()).isEqualTo(pautaResponse);

        verify(pautaService, times(1)).criar(request.titulo(), request.descricao());
        verify(pautaMapper, times(1)).toResponse(pauta);
    }

    @Test
    void deveRetornarListaDePautaResponse() {
        when(pautaService.listar()).thenReturn(List.of(pauta));
        when(pautaMapper.toResponse(pauta)).thenReturn(pautaResponse);

        ResponseEntity<List<PautaResponse>> response = pautaController.listar();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1).contains(pautaResponse);

        verify(pautaService, times(1)).listar();
        verify(pautaMapper, times(1)).toResponse(pauta);
    }

    @Test
    void deveRetornarResultadoVotacaoResponse() {
        when(pautaService.buscarResultado(pautaId)).thenReturn(resultadoVotacao);

        ResponseEntity<ResultadoVotacaoResponse> response = pautaController.resultado(pautaId);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().sessaoId()).isEqualTo(pautaId);
        assertThat(response.getBody().votosSim()).isEqualTo(10L);
        assertThat(response.getBody().votosNao()).isEqualTo(5L);
        assertThat(response.getBody().resultado()).isEqualTo("APROVADA");

        verify(pautaService, times(1)).buscarResultado(pautaId);
    }
}

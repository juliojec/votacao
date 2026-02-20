package br.com.cooperativismo.votacao.service;

import br.com.cooperativismo.votacao.domain.model.*;
import br.com.cooperativismo.votacao.exception.AssociadoJaVotouException;
import br.com.cooperativismo.votacao.messaging.VotacaoEventPublisher;
import br.com.cooperativismo.votacao.messaging.VotacaoResultadoEvent;
import br.com.cooperativismo.votacao.repository.SessaoVotacaoRepository;
import br.com.cooperativismo.votacao.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @InjectMocks
    private VotoService votoService;

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private SessaoVotacaoRepository sessaoRepository;

    @Mock
    private CpfValidacaoService cpfValidacaoService;

    @Mock
    private VotacaoEventPublisher eventPublisher;

    private UUID sessaoId;
    private UUID pautaId;
    private SessaoVotacao sessao;
    private Pauta pauta;

    @BeforeEach
    void setup() {
        sessaoId = UUID.randomUUID();
        pautaId = UUID.randomUUID();
        pauta = new Pauta("Titulo Teste", "Descricao Teste");
        pauta.setId(pautaId);
        sessao = new SessaoVotacao(pauta, 1);
        sessao.setId(sessaoId);
    }

    @Test
    void deveRegistrarVotoComSucesso() {
        String cpf = "05911295903";
        OpcaoVoto opcao = OpcaoVoto.SIM;

        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.of(sessao));
        when(votoRepository.existsBySessaoVotacaoIdAndCpfAssociado(sessaoId, cpf)).thenReturn(false);
        when(votoRepository.save(any(Voto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Voto voto = votoService.registrarVoto(sessaoId, cpf, opcao);

        assertNotNull(voto);
        assertEquals(sessao, voto.getSessaoVotacao());
        assertEquals(cpf, voto.getCpfAssociado());
        assertEquals(opcao, voto.getOpcao());

        verify(cpfValidacaoService, times(1)).validarAptoParaVotar(cpf);
        verify(votoRepository, times(1)).save(voto);
    }

    @Test
    void deveLancarExcecao_QuandoAssociadoJaVotou() {
        String cpf = "05911295903";

        when(sessaoRepository.findById(sessaoId)).thenReturn(Optional.of(sessao));
        when(votoRepository.existsBySessaoVotacaoIdAndCpfAssociado(sessaoId, cpf)).thenReturn(true);

        assertThrows(AssociadoJaVotouException.class,
                () -> votoService.registrarVoto(sessaoId, cpf, OpcaoVoto.SIM));

        verify(cpfValidacaoService, times(1)).validarAptoParaVotar(cpf);
        verify(votoRepository, never()).save(any());
    }

    @Test
    void deveEncerrarESalvarSessaoEPubicarResultado() {
        SessaoVotacao sessaoExpirada = new SessaoVotacao(pauta, 1);
        sessaoExpirada.setId(sessaoId);
        sessaoExpirada.setAbertaEm(LocalDateTime.now().minusMinutes(5));
        sessaoExpirada.setEncerrraEm(LocalDateTime.now().minusMinutes(1));
        sessaoExpirada.setStatus(StatusSessao.ABERTA);

        when(sessaoRepository.findByStatusAndEncerrraEmBefore(eq(StatusSessao.ABERTA), any(LocalDateTime.class)))
                .thenReturn(List.of(sessaoExpirada));

        when(votoRepository.countBySessaoIdAndOpcao(sessaoId, OpcaoVoto.SIM)).thenReturn(2L);
        when(votoRepository.countBySessaoIdAndOpcao(sessaoId, OpcaoVoto.NAO)).thenReturn(1L);

        votoService.encerrarSessoesExpiradas();

        assertEquals(StatusSessao.ENCERRADA, sessaoExpirada.getStatus());
        verify(sessaoRepository, times(1)).save(sessaoExpirada);
        verify(eventPublisher, times(1)).publicarResultado(any(VotacaoResultadoEvent.class));
    }

    @Test
    void naoDeveFazerNadaSeNaoHouverSessoesExpiradas() {
        when(sessaoRepository.findByStatusAndEncerrraEmBefore(eq(StatusSessao.ABERTA), any(LocalDateTime.class)))
                .thenReturn(List.of());

        votoService.encerrarSessoesExpiradas();

        verify(sessaoRepository, never()).save(any());
        verify(eventPublisher, never()).publicarResultado(any());
    }
}

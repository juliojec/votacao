package br.com.cooperativismo.votacao.service;

import br.com.cooperativismo.votacao.domain.model.Pauta;
import br.com.cooperativismo.votacao.domain.model.SessaoVotacao;
import br.com.cooperativismo.votacao.exception.RecursoNaoEncontradoException;
import br.com.cooperativismo.votacao.repository.PautaRepository;
import br.com.cooperativismo.votacao.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoServiceTest {

    @InjectMocks
    private SessaoService sessaoService;

    @Mock
    private SessaoVotacaoRepository sessaoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @Test
    void deveCriarSessaoComDuracaoInformada() {
        UUID pautaId = UUID.randomUUID();
        Pauta pauta = new Pauta("Titulo Teste", "Descricao Teste");
        pauta.setId(pautaId);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.save(any(SessaoVotacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SessaoVotacao sessao = sessaoService.abrir(pautaId, 5);

        assertNotNull(sessao);
        assertEquals(pauta, sessao.getPauta());

        long duracao = Duration.between(sessao.getAbertaEm(), sessao.getEncerrraEm()).toMinutes();
        assertEquals(5, duracao);

        verify(sessaoRepository, times(1)).save(any(SessaoVotacao.class));
    }

    @Test
    void deveCriarSessaoComDuracaoPadraoQuandoDuracaoZero() {
        UUID pautaId = UUID.randomUUID();
        Pauta pauta = new Pauta("Titulo Teste", "Descricao Teste");
        pauta.setId(pautaId);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.save(any(SessaoVotacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SessaoVotacao sessao = sessaoService.abrir(pautaId, 0);

        assertNotNull(sessao);
        assertEquals(pauta, sessao.getPauta());

        long duracao = Duration.between(sessao.getAbertaEm(), sessao.getEncerrraEm()).toMinutes();
        assertEquals(1, duracao);

        verify(sessaoRepository, times(1)).save(any(SessaoVotacao.class));
    }

    @Test
    void deveLancarExcecaoQuandoPautaNaoExiste() {
        UUID pautaId = UUID.randomUUID();
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> sessaoService.abrir(pautaId, 5));
        verify(sessaoRepository, never()).save(any(SessaoVotacao.class));
    }
}

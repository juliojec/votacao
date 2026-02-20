package br.com.cooperativismo.votacao.service;

import br.com.cooperativismo.votacao.domain.model.*;
import br.com.cooperativismo.votacao.exception.RecursoNaoEncontradoException;
import br.com.cooperativismo.votacao.repository.PautaRepository;
import br.com.cooperativismo.votacao.repository.SessaoVotacaoRepository;
import br.com.cooperativismo.votacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @InjectMocks
    private PautaService pautaService;

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private SessaoVotacaoRepository sessaoRepository;

    @Mock
    private VotoRepository votoRepository;

    @Test
    void deveSalvarPauta() {
        Pauta pauta = new Pauta("Titulo Teste", "Descricao Teste");
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);

        Pauta result = pautaService.criar("Titulo Teste", "Descricao Teste");

        assertNotNull(result);
        assertEquals("Titulo Teste", result.getTitulo());
        verify(pautaRepository, times(1)).save(any(Pauta.class));
    }

    @Test
    void deveRetornarListaDePautas() {
        Pauta pauta1 = new Pauta("Titulo1", "Desc1");
        Pauta pauta2 = new Pauta("Titulo2", "Desc2");
        when(pautaRepository.findAll()).thenReturn(List.of(pauta1, pauta2));

        List<Pauta> result = pautaService.listar();

        assertEquals(2, result.size());
        verify(pautaRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarPautaQuandoExiste() {
        UUID id = UUID.randomUUID();
        Pauta pauta = new Pauta("Titulo", "Desc");
        when(pautaRepository.findById(id)).thenReturn(Optional.of(pauta));

        Pauta result = pautaService.buscarPorId(id);

        assertNotNull(result);
        assertEquals("Titulo", result.getTitulo());
        verify(pautaRepository, times(1)).findById(id);
    }

    @Test
    void deveLancarExcecaoQuandoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(pautaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> pautaService.buscarPorId(id));
        verify(pautaRepository, times(1)).findById(id);
    }

    @Test
    void deveRetornarResultadoVotacao() {
        UUID pautaId = UUID.randomUUID();
        Pauta pauta = new Pauta("Titulo", "Descricao");
        pauta.setId(pautaId);

        SessaoVotacao sessao = new SessaoVotacao();
        sessao.setId(UUID.randomUUID());
        sessao.setStatus(StatusSessao.ENCERRADA);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.findTopByPauta_Id(pautaId))
                .thenReturn(Optional.of(sessao));
        when(votoRepository.countBySessaoIdAndOpcao(sessao.getId(), OpcaoVoto.SIM)).thenReturn(5L);
        when(votoRepository.countBySessaoIdAndOpcao(sessao.getId(), OpcaoVoto.NAO)).thenReturn(3L);

        ResultadoVotacao resultado = pautaService.buscarResultado(pautaId);

        assertEquals(5L, resultado.votosSim());
        assertEquals(3L, resultado.votosNao());
        assertEquals("APROVADA", resultado.resultado());
        assertEquals(pauta, resultado.pauta());
    }

    @Test
    void deveLancarExcecaoQuandoSessaoNaoEncontrada() {
        UUID pautaId = UUID.randomUUID();
        Pauta pauta = new Pauta("Titulo", "Descricao");
        pauta.setId(pautaId);

        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.findTopByPauta_Id(pautaId))
                .thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> pautaService.buscarResultado(pautaId));
    }
}

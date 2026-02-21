package br.com.cooperativismo.votacao.service;

import br.com.cooperativismo.votacao.domain.model.OpcaoVoto;
import br.com.cooperativismo.votacao.domain.model.Pauta;
import br.com.cooperativismo.votacao.exception.RecursoNaoEncontradoException;
import br.com.cooperativismo.votacao.repository.PautaRepository;
import br.com.cooperativismo.votacao.repository.SessaoVotacaoRepository;
import br.com.cooperativismo.votacao.repository.VotoRepository;
import br.com.cooperativismo.votacao.domain.model.ResultadoVotacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PautaService {

    private final PautaRepository pautaRepository;
    private final SessaoVotacaoRepository sessaoRepository;
    private final VotoRepository votoRepository;

    @Transactional
    public Pauta criar(String titulo, String descricao) {
        Pauta pauta = new Pauta(titulo, descricao);
        pautaRepository.save(pauta);
        log.info("Pauta criada — id: {}, titulo: {}", pauta.getId(), pauta.getTitulo());
        return pauta;
    }

    @Transactional(readOnly = true)
    public List<Pauta> listar() {
        return pautaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pauta buscarPorId(UUID id) {
        return pautaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pauta", id));
    }

    @Cacheable(value = "resultados", key = "#pautaId", condition = "#result != null && #result.encerrada()")
    @Transactional(readOnly = true)
    public ResultadoVotacao buscarResultado(UUID pautaId) {
        Pauta pauta = buscarPorId(pautaId);

        var sessao = sessaoRepository
                .findTopByPauta_Id(pautaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pauta não encontrada", pautaId));

        long sim = votoRepository.countBySessaoIdAndOpcao(sessao.getId(), OpcaoVoto.SIM);
        long nao = votoRepository.countBySessaoIdAndOpcao(sessao.getId(), OpcaoVoto.NAO);
        String resultado = sim > nao ? "APROVADA" : nao > sim ? "REPROVADA" : "EMPATE";

        return new ResultadoVotacao(pauta, sessao.getId(), sessao.getStatus(), sim, nao, resultado);
    }
}
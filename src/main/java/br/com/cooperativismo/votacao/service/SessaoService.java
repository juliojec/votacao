package br.com.cooperativismo.votacao.service;

import br.com.cooperativismo.votacao.domain.model.Pauta;
import br.com.cooperativismo.votacao.domain.model.SessaoVotacao;
import br.com.cooperativismo.votacao.exception.RecursoNaoEncontradoException;
import br.com.cooperativismo.votacao.repository.PautaRepository;
import br.com.cooperativismo.votacao.repository.SessaoVotacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessaoService {

    private static final int DURACAO_PADRAO_MINUTOS = 1;

    private final SessaoVotacaoRepository sessaoRepository;
    private final PautaRepository pautaRepository;

    public List<SessaoVotacao> listar() {
        return sessaoRepository.findAll();
    }

    @Transactional
    public SessaoVotacao abrir(UUID pautaId, int duracaoMinutos) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pauta", pautaId));

        int duracao = duracaoMinutos > 0 ? duracaoMinutos : DURACAO_PADRAO_MINUTOS;

        SessaoVotacao sessao = new SessaoVotacao(pauta, duracao);
        sessaoRepository.save(sessao);

        log.info("Sessão aberta — id: {}, pauta: {}, duração: {} min", sessao.getId(), pautaId, duracao);

        return sessao;
    }
}
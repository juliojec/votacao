package br.com.cooperativismo.votacao.service;

import br.com.cooperativismo.votacao.domain.model.*;
import br.com.cooperativismo.votacao.exception.AssociadoJaVotouException;
import br.com.cooperativismo.votacao.exception.RecursoNaoEncontradoException;
import br.com.cooperativismo.votacao.messaging.VotacaoEventPublisher;
import br.com.cooperativismo.votacao.messaging.VotacaoResultadoEvent;
import br.com.cooperativismo.votacao.repository.SessaoVotacaoRepository;
import br.com.cooperativismo.votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class VotoService {

    private final VotoRepository votoRepository;
    private final SessaoVotacaoRepository sessaoRepository;
    private final CpfValidacaoService cpfValidacaoService;
    private final VotacaoEventPublisher eventPublisher;
    private final CacheManager cacheManager;

    private static final String SESSAO_NAO_ENCONTRADA = "Sessão de votação";
    private static final String RESULTADO_APROVADA = "APROVADA";
    private static final String RESULTADO_REPROVADA = "REPROVADA";
    private static final String RESULTADO_EMPATE = "EMPATE";

    @Transactional
    public Voto registrarVoto(UUID sessaoId, String cpf, OpcaoVoto opcao) {
        cpfValidacaoService.validarAptoParaVotar(cpf);

        SessaoVotacao sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(SESSAO_NAO_ENCONTRADA, sessaoId));

        sessao.validarAberta();

        if (votoRepository.existsBySessaoVotacaoIdAndCpfAssociado(sessaoId, cpf)) {
            throw new AssociadoJaVotouException(cpf, sessao.getPauta().getTitulo());
        }

        Voto voto = new Voto(sessao, cpf, opcao);
        votoRepository.save(voto);

        log.info("Voto registrado — sessão: {}, cpf associado: {}, opção: {}", sessaoId, cpf, opcao);

        return voto;
    }

    @Scheduled(fixedDelayString = "${sessao.scheduler.delay-ms:30000}")
    @Transactional
    public void encerrarSessoesExpiradas() {
        log.info("Scheduler: Verificando sessões expiradas...");

        List<SessaoVotacao> expiradas = sessaoRepository
                .findByStatusAndEncerrraEmBefore(StatusSessao.ABERTA, LocalDateTime.now());

        if (expiradas.isEmpty()) {
            log.info("Scheduler: Nenhuma sessão expirada encontrada");
            return;
        }

        log.info("Encerrando {} sessão(ões) expirada(s)", expiradas.size());

        expiradas.forEach(sessao -> {
            sessao.encerrar();
            sessaoRepository.save(sessao);

            long sim = votoRepository.countBySessaoIdAndOpcao(sessao.getId(), OpcaoVoto.SIM);
            long nao = votoRepository.countBySessaoIdAndOpcao(sessao.getId(), OpcaoVoto.NAO);
            String resultado = sim > nao ? RESULTADO_APROVADA
                    : nao > sim ? RESULTADO_REPROVADA
                    : RESULTADO_EMPATE;

            Pauta pauta = sessao.getPauta();
            ResultadoVotacao resultadoVotacao = new ResultadoVotacao(pauta, sessao.getId(), sessao.getStatus(), sim, nao, resultado);
            Cache cache = cacheManager.getCache("resultados");
            if (cache != null) {
                cache.put(pauta.getId(), resultadoVotacao);
                log.info("Resultado da pauta {} cacheado", pauta.getId());
            }

            eventPublisher.publicarResultado(new VotacaoResultadoEvent(
                    sessao.getId(),
                    sessao.getPauta().getId(),
                    sessao.getPauta().getTitulo(),
                    sim,
                    nao,
                    resultado));

            log.info("Sessão {} encerrada — resultado: {} (SIM: {}, NÃO: {})",
                    sessao.getId(), resultado, sim, nao);
        });
    }
}

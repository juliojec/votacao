package br.com.cooperativismo.votacao.mapper;

import br.com.cooperativismo.votacao.controller.dto.SessaoResponse;
import br.com.cooperativismo.votacao.domain.model.SessaoVotacao;
import org.springframework.stereotype.Component;

@Component
public class SessaoMapper {
    public SessaoResponse toResponse(SessaoVotacao sessao) {
        return new SessaoResponse(
            sessao.getId(),
            sessao.getPauta().getId(),
            sessao.getPauta().getTitulo(),
            sessao.getAbertaEm(),
            sessao.getEncerrraEm(),
            sessao.getStatus().name()
        );
    }
}
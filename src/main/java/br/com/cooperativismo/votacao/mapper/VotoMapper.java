package br.com.cooperativismo.votacao.mapper;

import br.com.cooperativismo.votacao.controller.dto.VotoResponse;
import br.com.cooperativismo.votacao.domain.model.Voto;
import org.springframework.stereotype.Component;

@Component
public class VotoMapper {
    public VotoResponse toResponse(Voto voto) {
        return new VotoResponse(voto.getId(),
            voto.getSessaoVotacao().getId(),
            voto.getCpfAssociado(),
            voto.getOpcao(),
            "Voto registrado com sucesso");
    }
}

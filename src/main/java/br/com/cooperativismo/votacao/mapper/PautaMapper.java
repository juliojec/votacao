package br.com.cooperativismo.votacao.mapper;

import br.com.cooperativismo.votacao.controller.dto.PautaResponse;
import br.com.cooperativismo.votacao.domain.model.Pauta;
import org.springframework.stereotype.Component;

@Component
public class PautaMapper {
    public PautaResponse toResponse(Pauta pauta) {
        return new PautaResponse(
            pauta.getId(),
            pauta.getTitulo(),
            pauta.getDescricao(),
            pauta.getCriadaEm()
        );
    }
}
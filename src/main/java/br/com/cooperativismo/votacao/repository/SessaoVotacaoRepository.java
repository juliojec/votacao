package br.com.cooperativismo.votacao.repository;

import br.com.cooperativismo.votacao.domain.model.SessaoVotacao;
import br.com.cooperativismo.votacao.domain.model.StatusSessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, UUID> {

    Boolean existsByPautaId(UUID pautaId);

    List<SessaoVotacao> findByStatusAndEncerrraEmBefore(StatusSessao status, LocalDateTime dataEncerramento);

    Optional<SessaoVotacao> findTopByPauta_Id(UUID pautaId);

}

package br.com.cooperativismo.votacao.repository;

import br.com.cooperativismo.votacao.domain.model.OpcaoVoto;
import br.com.cooperativismo.votacao.domain.model.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VotoRepository extends JpaRepository<Voto, UUID> {

    Boolean existsBySessaoVotacaoIdAndCpfAssociado(UUID sessaoVotacaoId, String cpfAssociado);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.sessaoVotacao.id = :sessaoId AND v.opcao = :opcao")
    Long countBySessaoIdAndOpcao(@Param("sessaoId") UUID sessaoId, @Param("opcao") OpcaoVoto opcao);
}

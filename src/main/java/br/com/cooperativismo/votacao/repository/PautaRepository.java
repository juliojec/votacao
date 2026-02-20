package br.com.cooperativismo.votacao.repository;

import br.com.cooperativismo.votacao.domain.model.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PautaRepository extends JpaRepository<Pauta, UUID> {}

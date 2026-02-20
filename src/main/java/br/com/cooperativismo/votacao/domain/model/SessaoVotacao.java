package br.com.cooperativismo.votacao.domain.model;

import br.com.cooperativismo.votacao.exception.SessaoJaEncerradaException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "sessoes_votacao")
public class SessaoVotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @Column(name = "aberta_em", nullable = false, updatable = false)
    private LocalDateTime abertaEm;

    @Column(name = "encerra_em", nullable = false)
    private LocalDateTime encerrraEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSessao status;

    public SessaoVotacao(Pauta pauta, int duracaoMinutos) {
        this.pauta = pauta;
        this.abertaEm = LocalDateTime.now();
        this.encerrraEm = abertaEm.plusMinutes(duracaoMinutos);
        this.status = StatusSessao.ABERTA;
    }

    public void encerrar() {
        this.status = StatusSessao.ENCERRADA;
    }

    public boolean estaAberta() {
        return status == StatusSessao.ABERTA && LocalDateTime.now().isBefore(encerrraEm);
    }

    public void validarAberta() {
        if (!estaAberta()) {
            throw new SessaoJaEncerradaException(id);
        }
    }
}

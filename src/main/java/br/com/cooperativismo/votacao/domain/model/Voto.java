package br.com.cooperativismo.votacao.domain.model;

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
@Table(name = "votos", uniqueConstraints = @UniqueConstraint(name = "uk_voto_sessao_associado", columnNames = {"sessao_votacao_id", "cpf_associado"}))
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_votacao_id", nullable = false)
    private SessaoVotacao sessaoVotacao;

    @Column(name = "cpf_associado", nullable = false)
    private String cpfAssociado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OpcaoVoto opcao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    public Voto(SessaoVotacao sessaoVotacao, String cpfAssociado, OpcaoVoto opcao) {
        this.sessaoVotacao = sessaoVotacao;
        this.cpfAssociado = cpfAssociado;
        this.opcao = opcao;
    }

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

}

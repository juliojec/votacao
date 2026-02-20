CREATE TABLE pautas (
    id          UUID        PRIMARY KEY,
    titulo      VARCHAR(255) NOT NULL,
    descricao   TEXT         NOT NULL,
    criada_em   TIMESTAMP    NOT NULL
);

CREATE TABLE sessoes_votacao (
    id          UUID        PRIMARY KEY,
    pauta_id    UUID        NOT NULL REFERENCES pautas(id),
    aberta_em   TIMESTAMP   NOT NULL,
    encerra_em  TIMESTAMP   NOT NULL,
    status      VARCHAR(20) NOT NULL,
    CONSTRAINT chk_status CHECK (status IN ('ABERTA', 'ENCERRADA'))
);

CREATE TABLE votos (
    id                  UUID        PRIMARY KEY,
    sessao_votacao_id   UUID        NOT NULL REFERENCES sessoes_votacao(id),
    cpf_associado        VARCHAR(255) NOT NULL,
    opcao               VARCHAR(3)  NOT NULL,
    criado_em           TIMESTAMP   NOT NULL,
    CONSTRAINT uk_voto_sessao_associado UNIQUE (sessao_votacao_id, cpf_associado),
    CONSTRAINT chk_opcao CHECK (opcao IN ('SIM', 'NAO'))
);

CREATE INDEX idx_sessoes_status_encerra_em ON sessoes_votacao(status, encerra_em);
CREATE INDEX idx_votos_sessao_id ON votos(sessao_votacao_id);
CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE revinfo (
    rev      INTEGER NOT NULL DEFAULT nextval('revinfo_seq'),
    revtstmp BIGINT,
    PRIMARY KEY (rev)
);

CREATE TABLE tb_perfil_autoridade_aud (
    id         BIGINT   NOT NULL,
    rev        INTEGER  NOT NULL,
    revtype    SMALLINT,
    nome       VARCHAR(50),
    descricao  VARCHAR(200),
    status     BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE tb_unidade_aud (
    id            BIGINT   NOT NULL,
    rev           INTEGER  NOT NULL,
    revtype       SMALLINT,
    nome_fantasia VARCHAR(100),
    cnpj          VARCHAR(14),
    endereco      VARCHAR(255),
    contato       VARCHAR(100),
    regiao        VARCHAR(20),
    estado        VARCHAR(50),
    status        BOOLEAN,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255),
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE tb_usuario_aud (
    id         BIGINT   NOT NULL,
    rev        INTEGER  NOT NULL,
    revtype    SMALLINT,
    nome       VARCHAR(100),
    email      VARCHAR(150),
    cpf        VARCHAR(11),
    senha      VARCHAR(255),
    status     BOOLEAN,
    id_unidade BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE usuario_perfil_autoridade_aud (
    id_usuario           BIGINT   NOT NULL,
    id_perfil_autoridade BIGINT   NOT NULL,
    rev                  INTEGER  NOT NULL,
    revtype              SMALLINT,
    PRIMARY KEY (id_usuario, id_perfil_autoridade, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE tb_auditoria_login_aud (
    id         BIGINT   NOT NULL,
    rev        INTEGER  NOT NULL,
    revtype    SMALLINT,
    id_usuario BIGINT,
    ip         VARCHAR(45),
    sucesso    BOOLEAN,
    data_hora  TIMESTAMP,
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE tb_cliente_aud (
    id                   BIGINT   NOT NULL,
    rev                  INTEGER  NOT NULL,
    revtype              SMALLINT,
    id_usuario           BIGINT,
    data_nascimento      DATE,
    telefone             VARCHAR(100),
    endereco             VARCHAR(255),
    status_consentimento BOOLEAN,
    data_consentimento   TIMESTAMP,
    data_revogacao       TIMESTAMP,
    anonimizado          BOOLEAN,
    created_at           TIMESTAMP,
    updated_at           TIMESTAMP,
    created_by           VARCHAR(255),
    updated_by           VARCHAR(255),
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE tb_fidelidade_aud (
    id                BIGINT         NOT NULL,
    rev               INTEGER        NOT NULL,
    revtype           SMALLINT,
    id_cliente        BIGINT,
    pontos_acumulados INTEGER,
    total_gasto       NUMERIC(10, 2),
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE tb_cardapio_aud (
    id         BIGINT   NOT NULL,
    rev        INTEGER  NOT NULL,
    revtype    SMALLINT,
    id_unidade BIGINT,
    nome       VARCHAR(100),
    ativo      BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE tb_item_aud (
    id         BIGINT         NOT NULL,
    rev        INTEGER        NOT NULL,
    revtype    SMALLINT,
    nome       VARCHAR(100),
    descricao  VARCHAR(255),
    preco      NUMERIC(10, 2),
    categoria  VARCHAR(20),
    status     BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE tb_item_cardapio_aud (
    id          BIGINT   NOT NULL,
    rev         INTEGER  NOT NULL,
    revtype     SMALLINT,
    id_cardapio BIGINT,
    id_item     BIGINT,
    disponivel  BOOLEAN,
    ativo       BOOLEAN,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE tb_estoque_aud (
    id                BIGINT   NOT NULL,
    rev               INTEGER  NOT NULL,
    revtype           SMALLINT,
    id_unidade        BIGINT,
    id_item           BIGINT,
    quantidade        INTEGER,
    quantidade_minima INTEGER,
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE tb_movimentacao_estoque_aud (
    id         BIGINT   NOT NULL,
    rev        INTEGER  NOT NULL,
    revtype    SMALLINT,
    id_estoque BIGINT,
    id_usuario BIGINT,
    quantidade INTEGER,
    observacao VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE tb_pedido_aud (
    id                BIGINT         NOT NULL,
    rev               INTEGER        NOT NULL,
    revtype           SMALLINT,
    id_cliente        BIGINT,
    id_unidade        BIGINT,
    canal             VARCHAR(20),
    status            VARCHAR(30),
    valor_total       NUMERIC(10, 2),
    meio_pagamento    VARCHAR(20),
    pontos_utilizados INTEGER,
    desconto_pontos   NUMERIC(10, 2),
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255),
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE TABLE tb_item_pedido_aud (
    id               BIGINT         NOT NULL,
    rev              INTEGER        NOT NULL,
    revtype          SMALLINT,
    id_pedido        BIGINT,
    id_item_cardapio BIGINT,
    quantidade       INTEGER,
    preco_unitario   NUMERIC(10, 2),
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255),
    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES revinfo(rev)
);
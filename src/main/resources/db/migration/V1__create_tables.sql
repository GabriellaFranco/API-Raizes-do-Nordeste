-- ==================== PERFIL_AUTORIDADE ====================
CREATE TABLE tb_perfil_autoridade (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(50)  NOT NULL UNIQUE,
    descricao   VARCHAR(200),
    status      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255)
);

-- ==================== UNIDADE ====================
CREATE TABLE tb_unidade (
    id            BIGSERIAL PRIMARY KEY,
    nome_fantasia VARCHAR(100) NOT NULL,
    cnpj          VARCHAR(14)  NOT NULL UNIQUE,
    endereco      VARCHAR(255) NOT NULL,
    contato       VARCHAR(100) NOT NULL,
    regiao        VARCHAR(20)  NOT NULL,
    estado        VARCHAR(50)  NOT NULL,
    status        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255)
);

-- ==================== USUARIO ====================
CREATE TABLE tb_usuario (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    cpf         VARCHAR(11)  NOT NULL UNIQUE,
    senha       VARCHAR(255) NOT NULL,
    status      BOOLEAN      NOT NULL DEFAULT TRUE,
    id_unidade  BIGINT       REFERENCES tb_unidade(id),
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255)
);

-- ==================== USUARIO_PERFIL_AUTORIDADE ====================
CREATE TABLE usuario_perfil_autoridade (
    id_usuario           BIGINT NOT NULL REFERENCES tb_usuario(id),
    id_perfil_autoridade BIGINT NOT NULL REFERENCES tb_perfil_autoridade(id),
    PRIMARY KEY (id_usuario, id_perfil_autoridade)
);

-- ==================== AUDITORIA_LOGIN ====================
CREATE TABLE tb_auditoria_login (
    id         BIGSERIAL PRIMARY KEY,
    id_usuario BIGINT    REFERENCES tb_usuario(id),
    ip         VARCHAR(45)  NOT NULL,
    sucesso    BOOLEAN      NOT NULL,
    data_hora  TIMESTAMP    NOT NULL
);

-- ==================== CLIENTE ====================
CREATE TABLE tb_cliente (
    id                   BIGSERIAL PRIMARY KEY,
    id_usuario           BIGINT    NOT NULL UNIQUE REFERENCES tb_usuario(id),
    data_nascimento      DATE,
    telefone             VARCHAR(100),
    endereco             VARCHAR(255),
    status_consentimento BOOLEAN   NOT NULL DEFAULT TRUE,
    data_consentimento   TIMESTAMP,
    data_revogacao       TIMESTAMP,
    anonimizado          BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at           TIMESTAMP NOT NULL,
    updated_at           TIMESTAMP,
    created_by           VARCHAR(255),
    updated_by           VARCHAR(255)
);

-- ==================== FIDELIDADE ====================
CREATE TABLE tb_fidelidade (
    id                BIGSERIAL PRIMARY KEY,
    id_cliente        BIGINT         NOT NULL UNIQUE REFERENCES tb_cliente(id),
    pontos_acumulados INTEGER        NOT NULL DEFAULT 0,
    total_gasto       NUMERIC(10, 2) NOT NULL DEFAULT 0,
    created_at        TIMESTAMP      NOT NULL,
    updated_at        TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255)
);

-- ==================== CARDAPIO ====================
CREATE TABLE tb_cardapio (
    id         BIGSERIAL PRIMARY KEY,
    id_unidade BIGINT       NOT NULL REFERENCES tb_unidade(id),
    nome       VARCHAR(100) NOT NULL,
    ativo      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- ==================== ITEM ====================
CREATE TABLE tb_item (
    id         BIGSERIAL PRIMARY KEY,
    nome       VARCHAR(100)   NOT NULL,
    descricao  VARCHAR(255),
    preco      NUMERIC(10, 2) NOT NULL,
    categoria  VARCHAR(20)    NOT NULL,
    status     BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP      NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- ==================== ITEM_CARDAPIO ====================
CREATE TABLE tb_item_cardapio (
    id          BIGSERIAL PRIMARY KEY,
    id_cardapio BIGINT    NOT NULL REFERENCES tb_cardapio(id),
    id_item     BIGINT    NOT NULL REFERENCES tb_item(id),
    disponivel  BOOLEAN   NOT NULL DEFAULT TRUE,
    ativo       BOOLEAN   NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255)
);

-- ==================== ESTOQUE ====================
CREATE TABLE tb_estoque (
    id                BIGSERIAL PRIMARY KEY,
    id_unidade        BIGINT    NOT NULL REFERENCES tb_unidade(id),
    id_item           BIGINT    NOT NULL REFERENCES tb_item(id),
    quantidade        INTEGER   NOT NULL DEFAULT 0,
    quantidade_minima INTEGER   NOT NULL DEFAULT 0,
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP,
    created_by        VARCHAR(255),
    updated_by        VARCHAR(255)
);

-- ==================== MOVIMENTACAO_ESTOQUE ====================
CREATE TABLE tb_movimentacao_estoque (
    id         BIGSERIAL PRIMARY KEY,
    id_estoque BIGINT       NOT NULL REFERENCES tb_estoque(id),
    id_usuario BIGINT       NOT NULL REFERENCES tb_usuario(id),
    quantidade INTEGER      NOT NULL,
    observacao VARCHAR(255),
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- ==================== PEDIDO ====================
CREATE TABLE tb_pedido (
    id               BIGSERIAL PRIMARY KEY,
    id_cliente       BIGINT         REFERENCES tb_cliente(id),
    id_unidade       BIGINT         NOT NULL REFERENCES tb_unidade(id),
    canal            VARCHAR(20)    NOT NULL,
    status           VARCHAR(30)    NOT NULL,
    valor_total      NUMERIC(10, 2) NOT NULL,
    meio_pagamento   VARCHAR(20)    NOT NULL,
    pontos_utilizados INTEGER,
    desconto_pontos  NUMERIC(10, 2),
    created_at       TIMESTAMP      NOT NULL,
    updated_at       TIMESTAMP,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255)
);

-- ==================== ITEM_PEDIDO ====================
CREATE TABLE tb_item_pedido (
    id               BIGSERIAL PRIMARY KEY,
    id_pedido        BIGINT         NOT NULL REFERENCES tb_pedido(id),
    id_item_cardapio BIGINT         NOT NULL REFERENCES tb_item_cardapio(id),
    quantidade       INTEGER        NOT NULL,
    preco_unitario   NUMERIC(10, 2) NOT NULL,
    created_at       TIMESTAMP      NOT NULL,
    updated_at       TIMESTAMP,
    created_by       VARCHAR(255),
    updated_by       VARCHAR(255)
);

-- ==================== DADOS INICIAIS ====================
INSERT INTO tb_perfil_autoridade (nome, descricao, status, created_at) VALUES
('CLIENTE',   'Perfil de cliente da rede',        TRUE, NOW()),
('ATENDENTE', 'Perfil de atendente de balcão',    TRUE, NOW()),
('GERENTE',   'Perfil de gerente de unidade',     TRUE, NOW()),
('MATRIZ',    'Perfil da matriz da rede',          TRUE, NOW());
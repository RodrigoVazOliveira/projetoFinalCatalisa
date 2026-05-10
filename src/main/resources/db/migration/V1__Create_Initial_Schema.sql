-- V1__Create_Initial_Schema.sql
-- Criação das tabelas iniciais da aplicação ZUPayments

-- Tabela: RESPONSAVEIS
CREATE TABLE IF NOT EXISTS responsaveis (
    email VARCHAR(100) PRIMARY KEY NOT NULL,
    nome_completo VARCHAR(100) NOT NULL,
    nome_do_projeto VARCHAR(70) NOT NULL,
    ativo BOOLEAN NOT NULL,
    CONSTRAINT uk_responsaveis_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: FORNECEDORES
CREATE TABLE IF NOT EXISTS fornecedores (
    cnpj_ou_cpf VARCHAR(25) PRIMARY KEY NOT NULL,
    razao_social VARCHAR(100) NOT NULL,
    logradouro VARCHAR(80) NOT NULL,
    numero VARCHAR(10) NOT NULL,
    bairro VARCHAR(60) NOT NULL,
    cidade VARCHAR(80) NOT NULL,
    estado VARCHAR(25) NOT NULL,
    cep VARCHAR(15) NOT NULL,
    telefone VARCHAR(25) NOT NULL,
    email VARCHAR(80) NOT NULL,
    categoria_de_custo VARCHAR(50) NOT NULL,
    ativo BOOLEAN NOT NULL,
    CONSTRAINT uk_fornecedores_cnpj_cpf UNIQUE (cnpj_ou_cpf)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: PEDIDOS_DE_COMPRAS
CREATE TABLE IF NOT EXISTS pedidos_de_compras (
    numero_de_pedido BINARY(16) PRIMARY KEY NOT NULL,
    data_de_vencimento DATE NOT NULL,
    saldo DOUBLE NOT NULL,
    data_de_pagamento DATE NOT NULL,
    responsavel_email VARCHAR(100),
    data_limite_envio DATE NOT NULL,
    forma_de_pagamento VARCHAR(50) NOT NULL,
    fornecedor_cnpj_ou_cpf VARCHAR(25),
    cancelado BOOLEAN NOT NULL,
    CONSTRAINT fk_pedidos_responsavel FOREIGN KEY (responsavel_email)
        REFERENCES responsaveis(email),
    CONSTRAINT fk_pedidos_fornecedor FOREIGN KEY (fornecedor_cnpj_ou_cpf)
        REFERENCES fornecedores(cnpj_ou_cpf),
    INDEX idx_pedidos_responsavel (responsavel_email),
    INDEX idx_pedidos_fornecedor (fornecedor_cnpj_ou_cpf),
    INDEX idx_pedidos_data_vencimento (data_de_vencimento),
    INDEX idx_pedidos_cancelado (cancelado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: NOTAS_FISCAIS
CREATE TABLE IF NOT EXISTS notas_fiscais (
    id BINARY(16) PRIMARY KEY NOT NULL,
    numero_da_nota BIGINT NOT NULL,
    fornecedor_cnpj_ou_cpf VARCHAR(25),
    valor_a_pagar DOUBLE,
    data_de_emissao DATE,
    data_de_envio DATE,
    responsavel_email VARCHAR(100),
    cancelar BOOLEAN,
    CONSTRAINT fk_notas_fornecedor FOREIGN KEY (fornecedor_cnpj_ou_cpf)
        REFERENCES fornecedores(cnpj_ou_cpf),
    CONSTRAINT fk_notas_responsavel FOREIGN KEY (responsavel_email)
        REFERENCES responsaveis(email),
    INDEX idx_notas_fornecedor (fornecedor_cnpj_ou_cpf),
    INDEX idx_notas_responsavel (responsavel_email),
    INDEX idx_notas_numero (numero_da_nota)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: NOTA_FISCAL_PEDIDO_COMPRA (Many-to-Many)
CREATE TABLE IF NOT EXISTS nota_fiscal_pedido_de_compra (
    nota_fiscal_id BINARY(16) NOT NULL,
    pedido_de_compra_numero_de_pedido BINARY(16) NOT NULL,
    PRIMARY KEY (nota_fiscal_id, pedido_de_compra_numero_de_pedido),
    CONSTRAINT fk_nf_pedido_nota FOREIGN KEY (nota_fiscal_id)
        REFERENCES notas_fiscais(id),
    CONSTRAINT fk_nf_pedido_pedido FOREIGN KEY (pedido_de_compra_numero_de_pedido)
        REFERENCES pedidos_de_compras(numero_de_pedido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabela: USUARIOS
CREATE TABLE IF NOT EXISTS usuarios (
    id BINARY(16) PRIMARY KEY NOT NULL,
    nome_completo VARCHAR(50) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    nivel_de_acesso VARCHAR(50),
    ativo BOOLEAN,
    data_hora_criacao DATETIME NOT NULL,
    data_hora_atualizacao DATETIME,
    CONSTRAINT uk_usuarios_email UNIQUE (email),
    INDEX idx_usuarios_email (email),
    INDEX idx_usuarios_ativo (ativo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


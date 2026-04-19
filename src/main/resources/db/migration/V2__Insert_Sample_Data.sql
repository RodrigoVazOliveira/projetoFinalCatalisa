-- V2__Insert_Sample_Data.sql
-- Dados de exemplo para desenvolvimento e testes

-- Insert de RESPONSÁVEIS
INSERT INTO responsaveis (email, nome_completo, nome_do_projeto, ativo) VALUES
('responsavel1@email.com', 'João Silva', 'PROJETO_A', TRUE),
('responsavel2@email.com', 'Maria Santos', 'PROJETO_B', TRUE),
('responsavel3@email.com', 'Pedro Oliveira', 'PROJETO_C', FALSE);

-- Insert de FORNECEDORES
INSERT INTO fornecedores (cnpj_ou_cpf, razao_social, logradouro, numero, bairro, cidade, estado, cep, telefone, email, categoria_de_custo, ativo) VALUES
('12.345.678/0001-00', 'Fornecedor ABC Ltda', 'Rua A', '123', 'Centro', 'São Paulo', 'SP', '01234-000', '(11)98765-4321', 'contato@abc.com', 'TECNOLOGIA', TRUE),
('87.654.321/0001-11', 'Fornecedor XYZ Ltda', 'Avenida B', '456', 'Vila Nova', 'Rio de Janeiro', 'RJ', '20000-000', '(21)99876-5432', 'contato@xyz.com', 'FACILITIES', TRUE),
('111.222.333/0001-44', 'Fornecedor Premium', 'Rua C', '789', 'Bairro', 'Belo Horizonte', 'MG', '30100-000', '(31)98765-4321', 'contato@premium.com', 'BENEFICIOS', TRUE);

-- Insert de USUÁRIOS
INSERT INTO usuarios (id, nome_completo, email, senha, nivel_de_acesso, ativo, data_hora_criacao) VALUES
(UNHEX(REPLACE('12345678-1234-5678-1234-567812345678', '-', '')), 'Admin Master', 'admin@zupayments.com', '$2a$10$tYQmQ2CSCF9UeJ4L4xPHee6uH4o0nQhEa0W08l1N5L5K5zKe8n4vu', 'PERFIL_MASTER', TRUE, NOW()),
(UNHEX(REPLACE('87654321-4321-8765-4321-876543218765', '-', '')), 'Usuario Financeiro', 'financeiro@zupayments.com', '$2a$10$tYQmQ2CSCF9UeJ4L4xPHee6uH4o0nQhEa0W08l1N5L5K5zKe8n4vu', 'PERFIL_FINANCEIRO', TRUE, NOW()),
(UNHEX(REPLACE('11111111-2222-3333-4444-555555555555', '-', '')), 'Usuario Compras', 'compras@zupayments.com', '$2a$10$tYQmQ2CSCF9UeJ4L4xPHee6uH4o0nQhEa0W08l1N5L5K5zKe8n4vu', 'PERFIL_COMPRAS', TRUE, NOW());


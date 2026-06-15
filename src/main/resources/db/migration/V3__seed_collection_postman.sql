INSERT INTO tb_perfil_autoridade (nome, descricao, status, created_at, created_by)
VALUES
    ('CLIENTE',   'Perfil de cliente da rede',     true, NOW(), 'seed'),
    ('ATENDENTE', 'Perfil de atendente de balcão', true, NOW(), 'seed'),
    ('GERENTE',   'Perfil de gerente de unidade',  true, NOW(), 'seed'),
    ('MATRIZ',    'Perfil de usuário da matriz',    true, NOW(), 'seed')
ON CONFLICT (nome) DO NOTHING;

INSERT INTO tb_unidade (nome_fantasia, cnpj, endereco, contato, regiao, estado, status, created_at, created_by)
VALUES (
    'Raízes do Nordeste - Centro',
    '12345678000190',
    'Rua XV de Novembro, 100 - Centro, Blumenau/SC',
    '47333312345',
    'SUL',
    'SANTA_CATARINA',
    true,
    NOW(),
    'seed'
) ON CONFLICT (cnpj) DO NOTHING;

INSERT INTO tb_usuario (nome, email, cpf, senha, status, id_unidade, created_at, created_by)
VALUES (
    'Carlos Eduardo Silva',
    'carlos.silva@raizesnordeste.com',
    '52998224725',
    '$2a$10$sEoyV5iz.Qt.LE/dNlkUVuQ1YLeIPSippB.HaPtqDAZ7d8CEyPjHa',
    true,
    (SELECT id FROM tb_unidade WHERE cnpj = '12345678000190'),
    NOW(),
    'seed'
) ON CONFLICT (email) DO NOTHING;

INSERT INTO usuario_perfil_autoridade (id_usuario, id_perfil_autoridade)
SELECT u.id, p.id
FROM tb_usuario u
CROSS JOIN tb_perfil_autoridade p
WHERE u.email = 'carlos.silva@raizesnordeste.com'
  AND p.nome  = 'GERENTE'
ON CONFLICT DO NOTHING;

INSERT INTO tb_item (nome, descricao, preco, categoria, status, created_at, created_by)
VALUES
    ('Baião de Dois',    'Arroz com feijão verde, queijo coalho e bacon', 32.90, 'REFEICAO', true, NOW(), 'seed'),
    ('Carne de Sol',     'Carne de sol grelhada com macaxeira frita',      45.90, 'REFEICAO', true, NOW(), 'seed'),
    ('Tapioca Recheada', 'Tapioca com carne seca e queijo coalho',        22.90, 'REFEICAO', true, NOW(), 'seed'),
    ('Suco de Cajá',     'Suco natural de cajá 500ml',                     9.90, 'BEBIDA',   true, NOW(), 'seed'),
    ('Suco de Umbú',     'Suco natural de umbu 500ml',                     9.90, 'BEBIDA',   true, NOW(), 'seed')
ON CONFLICT DO NOTHING;

INSERT INTO tb_cardapio (id_unidade, nome, ativo, created_at, created_by)
VALUES (
    (SELECT id FROM tb_unidade WHERE cnpj = '12345678000190'),
    'Cardápio Principal',
    true,
    NOW(),
    'seed'
) ON CONFLICT DO NOTHING;

INSERT INTO tb_item_cardapio (id_cardapio, id_item, disponivel, ativo, created_at, created_by)
SELECT
    (SELECT id FROM tb_cardapio WHERE nome = 'Cardápio Principal'),
    i.id,
    true,
    true,
    NOW(),
    'seed'
FROM tb_item i
WHERE i.nome IN ('Baião de Dois', 'Carne de Sol', 'Tapioca Recheada', 'Suco de Cajá', 'Suco de Umbú')
ON CONFLICT DO NOTHING;

INSERT INTO tb_estoque (id_unidade, id_item, quantidade, quantidade_minima, created_at, created_by)
SELECT
    (SELECT id FROM tb_unidade WHERE cnpj = '12345678000190'),
    i.id,
    100,
    10,
    NOW(),
    'seed'
FROM tb_item i
WHERE i.nome IN ('Baião de Dois', 'Carne de Sol', 'Tapioca Recheada', 'Suco de Cajá', 'Suco de Umbú')
ON CONFLICT DO NOTHING;
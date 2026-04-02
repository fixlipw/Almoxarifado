-- Limpa as tabelas existentes (opcional, dependendo de onde rodar)
DELETE FROM itens_pedido;
DELETE FROM pedidos;
DELETE FROM bloqueios;
DELETE FROM estoque;
DELETE FROM usuarios;

-- Insere 1 usuário (Aluno) e 1 Administrador
INSERT INTO usuarios (id, email, matricula, senha, nome, sobrenome, curso, foto_perfil, acesso, is_ativada, is_bloqueado)
VALUES
('11111111-1111-1111-1111-111111111111', 'aluno@ufc.br', '123456', 'senha_forte_hash', 'João', 'Silva', 'Sistemas de Informação', 'foto.png', 'ALUNO', true, false),
('66666666-6666-6666-6666-666666666666', 'admin@ufc.br', '987654', 'senha_admin_hash', 'Admin', 'Root', 'Administração', 'admin_foto.png', 'ADMIN', true, false);

-- Insere >20 itens no estoque (apenas Resistores e Protoboards)
INSERT INTO estoque (id, nome, quantidade, tipo, is_ativado) VALUES
('22222222-2222-2222-2222-222222222001', 'Resistor 10k ohms', 100, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222002', 'Resistor 1k ohms', 50, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222003', 'Resistor 220 ohms', 200, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222004', 'Resistor 330 ohms', 150, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222005', 'Resistor 4.7k ohms', 80, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222006', 'Resistor 100 ohms', 300, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222007', 'Resistor 47k ohms', 120, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222008', 'Resistor 1M ohms', 60, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222009', 'Resistor 2M ohms', 40, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222010', 'Resistor 5.6k ohms', 90, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222011', 'Resistor 10 ohms', 150, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222012', 'Resistor 470 ohms', 200, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222013', 'Resistor 680 ohms', 100, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222014', 'Resistor 820 ohms', 110, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222015', 'Resistor 1.5k ohms', 80, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222016', 'Resistor 2.2k ohms', 130, 'COMPONENTE', true),
('22222222-2222-2222-2222-222222222017', 'Protoboard 400 furos', 10, 'EQUIPAMENTO', true),
('22222222-2222-2222-2222-222222222018', 'Protoboard 830 furos', 15, 'EQUIPAMENTO', true),
('22222222-2222-2222-2222-222222222019', 'Protoboard 170 furos', 20, 'EQUIPAMENTO', true),
('22222222-2222-2222-2222-222222222020', 'Protoboard Transparente 400 furos', 5, 'EQUIPAMENTO', true),
('22222222-2222-2222-2222-222222222021', 'Protoboard Base Metálica', 3, 'EQUIPAMENTO', true),
('22222222-2222-2222-2222-222222222022', 'Protoboard Duplo 1660 furos', 2, 'EQUIPAMENTO', true);

-- Insere Vários Pedidos associados ao Aluno (Diferentes status)
INSERT INTO pedidos (id, codigo_pedido, feedback, solicitante_id, aprovador_id, finalizador_id, data_solicitacao, data_aprovacao, data_finalizado, data_atualizacao, aprovado, finalizado, emprestimo_especial, hash)
VALUES
-- PED-001: Pendente (Aguardando aprovação)
('33333333-3333-3333-3333-333333333331', 'PED-001', 'Preciso para a aula de circuitos', '11111111-1111-1111-1111-111111111111', NULL, NULL, '2026-04-01 10:00:00', NULL, NULL, '2026-04-01 10:00:00', false, false, false, 'hash123'),

-- PED-002: Aprovado (Em andamento)
('33333333-3333-3333-3333-333333333332', 'PED-002', 'Projeto integrador', '11111111-1111-1111-1111-111111111111', '66666666-6666-6666-6666-666666666666', NULL, '2026-03-25 14:00:00', '2026-03-25 14:30:00', NULL, '2026-03-25 14:30:00', true, false, false, 'hash124'),

-- PED-003: Finalizado (Devolvido/Concluído)
('33333333-3333-3333-3333-333333333333', 'PED-003', 'Prática de eletrônica', '11111111-1111-1111-1111-111111111111', '66666666-6666-6666-6666-666666666666', '66666666-6666-6666-6666-666666666666', '2026-03-10 09:00:00', '2026-03-10 09:30:00', '2026-03-12 16:00:00', '2026-03-12 16:00:00', true, true, false, 'hash125'),

-- PED-004: Especial Pendente (Requer mais tempo)
('33333333-3333-3333-3333-333333333334', 'PED-004', 'Competição de robótica em outra cidade', '11111111-1111-1111-1111-111111111111', NULL, NULL, '2026-04-01 11:30:00', NULL, NULL, '2026-04-01 11:30:00', false, false, true, 'hash126'),

-- PED-005: Rejeitado / Faltou Estoque - Rejeitado pelo admin (marcado como finalizado sem ser aprovado)
('33333333-3333-3333-3333-333333333335', 'PED-005', 'Material em falta. Tente novamente próxima semana.', '11111111-1111-1111-1111-111111111111', '66666666-6666-6666-6666-666666666666', '66666666-6666-6666-6666-666666666666', '2026-03-20 08:00:00', NULL, '2026-03-21 09:00:00', '2026-03-21 09:00:00', false, true, false, 'hash127');

-- Insere Itens nos Pedidos correspondentes
INSERT INTO itens_pedido (id, pedido_id, estoque_id, quantidade_item)
VALUES
-- Itens do PED-001 (Pendente)
('44444444-4444-4444-4444-444444444401', '33333333-3333-3333-3333-333333333331', '22222222-2222-2222-2222-222222222001', 5),
('44444444-4444-4444-4444-444444444402', '33333333-3333-3333-3333-333333333331', '22222222-2222-2222-2222-222222222017', 1),

-- Itens do PED-002 (Aprovado)
('44444444-4444-4444-4444-444444444403', '33333333-3333-3333-3333-333333333332', '22222222-2222-2222-2222-222222222004', 10),
('44444444-4444-4444-4444-444444444404', '33333333-3333-3333-3333-333333333332', '22222222-2222-2222-2222-222222222018', 2),

-- Itens do PED-003 (Finalizado)
('44444444-4444-4444-4444-444444444405', '33333333-3333-3333-3333-333333333333', '22222222-2222-2222-2222-222222222002', 3),

-- Itens do PED-005 (Rejeitado)
('44444444-4444-4444-4444-444444444409', '33333333-3333-3333-3333-333333333335', '22222222-2222-2222-2222-222222222003', 50);

-- Insere 1 Bloqueio (gerenciado pelo Admin)
INSERT INTO bloqueios (id, usuario_id, admin_block_id, admin_unblock_id, motivo_bloqueio, data_bloqueio, motivo_desbloqueio, data_desbloqueio)
VALUES ('55555555-5555-5555-5555-555555555555', '11111111-1111-1111-1111-111111111111', '66666666-6666-6666-6666-666666666666', '66666666-6666-6666-6666-666666666666', 'Não devolveu os protoboards no prazo', '2026-03-01 10:00:00', 'Devolveu', '2026-03-05 10:00:00');


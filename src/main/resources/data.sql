INSERT INTO tb_roles (role_id, name) VALUES (1, 'ADMIN')
ON CONFLICT (role_id) DO NOTHING;

INSERT INTO tb_roles (role_id, name) VALUES (2, 'BASIC')
ON CONFLICT (role_id) DO NOTHING;

INSERT INTO tb_status (status_id, name) VALUES (1, 'REGULAR')
ON CONFLICT (status_id) DO NOTHING;

INSERT INTO tb_status (status_id, name) VALUES (2, 'CANCELADO')
ON CONFLICT (status_id) DO NOTHING;

INSERT INTO tb_status (status_id, name) VALUES (3, 'FALECIDO')
ON CONFLICT (status_id) DO NOTHING;

INSERT INTO tb_status (status_id, name) VALUES (4, 'TRANSFERIDO')
ON CONFLICT (status_id) DO NOTHING;

INSERT INTO tb_status (status_id, name) VALUES (5, 'SOB_ANALISE')
ON CONFLICT (status_id) DO NOTHING;
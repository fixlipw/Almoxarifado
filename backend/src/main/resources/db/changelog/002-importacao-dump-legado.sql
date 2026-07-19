--liquibase formatted sql

--changeset desou:002-importacao-dump-legado
--comment: Cria uma snapshot de demonstração usando usuários e itens do dump PostgreSQL de 2026-07-19.
-- O catálogo é normalizado como ativo e com disponibilidade total; os pedidos abaixo são dados fictícios.

INSERT INTO ${appSchema}.usuarios (id, bloqueado, cpf, curso, email, foto_perfil, matricula, nome, nome_social, papel,
                                   status, username)
VALUES (6, false, '39463152455', NULL, 'develop3@mail.com', NULL, NULL, 'Developer admin', NULL, 'ADMIN', true,
        'develop');
INSERT INTO ${appSchema}.usuarios (id, bloqueado, cpf, curso, email, foto_perfil, matricula, nome, nome_social, papel,
                                   status, username)
VALUES (7, false, '46882959466', NULL, 'admin@mail.com', NULL, NULL, 'Admin Dev', NULL, 'ALUNO', true, 'admin');
INSERT INTO ${appSchema}.usuarios (id, bloqueado, cpf, curso, email, foto_perfil, matricula, nome, nome_social, papel,
                                   status, username)
VALUES (12, false, '20501427022', NULL, 'developg@mail.com', NULL, NULL, 'Developer Gestor', NULL, 'ALUNO', true,
        'developg');
INSERT INTO ${appSchema}.usuarios (id, bloqueado, cpf, curso, email, foto_perfil, matricula, nome, nome_social, papel,
                                   status, username)
VALUES (16, false, '90528921311', NULL, 'junior.fernandes@ufc.br', NULL, NULL, 'Junior Fernandes', NULL, 'ADMIN', true,
        'junior');

SELECT setval(pg_get_serial_sequence('${appSchema}.usuarios', 'id'),
              COALESCE((SELECT MAX(id) FROM ${appSchema}.usuarios), 1), true);

INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('ee78b2fa-3bc0-4e55-b5c1-6584dfa62035', true, 'Protoboard 40 - 2018006522', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('6a9a6884-c04b-4a67-8d28-3cf3cee57362', true, 'BeagleBone Black - #01-1026-994743', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('7aadb996-fa98-4315-b88c-18fb29d8fcdd', true, 'BeagleBone Black - #02-0077-994744', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('ef579573-905c-4b5c-940f-b75b01f7e1a0', true, '1N4741', 99, 99, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('da784bbb-28c2-41b0-adec-4fdd5f93a3e6', true, '1N4728', 55, 55, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('1306e611-f7bb-4282-b262-d7d0c056e10b', true, '1N4148', 182, 182, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('1094b792-9ddb-4afa-a01a-ee131d338b08', true, '4013', 47, 47, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('5ffb8050-7211-4ed7-bdaf-02e878a2a662', true, 'BC 337', 141, 141, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d7c837d5-1bd5-490e-85df-8fc768a9aaba', true, 'ADS1115', 1, 1, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('b40b0c82-c9c6-4b9b-90d8-0132780686e2', true, '7473', 44, 44, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('3b785835-94bc-47d8-833b-807c88e75d97', true, '74109', 49, 49, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('b49025ca-8c9b-4c47-9f6c-fece9659f394', true, '1N4730', 191, 191, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('a306150a-1547-4e67-a26a-0671d0cd1364', true, 'BeagleBone Black - #07-1052-994761', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('31c6af57-802b-42e0-9753-1a20778a7796', true, '7906', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('28a3878a-5673-4cf4-a9be-2de041d20386', true, 'AC 4N25', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('1988ab0e-889d-411d-a156-a88220d14e89', true, 'BeagleBone Black - #05-9099-994769', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('fdecf557-c1c8-40cc-abbc-1a05758fd152', true, '74LS173', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('98d37e4a-b2b2-4ec6-948b-8173f2bddc7b', true, 'BeagleBone Black - #08-7188-994748', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('6e00c547-24e6-4e24-8c34-d754e40c9b17', true, 'ADS1115', 6, 6, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('5fbdcac2-32e3-4612-923d-ae7364ebf9b8', true, 'BC237', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('aa99ee1e-b33e-4730-b915-66b7eda7867b', true, 'BeagleBone Black - #10-6669-994755', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('ffd6c0c3-cb31-40a8-9780-bdf6b4a92cb0', true, 'BeagleBone Black - #12-1521-994770', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('24ca7009-e3e1-4143-ad04-472b31c88752', true, '7905', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('05b79899-3ae3-40b0-b785-5b487cedef96', true, 'BC239', 55, 55, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d43bd914-f64e-4292-a37d-81d8d36b2f9c', true, 'BeagleBone Black - #04-5589-994758', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('ad070750-e3e2-42ea-86ce-c1896c8b05ea', true, 'BeagleBone Black - #06-1511-994742', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('6d305365-dc64-46e7-9b9d-02a0f0e13d43', true, '1N4732', 201, 201, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f1314f7d-21c6-4322-bd93-5c2e1a098d66', true, '74LS48', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('19a6007c-f4fd-4a1f-995d-a1fdeabfe795', true, '74LS147', 51, 51, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d07cd7f9-69bc-41c6-8f64-65677a1e740a', true, '74LS42', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('171b83a8-2beb-4353-821c-697ca1de7de2', true, '74LS174', 79, 79, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('ec5c0f0c-31cd-42fc-b566-5c02d50a8652', true, '74LS00', 91, 91, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('5dc87971-94b8-43ca-b9b0-bdcc78c1b12d', true, 'ADC0808', 73, 73, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('61553b83-b6f0-4dba-81ae-02476348df20', true, '7818', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('6a6927ac-07e6-4f35-bb5f-de5c63457d4e', true, '74LS10', 89, 89, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('bed0bc5d-d3a7-4b0c-bca4-a04a2532659c', true, '74LS153', 150, 150, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('67baa85d-242e-4b23-a810-3a3f1177b47d', true, '74LS08', 149, 149, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('7a58b990-78b3-4bda-b717-0e3293bb6b82', true, 'BeagleBone Black - #03-5202-994757', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('9c2e6a1f-581a-4ba3-85b7-a90bb1d295ed', true, '1N4733', 219, 219, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('0418ee6a-8a73-441e-987a-906757647b8d', true, '1N4742', 378, 378, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('ef79e35c-17ff-4f21-9815-732d3448486d', true, 'BC558', 150, 150, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('845e9435-6b5b-44be-a794-185cd5032aae', true, '74LS11', 43, 43, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('27b0130a-9ab4-4746-b727-3b04f0faea5f', true, '74LS02', 134, 134, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('1a4e82e6-6003-40c3-86ce-be424c8d5d63', true, '74LS11', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('c75bdcee-db26-4622-979b-e891dfe42410', true, '74LS76', 48, 48, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('232a3be9-d3a2-4817-bf5a-b1445c534c12', true, 'BC 338', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('5d37c1cc-60a2-4388-9658-4a077a3dac5d', true, '7812', 99, 99, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('0bdca422-12db-4cfc-bc05-0c32cbab10cd', true, 'ZYBO 25 - 995082 - D778457', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('1358f7d3-7d95-48ec-85e2-94a178079ea3', true, 'Borne Terminal', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('63f634d5-4c83-474e-bc70-d158a2158036', true, 'Garra Jacaré Grande Vermelho', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('973c2ec6-edc1-4443-ad45-63460c4191e3', true, 'ZYBO 18 - 995097 - D787986', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('9007ae3a-c894-4c80-94d1-1ea4b8ef31d2', true, 'ZYBO 30 - 995087 - D787539', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('890f279a-9941-4c46-a7bd-70d0891cdb45', true, 'Diodo 1N5333', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('56cd3c86-f34f-4a36-8a94-a120e397650b', true, 'Resistor 6k80', 300, 300, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('197f2f60-870d-4b46-9855-1f913830e16b', true, 'Resistor 820Ω', 407, 407, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('77206838-4539-49c5-b189-303de7431a74', true, 'ZYBO 26 - 995093 - D788010', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('3dba23c0-4ed3-44f6-88bc-dd5d309c33b2', true, 'IRF 450', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('1223d595-9beb-4703-8af9-eee5ae657458', true, 'Protoboard 03 - 2018006534', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d9708777-0ebd-4166-ab72-010c4cf8a492', true, 'Diodo 1N965', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('50393cbe-bd77-4fe2-8abe-a05e9a791959', true, 'ZYBO 20 - 995091 - D787991', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('e69a4bb8-54b2-45db-adbe-6079e503c7f8', true, 'tv Box - Mx Q Pro 4K', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('8ff113df-4c81-4f88-946f-4dfe4c584ddc', true, 'Cristal Quartzo 20mh', 10, 10, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('eee66d01-c14f-4f75-bf0a-57ddc508c880', true, 'ZYBO 33 - 995083 - D787545', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('4cce3230-cfe1-4e7d-93b6-d6fb4453836e', true, 'LED 3mm - VERDE', 98, 98, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('9a12d639-8706-4df6-bfba-e00d9c1803c2', true, 'Capacitor Cerâmico 12pF 50V', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('1df27682-31f8-4888-a571-650cce061ffc', true, 'Capacitor Cerâmico 68pF 50V', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('0004a2d5-988d-45a6-b4b0-3a5ca58b1590', true, 'Protoboard 06 - 2018006539', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('657f3dec-2b9c-408a-b62b-164b5cd582a4', true, 'Capacitor Eletrolítico 470uF 35V', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('bee9613b-6da6-4fff-aa80-c87f08d64b96', true, 'cabo USB (BBB)', 2, 2, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('568d7098-20cd-45e0-a728-4991c510ebfe', true, 'Capacitor Cerâmico 39pF 50V', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('e0bbfd84-23d9-4467-b639-9ceaa87f09ca', true, 'BF245', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('19d80256-3d49-40e4-b0ce-bc09597a59cf', true, 'Protoboard 50 - 2018006525', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('45556a0a-57c2-43ac-954d-bbccef6e13c1', true, 'Protoboard 35 - 2018006510', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f9f5b76d-aacd-417b-9c3f-716d7d190c5c', true, 'Capacitor Eletrolítico 22uF 25V', 98, 98, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('4043c910-796d-46d7-8e83-fc3d488261dd', true, 'Capacitor Eletrolítico 1000uF 50V', 95, 95, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f4392bf2-eb00-4562-a794-678097c327ea', true, 'Resistor 8k20', 419, 419, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('5cd0cae1-56d9-46a0-8697-43b5cd16cb58', true, 'Resistor 1500', 82, 82, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('53f4c06e-87e3-4b7f-b4e6-7711abaee345', true, 'Garra Jacaré Preto 5x25mm', 48, 48, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('7764a9c4-cfea-4936-b17d-b7d62aa3b995', true, 'Protoboard 39 - 2018006504', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('de1b4346-aa2f-4539-8acd-313e2250858e', true, 'ZYBO 29 - 995100 - D787983', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('6e6eb934-203d-40e9-82a1-d238cbbb1aa4', true, 'Protoboard 09 - 2018006532', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('e593d3ac-5e56-4fe4-b7f9-a50f46a5b089', true, 'ZYBO 17 - 995104 - D786714', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d36e5bf9-e44f-4e94-85bd-6bf3a7f690d4', true, 'Resistor 330', 229, 229, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('5205130a-fed0-468f-8474-e20b840eefb7', true, 'Conector Áudio/Vídeo - Fêmea', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('936b931f-e4f8-49c5-9d01-966a3f590a81', true, 'Capacitor Cerâmico 330pF 50V', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('4554f944-94ff-470b-8098-cbf2b1a633ab', true, 'L298N', 105, 105, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('39878e98-4eec-431d-95bf-6650160136ad', true, 'Protoboard 08 - 2018006514', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('70545265-80b1-468b-b0e2-976118999ab1', true, 'ZYBO 12 - 995085 - D787870', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('e9ddbf6d-e0d7-453e-afac-bc278c4a34ea', true, 'ZYBO 06 - 995088 - D787530', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('cf2d15bd-e41c-4791-9b86-75d37445d521', true, 'Capacitor Cerâmico 18pF 100V', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('8feebe8e-96cc-4d11-b0d9-8bc3e13ee456', true, 'Protoboard 36 - 2018006508', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('5d51eae6-ade4-429e-8a56-fdf4bdc83aa9', true, 'Protoboard 21 - 2018006530', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f065b049-ea11-49aa-8e12-391c5601b79b', true, 'ZYBO 32 - 995080 - D788003', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('84f8c243-f1ef-4f01-89ee-108913432ca5', true, 'Cabo TTL', 5, 5, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('be5aa9f6-21dd-452b-be8c-434cfe7724a1', true, 'TCA 785', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('0f3076f2-8194-4bde-8a61-577156cbb546', true, 'Protoboard 05 - 2018006506', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('5a7c14ad-b4d1-426b-b556-591948c4e1eb', true, 'Protoboard 38 - 2018006515', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('c0bbcab1-3cf6-48be-8e5a-26130b841f97', true, 'Resistor 5W 3300', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('2586808a-fe37-4b22-b277-25aab01fac27', true, 'LM 567', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('352195a8-b83d-42f4-9821-1e7257f68d78', true, 'ZYBO 05 - 995078 - D786693', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('6f0ea52e-5545-41d4-b515-fddc170bd301', true, 'ZYBO 04 - 995112 - D787544', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('8734be2b-e715-4720-8809-728bf86b61d6', true, 'Resistor 10W 100', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('be80330a-c00e-46d8-a12b-2848f77987fd', true, 'Cabo Grande Compatível com BBB', 1, 1, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('856f7e37-08b8-4904-a164-8c68b60aaec1', true, 'Resistor 220ΚΩ', 380, 380, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('0b8df808-9b66-4c99-ae62-3d8c200e9bce', true, 'Protoboard 01 - 2018006519', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('c9c59aea-de7b-4dce-84e0-f93d8361c98a', true, 'ZYBO 34 - 995113 - D786691', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('6cde8ca1-563f-420f-befb-d716756a2838', true, 'Resistor 1000', 291, 291, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('dd71a426-05a4-4411-ac89-68affd24f348', true, 'CNY 70', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('46aa7962-8aca-4973-8f34-846404767274', true, 'LED 5mm - VERMELHO', 91, 91, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('9a89b1b6-b05e-4cc5-8edd-d26c0621317a', true, 'ZYBO 09 - 995076 - D788015', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('69ced5c0-9922-44b8-a8eb-ec92367fd155', true, 'Protoboard 49 - 2018006538', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('2756b07b-d6e4-4642-aaf8-da86f068d35e', true, 'Potenciômetro 1k', 43, 43, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('aaa2f8a3-8900-44a4-aa37-c89d3a9daee5', true, 'Protoboard 22 - 2018006533', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d7a1b589-8cf5-4227-967e-8a8fb2c8eedf', true, 'Driver L293', 50, 50, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('a79e43a2-d32d-4d1c-b6d2-edae61ee545f', true, 'Resistor 200', 234, 234, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('68562167-f1e1-4f1c-a03b-9d7e696a21bd', true, 'LF356', 47, 47, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('e37fc50b-3426-40bf-a5da-8d7bf2d06ddd', true, 'DAC0808', 55, 55, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('0cde31b9-de65-4bfb-946d-97e6ddbb3997', true, 'Protoboard 27 - 2018006542', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('c26a33bf-17e9-498a-99af-82fc938c11f3', true, 'Resistor 110Ω', 319, 319, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('eb08e403-a5df-496a-8e4f-f76f600e4d50', true, 'Capacitor Cerâmico 820pF 50V', 95, 95, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('bbf38d54-c1c1-4521-809d-b8a94f35361a', true, 'Capacitor Eletrolítico 2.2uF 25V', 105, 105, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('0a5a2942-4822-4dfc-bc48-1d61f766d4f7', true, 'ZYBO 35 - 995109 - D787985', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('b59471b7-beb7-4280-aa35-78732098834c', true, 'ZYBO 14 - 995098 - D787982', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('80250203-ee6c-4aee-9b9f-ecb8b310c29e', true, 'Indutor 10uh', 108, 108, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('04a82db6-8e8e-485a-8590-859031d6a1da', true, 'LM567', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('2a21de1f-5a2b-48ff-b8df-7e41696c8a28', true, 'Resistor 820ΚΩ', 294, 294, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d882e2d5-1a60-4498-858a-4430fc5ea98f', true, 'LM3914', 54, 54, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('50eee8e0-3452-4501-b8fc-c426c0bb3290', true, 'Protoboard 02 - 2018006500', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('2ee9693a-5c07-4bb1-9dc0-b5e3ff832c51', true, 'Fusível 1A', 11, 11, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('9dcac7cc-aa2f-4e24-8a8b-15ddcf32ba49', true, 'IRF 640', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('0ecbc069-5880-4573-bd70-052b50d3b4f9', true, 'Capacitor Eletrolítico 1uF 63V', 84, 84, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('0a392d80-7776-4e96-a0f2-b99373429eaf', true, 'Capacitor Cerâmico 56pF 50V', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('bd4285ee-3906-47ea-bdaf-b85bad630d42', true, 'Potenciômetro 100kr', 46, 46, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('29c48cf7-bc42-4b84-bb86-35ce75140782', true, 'ZYBO 38 - 995103 - D653366', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('e74477e6-b878-47d0-ba33-84c65bc1b2fe', true, 'ZYBO 02 - 995116 - D786715', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('481c7a79-036c-4549-b410-9767a5edfa33', true, 'LM35', 20, 20, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('ff7e549f-2db2-46cc-83a1-7af595d18ffa', true, 'Relógio', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('3b467e73-9132-4197-9728-459c915cb33d', true, 'LM324', 49, 49, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('25d5e4cc-5626-45ea-a68e-0e11cf135b6d', true, 'BeagleBone Black - #15-8787-994768', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('1a55222b-cf7b-477b-95eb-8fe8f0bf3b06', true, 'Protoboard 17 - 2018666526', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('ce6d4fdd-0948-4e85-84f1-20bee181b2eb', true, 'Resistor 3900', 307, 307, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f36d5e86-66eb-4f0b-b681-c18895a5be2a', true, 'ZYBO 37 - 995102 - D786695', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('b9261666-3ae0-458e-88ac-e824735ce094', true, 'Capacitor Cerâmico 2.2pF 50V', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('70e49296-872a-4f7e-9481-98a83e0b82bc', true, 'Potenciômetro 100r', 48, 48, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('39cad9bb-9c6d-47dc-95a1-252504c59f4b', true, 'LED 3mm - ROSA', 72, 72, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f8f6b8b6-2ccb-40ed-99b4-1af4b950011f', true, 'Carro AWS', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('98b56dc8-e3ba-47b7-a446-ad80fd3cd08d', true, 'Protoboard 24 - 2018006513', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('04094850-53a7-48a8-9dd2-2ac56a4aa856', true, 'ZYBO 13 - 995092 - D786709', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('2aea084b-aab0-4db2-aefa-58ebca358248', true, 'TIP 42', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('4b736adc-ab47-4db9-b403-0652a4b30338', true, 'CL 555', 99, 99, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('c91cab91-1976-4f2e-89c7-e87c6e1436cb', true, 'Resistor 5k60', 388, 388, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('be60de49-d249-495c-8bfb-11b64b086071', true, 'ZYBO 21 - 995081 - D787979', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('8c640674-ae25-4c3b-8583-73115e649379', true, 'ZYBO 19 - 995096 - D787540', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('51af6e82-9917-4c6b-9471-0679185f89e8', true, 'Resistor 3K3', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('a4e1537e-ac3d-4f0a-8198-636832b3ee85', true, 'Protoboard 48 - 2018006505', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('93f4b75c-19ca-4423-8c5a-61117a57c827', true, 'Protoboard 37 - 2018006507', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('e685198d-3b74-45fc-a7ab-4403b188ac43', true, 'Protoboard 15 - 2018006524', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('39d30780-c251-45ca-b74b-68308834cbc0', true, 'Capacitor Eletrolítico 220uF 63V', 3, 3, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('290511a9-cd34-4e95-a0fe-3e05f9bd7a13', true, 'Conector Áudio/Vídeo - Macho', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('528bf201-7b9b-4793-a46a-c835364dd441', true, 'Fusível 10A', 4, 4, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('e6d51180-99f0-4877-884e-0b06045cd580', true, 'TIP 127', 49, 49, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('fb8254e2-76b4-4447-90f3-0f2f8c22a751', true, 'Protoboard 46 - 2018006521', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('fc122270-fc70-4d4e-b183-dc3767963fc2', true, 'Capacitor Cerâmico 15pF 50V', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('2c0ddb02-8d09-4d68-b301-b30aa977e6a6', true, 'DIP', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f128e2f4-b86d-49f6-a3a8-f5f4579720dc', true, 'ZYBO 08 - 995089 - D787532', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('65593021-ee5d-4785-91d7-960163d67ee5', true, 'Protoboard 47 - 2018006547', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('c59a733c-fcb5-4e79-90dd-d1999a8f455b', true, 'Capacitor Cerâmico 8.2pF 50V', 93, 93, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d0169d4b-4c83-42e6-9018-c11ef7afacd8', true, 'Resistor 22Ω', 313, 313, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d0d7672a-1e2d-403c-b438-6d7123e992d0', true, 'Capacitor Cerâmico 470pF 50V', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('57fb778b-0121-4db9-8a9e-9f9353917c41', true, 'Protoboard 45 - 2018006535', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('fffb028c-3756-452f-a099-c55eb8438862', true, 'Capacitor Eletrolítico 1000uF 63V', 94, 94, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('5d097356-7111-459a-ba9f-9c78ae0d1b56', true, 'IRG 4BC30', 25, 25, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d7439215-aa4f-4541-b8dc-ac3504c09ece', true, 'IM324', 146, 146, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('5c917999-1acd-4795-9762-7bf61fa95c34', true, 'IRF 740', 50, 50, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f39db22b-a58f-46fe-ae66-2af50736e2db', true, 'display lcd 16x2', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('90bd8155-b3ab-4310-ba18-026332b7326a', true, 'TIP 41', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('58ea8929-59ab-48ef-9129-2bb88cfb170b', true, 'Max 232', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('5cfaa4a4-5adb-4112-953c-9b1ebc80e6b1', true, 'Protoboard 07 - 2018006512', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('a2c9bfad-f74a-44dd-99da-6240d0430dcd', true, 'TV Box - Mx Q Pro', 2, 2, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('8317856d-e7ba-41f1-961d-c808eeee67a5', true, 'Soquete 14 Pinos', 86, 86, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('886b79a0-9dfa-4622-8af5-746e7b6ee4ed', true, 'Display 7 segmentos', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('2942638e-a748-4c42-be00-6df8378935d6', true, 'ZYBO 03 - 995110 - D788013', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('3d3bb25f-5c33-41fc-9372-03f59154c338', true, 'ZYBO 10 - 995111 - D654316', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('a1d9b83f-7c31-42b7-984a-e75d0c5f91f6', true, 'Resistor 100', 185, 185, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('bb898104-0493-4b18-b196-4ae1071c983b', true, 'Protoboard 32 - 2018006548', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('e10e3919-26be-4d56-b885-b49d8472ec46', true, 'Módulo bluetooth', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('0501cbce-2855-467a-a21d-98f4a56ade51', true, 'Protoboard 18 - 2018006537', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('05622f4d-a9cc-4ca9-8908-a914b9acd837', true, 'MG90', 20, 20, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('95c46878-49ff-43e7-9aa9-b04943c3b96c', true, 'Capacitor Eletrolítico 1000uF 35V', 92, 92, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('e60981c0-ca03-4ea8-97ba-113cd882312d', true, 'Protoboard 42 - 2018006501', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('331300d1-ed6b-499d-9a3e-8dd518651b6b', true, 'ZYBO 07 - 995094 - D785791', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('906a5375-7a12-47b2-9c68-394711f1be60', true, 'Capacitor Cerâmico 220pF 50V', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f70f56c2-b9b4-47b2-81ce-078da6e8d523', true, 'Soquete 40 Pinos', 49, 49, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('5b68979d-abc7-45f7-adda-e5d0f25af95c', true, 'Indutor 100uh', 99, 99, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('4082adda-f0b0-47c7-9348-a61187b237e0', true, 'ZYBO 39 - 995077 - D786696', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f1d58ab7-96c1-427f-8c59-59edad5c9417', true, 'Resistor 5W 1000', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('9ee7d414-ab03-46d2-8500-356a5bffefc6', true, 'Protoboard 29 - 2018006544', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('dd116620-e0c5-4909-937b-aed54c324066', true, 'Fusível 0,5A', 12, 12, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('96952c05-a9f9-4c6b-8aa4-e45232034cb6', true, 'LED 5mm - AMARELO', 1, 1, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f51a7f5b-e9e8-498b-96f9-1cbed6797001', true, 'Protoboard 19 - 2018006529', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('1ca21952-b7a2-4108-9d97-841f6b0e1612', true, 'MG90s', 16, 16, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('95df2d85-6467-4606-9a36-97af0ed05a00', true, 'PCB dupla face 10x15', 18, 18, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('842c922b-7fa5-42a8-8ff7-462201aef6cc', true, 'Pino Banana', 14, 14, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('8f2bacb4-c64d-4e56-a710-7c109d635b11', true, 'Cristal Quartzo 16mh', 14, 14, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('88da0806-e9b1-40ab-8424-f46bbdf85a99', true, 'Resistor 82Ω', 317, 317, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f9615ebb-d85e-4166-8a45-350de16b72a4', true, 'DIAC DB3', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('69f2e20e-7cee-4632-b2c5-6337fffd5015', true, 'Protoboard 12 - 2018006523', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('c0143de6-8fe2-4a2f-ba61-664ab07197fb', true, 'LM 613', 48, 48, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('927e668e-2c15-4d9b-a8e2-fc8e2a196573', true, 'Resistor 10W 1000', 101, 101, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('4f24f2d1-92f3-409e-a1c9-ebe8d24180f7', true, 'LM2907', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f18cf88a-0d71-4732-a8ac-321650dce1e0', true, 'Capacitor Cerâmico 33pF 50V', 101, 101, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('94833a7a-22e8-4ba6-a823-6a4265178e4a', true, 'BeagleBone Black - #13-0540-994749', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('4e5e522e-8c64-429c-bd45-c7cc7fbf21a2', true, 'Resistor 4k30', 95, 95, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('b2a72d3f-ae05-49c3-b5c2-78d45531d1fb', true, 'Protoboard 16 - 2018006527', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('1c8a7c42-5d8c-4e8f-ba99-ec5785ce340d', true, 'Capacitor Eletrolítico 20uF 63V', 2, 2, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('0f6e86f2-35b3-478b-84d5-d5978ede11f6', true, 'ZYBO 36 - 995109 - D788002', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('56ff07b2-136c-47fa-853b-c4c116b22108', true, 'ZYBO 31 - 995107 - D786708', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f7c1fa3f-adde-4404-86f3-f7694db73f2e', true, 'ZYBO 28 - 995108 - D788011', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('e3af9b23-61fe-47e1-b0fd-f1e64ed65f55', true, 'ZYBO 15 - 995084 - D787999', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('4be018ea-8552-42d7-ade9-1731cb648e96', true, 'Protoboard 13 - 2018006517', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('16e95405-f066-43e5-bd37-46e19a4c63b7', true, 'IRF 470', 49, 49, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f4a95e32-7637-4573-bb23-c13b16bbde04', true, 'Resistor 10W 680', 98, 98, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d07f4dbc-5dde-429e-87a8-250c51e035cc', true, 'Protoboard 11 - 2018006528', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d871eec2-a504-43c1-9a2b-74bc29415eaf', true, 'Resistor 6k20', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('79ed7974-410d-4415-8164-135442484590', true, 'Jump Fêmea', 10, 10, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('faa84783-54d8-4669-a146-4c68946d8b17', true, 'BC 557', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('2271d1fc-ea9e-407b-91f6-dc6dfe14708d', true, 'Pneus Kit Mindstorms', 4, 4, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('369d53ae-cf3d-4325-9d34-4aa1090d16cc', true, 'Sensor RFID', 1, 1, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('2f614026-67c7-4d1d-a164-567aef9a36d8', true, 'IM710', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('c83892ef-0dde-468a-bfb5-76e5d4910423', true, '74LS04', 85, 85, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('b0bf89c3-e4fe-4c42-9c59-eceb0ea324a5', true, 'ZYBO 23 - 995101 - D787984', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('12acc99e-5312-45c1-9292-c7ffb74d482d', true, 'Capacitor Eletrolítico 1uF 25V', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('cdc91039-9378-4ea6-ab87-aa74aef71259', true, 'Fusível 5A', 2, 2, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d2d8f9ac-57bb-461e-ba95-d71f7c61143f', true, 'Protoboard 44 - 2018006520', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('55028c19-5845-4733-8eed-5dfd6f2e0ce4', true, 'ZYBO 01 - 995105 - D787547', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('ac075ec2-b287-430e-88eb-d4f76eef2394', true, 'Fusível 1A', 19, 19, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('1d7fdd99-799b-4207-85fd-d3a80869b43d', true, 'Soquete 16 Pinos', 110, 110, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f0fb4a44-ceea-4b2a-86b1-e3550d5f3a1d', true, 'Resistor 10W 100', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('dfc64734-607d-46b0-8740-1a606d504803', true, 'Potenciômetro 5k', 49, 49, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('ad68bc65-933a-4451-89a1-e93745bf7c6b', true, 'Resistor 24Ω', 103, 103, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('da4b2d90-a9ad-4783-bb67-65ba8e081152', true, 'Protoboard 31 - 2018006549', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('bafc1443-e74e-4b14-92b9-93723cf31896', true, 'Protoboard 26 - 2018006540', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f3e48bf5-56b4-4488-a2a0-efecafcb7f8d', true, 'LM317', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('75ef4531-12c5-4be4-b0ed-47b93d96ffe5', true, 'LED 5mm - VERDE', 195, 195, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('0a3e5d63-b741-4bb5-8633-3c3c12c8de3b', true, 'BeagleBone Black - #14-5673-994751', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('91a8ff96-94ee-4d04-b54c-625fe6149479', true, 'Mouse', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('349bf39f-58b2-4607-82aa-ab0a790fa2b9', true, 'PCB Tipo 120x30', 96, 96, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('2336b3be-6051-448d-9198-edb0fba12243', true, 'Motor de Passo', 26, 26, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('9eb1bfbf-425a-4a00-aee5-4fc7cc470076', true, 'Protoboard 14 - 2018006518', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('55f96bb7-5120-4d3f-a36c-12d385158d0f', true, 'ZYBO 27 - 995079 - D787529', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('760cc25d-60ae-440e-816f-36cd2aa11ed7', true, 'ZYBO 11 - 995086 - D788069', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('2e90a49f-24a7-4ab1-8cf1-1e4b235e6b66', true, 'Resistor 560', 302, 302, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('3f874a24-d4c0-4831-8aed-3ecb6903eef6', true, 'Resistor 470Ω', 302, 302, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('32e9e36f-da4c-4805-a333-c449aadc835c', true, 'Capacitor Eletrolítico 220uF 25V', 97, 97, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('8bdc539d-4df7-40b9-92bb-4267aa13ab27', true, 'Protoboard 33 - 2018006509', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('9ee8bc08-c180-4feb-9485-1eb3d61c8c5f', true, 'Fonte CC 12V', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f7632778-76cb-41c4-8007-f2b63b981379', true, 'Protoboard 25 - 2018006545', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('4d49b189-b915-43b6-973f-504ea86dbd76', true, 'ZYBO 24 - 995106 - D788012', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('11d65ed0-71ce-4756-b811-941088ba9d8d', true, 'LED 3mm - AMARELO', 96, 96, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('e4738ada-0d88-438d-95ab-aaff07583f94', true, 'Indutor 1000uh', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('ed91b133-04fe-44a4-b9f7-d367c3b89c98', true, 'Soquete 28 Pinos', 82, 82, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('874999f9-22b0-45f0-88e7-ab7b9c175bde', true, 'Trimpot 10K 10mm', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('fe6cf2d2-ad17-4f43-b14f-3e12fe5cbd8e', true, 'Relé 12v', 44, 44, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('3a57c11e-7252-439c-b921-e9025ce65f7c', true, 'Protoboard 30 - 2018006511', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('dee22cf4-1fe7-4077-8a71-1a2fe323314f', true, 'LED 3mm - BRANCO/AZUL', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('12f09e56-7381-4ecb-809e-2661a824bdae', true, '74LS85', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d561c65d-af8d-457c-91b2-8b9d25b60c12', true, 'Protoboard 43 - 2018006503', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('c4b3828b-032b-4bbd-a4b6-d78c62876de4', true, 'Protoboard 34 - 2018006546', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('7ac08aee-d297-458e-be10-346bc84b4911', true, 'BC 546', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('77f79bd0-ca4d-43a6-ae14-8db2280d5878', true, 'FRDM KL 25', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('dfa60c5a-3c9f-45ee-983b-0b6a2ded1b4e', true, 'BeagleBone Black - #16-0005-994767', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('aa80eca7-ec60-441d-b0f7-9dfa9ed6f77b', true, 'Borne p/ Pino Banana', 60, 60, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('43b36bcf-de66-43e9-be3c-d45920fc80ce', true, 'LED 5mm - Alto brilho AZUL', 81, 81, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('69a58686-47f5-40c3-81c6-480f81e7dd51', true, 'Protoboard 10 - 2018006516', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('e9353192-47ac-4a20-a967-d947cf84e228', true, '2N3904', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('87564952-b96f-4372-bb78-3d4127e17b00', true, 'Capacitor Cerâmico 22pF 50V', 95, 95, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('ebd5a27b-8cf9-4a52-8321-00dcb56586fb', true, 'BC 548', 97, 97, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('99364701-b46a-49f4-8cf1-abb1e1956c31', true, 'Cabo HDMI', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('c40dfa63-b092-4601-8815-f5ddef24cf0a', true, 'Protoboard 20 - 2018006531', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('987888d1-c0e5-4bcf-a3e9-603c1e833994', true, 'Resistor 10W 680', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('7efd6344-931c-4801-8c8e-29f4719c77ca', true, 'ZYBO 22 - 995099 - D787548', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('baec054a-26bf-48fe-bc8e-1cffca26d981', true, 'ZYBO 16 - 995115 - D653147', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('e8a57dc8-9de6-4ecb-a858-6c232e374e47', true, 'Capacitor Cerâmico 680pF 50V', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d4f9fc04-7e08-4dbf-a524-f93161ddf349', true, 'Protoboard 23 - 2018006536', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('7be2b3ad-0112-4db3-8160-c106cfda0769', true, 'LED 5mm - Alto brilho VERMELHO', 99, 99, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('5955dd86-568c-4afc-a029-b9129a81ccb1', true, 'Sensor Infravermelho', 13, 13, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('446efd51-5c5e-4545-a040-1a3053ddd3f2', true, 'Relé 24v', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('1759d6ad-8ddc-443d-961e-8235a3ae5bee', true, 'BeagleBone Black - #11-9231-994754', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('176b6cea-223f-4c9f-92c8-8fb2254575e6', true, 'Protoboard 28 - 2018006541', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('5d2eeb78-86f3-49cd-a2de-f4d4ced040af', true, 'Conector Sindal', 60, 60, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('85ec37cc-3620-4092-b39a-18fa5e6a553a', true, '4051', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('7a75c3bb-202e-470c-8911-b3df6fb78f78', true, '74LS47', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('55674197-3bcf-4421-8fe7-f7d5db8771cd', true, 'BC 254', 50, 50, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('f5d65ddc-36db-408d-b06c-3c9b9314e746', true, 'IRF 244', 51, 51, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('20ecf58b-45b8-47ce-a337-703b814a6b53', true, 'L293D', 51, 51, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('7dde28b3-af77-4e91-ba95-7a6d35b50bec', true, 'LED 5mm - AZUL', 67, 67, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('fc00e876-bd35-4b36-899b-22bca91dad8c', true, 'Protoboard 04 - 2018006543', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('18f2362f-14d9-4d62-a99d-b5bb82d746b6', true, 'Cabo de Rede', 7, 7, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('e6f51a11-45fa-4fe9-8ff6-0d25957a4335', true, 'LED 5mm - Alto brilho AMARELO', 191, 191, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('6e0da9fe-dfb9-4e36-8e7c-e091c66da1d0', true, '1N4745', 200, 200, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('5f740689-1acb-4665-9d89-8a8cbfd261ab', true, 'BeagleBone Black - #09-0017-994745', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d1249235-6bca-449c-b6a7-91a41e591a3b', true, 'LM556', 100, 100, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('d817b410-0826-426c-a15c-1f443da56109', true, 'TIC 226', 101, 101, 'COMPONENTE', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('cee72126-3c20-4604-8391-941710447e84', true, 'Protoboard 41 - 2018006502', 1, 1, 'EQUIPAMENTO', NULL);
INSERT INTO ${appSchema}.estoque (id, is_ativado, nome, quantidade, quantidade_disponivel, tipo, version)
VALUES ('b5fddac1-ff70-4c80-b105-f3fdb41dab23', true, '1N4004', 100, 100, 'COMPONENTE', NULL);

-- Pedidos pendentes
INSERT INTO ${appSchema}.pedidos (id, codigo_pedido, data_solicitacao, data_atualizacao, status, emprestimo_especial,
                                  feedback, solicitante_id)
VALUES ('10000000-0000-4000-8000-000000000001', 'DEMO-PEND-001', CURRENT_TIMESTAMP - INTERVAL '3 hours', NULL,
        'PENDENTE', false, 'Material para aula prática de eletrônica digital.', 7);
INSERT INTO ${appSchema}.pedidos (id, codigo_pedido, data_solicitacao, data_atualizacao, status, emprestimo_especial,
                                  feedback, solicitante_id)
VALUES ('10000000-0000-4000-8000-000000000002', 'DEMO-PEND-002', CURRENT_TIMESTAMP - INTERVAL '1 day',
        CURRENT_TIMESTAMP - INTERVAL '2 hours', 'PENDENTE', true, 'Empréstimo especial para projeto de extensão.', 12);
INSERT INTO ${appSchema}.pedidos (id, codigo_pedido, data_solicitacao, data_atualizacao, status, emprestimo_especial,
                                  feedback, solicitante_id)
VALUES ('10000000-0000-4000-8000-000000000003', 'DEMO-PEND-003', CURRENT_TIMESTAMP - INTERVAL '2 days', NULL,
        'PENDENTE', false, NULL, 16);

-- Pedidos aprovados: o primeiro é antigo o suficiente para aparecer como atrasado.
INSERT INTO ${appSchema}.pedidos (id, codigo_pedido, data_solicitacao, data_aprovacao, status, emprestimo_especial,
                                  feedback, solicitante_id, aprovador_id)
VALUES ('10000000-0000-4000-8000-000000000004', 'DEMO-ATIVO-001', CURRENT_TIMESTAMP - INTERVAL '12 days',
        CURRENT_TIMESTAMP - INTERVAL '10 days', 'APROVADO', false, 'Equipamentos aguardando devolução.', 7, 6);
INSERT INTO ${appSchema}.pedidos (id, codigo_pedido, data_solicitacao, data_aprovacao, status, emprestimo_especial,
                                  feedback, solicitante_id, aprovador_id)
VALUES ('10000000-0000-4000-8000-000000000005', 'DEMO-ATIVO-002', CURRENT_TIMESTAMP - INTERVAL '5 hours',
        CURRENT_TIMESTAMP - INTERVAL '2 hours', 'APROVADO', false, 'Retirada autorizada no almoxarifado.', 12, 16);
INSERT INTO ${appSchema}.pedidos (id, codigo_pedido, data_solicitacao, data_aprovacao, status, emprestimo_especial,
                                  feedback, solicitante_id, aprovador_id)
VALUES ('10000000-0000-4000-8000-000000000006', 'DEMO-ATIVO-003', CURRENT_TIMESTAMP - INTERVAL '4 days',
        CURRENT_TIMESTAMP - INTERVAL '3 days', 'APROVADO', true, 'Uso no laboratório de robótica.', 16, 6);

-- Pedidos rejeitados
INSERT INTO ${appSchema}.pedidos (id, codigo_pedido, data_solicitacao, data_aprovacao, status, emprestimo_especial,
                                  feedback, solicitante_id, aprovador_id)
VALUES ('10000000-0000-4000-8000-000000000007', 'DEMO-REJ-001', CURRENT_TIMESTAMP - INTERVAL '8 days',
        CURRENT_TIMESTAMP - INTERVAL '7 days', 'REJEITADO', false,
        'Quantidade solicitada indisponível para este período.', 7, 16);
INSERT INTO ${appSchema}.pedidos (id, codigo_pedido, data_solicitacao, data_aprovacao, status, emprestimo_especial,
                                  feedback, solicitante_id, aprovador_id)
VALUES ('10000000-0000-4000-8000-000000000008', 'DEMO-REJ-002', CURRENT_TIMESTAMP - INTERVAL '2 days',
        CURRENT_TIMESTAMP - INTERVAL '1 day', 'REJEITADO', true,
        'Solicitação sem justificativa para empréstimo especial.', 12, 6);

-- Pedidos finalizados
INSERT INTO ${appSchema}.pedidos (id, codigo_pedido, data_solicitacao, data_aprovacao, data_finalizado, status,
                                  emprestimo_especial, feedback, solicitante_id, aprovador_id, finalizador_id)
VALUES ('10000000-0000-4000-8000-000000000009', 'DEMO-FIM-001', CURRENT_TIMESTAMP - INTERVAL '35 days',
        CURRENT_TIMESTAMP - INTERVAL '34 days', CURRENT_TIMESTAMP - INTERVAL '30 days', 'FINALIZADO', false,
        'Itens devolvidos em bom estado.', 7, 6, 16);
INSERT INTO ${appSchema}.pedidos (id, codigo_pedido, data_solicitacao, data_aprovacao, data_finalizado, status,
                                  emprestimo_especial, feedback, solicitante_id, aprovador_id, finalizador_id)
VALUES ('10000000-0000-4000-8000-000000000010', 'DEMO-FIM-002', CURRENT_TIMESTAMP - INTERVAL '18 days',
        CURRENT_TIMESTAMP - INTERVAL '17 days', CURRENT_TIMESTAMP - INTERVAL '12 days', 'FINALIZADO', true,
        'Projeto concluído e materiais conferidos.', 12, 16, 6);
INSERT INTO ${appSchema}.pedidos (id, codigo_pedido, data_solicitacao, data_aprovacao, data_finalizado, status,
                                  emprestimo_especial, feedback, solicitante_id, aprovador_id, finalizador_id)
VALUES ('10000000-0000-4000-8000-000000000011', 'DEMO-FIM-003', CURRENT_TIMESTAMP - INTERVAL '7 days',
        CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP - INTERVAL '1 day', 'FINALIZADO', false, NULL, 16, 6,
        6);

-- Pedido cancelado (visível na listagem geral e útil para relatórios).
INSERT INTO ${appSchema}.pedidos (id, codigo_pedido, data_solicitacao, data_atualizacao, status, emprestimo_especial,
                                  feedback, solicitante_id)
VALUES ('10000000-0000-4000-8000-000000000012', 'DEMO-CANC-001', CURRENT_TIMESTAMP - INTERVAL '4 days',
        CURRENT_TIMESTAMP - INTERVAL '3 days', 'CANCELADO', false, 'Cancelado pelo solicitante antes da análise.', 7);

INSERT INTO ${appSchema}.itens_pedido (id, quantidade_item, estoque_id, pedido_id)
VALUES ('20000000-0000-4000-8000-000000000001', 1, 'ee78b2fa-3bc0-4e55-b5c1-6584dfa62035',
        '10000000-0000-4000-8000-000000000001'),
       ('20000000-0000-4000-8000-000000000002', 4, 'ef579573-905c-4b5c-940f-b75b01f7e1a0',
        '10000000-0000-4000-8000-000000000001'),
       ('20000000-0000-4000-8000-000000000003', 1, '6a9a6884-c04b-4a67-8d28-3cf3cee57362',
        '10000000-0000-4000-8000-000000000002'),
       ('20000000-0000-4000-8000-000000000004', 6, '1306e611-f7bb-4282-b262-d7d0c056e10b',
        '10000000-0000-4000-8000-000000000002'),
       ('20000000-0000-4000-8000-000000000005', 2, '1094b792-9ddb-4afa-a01a-ee131d338b08',
        '10000000-0000-4000-8000-000000000003'),
       ('20000000-0000-4000-8000-000000000006', 1, 'd7c837d5-1bd5-490e-85df-8fc768a9aaba',
        '10000000-0000-4000-8000-000000000003'),
       ('20000000-0000-4000-8000-000000000007', 1, '7aadb996-fa98-4315-b88c-18fb29d8fcdd',
        '10000000-0000-4000-8000-000000000004'),
       ('20000000-0000-4000-8000-000000000008', 10, 'da784bbb-28c2-41b0-adec-4fdd5f93a3e6',
        '10000000-0000-4000-8000-000000000004'),
       ('20000000-0000-4000-8000-000000000009', 3, '5ffb8050-7211-4ed7-bdaf-02e878a2a662',
        '10000000-0000-4000-8000-000000000005'),
       ('20000000-0000-4000-8000-000000000010', 1, 'b40b0c82-c9c6-4b9b-90d8-0132780686e2',
        '10000000-0000-4000-8000-000000000005'),
       ('20000000-0000-4000-8000-000000000011', 1, 'a306150a-1547-4e67-a26a-0671d0cd1364',
        '10000000-0000-4000-8000-000000000006'),
       ('20000000-0000-4000-8000-000000000012', 5, '3b785835-94bc-47d8-833b-807c88e75d97',
        '10000000-0000-4000-8000-000000000006'),
       ('20000000-0000-4000-8000-000000000013', 2, 'b49025ca-8c9b-4c47-9f6c-fece9659f394',
        '10000000-0000-4000-8000-000000000007'),
       ('20000000-0000-4000-8000-000000000014', 1, '31c6af57-802b-42e0-9753-1a20778a7796',
        '10000000-0000-4000-8000-000000000007'),
       ('20000000-0000-4000-8000-000000000015', 1, '28a3878a-5673-4cf4-a9be-2de041d20386',
        '10000000-0000-4000-8000-000000000008'),
       ('20000000-0000-4000-8000-000000000016', 3, 'fdecf557-c1c8-40cc-abbc-1a05758fd152',
        '10000000-0000-4000-8000-000000000008'),
       ('20000000-0000-4000-8000-000000000017', 1, '98d37e4a-b2b2-4ec6-948b-8173f2bddc7b',
        '10000000-0000-4000-8000-000000000009'),
       ('20000000-0000-4000-8000-000000000018', 2, '6e00c547-24e6-4e24-8c34-d754e40c9b17',
        '10000000-0000-4000-8000-000000000009'),
       ('20000000-0000-4000-8000-000000000019', 4, '5fbdcac2-32e3-4612-923d-ae7364ebf9b8',
        '10000000-0000-4000-8000-000000000010'),
       ('20000000-0000-4000-8000-000000000020', 1, 'aa99ee1e-b33e-4730-b915-66b7eda7867b',
        '10000000-0000-4000-8000-000000000010'),
       ('20000000-0000-4000-8000-000000000021', 1, 'ffd6c0c3-cb31-40a8-9780-bdf6b4a92cb0',
        '10000000-0000-4000-8000-000000000011'),
       ('20000000-0000-4000-8000-000000000022', 3, '24ca7009-e3e1-4143-ad04-472b31c88752',
        '10000000-0000-4000-8000-000000000011'),
       ('20000000-0000-4000-8000-000000000023', 2, '05b79899-3ae3-40b0-b785-5b487cedef96',
        '10000000-0000-4000-8000-000000000012'),
       ('20000000-0000-4000-8000-000000000024', 1, 'd43bd914-f64e-4292-a37d-81d8d36b2f9c',
        '10000000-0000-4000-8000-000000000012');

--rollback DELETE FROM ${appSchema}.itens_pedido;
--rollback DELETE FROM ${appSchema}.pedidos;
--rollback DELETE FROM ${appSchema}.estoque;
--rollback DELETE FROM ${appSchema}.usuarios;

-- TIPO_REGIMEN_CONCURRENCIA
insert into test.tipo_regimen_concurrencia (id,activo) values (1,true);
insert into test.tipo_regimen_concurrencia (id,activo) values (2,true);
insert into test.tipo_regimen_concurrencia (id,activo) values (3,true);
insert into test.tipo_regimen_concurrencia (id,activo) values (4,false);
insert into test.tipo_regimen_concurrencia (id,activo) values (5,true);
insert into test.tipo_regimen_concurrencia (id,activo) values (6,true);
insert into test.tipo_regimen_concurrencia (id,activo) values (7,false);


-- TIPO_REGIMEN_CONCURRENCIA_NOMBRE
INSERT INTO test.tipo_regimen_concurrencia_nombre (tipo_regimen_concurrencia_id, lang, value_) VALUES(1, 'es', 'nombre-01');
INSERT INTO test.tipo_regimen_concurrencia_nombre (tipo_regimen_concurrencia_id, lang, value_) VALUES(2, 'es', 'nombre-02');
INSERT INTO test.tipo_regimen_concurrencia_nombre (tipo_regimen_concurrencia_id, lang, value_) VALUES(3, 'es', 'nombre-03');
INSERT INTO test.tipo_regimen_concurrencia_nombre (tipo_regimen_concurrencia_id, lang, value_) VALUES(4, 'es', 'nombre-04');
INSERT INTO test.tipo_regimen_concurrencia_nombre (tipo_regimen_concurrencia_id, lang, value_) VALUES(5, 'es', 'nombre-5');
INSERT INTO test.tipo_regimen_concurrencia_nombre (tipo_regimen_concurrencia_id, lang, value_) VALUES(6, 'es', 'nombre-6');
INSERT INTO test.tipo_regimen_concurrencia_nombre (tipo_regimen_concurrencia_id, lang, value_) VALUES(7, 'es', 'nombre-7');
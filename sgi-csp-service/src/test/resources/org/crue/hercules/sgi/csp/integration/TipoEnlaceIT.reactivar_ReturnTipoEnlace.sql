-- TIPO_ENLACE
insert into test.tipo_enlace (id,activo) values (1,false);
-- TIPO_ENLACE_NOMBRE
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(1, 'es', 'nombre-1');
-- TIPO_ENLACE_DESCRIPCION
INSERT INTO test.tipo_enlace_descripcion (tipo_enlace_id, lang, value_) VALUES(1, 'es', 'descripcion-1');
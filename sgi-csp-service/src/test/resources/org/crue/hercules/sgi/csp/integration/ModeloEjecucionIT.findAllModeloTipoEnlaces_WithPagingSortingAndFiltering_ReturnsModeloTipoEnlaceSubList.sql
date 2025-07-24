
-- TIPO ENLACE
INSERT INTO test.tipo_enlace (id, activo) VALUES (1, true);
INSERT INTO test.tipo_enlace (id, activo) VALUES (2, true);
INSERT INTO test.tipo_enlace (id, activo) VALUES (3, true);
INSERT INTO test.tipo_enlace (id, activo) VALUES (4, false);
INSERT INTO test.tipo_enlace (id, activo) VALUES (10, true);
INSERT INTO test.tipo_enlace (id, activo) VALUES (11, true);
INSERT INTO test.tipo_enlace (id, activo) VALUES (12, true);
INSERT INTO test.tipo_enlace (id, activo) VALUES (13, true);
INSERT INTO test.tipo_enlace (id, activo) VALUES (14, true);

-- TIPO_ENLACE_NOMBRE
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(2, 'es', 'nombre-002');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(3, 'es', 'nombre-003');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(4, 'es', 'nombre-004');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(10, 'es', 'nombre-010');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(11, 'es', 'nombre-011');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(12, 'es', 'nombre-012');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(13, 'es', 'nombre-013');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(14, 'es', 'nombre-014');

-- TIPO_ENLACE_DESCIPCION
INSERT INTO test.tipo_enlace_descripcion (tipo_enlace_id, lang, value_) VALUES(1, 'es', 'descripcion-001');
INSERT INTO test.tipo_enlace_descripcion (tipo_enlace_id, lang, value_) VALUES(2, 'es', 'descripcion-002');
INSERT INTO test.tipo_enlace_descripcion (tipo_enlace_id, lang, value_) VALUES(3, 'es', 'descripcion-003');
INSERT INTO test.tipo_enlace_descripcion (tipo_enlace_id, lang, value_) VALUES(4, 'es', 'descripcion-004');
INSERT INTO test.tipo_enlace_descripcion (tipo_enlace_id, lang, value_) VALUES(10, 'es', 'descripcion-010');
INSERT INTO test.tipo_enlace_descripcion (tipo_enlace_id, lang, value_) VALUES(11, 'es', 'descripcion-011');
INSERT INTO test.tipo_enlace_descripcion (tipo_enlace_id, lang, value_) VALUES(12, 'es', 'descripcion-012');
INSERT INTO test.tipo_enlace_descripcion (tipo_enlace_id, lang, value_) VALUES(13, 'es', 'descripcion-013');
INSERT INTO test.tipo_enlace_descripcion (tipo_enlace_id, lang, value_) VALUES(14, 'es', 'descripcion-014');

-- MODELO_EJECUCION
INSERT INTO test.modelo_ejecucion (id, activo) VALUES (1, true);
INSERT INTO test.modelo_ejecucion (id, activo) VALUES (2, true);

-- MODELO_EJECUCION_NOMBRE
INSERT INTO test.modelo_ejecucion_nombre (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.modelo_ejecucion_nombre (modelo_ejecucion_id, lang, value_) VALUES (2, 'es', 'nombre-002');

-- MODELO_EJECUCION_DESCRIPCION
INSERT INTO test.modelo_ejecucion_descripcion (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'descripcion-001');
INSERT INTO test.modelo_ejecucion_descripcion (modelo_ejecucion_id, lang, value_) VALUES (2, 'es', 'descripcion-002');

-- MODELO TIPO ENLACE
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (1, 1, 1, true);
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (2, 2, 1, true);
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (3, 3, 1, true);
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (4, 4, 1, true);
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (5, 10, 1, true);
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (6, 1, 2, true);
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (7, 11, 2, true);
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (8, 12, 2, true);
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (9, 13, 2, true);
INSERT INTO test.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (10, 14, 2, true);

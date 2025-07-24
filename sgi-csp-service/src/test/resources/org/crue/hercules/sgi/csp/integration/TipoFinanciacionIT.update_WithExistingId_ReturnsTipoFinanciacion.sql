-- TIPO FINANCIACION
INSERT INTO test.tipo_financiacion (id, activo) VALUES (1, true);
INSERT INTO test.tipo_financiacion (id, activo) VALUES (2, true);
-- TIPO FINANCIACION NOMBRE
INSERT INTO test.tipo_financiacion_nombre (tipo_financiacion_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.tipo_financiacion_nombre (tipo_financiacion_id, lang, value_) VALUES(2, 'es', 'nombre-002');
-- TIPO FINANCIACION DESCRIPCION
INSERT INTO test.tipo_financiacion_descripcion (tipo_financiacion_id, lang, value_) VALUES(1, 'es', 'descripcion-001');
INSERT INTO test.tipo_financiacion_descripcion (tipo_financiacion_id, lang, value_) VALUES(2, 'es', 'descripcion-002');
-- TIPO HITO
INSERT INTO test.tipo_hito (id, activo) VALUES (1, true);
INSERT INTO test.tipo_hito (id, activo) VALUES (2, true);

-- TIPO HITO NOMBRE
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (1, 'es', 'TipoHito1');
INSERT INTO test.tipo_hito_nombre (tipo_hito_id, lang, value_) VALUES (2, 'es', 'TipoHito2');

-- TIPO HITO DESCRIPCIÓN
INSERT INTO test.tipo_hito_descripcion (tipo_hito_id, lang, value_) VALUES (1, 'es', 'Descripcion1');
INSERT INTO test.tipo_hito_descripcion (tipo_hito_id, lang, value_) VALUES (2, 'es', 'Descripcion2');

-- TIPO DOCUMENTO
INSERT INTO test.tipo_documento (id, activo) VALUES (1, true);

-- TIPO_DOCUMENTO_NOMBRE
INSERT INTO test.tipo_documento_nombre (tipo_documento_id, lang, value_) VALUES (1, 'es', 'nombre-1');

-- TIPO_DOCUMENTO_DESCRIPCION
INSERT INTO test.tipo_documento_descripcion (tipo_documento_id, lang, value_) VALUES (1, 'es', 'descripcion-1');
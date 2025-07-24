-- DEPENDENCIAS: TIPO DOCUMENTO
/*
  scripts = { 
    "classpath:scripts/formulario.sql",
  }
*/

-- TIPO DOCUMENTO 
INSERT INTO test.tipo_documento (id, codigo, formulario_id, adicional, activo) VALUES (1, 'Codigo1', 4, true, true);
INSERT INTO test.tipo_documento (id, codigo, formulario_id, adicional, activo) VALUES (2, 'Codigo2', 5, true, true);
INSERT INTO test.tipo_documento (id, codigo, formulario_id, adicional, activo) VALUES (3, 'Codigo3', 6, true, true);
INSERT INTO test.tipo_documento (id, codigo, formulario_id, adicional, activo) VALUES (4, 'Codigo4', 1, true, true);
INSERT INTO test.tipo_documento (id, codigo, formulario_id, adicional, activo) VALUES (5, 'Codigo5', 1, false, true);
INSERT INTO test.tipo_documento (id, codigo, formulario_id, adicional, activo) VALUES (6, 'Codigo6', 1, false, true);
INSERT INTO test.tipo_documento (id, codigo, formulario_id, adicional, activo) VALUES (7, 'Codigo7', 1, false, true);
INSERT INTO test.tipo_documento (id, codigo, formulario_id, adicional, activo) VALUES (8, 'Codigo8', 1, false, true);
INSERT INTO test.tipo_documento (id, codigo, formulario_id, adicional, activo) VALUES (9, 'Codigo9', 1, false, true);
INSERT INTO test.tipo_documento (id, codigo, formulario_id, adicional, activo) VALUES (10, 'Codigo10', 1, false, true);
INSERT INTO test.tipo_documento (id, codigo, formulario_id, adicional, activo) VALUES (11, 'Codigo11', 1, false, true);
INSERT INTO test.tipo_documento (id, codigo, formulario_id, adicional, activo) VALUES (12, 'Codigo12', 2, true, true);

--TIPO DOCUMENTO  NOMBRES
INSERT INTO test.tipo_documento_nombre(tipo_documento_id, lang, value_) VALUES(1, 'es', 'TipoDocumento1');
INSERT INTO test.tipo_documento_nombre(tipo_documento_id, lang, value_) VALUES(2, 'es', 'TipoDocumento2');
INSERT INTO test.tipo_documento_nombre(tipo_documento_id, lang, value_) VALUES(3, 'es', 'TipoDocumento3');
INSERT INTO test.tipo_documento_nombre(tipo_documento_id, lang, value_) VALUES(4, 'es', 'TipoDocumento4');
INSERT INTO test.tipo_documento_nombre(tipo_documento_id, lang, value_) VALUES(5, 'es', 'TipoDocumento5');
INSERT INTO test.tipo_documento_nombre(tipo_documento_id, lang, value_) VALUES(6, 'es', 'TipoDocumento6');
INSERT INTO test.tipo_documento_nombre(tipo_documento_id, lang, value_) VALUES(7, 'es', 'TipoDocumento7');
INSERT INTO test.tipo_documento_nombre(tipo_documento_id, lang, value_) VALUES(8, 'es', 'TipoDocumento8');
INSERT INTO test.tipo_documento_nombre(tipo_documento_id, lang, value_) VALUES(9, 'es', 'TipoDocumento9');
INSERT INTO test.tipo_documento_nombre(tipo_documento_id, lang, value_) VALUES(10, 'es', 'TipoDocumento10');
INSERT INTO test.tipo_documento_nombre(tipo_documento_id, lang, value_) VALUES(11, 'es', 'TipoDocumento11');
INSERT INTO test.tipo_documento_nombre(tipo_documento_id, lang, value_) VALUES(12, 'es', 'TipoDocumento12');
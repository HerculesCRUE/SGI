-- TIPO AMBITO GEOGRAFICO
INSERT INTO test.tipo_ambito_geografico (id, activo) VALUES (1, true);
INSERT INTO test.tipo_ambito_geografico (id, activo) VALUES (2, true);

INSERT INTO test.tipo_ambito_geografico_nombre (tipo_ambito_geografico_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.tipo_ambito_geografico_nombre (tipo_ambito_geografico_id, lang, value_) VALUES(2, 'es', 'nombre-002');

-- TIPO ORIGEN FUENTE FINANCIACION
INSERT INTO test.tipo_origen_fuente_financiacion (id, activo) VALUES (1, true);
INSERT INTO test.tipo_origen_fuente_financiacion (id, activo) VALUES (2, true);

-- TIPO ORIGEN FUENTE FINANCIACION NOMBRE
INSERT INTO test.tipo_origen_fuente_financiacion_nombre (tipo_origen_fuente_financiacion_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.tipo_origen_fuente_financiacion_nombre (tipo_origen_fuente_financiacion_id, lang, value_) VALUES(2, 'es', 'nombre-002');

-- FUENTE FINANCIACION
INSERT INTO test.fuente_financiacion (id, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (1, true, 1, 1, true);
INSERT INTO test.fuente_financiacion (id, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (2,  true, 2, 2, true);
INSERT INTO test.fuente_financiacion (id, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (3,  true, 2, 2, false);
INSERT INTO test.fuente_financiacion (id, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (11,  true, 1, 1, true);
INSERT INTO test.fuente_financiacion (id, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (12, true, 2, 2, true);
  INSERT INTO test.fuente_financiacion (id, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (13, true, 1, 1, true);

  -- FUENTE FINANCIACION NOMBRE
INSERT INTO test.fuente_financiacion_nombre (fuente_financiacion_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.fuente_financiacion_nombre (fuente_financiacion_id, lang, value_) VALUES(2, 'es', 'nombre-002');
INSERT INTO test.fuente_financiacion_nombre (fuente_financiacion_id, lang, value_) VALUES(3, 'es', 'nombre-003');
INSERT INTO test.fuente_financiacion_nombre (fuente_financiacion_id, lang, value_) VALUES(11, 'es', 'nombre-011');
INSERT INTO test.fuente_financiacion_nombre (fuente_financiacion_id, lang, value_) VALUES(12, 'es', 'nombre-012');
INSERT INTO test.fuente_financiacion_nombre (fuente_financiacion_id, lang, value_) VALUES(13, 'es', 'nombre-013');

  -- FUENTE FINANCIACION DESCRIPCION
INSERT INTO test.fuente_financiacion_descripcion (fuente_financiacion_id, lang, value_) VALUES(1, 'es', 'descripcion-001');
INSERT INTO test.fuente_financiacion_descripcion (fuente_financiacion_id, lang, value_) VALUES(2, 'es', 'descripcion-002');
INSERT INTO test.fuente_financiacion_descripcion (fuente_financiacion_id, lang, value_) VALUES(3, 'es', 'descripcion-003');
INSERT INTO test.fuente_financiacion_descripcion (fuente_financiacion_id, lang, value_) VALUES(11, 'es', 'descripcion-011');
INSERT INTO test.fuente_financiacion_descripcion (fuente_financiacion_id, lang, value_) VALUES(12, 'es', 'descripcion-012');
INSERT INTO test.fuente_financiacion_descripcion (fuente_financiacion_id, lang, value_) VALUES(13, 'es', 'descripcion-013');

-- DEPENDENCIAS: tipo_ambito_geografico, tipo_origen_fuente_financiacion
/*
  scripts = { 
    // @formatter:off
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_origen_fuente_financiacion.sql"
    // @formatter:on
  }
*/

INSERT INTO test.fuente_financiacion 
(id, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
VALUES (1, true, 1, 1, true);

  -- FUENTE FINANCIACION NOMBRE
INSERT INTO test.fuente_financiacion_nombre (fuente_financiacion_id, lang, value_) VALUES(1, 'es', 'nombre-001');

  -- FUENTE FINANCIACION DESCRIPCION
INSERT INTO test.fuente_financiacion_descripcion (fuente_financiacion_id, lang, value_) VALUES(1, 'es', 'descripcion-001');

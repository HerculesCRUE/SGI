-- DEPENDENCIAS: tipo_ambito_geografico, tipo_origen_fuente_financiacion
/*
  scripts = { 
    // @formatter:off
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/tipo_origen_fuente_financiacion.sql"
    // @formatter:on
  }
*/

INSERT INTO csp.fuente_financiacion 
(id, nombre, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
VALUES (1, 'nombre-001', 'descripcion-001', true, 1, 1, true);
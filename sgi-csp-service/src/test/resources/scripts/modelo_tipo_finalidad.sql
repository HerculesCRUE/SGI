-- DEPENDENCIAS: modelo_ejecucion, tipo_finalidad
/*
  scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql"
    // @formatter:on
  }
*/

INSERT INTO csp.modelo_tipo_finalidad 
(id, modelo_ejecucion_id, tipo_finalidad_id, activo) 
VALUES (1, 1, 1, true);
-- DEPENDENCIAS: modelo_ejecucion, tipo_enlace 
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_enlace.sql"
  }
*/

INSERT INTO test.modelo_tipo_enlace 
(id, modelo_ejecucion_id, tipo_enlace_id, activo) 
VALUES (1, 1, 1, true);
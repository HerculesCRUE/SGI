-- DEPENDENCIAS: modelo_ejecucion, tipo_fase, modelo_tipo_fase, tipo_documento
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/modelo_tipo_fase.sql",
    "classpath:scripts/tipo_documento.sql"
  }
*/

INSERT INTO csp.modelo_tipo_documento 
(id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) 
VALUES (1, 1, 1, null, true);

INSERT INTO csp.modelo_tipo_documento 
(id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) 
VALUES (2, 2, 1, 1, true);

INSERT INTO csp.modelo_tipo_documento 
(id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) 
VALUES (3, 3, 1, null, true);

INSERT INTO csp.modelo_tipo_documento 
(id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) 
VALUES (4, 11, 1, 1, true);

INSERT INTO csp.modelo_tipo_documento 
(id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) 
VALUES (5, 12, 1, null, true);
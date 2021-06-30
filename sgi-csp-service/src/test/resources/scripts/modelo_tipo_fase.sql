-- DEPENDENCIAS: modelo_ejecucion, tipo_fase 
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_fase.sql"
  }
*/

INSERT INTO csp.modelo_tipo_fase 
(id, modelo_ejecucion_id, tipo_fase_id, solicitud, proyecto, convocatoria, activo) 
VALUES (1, 1, 1, true, true, true, true);

INSERT INTO csp.modelo_tipo_fase 
(id, modelo_ejecucion_id, tipo_fase_id, solicitud, proyecto, convocatoria, activo) 
VALUES (2, 1, 2, true, true, true, true);

INSERT INTO csp.modelo_tipo_fase 
(id, modelo_ejecucion_id, tipo_fase_id, solicitud, proyecto, convocatoria, activo) 
VALUES (3, 1, 3, true, true, true, true);

INSERT INTO csp.modelo_tipo_fase 
(id, modelo_ejecucion_id, tipo_fase_id, solicitud, proyecto, convocatoria, activo) 
VALUES (4, 1, 4, true, true, true, true);

INSERT INTO csp.modelo_tipo_fase 
(id, modelo_ejecucion_id, tipo_fase_id, solicitud, proyecto, convocatoria, activo) 
VALUES (5, 1, 5, true, true, true, true);
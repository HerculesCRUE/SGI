-- DEPENDENCIAS: tipo_fase, proyecto, modelo_tipo_fase 
/*
  scripts = { 
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/estado_proyecto.sql",    
    "classpath:scripts/proyecto.sql", 
    "classpath:scripts/modelo_tipo_fase.sql" 
  }
*/

INSERT INTO csp.proyecto_fase
(id, observaciones, fecha_inicio, fecha_fin, genera_aviso, proyecto_id, tipo_fase_id)
VALUES(1,'observaciones-proyecto-fase-001', '2020-10-01T00:00:00Z','2020-10-02T23:59:59Z', false, 1, 1);

INSERT INTO csp.proyecto_fase
(id, observaciones, fecha_inicio, fecha_fin, genera_aviso, proyecto_id, tipo_fase_id)
VALUES(2,'observaciones-proyecto-fase-002', '2020-10-03T00:00:00Z','2020-10-04T23:59:59Z', false, 1, 2);

INSERT INTO csp.proyecto_fase
(id, observaciones, fecha_inicio, fecha_fin, genera_aviso, proyecto_id, tipo_fase_id)
VALUES(3,'observaciones-proyecto-fase-003', '2020-11-05T00:00:00Z','2020-10-06T23:59:59Z', false, 1, 3);

INSERT INTO csp.proyecto_fase
(id, observaciones, fecha_inicio, fecha_fin, genera_aviso, proyecto_id, tipo_fase_id)
VALUES(4,'observaciones-proyecto-fase-4', '2020-10-05T00:00:00Z','2020-10-06T23:59:59Z', false, 1, 4);

INSERT INTO csp.proyecto_fase
(id, observaciones, fecha_inicio, fecha_fin, genera_aviso, proyecto_id, tipo_fase_id)
VALUES(5,'observaciones-proyecto-fase-5', '2020-11-07T00:00:00Z','2020-10-08T23:59:59Z', false, 1, 5);
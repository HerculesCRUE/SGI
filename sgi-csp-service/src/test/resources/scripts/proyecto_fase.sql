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
    "classpath:scripts/modelo_tipo_fase.sql", 
    "classpath:scripts/modelo_fase_aviso.sql", 
  }
*/

INSERT INTO test.proyecto_fase
(id, fecha_inicio, fecha_fin, proyecto_id, tipo_fase_id, proyecto_fase_aviso1_id, proyecto_fase_aviso2_id)
VALUES(1, '2020-10-01T00:00:00Z','2020-10-02T23:59:59Z', 1, 1, 1, 2);

INSERT INTO test.proyecto_fase
(id, fecha_inicio, fecha_fin, proyecto_id, tipo_fase_id)
VALUES(2, '2020-10-03T00:00:00Z','2020-10-04T23:59:59Z', 1, 2);

INSERT INTO test.proyecto_fase
(id, fecha_inicio, fecha_fin, proyecto_id, tipo_fase_id)
VALUES(3, '2020-11-05T00:00:00Z','2020-10-06T23:59:59Z', 1, 3);

INSERT INTO test.proyecto_fase
(id, fecha_inicio, fecha_fin, proyecto_id, tipo_fase_id)
VALUES(4, '2020-10-05T00:00:00Z','2020-10-06T23:59:59Z', 1, 4);

INSERT INTO test.proyecto_fase
(id, fecha_inicio, fecha_fin, proyecto_id, tipo_fase_id)
VALUES(5, '2020-11-07T00:00:00Z','2020-10-08T23:59:59Z', 1, 5);

-- PROYECTO_FASE_OBSERVACIONES
INSERT INTO test.proyecto_fase_observaciones (proyecto_id, lang, value_) VALUES (1, 'es', 'observaciones-proyecto-fase-001');
INSERT INTO test.proyecto_fase_observaciones (proyecto_id, lang, value_) VALUES (2, 'es', 'observaciones-proyecto-fase-002');
INSERT INTO test.proyecto_fase_observaciones (proyecto_id, lang, value_) VALUES (3, 'es', 'observaciones-proyecto-fase-003');
INSERT INTO test.proyecto_fase_observaciones (proyecto_id, lang, value_) VALUES (4, 'es', 'observaciones-proyecto-fase-4');
INSERT INTO test.proyecto_fase_observaciones (proyecto_id, lang, value_) VALUES (5, 'es', 'observaciones-proyecto-fase-5');
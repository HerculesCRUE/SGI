-- DEPENDENCIAS: proyecto
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/estado_proyecto.sql",    
    "classpath:scripts/proyecto.sql"
  }
*/

INSERT INTO test.proyecto_prorroga
(id, fecha_concesion, fecha_fin, importe, num_prorroga, tipo, proyecto_id)
VALUES(1, '2020-01-01T00:00:00Z', '2022-1-31T23:59:59Z', 123.45, 1, 'TIEMPO_IMPORTE', 1);

INSERT INTO test.proyecto_prorroga
(id, fecha_concesion, fecha_fin, importe, num_prorroga, tipo, proyecto_id)
VALUES(2, '2020-02-01T00:00:00Z', '2022-03-01T23:59:59Z', 123.45, 2, 'TIEMPO', 1);

INSERT INTO test.proyecto_prorroga
(id, fecha_concesion, fecha_fin, importe, num_prorroga, tipo, proyecto_id)
VALUES(3, '2020-03-01T00:00:00Z', '2022-04-01T23:59:59Z', 123.45, 3, 'TIEMPO_IMPORTE', 1);

INSERT INTO test.proyecto_prorroga
(id, fecha_concesion, fecha_fin, importe, num_prorroga, tipo, proyecto_id)
VALUES(4, '2020-04-01T00:00:00Z', '2022-05-01T23:59:59Z', 543.21, 4, 'IMPORTE', 1);

INSERT INTO test.proyecto_prorroga
(id, fecha_concesion, fecha_fin, importe, num_prorroga, tipo, proyecto_id)
VALUES(5, '2020-05-01T00:00:00Z', '2022-06-01T23:59:59Z', 543.21, 5, 'TIEMPO_IMPORTE', 1);

INSERT INTO test.proyecto_prorroga_observaciones(proyecto_prorroga_id, lang, value_)
VALUES (1, 'es', 'observaciones-proyecto-prorroga-001');
INSERT INTO test.proyecto_prorroga_observaciones(proyecto_prorroga_id, lang, value_)
VALUES (2, 'es', 'observaciones-proyecto-prorroga-002');
INSERT INTO test.proyecto_prorroga_observaciones(proyecto_prorroga_id, lang, value_)
VALUES (3, 'es', 'observaciones-proyecto-prorroga-003');
INSERT INTO test.proyecto_prorroga_observaciones(proyecto_prorroga_id, lang, value_)
VALUES (4, 'es', 'observaciones-proyecto-prorroga-4');
INSERT INTO test.proyecto_prorroga_observaciones(proyecto_prorroga_id, lang, value_)
VALUES (5, 'es', 'observaciones-proyecto-prorroga-5');

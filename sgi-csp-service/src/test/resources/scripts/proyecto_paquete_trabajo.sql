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

INSERT INTO test.proyecto_paquete_trabajo
(id, fecha_inicio, fecha_fin, nombre, persona_mes, proyecto_id)
VALUES(1, '2020-01-01T00:00:00Z', '2020-01-15T23:59:59Z', 'proyecto-paquete-trabajo-001', 1, 1);

INSERT INTO test.proyecto_paquete_trabajo
(id, fecha_inicio, fecha_fin, nombre, persona_mes, proyecto_id)
VALUES(2, '2020-02-01T00:00:00Z', '2020-02-15T23:59:59Z', 'proyecto-paquete-trabajo-002', 2, 1);

INSERT INTO test.proyecto_paquete_trabajo
(id, fecha_inicio, fecha_fin, nombre, persona_mes, proyecto_id)
VALUES(3, '2020-03-01T00:00:00Z', '2020-03-15T23:59:59Z', 'proyecto-paquete-trabajo-003', 3, 1);

INSERT INTO test.proyecto_paquete_trabajo
(id, fecha_inicio, fecha_fin, nombre, persona_mes, proyecto_id)
VALUES(4, '2020-04-01T00:00:00Z', '2020-04-15T23:59:59Z', 'proyecto-paquete-trabajo-4', 4, 1);

INSERT INTO test.proyecto_paquete_trabajo
(id, fecha_inicio, fecha_fin, nombre, persona_mes, proyecto_id)
VALUES(5, '2020-05-01T00:00:00Z', '2020-05-15T23:59:59Z', 'proyecto-paquete-trabajo-5', 5, 1);

-- PROYECTO_PAQUETE_TRABAJO_DESCRIPCION
INSERT INTO test.proyecto_paquete_trabajo_descripcion (proyecto_id, lang, value_) VALUES (1, 'es', 'descripcion-proyecto-equipo-trabajo-001');
INSERT INTO test.proyecto_paquete_trabajo_descripcion (proyecto_id, lang, value_) VALUES (2, 'es', 'descripcion-proyecto-equipo-trabajo-002');
INSERT INTO test.proyecto_paquete_trabajo_descripcion (proyecto_id, lang, value_) VALUES (3, 'es', 'descripcion-proyecto-equipo-trabajo-003');
INSERT INTO test.proyecto_paquete_trabajo_descripcion (proyecto_id, lang, value_) VALUES (4, 'es', 'descripcion-proyecto-equipo-trabajo-004');
INSERT INTO test.proyecto_paquete_trabajo_descripcion (proyecto_id, lang, value_) VALUES (5, 'es', 'descripcion-proyecto-equipo-trabajo-005');

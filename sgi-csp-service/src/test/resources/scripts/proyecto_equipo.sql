-- DEPENDENCIAS: proyecto 
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/estado_proyecto.sql",    
    "classpath:scripts/proyecto.sql",
     "classpath:scripts/rol_proyecto.sql"
  }
*/

INSERT INTO csp.proyecto_equipo
(id, proyecto_id, fecha_inicio, fecha_fin, persona_ref, horas_dedicacion, rol_proyecto_id)
VALUES(100, 1, '2020-01-01T00:00:00Z', '2020-01-15T23:59:59Z', 'ref-001', 1, 1);

INSERT INTO csp.proyecto_equipo
(id, proyecto_id, fecha_inicio, fecha_fin, persona_ref, horas_dedicacion, rol_proyecto_id)
VALUES(101, 1, '2020-02-01T00:00:00Z', '2020-02-15T23:59:59Z', 'ref-002', 2, 1);

INSERT INTO csp.proyecto_equipo
(id, proyecto_id, fecha_inicio, fecha_fin, persona_ref, horas_dedicacion, rol_proyecto_id)
VALUES(102, 2, '2020-03-01T00:00:00Z', '2020-03-15T23:59:59Z', 'ref-003', 3, 1);

INSERT INTO csp.proyecto_equipo
(id, proyecto_id, fecha_inicio, fecha_fin, persona_ref, horas_dedicacion, rol_proyecto_id)
VALUES(103, 1, '2020-04-01T00:00:00Z', '2020-04-15T23:59:59Z', 'ref-004', 4, 1);

INSERT INTO csp.proyecto_equipo
(id, proyecto_id, fecha_inicio, fecha_fin, persona_ref, horas_dedicacion, rol_proyecto_id)
VALUES(104, 1, '2020-05-01T00:00:00Z', '2020-05-15T23:59:59Z', 'ref-5', 5, 1);

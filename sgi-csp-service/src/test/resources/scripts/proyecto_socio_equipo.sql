
-- DEPENDENCIAS: rol proyecto, proyecto socio
/*
  scripts = { 
    "classpath:scripts/proyecto_socio.sql",
    "classpath:scripts/rol_proyecto.sql"
  }
*/
-- PROYECTO SOCIO EQUIPO
INSERT INTO csp.proyecto_socio_equipo (id, proyecto_socio_id, rol_proyecto_id, persona_ref,  fecha_inicio, fecha_fin)
  VALUES (1, 1, 1,'personaRef-001', '2021-01-11T00:00:00Z', '2022-01-11T23:59:59Z');
INSERT INTO csp.proyecto_socio_equipo (id, proyecto_socio_id, rol_proyecto_id,persona_ref,  fecha_inicio, fecha_fin)
  VALUES (2, 1, 1,'personaRef-002', '2021-01-11T00:00:00Z', '2022-01-11T23:59:59Z');
INSERT INTO csp.proyecto_socio_equipo (id, proyecto_socio_id, rol_proyecto_id,persona_ref,  fecha_inicio, fecha_fin)
  VALUES (3, 1, 1,'personaRef-003', '2021-01-11T00:00:00Z', '2022-01-11T23:59:59Z');
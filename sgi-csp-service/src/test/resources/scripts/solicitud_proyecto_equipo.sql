-- DEPENDENCIAS
/*
    scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/estado_solicitud.sql",
    "classpath:scripts/solicitud_proyecto.sql",
    "classpath:scripts/rol_proyecto.sql"
    // @formatter:on
  }
*/

INSERT INTO test.solicitud_proyecto_equipo
(id, mes_fin, mes_inicio, persona_ref, solicitud_proyecto_id, rol_proyecto_id)
VALUES
(1, 24, 1, '01889311', 1, 1),
(2, 24, 1, '01925489', 1, 2),
(3, 24, 1, 'usr-002', 1, 2),
(4, 36, 1, '01889311', 3, 1),
(5, 36, 1, '01925489', 3, 2);

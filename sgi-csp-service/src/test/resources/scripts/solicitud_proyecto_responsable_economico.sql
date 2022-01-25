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
    "classpath:scripts/solicitud_proyecto.sql"
    // @formatter:on
  }
*/
INSERT INTO test.solicitud_proyecto_responsable_economico
(id, mes_fin, mes_inicio, persona_ref, solicitud_proyecto_id)
VALUES
(1, 1, 3, '00001', 1),
(2, 3, 5, '00002', 1),
(3, 5, 8, '00003', 1);

-- DEPENDENCIAS:
/*
scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/solicitud.sql",
    "classpath:scripts/programa.sql"
    // @formatter:on
  }
*/

INSERT INTO test.convocatoria_entidad_convocante
(id, convocatoria_id, entidad_ref, programa_id)
VALUES
(1, 1, '00051494', 1),
(2, 1, 'G0021150', 2),
(3, 1, 'G0021150', 3),
(4, 1, 'G0021150', 4),
(5, 2, 'G0021150', 4);

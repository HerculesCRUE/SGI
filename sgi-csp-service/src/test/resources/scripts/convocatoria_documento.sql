-- DEPENDENCIAS:
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/convocatoria_fase.sql",
    "classpath:scripts/tipo_documento.sql"
    // @formatter:on
  }
*/

INSERT INTO test.convocatoria_documento
(id, convocatoria_id, documento_ref, nombre, observaciones, publico, tipo_documento_id, tipo_fase_id)
VALUES
(1, 2, '61f34b61-0e67-40a6-a581-2e188c1cbd78', 'Bases convocatoria 1', NULL, true, 1, 1),
(2, 1, '61f34b61-0e67-40a6-a581-2e188c1cbd78', 'Bases convocatoria 2', NULL, true, 1, 1),
(3, 1, '61f34b61-0e67-40a6-a581-2e188c1cbd78', 'Bases convocatoria 3', NULL, true, 2, 2),
(4, 1, '61f34b61-0e67-40a6-a581-2e188c1cbd78', 'Bases convocatoria 4', NULL, true, 3, 3),
(5, 3, '61f34b61-0e67-40a6-a581-2e188c1cbd78', 'Bases convocatoria 5', NULL, true, 1, 2);

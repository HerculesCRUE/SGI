-- DEPENDENCIAS: convocatoria_periodo_justificacion, proyecto_anualidad
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/contexto_proyecto.sql",
    "classpath:scripts/estado_proyecto.sql",
    "classpath:scripts/convocatoria_periodo_justificacion.sql",
    "classpath:scripts/proyecto_anualidad.sql",
  }
*/

INSERT INTO test.proyecto_periodo_justificacion_seguimiento
(id, proyecto_periodo_justificacion_id, proyecto_anualidad_id, importe_justificado, importe_justificado_cd, importe_justificado_ci, fecha_reintegro, justificante_reintegro)
VALUES(1, 1, 1, 300, 150, 150, '2022-05-11 00:00:00.000','justificante-reintegro-001');
INSERT INTO test.proyecto_periodo_justificacion_seguimiento
(id, proyecto_periodo_justificacion_id, proyecto_anualidad_id, importe_justificado, importe_justificado_cd, importe_justificado_ci, fecha_reintegro, justificante_reintegro)
VALUES(2, 2, 1, 200, 100, 100, '2022-05-17 00:00:00.000','justificante-reintegro-002');

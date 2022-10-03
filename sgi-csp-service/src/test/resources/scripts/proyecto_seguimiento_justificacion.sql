-- DEPENDENCIAS: proyecto_proyecto_sge
/*
  scripts = { 
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/proyecto_proyecto_sge.sql",
  }
*/

INSERT INTO test.proyecto_seguimiento_justificacion
(id, proyecto_proyecto_sge_id, importe_justificado, importe_justificado_cd, importe_justificado_ci, fecha_reintegro, justificante_reintegro)
VALUES(1, 1, 300, 150, 150, '2022-05-11 00:00:00.000','justificante-reintegro-001');
INSERT INTO test.proyecto_seguimiento_justificacion
(id, proyecto_proyecto_sge_id, importe_justificado, importe_justificado_cd, importe_justificado_ci, fecha_reintegro, justificante_reintegro)
VALUES(2, 3, 400, 200, 200, '2022-07-11 00:00:00.000','justificante-reintegro-002');
INSERT INTO test.proyecto_seguimiento_justificacion
(id, proyecto_proyecto_sge_id, importe_justificado, importe_justificado_cd, importe_justificado_ci, fecha_reintegro, justificante_reintegro)
VALUES(3, 2, 400, 200, 200, '2022-09-11 00:00:00.000','justificante-reintegro-003');
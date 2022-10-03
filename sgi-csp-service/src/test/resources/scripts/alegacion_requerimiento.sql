-- DEPENDENCIAS: requerimiento_justificacion
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
    "classpath:scripts/proyecto_periodo_justificacion.sql",
    "classpath:scripts/tipo_requerimiento.sql",
    "classpath:scripts/requerimiento_justificacion.sql",
  }
*/

INSERT INTO test.alegacion_requerimiento
(id, requerimiento_justificacion_id, fecha_alegacion ,importe_alegado, importe_alegado_cd, importe_alegado_ci, 
importe_reintegrado, importe_reintegrado_cd, importe_reintegrado_ci, intereses_reintegrados, fecha_reintegro, justificante_reintegro, observaciones)
VALUES
(1, 1, '2022-05-11 00:00:00.000', 500.22, 250.11, 250.11, 
300.00, 200.00, 100.00, 0.00, '2022-06-11 00:00:00.000', 'Justificante-001', 'Observacion-001');
INSERT INTO test.alegacion_requerimiento
(id, requerimiento_justificacion_id, fecha_alegacion ,importe_alegado, importe_alegado_cd, importe_alegado_ci, 
importe_reintegrado, importe_reintegrado_cd, importe_reintegrado_ci, intereses_reintegrados, fecha_reintegro, justificante_reintegro, observaciones)
VALUES
(2, 2, '2022-07-21 00:00:00.000', 200.22, 100.11, 100.11, 
0.00, 0.00, 0.00, 0.00, '2022-09-22 00:00:00.000', 'Justificante-002', 'Observacion-002');
INSERT INTO test.alegacion_requerimiento
(id, requerimiento_justificacion_id, fecha_alegacion ,importe_alegado, importe_alegado_cd, importe_alegado_ci, 
importe_reintegrado, importe_reintegrado_cd, importe_reintegrado_ci, intereses_reintegrados, fecha_reintegro, justificante_reintegro, observaciones)
VALUES
(3, 4, '2022-11-01 00:00:00.000', 0.00, 0.00, 0.00, 
700.00, 700.00, 0.00, 350.00, '2022-12-25 00:00:00.000', 'Justificante-003', 'Observacion-003');

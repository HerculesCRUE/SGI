--DEPENDENCIAS
/*
scripts = {
    // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/tipo_regimen_concurrencia.sql",
      "classpath:scripts/convocatoria.sql",
      "classpath:scripts/proyecto.sql",
    "classpath:scripts/autorizacion.sql"
    // @formatter:on
  }
*/

INSERT INTO test.notificacion_proyecto_externo_cvn
(id, ambito_geografico, autorizacion_id, cod_externo, datos_entidad_participacion, datos_responsable, documento_ref, entidad_participacion_ref, fecha_fin, fecha_inicio, grado_contribucion, importe_total, nombre_programa, porcentaje_subvencion, proyecto_cvn_id, proyecto_id, responsable_ref, titulo, url_documento_acreditacion, solicitante_ref)
VALUES
(1, NULL, 1, NULL, NULL, NULL, NULL, '00132245', '2022-01-01 14:00:00.000', '2021-10-07 14:00:00.000', NULL, NULL, NULL, NULL, '1', 1, '23302408', 'Notificación 1 con Entidad y Solicitante', NULL, '33870580'),
(2, NULL, NULL, NULL, NULL, NULL, NULL, '00132245', '2022-02-01 14:00:00.000', '2021-11-07 14:00:00.000', NULL, NULL, NULL, NULL, '2', 1, '23302408', 'Notificación 2 con Entidad y Solicitante', NULL, '33870580'),
(3, NULL, NULL, NULL, NULL, NULL, NULL, 'U0011919', '2022-03-01 14:00:00.000', '2021-12-07 14:00:00.000', NULL, NULL, NULL, NULL, '3', 1, '74439415', 'Notificación 3 con Entidad y Solicitante', NULL, '74439415'),
(4, NULL, NULL, NULL, 'Brécol S.L.', 'Responsable 1', NULL, NULL, '2022-01-21 14:00:00.000', '2021-10-17 14:00:00.000', NULL, NULL, NULL, NULL, '4', 2, NULL, 'Notificación 1 sin Entidad y Solicitante', NULL, '74439415'),
(5, NULL, NULL, NULL, 'Lomo S.A.', 'Responsable 2', NULL, NULL, '2022-02-21 14:00:00.000', '2021-11-17 14:00:00.000', NULL, NULL, NULL, NULL, '5', 2, NULL, 'Notificación 2 sin Entidad y Solicitante', NULL, '74439415');
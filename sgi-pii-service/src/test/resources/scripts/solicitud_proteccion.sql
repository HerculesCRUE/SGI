-- DEPENDENCIAS
/*
  scripts = { 
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql"
  }
*/

INSERT INTO test.solicitud_proteccion (id, agente_propiedad_ref, comentarios, estado, fecha_caducidad, fecha_concesion, fecha_fin_prior_pres_fas_nac_rec, fecha_prioridad_solicitud, fecha_publicacioin, numero_concesion, numero_publicacion, numero_registro, numero_solicitud, pais_proteccion_ref, titulo, invencion_id, tipo_caducidad_id, via_proteccion_id)
VALUES
(1, NULL, NULL, 'PUBLICADA', NULL, '2020-07-22 00:00:00.000', '2021-07-22 00:00:00.000', '2020-07-22 00:00:00.000', NULL, NULL, 'ES12345', NULL, 'P123456', NULL, 'Solicitud Proteccion Test 1', 1, NULL, 1),
(2, NULL, NULL, 'SOLICITADA', NULL, NULL, NULL, '2021-07-22 00:00:00.000', NULL, NULL, NULL, NULL, 'EP123456', NULL, 'Solicitud Proteccion Test 2', 1, NULL, 3),
(3, NULL, NULL, 'SOLICITADA', NULL, NULL, NULL, '2021-07-22 00:00:00.000', NULL, NULL, NULL, 'RII123456', 'SOL123456', NULL, 'Solicitud Proteccion Test 2', 1, NULL, 2),
(4, NULL, NULL, 'SOLICITADA', NULL, NULL, NULL, '2021-07-23 00:00:00.000', NULL, NULL, NULL, 'RII123459', 'SOL123458', NULL, 'Solicitud Proteccion Test 4 INTELECTUAL', 3, NULL, 2);

ALTER SEQUENCE test.solicitud_proteccion_seq RESTART WITH 5;

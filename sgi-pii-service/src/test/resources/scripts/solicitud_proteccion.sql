-- DEPENDENCIAS
/*
  scripts = { 
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/tipo_caducidad.sql",
    "classpath:scripts/via_proteccion.sql"
  }
*/

INSERT INTO test.solicitud_proteccion (id, agente_propiedad_ref, estado, fecha_caducidad, fecha_concesion, fecha_fin_prior_pres_fas_nac_rec, fecha_prioridad_solicitud, fecha_publicacioin, numero_concesion, numero_publicacion, numero_registro, numero_solicitud, pais_proteccion_ref, invencion_id, tipo_caducidad_id, via_proteccion_id)
VALUES
(1, NULL, 'PUBLICADA', NULL, '2020-07-22 00:00:00.000', '2021-07-22 00:00:00.000', '2020-07-22 00:00:00.000', NULL, NULL, 'ES12345', NULL, 'P123456', NULL, 1, NULL, 1),
(2, NULL, 'SOLICITADA', NULL, NULL, NULL, '2021-07-22 00:00:00.000', NULL, NULL, NULL, NULL, 'EP123456', NULL, 1, NULL, 3),
(3, NULL, 'SOLICITADA', NULL, NULL, NULL, '2021-07-22 00:00:00.000', NULL, NULL, NULL, 'RII123456', 'SOL123456', NULL, 1, NULL, 2),
(4, NULL, 'SOLICITADA', NULL, NULL, NULL, '2021-07-23 00:00:00.000', NULL, NULL, NULL, 'RII123459', 'SOL123458', NULL, 3, NULL, 2);

-- SOLICITUD PROTECCION TITULO
INSERT INTO test.solicitud_proteccion_titulo(solicitud_proteccion_id, lang, value_) 
VALUES
(1, 'es', 'Solicitud Proteccion Test 1'),
(2, 'es', 'Solicitud Proteccion Test 2'),
(3, 'es', 'Solicitud Proteccion Test 3'),
(4, 'es', 'Solicitud Proteccion Test 4 INTELECTUAL');

ALTER SEQUENCE test.solicitud_proteccion_seq RESTART WITH 5;

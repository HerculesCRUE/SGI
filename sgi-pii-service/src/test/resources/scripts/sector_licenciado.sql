-- DEPENDENCIES
/*
  scripts = { 
    "classpath:scripts/tipo_proteccion.sql",
    "classpath:scripts/invencion.sql",
    "classpath:scripts/sector_aplicacion.sql",
  }
*/

INSERT INTO test.sector_licenciado
(id, contrato_ref, exclusividad, fecha_fin_licencia, fecha_inicio_licencia, invencion_id, pais_ref, sector_aplicacion_id)
VALUES
(1, 'CONT_01', false, '2022-11-06T11:06:00.00Z', '2022-11-05T11:11:00.00Z', 1, 'ES', 1),
(2, 'CONT_01', false, '2022-11-07T11:11:07.00Z', '2022-11-06T11:11:00.00Z', 1, 'ES', 1),
(3, 'CONT_01', false, '2022-11-08T11:11:00.00Z', '2022-11-07T11:11:00.00Z', 1, 'ES', 1),
(4, 'CONT_01', true, '2022-11-09T11:11:00.00Z', '2022-11-08T11:11:00.00Z', 1, 'ES', 1),
(5, 'CONT_01', true, '2022-11-10T11:11:00.00Z', '2022-11-09T11:11:00.00Z', 1, 'ES', 1);

ALTER SEQUENCE test.sector_licenciado_seq RESTART WITH 6;

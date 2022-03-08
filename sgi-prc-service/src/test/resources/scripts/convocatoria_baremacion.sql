-- CONVOCATORIA_BAREMACION
INSERT INTO test.convocatoria_baremacion (id, nombre, anio, anios_baremables, ultimo_anio, importe_total, partida_presupuestaria, fecha_ejecucion, punto_costes_indirectos, punto_produccion, punto_sexenio)
VALUES(1, 'Convocatoria ACI 2021', 2021, 2, 2021, 150000.00, '11.1234.1234.12345', NULL, NULL, NULL, NULL);

ALTER SEQUENCE test.convocatoria_baremacion_seq RESTART WITH 100;




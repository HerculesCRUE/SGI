-- CONVOCATORIA_BAREMACION
INSERT INTO test.convocatoria_baremacion (id, nombre, anio, anios_baremables, ultimo_anio, importe_total, partida_presupuestaria, fecha_inicio_ejecucion, fecha_fin_ejecucion, punto_costes_indirectos, punto_produccion, punto_sexenio, activo)
VALUES(1, 'Convocatoria ACI 2021', 2021, 2, 2021, 150000.00, '11.1234.1234.12345', NULL, NULL, NULL, NULL, NULL, TRUE);
INSERT INTO test.convocatoria_baremacion (id, nombre, anio, anios_baremables, ultimo_anio, importe_total, partida_presupuestaria, fecha_inicio_ejecucion, fecha_fin_ejecucion, punto_costes_indirectos, punto_produccion, punto_sexenio, activo)
VALUES(2, 'Convocatoria ACI2 2021', 2021, 2, 2021, 250000.00, '11.1234.1234.12345', '2021-01-24T00:00:00Z', '2021-01-25T00:00:00Z', NULL, NULL, NULL, FALSE);
INSERT INTO test.convocatoria_baremacion (id, nombre, anio, anios_baremables, ultimo_anio, importe_total, partida_presupuestaria, fecha_inicio_ejecucion, fecha_fin_ejecucion, punto_costes_indirectos, punto_produccion, punto_sexenio, activo)
VALUES(3, 'Convocatoria ACI3 2022', 2022, 2, 2022, 350000.00, '11.1234.1234.12345', NULL, NULL, NULL, NULL, NULL, TRUE);
INSERT INTO test.convocatoria_baremacion (id, nombre, anio, anios_baremables, ultimo_anio, importe_total, partida_presupuestaria, fecha_inicio_ejecucion, fecha_fin_ejecucion, punto_costes_indirectos, punto_produccion, punto_sexenio, activo)
VALUES(4, 'Convocatoria ACI 2023', 2023, 2, 2023, 450000.00, '11.1234.1234.12345', NULL, NULL, NULL, NULL, NULL, TRUE);
INSERT INTO test.convocatoria_baremacion (id, nombre, anio, anios_baremables, ultimo_anio, importe_total, partida_presupuestaria, fecha_inicio_ejecucion, fecha_fin_ejecucion, punto_costes_indirectos, punto_produccion, punto_sexenio, activo)
VALUES(5, 'Convocatoria ACI2 2024', 2024, 2, 2024, 550000.00, '11.1234.1234.12345', NULL, NULL, NULL, NULL, NULL, FALSE);
INSERT INTO test.convocatoria_baremacion (id, nombre, anio, anios_baremables, ultimo_anio, importe_total, partida_presupuestaria, fecha_inicio_ejecucion, fecha_fin_ejecucion, punto_costes_indirectos, punto_produccion, punto_sexenio, activo)
VALUES(6, 'Convocatoria ACI3 2025', 2025, 2, 2025, 650000.00, '11.1234.1234.12345', NULL, NULL, NULL, NULL, NULL, TRUE);
INSERT INTO test.convocatoria_baremacion (id, nombre, anio, anios_baremables, ultimo_anio, importe_total, partida_presupuestaria, fecha_inicio_ejecucion, fecha_fin_ejecucion, punto_costes_indirectos, punto_produccion, punto_sexenio, activo)
VALUES(7, 'Convocatoria ACI4 2026', 2026, 2, 2026, 750000.00, '11.1234.1234.12345', NULL, NULL, NULL, NULL, NULL, TRUE);

ALTER SEQUENCE test.convocatoria_baremacion_seq RESTART WITH 100;




-- RANGO
INSERT INTO test.rango (id, convocatoria_baremacion_id, tipo_rango, desde, hasta, puntos, tipo_temporalidad) VALUES(1, 1, 'CUANTIA_CONTRATOS', 0, 29999, 10, 'INICIAL');
INSERT INTO test.rango (id, convocatoria_baremacion_id, tipo_rango, desde, hasta, puntos, tipo_temporalidad) VALUES(2, 1, 'CUANTIA_CONTRATOS', 30000, 59999, 15, 'INTERMEDIO');
INSERT INTO test.rango (id, convocatoria_baremacion_id, tipo_rango, desde, hasta, puntos, tipo_temporalidad) VALUES(3, 1, 'CUANTIA_CONTRATOS', 60000, NULL, 20, 'FINAL');

INSERT INTO test.rango (id, convocatoria_baremacion_id, tipo_rango, desde, hasta, puntos, tipo_temporalidad) VALUES(4, 1, 'LICENCIA', 0, 29999, 10, 'INICIAL');
INSERT INTO test.rango (id, convocatoria_baremacion_id, tipo_rango, desde, hasta, puntos, tipo_temporalidad) VALUES(5, 1, 'LICENCIA', 30000, 59999, 15, 'INTERMEDIO');
INSERT INTO test.rango (id, convocatoria_baremacion_id, tipo_rango, desde, hasta, puntos, tipo_temporalidad) VALUES(6, 1, 'LICENCIA', 60000, NULL, 20, 'FINAL');

INSERT INTO test.rango (id, convocatoria_baremacion_id, tipo_rango, desde, hasta, puntos, tipo_temporalidad) VALUES(7, 1, 'CUANTIA_COSTES_INDIRECTOS', 0, 29999, 10, 'INICIAL');
INSERT INTO test.rango (id, convocatoria_baremacion_id, tipo_rango, desde, hasta, puntos, tipo_temporalidad) VALUES(8, 1, 'CUANTIA_COSTES_INDIRECTOS', 30000, 59999, 15, 'INTERMEDIO');
INSERT INTO test.rango (id, convocatoria_baremacion_id, tipo_rango, desde, hasta, puntos, tipo_temporalidad) VALUES(9, 1, 'CUANTIA_COSTES_INDIRECTOS', 60000, NULL, 20, 'FINAL');

ALTER SEQUENCE test.rango_seq RESTART WITH 100;

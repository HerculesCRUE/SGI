-- TRAMO REPARTO
INSERT INTO test.tramo_reparto
(id, desde, hasta, porcentaje_inventores, porcentaje_universidad, tipo) 
VALUES
(1, 1, 500, 40, 60, 'INICIAL'),
(2, 501, 1000, 45, 56, 'INTERMEDIO');

ALTER SEQUENCE test.tramo_reparto_seq RESTART WITH 3;


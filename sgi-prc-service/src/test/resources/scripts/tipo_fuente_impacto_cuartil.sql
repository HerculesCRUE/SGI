
-- TIPO_FUENTE_IMPACTO_CUARTIL
INSERT INTO test.tipo_fuente_impacto_cuartil (id, tipo_fuente_impacto, cuartil, anio) VALUES(1, 'CITEC', 'Q1', 2020);
INSERT INTO test.tipo_fuente_impacto_cuartil (id, tipo_fuente_impacto, cuartil, anio) VALUES(2, 'CITEC', 'Q2', 2021);
INSERT INTO test.tipo_fuente_impacto_cuartil (id, tipo_fuente_impacto, cuartil, anio) VALUES(3, 'CITEC', 'Q3', 2022);

INSERT INTO test.tipo_fuente_impacto_cuartil (id, tipo_fuente_impacto, cuartil, anio) VALUES(4, 'ERIH', 'Q1', 2020);
INSERT INTO test.tipo_fuente_impacto_cuartil (id, tipo_fuente_impacto, cuartil, anio) VALUES(5, 'ERIH', 'Q2', 2021);
INSERT INTO test.tipo_fuente_impacto_cuartil (id, tipo_fuente_impacto, cuartil, anio) VALUES(6, 'ERIH', 'Q3', 2022);

ALTER SEQUENCE test.tipo_fuente_impacto_cuartil_seq RESTART WITH 100;

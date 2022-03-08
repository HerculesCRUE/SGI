-- indice_impacto
-- publicaciones
INSERT INTO test.indice_impacto (id, produccion_cientifica_id, anio, tipo_fuente_impacto, numero_revistas, otra_fuente_impacto, posicion_publicacion, tipo_ranking, revista_25) VALUES(1, 1, 2021, 'BCI', 3.00, 'otraFuenteImpacto', 1.0, 'CLASE1', false);
INSERT INTO test.indice_impacto (id, produccion_cientifica_id, anio, tipo_fuente_impacto, numero_revistas, otra_fuente_impacto, posicion_publicacion, tipo_ranking, revista_25) VALUES(2, 2, 2021, 'BCI', 3.00, 'otraFuenteImpacto', 2.0, 'CLASE2', false);
INSERT INTO test.indice_impacto (id, produccion_cientifica_id, anio, tipo_fuente_impacto, numero_revistas, otra_fuente_impacto, posicion_publicacion, tipo_ranking, revista_25) VALUES(3, 3, 2020, 'BCI', 3.00, 'otraFuenteImpacto', 3.0, 'CLASE1', false);

-- Comites
INSERT INTO test.indice_impacto (id, produccion_cientifica_id, anio, tipo_fuente_impacto, numero_revistas, otra_fuente_impacto, posicion_publicacion, tipo_ranking, revista_25) VALUES(300, 300, 2021, '000', 3.00, 'otraFuenteImpacto', 1.0, 'CLASE1', false);
INSERT INTO test.indice_impacto (id, produccion_cientifica_id, anio, tipo_fuente_impacto, numero_revistas, otra_fuente_impacto, posicion_publicacion, tipo_ranking, revista_25) VALUES(301, 301, 2021, 'CITEC', 3.00, 'otraFuenteImpacto', 2.0, 'CLASE2', false);
INSERT INTO test.indice_impacto (id, produccion_cientifica_id, anio, tipo_fuente_impacto, numero_revistas, otra_fuente_impacto, posicion_publicacion, tipo_ranking, revista_25) VALUES(302, 302, 2020, '000', 3.00, 'otraFuenteImpacto', 3.0, 'CLASE1', false);

-- Congresos
INSERT INTO test.indice_impacto (id, produccion_cientifica_id, anio, tipo_fuente_impacto, numero_revistas, otra_fuente_impacto, posicion_publicacion, tipo_ranking, revista_25) VALUES(400, 400, 2021, 'GII-GRIN-SCIE', 3.00, 'otraFuenteImpacto', 1.0, 'CLASE1', false);
INSERT INTO test.indice_impacto (id, produccion_cientifica_id, anio, tipo_fuente_impacto, numero_revistas, otra_fuente_impacto, posicion_publicacion, tipo_ranking, revista_25) VALUES(401, 401, 2021, 'CORE', 3.00, 'otraFuenteImpacto', 2.0, 'A_POR', false);
INSERT INTO test.indice_impacto (id, produccion_cientifica_id, anio, tipo_fuente_impacto, numero_revistas, otra_fuente_impacto, posicion_publicacion, tipo_ranking, revista_25) VALUES(402, 402, 2020, 'GII-GRIN-SCIE', 3.00, 'otraFuenteImpacto', 3.0, 'CLASE1', false);

ALTER SEQUENCE test.indice_impacto_seq RESTART WITH 10000;

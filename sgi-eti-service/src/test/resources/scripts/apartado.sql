-- DEPENDENCIAS: APARTADO
/*
  scripts = { 
    "classpath:scripts/formulario.sql",
    "classpath:scripts/bloque.sql",
  }
*/

-- APARTADO FORMULARIO
INSERT INTO test.apartado (id, bloque_id, padre_id, orden) VALUES(1, 1, NULL, 1);
INSERT INTO test.apartado (id, bloque_id, padre_id, orden) VALUES(2, 1, 1, 2);
INSERT INTO test.apartado (id, bloque_id, padre_id, orden) VALUES(3, 1, 1, 3);
INSERT INTO test.apartado (id, bloque_id, padre_id, orden) VALUES(4, 1, NULL, 4);
INSERT INTO test.apartado (id, bloque_id, padre_id, orden) VALUES(5, 1, 4, 5);

INSERT INTO test.apartado_definicion (apartado_id, nombre, esquema, lang) VALUES(1, 'Apartado01', '{"nombre":"EsquemaApartado01"}', 'es');
INSERT INTO test.apartado_definicion (apartado_id, nombre, esquema, lang) VALUES(1, 'EU:Apartado01', '{"nombre":"EsquemaApartado01"}', 'en');
INSERT INTO test.apartado_definicion (apartado_id, nombre, esquema, lang) VALUES(2, 'Apartado2', '{"nombre":"EsquemaApartado2"}', 'es');
INSERT INTO test.apartado_definicion (apartado_id, nombre, esquema, lang) VALUES(2, 'EU:Apartado2', '{"nombre":"EsquemaApartado2"}', 'en');
INSERT INTO test.apartado_definicion (apartado_id, nombre, esquema, lang) VALUES(3, 'Apartado03', '{"nombre":"EsquemaApartado03"}', 'es');
INSERT INTO test.apartado_definicion (apartado_id, nombre, esquema, lang) VALUES(4, 'Apartado4', '{"nombre":"EsquemaApartado4"}', 'es');
INSERT INTO test.apartado_definicion (apartado_id, nombre, esquema, lang) VALUES(5, 'Apartado05', '{"nombre":"EsquemaApartado05"}', 'es');

ALTER SEQUENCE test.apartado_seq RESTART WITH 6;
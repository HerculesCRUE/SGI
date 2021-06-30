-- DEPENDENCIAS: APARTADO
/*
  scripts = { 
    "classpath:scripts/formulario.sql",
    "classpath:scripts/bloque.sql",
  }
*/

-- APARTADO FORMULARIO
INSERT INTO eti.apartado (id, bloque_id, nombre, padre_id, orden, esquema) VALUES(1, 1, 'Apartado01', NULL, 1, '{"nombre":"EsquemaApartado01"}');
INSERT INTO eti.apartado (id, bloque_id, nombre, padre_id, orden, esquema) VALUES(2, 1, 'Apartado2', 1, 2, '{"nombre":"EsquemaApartado2"}');
INSERT INTO eti.apartado (id, bloque_id, nombre, padre_id, orden, esquema) VALUES(3, 1, 'Apartado03', 1, 3, '{"nombre":"EsquemaApartado03"}');
INSERT INTO eti.apartado (id, bloque_id, nombre, padre_id, orden, esquema) VALUES(4, 1, 'Apartado4', NULL, 4, '{"nombre":"EsquemaApartado4"}');
INSERT INTO eti.apartado (id, bloque_id, nombre, padre_id, orden, esquema) VALUES(5, 1, 'Apartado05', 4, 5, '{"nombre":"EsquemaApartado05"}');

ALTER SEQUENCE eti.apartado_seq RESTART WITH 6;
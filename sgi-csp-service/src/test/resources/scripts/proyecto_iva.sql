/* DEPENDENCIAS
  scripts = {
      "classpath:scripts/proyecto.sql" }
*/

INSERT INTO test.proyecto_iva (id, proyecto_id, iva, fecha_inicio, fecha_fin)
VALUES (1, 1, 3, '2020-01-12T00:00:00Z', '2021-12-31T23:59:59Z');

INSERT INTO test.proyecto_iva (id, proyecto_id, iva, fecha_inicio, fecha_fin)
VALUES (2, 1, 5, '2020-01-12T00:00:00Z', '2021-12-31T23:59:59Z');

ALTER SEQUENCE test.proyecto_iva_seq RESTART WITH 3;
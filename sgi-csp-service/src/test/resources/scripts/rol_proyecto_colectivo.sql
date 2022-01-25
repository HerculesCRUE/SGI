-- DEPENDENCIAS
/*
scripts = {
    // @formatter:off
    "classpath:scripts/rol_proyecto.sql"
    // @formatter:on
  }
*/


INSERT INTO test.rol_proyecto_colectivo
(id, colectivo_ref, rol_proyecto_id)
VALUES
(1, '3', 1),
(2, '4', 1),
(3, '4', 2),
(4, '3', 2),
(5, '3', 3),
(6, '4', 4),
(7, '2', 5),
(8, '3', 5),
(9, '4', 5),
(10, '2', 6),
(11, '4', 6),
(12, '1', 7),
(13, '3', 8);

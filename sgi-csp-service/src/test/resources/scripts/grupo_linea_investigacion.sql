-- DEPENDENCIAS
/*scripts = {
    // @formatter:off    
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/linea_investigacion.sql"
    // @formatter:on  
  }*/

INSERT INTO test.grupo_linea_investigacion (id, grupo_id, linea_investigacion_id, fecha_inicio, fecha_fin)
VALUES
(1, 1, 1, '2021-01-01 22:59:59.000', '2022-10-07 23:00:00.000'),
(2, 2, 2, '2021-11-01 22:59:59.000', '2022-02-14 23:00:00.000'),
(3, 2, 3, '2021-11-01 22:59:59.000', '2022-02-14 23:00:00.000');



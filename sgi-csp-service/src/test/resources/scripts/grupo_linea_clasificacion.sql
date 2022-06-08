-- DEPENDENCIAS
/*scripts = {
    // @formatter:off    
      "classpath:scripts/rol_proyecto.sql",
      "classpath:scripts/grupo.sql",
      "classpath:scripts/linea_investigacion.sql",
      "classpath:scripts/grupo_linea_investigacion.sql",
    // @formatter:on  
  }*/

INSERT INTO test.grupo_linea_clasificacion (id, grupo_linea_investigacion_id, clasificacion_ref)
VALUES
(1, 1, 'c1'),
(2, 1, 'c2'),
(3, 1, 'c3'),
(4, 1, '4'),
(5, 2, '2');

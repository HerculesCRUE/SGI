-- DEPENDENCIAS
/*scripts = {
    // @formatter:off    
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/proyecto.sql"
    // @formatter:on  
  }*/

INSERT INTO test.proyecto_area
(id, area_conocimiento_ref, proyecto_id)
VALUES
(1, '00001', 1),
(2, '00002', 1),
(3, '00003', 1);

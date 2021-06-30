-- DEPENDENCIAS: modelo_ejecucion, modelo_unidad, tipo_finalidad, tipo_ambito_geografico, estado_proyecto, proyecto, programa
/*
scripts = {
      // @formatter:off
      "classpath:scripts/modelo_ejecucion.sql",
      "classpath:scripts/modelo_unidad.sql",
      "classpath:scripts/tipo_finalidad.sql",
      "classpath:scripts/tipo_ambito_geografico.sql",
      "classpath:scripts/proyecto.sql",
      "classpath:scripts/estado_proyecto.sql",
      "classpath:scripts/programa.sql",
      "classpath:scripts/proyecto_entidad_convocante.sql"
      // @formatter:on
  }
*/
INSERT INTO csp.proyecto_entidad_convocante (id, entidad_ref, proyecto_id, programa_id, programa_convocatoria_id) 
VALUES (1, 'ent-001', 1, 8, 4);

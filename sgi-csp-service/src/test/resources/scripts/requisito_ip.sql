-- DEPENDENCIAS: modelo_ejecucion, tipo_finalidad, tipo_regimen_concurrencia, tipo_ambito_geografico, convocatoria
/*
  scripts = { 
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/convocatoria.sql"
    // @formatter:on
  }
*/
INSERT INTO test.requisito_ip
  (id, num_maximo_ip, f_min_nivel_academico, f_max_nivel_academico, edad_maxima, sexo_ref, vinculacion_universidad, f_min_categoria_profesional, f_max_categoria_profesional, num_minimo_competitivos, num_minimo_no_competitivos, num_maximo_competitivos_activos, num_maximo_no_competitivos_activos, otros_requisitos) 
VALUES
  (1, 4, null, null, 48, 'Hombre', false, null, null, 10, 10, 15, 15, 'otros'),
  (2, 4, null, null, 48, 'Hombre', false, null, null, 10, 10, 15, 15, 'otros');

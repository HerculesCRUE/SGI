-- DEPENDENCIAS: modelo_ejecucion, modelo_unidad, tipo_finalidad, modelo_tipo_finalidad, tipo_regimen_concurrencia, tipo_ambito_geografico, estado_proyecto, proyecto, tipo_origen_fuente_financiacion, fuente_financiacion, tipo_financiacion
/*
  scripts = {
    // @formatter:off
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_unidad.sql", 
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/modelo_tipo_finalidad.sql", 
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/tipo_ambito_geografico.sql", 
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/estado_proyecto.sql", 
    "classpath:scripts/tipo_origen_fuente_financiacion.sql",
    "classpath:scripts/fuente_financiacion.sql", 
    "classpath:scripts/tipo_financiacion.sql",
    "classpath:scripts/proyecto_entidad_financiadora.sql",
    // @formatter:on
  })
*/

INSERT INTO csp.proyecto_entidad_financiadora 
(id, proyecto_id, entidad_ref, fuente_financiacion_id, tipo_financiacion_id, porcentaje_financiacion, importe_financiacion, ajena) 
VALUES (1, 1, 'entidad-001', 1, 1, 20, 1000, false);
-- DEPENDENCIAS
/*
scripts = { 
    // @formatter:off
    "classpath:scripts/tipo_fase.sql",
    "classpath:scripts/tipo_documento.sql",
    "classpath:scripts/modelo_ejecucion.sql",
    "classpath:scripts/modelo_tipo_fase.sql",
    "classpath:scripts/modelo_tipo_documento.sql",
    "classpath:scripts/modelo_unidad.sql",
    "classpath:scripts/tipo_finalidad.sql",
    "classpath:scripts/tipo_ambito_geografico.sql",
    "classpath:scripts/proyecto.sql",
    "classpath:scripts/concepto_gasto.sql",
    "classpath:scripts/proyecto_concepto_gasto.sql",
    "classpath:scripts/tipo_regimen_concurrencia.sql",
    "classpath:scripts/convocatoria.sql"
    // @formatter:on
  }
*/

INSERT INTO test.convocatoria_concepto_gasto
(id, convocatoria_id, importe_maximo, mes_final, mes_inicial, observaciones, permitido, concepto_gasto_id)
VALUES
(1, 1, 10000.0, 3, 1, NULL, true, 1),
(2, 1, NULL, 5, 2, NULL, true, 2),
(3, 1, NULL, 10, 7, NULL, false, 3),
(4, 2, NULL, NULL, NULL, NULL, false, 1),
(5, 2, 20000.0, 12, 1, NULL, true, 1),
(6, 2, 30000.0, 24, 13, NULL, true, 3),
(7, 2, NULL, NULL, NULL, NULL, false, 11);


INSERT INTO test.configuracion
  (id, formato_partida_presupuestaria, plantilla_formato_partida_presupuestaria, formato_identificador_justificacion, validacion_clasificacion_gastos, formato_codigo_interno_proyecto, plantilla_formato_codigo_interno_proyecto, gin_ejecucion_economica, cardinalidad_relacion_sgi_sge, partidas_presupuestarias_sge) 
VALUES
  (1, 'formato-partida-presupuestaria', 'xx.xxxx.xxxx.xxxxx', '^[0-9]{2}\/[0-9]{4}$', 'VALIDACION','^[A-Za-z0-9]{4}-[A-Za-z0-9]{3}-[A-Za-z0-9]{3}$','AAAA-YYY-YYY', true, 'SGI_N_SGE_N', false);


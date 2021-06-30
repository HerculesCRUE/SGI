-- MODELO EJECUCION
INSERT INTO csp.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-1', 'descripcion-1', true);
INSERT INTO csp.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (2, 'nombre-2', 'descripcion-2', true);

-- MODELO UNIDAD
INSERT INTO csp.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (1, 'unidad-001', 1, true);
INSERT INTO csp.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (2, 'unidad-002', 2, true);

-- TIPO_FINALIDAD
INSERT INTO csp.tipo_finalidad (id,nombre,descripcion,activo) VALUES (1,'nombre-1','descripcion-1',true);
INSERT INTO csp.tipo_finalidad (id,nombre,descripcion,activo) VALUES (2,'nombre-2','descripcion-2',true);

-- MODELO TIPO FINALIDAD
INSERT INTO csp.modelo_tipo_finalidad (id, modelo_ejecucion_id, tipo_finalidad_id, activo) VALUES (1, 1, 1, true);
INSERT INTO csp.modelo_tipo_finalidad (id, modelo_ejecucion_id, tipo_finalidad_id, activo) VALUES (2, 2, 2, true);

-- TIPO_REGIMEN_CONCURRENCIA
INSERT INTO csp.tipo_regimen_concurrencia (id,nombre,activo) VALUES (1,'nombre-1',true);
INSERT INTO csp.tipo_regimen_concurrencia (id,nombre,activo) VALUES (2,'nombre-2',true);

-- TIPO AMBITO GEOGRAFICO
INSERT INTO csp.tipo_ambito_geografico (id, nombre, activo) VALUES (1, 'nombre-001', true);
INSERT INTO csp.tipo_ambito_geografico (id, nombre, activo) VALUES (2, 'nombre-002', true);

-- CONVOCATORIA
INSERT INTO csp.convocatoria
(id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, colaborativos, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo)
VALUES(1, 'unidad-001', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-001', 'objeto-001', 'observaciones-001', 1, 1, true, 'REGISTRADA', 12, 1, 'AYUDAS', true);
INSERT INTO csp.convocatoria
(id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, colaborativos, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo)
VALUES(2, 'unidad-002', 1, 'codigo-002', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-002', 'objeto-002', 'observaciones-002', 1, 1, true, 'BORRADOR', 12, 1, 'COMPETITIVOS', true);


-- CONVOCATORIA PERIODO JUSTIFICACION
INSERT INTO csp.convocatoria_periodo_justificacion (id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones, tipo) 
  VALUES (1, 1, 1, 1, 2, '2020-10-10T00:00:00Z', '2020-11-20T23:59:59Z', 'observaciones-001', 'PERIODICO');
INSERT INTO csp.convocatoria_periodo_justificacion (id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones, tipo) 
  VALUES (2, 1, 2, 10, 21, '2020-10-10T00:00:00Z', '2020-11-20T23:59:59Z', 'observaciones-002', 'PERIODICO');
INSERT INTO csp.convocatoria_periodo_justificacion (id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones, tipo) 
  VALUES (3, 1, 3, 23, 24, null, null, 'observaciones-003', 'PERIODICO');
INSERT INTO csp.convocatoria_periodo_justificacion (id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones, tipo) 
  VALUES (4, 1, 4, 25, 28, null, null, 'observaciones-4', 'FINAL');
INSERT INTO csp.convocatoria_periodo_justificacion (id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones, tipo) 
  VALUES (5, 2, 1, 3, 10, null, null,'observaciones-5', 'PERIODICO');
INSERT INTO csp.convocatoria_periodo_justificacion (id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones, tipo) 
  VALUES (6, 2, 2, 15, 18, '2020-09-10T00:00:00Z', '2020-10-01T23:59:59Z', 'observaciones-6', 'PERIODICO');
INSERT INTO csp.convocatoria_periodo_justificacion (id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones, tipo) 
  VALUES (7, 2, 3, 20, 24, '2020-10-10T00:00:00Z', '2020-11-20T23:59:59Z', 'observaciones-7', 'PERIODICO');
INSERT INTO csp.convocatoria_periodo_justificacion (id, convocatoria_id, num_periodo, mes_inicial, mes_final, fecha_inicio_presentacion, fecha_fin_presentacion, observaciones, tipo) 
  VALUES (8, 2, 4, 25, 28, null, null, 'observaciones-8', 'FINAL');



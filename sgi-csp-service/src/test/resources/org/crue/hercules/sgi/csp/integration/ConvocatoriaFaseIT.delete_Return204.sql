-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-1', 'descripcion-1', true);
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (2, 'nombre-2', 'descripcion-2', true);

-- MODELO UNIDAD
INSERT INTO test.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (1, 'unidad-001', 1, true);
INSERT INTO test.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (2, 'unidad-002', 2, true);

-- TIPO_FINALIDAD
INSERT INTO test.tipo_finalidad (id,nombre,descripcion,activo) VALUES (1,'nombre-1','descripcion-1',true);
INSERT INTO test.tipo_finalidad (id,nombre,descripcion,activo) VALUES (2,'nombre-2','descripcion-2',true);

-- MODELO TIPO FINALIDAD
INSERT INTO test.modelo_tipo_finalidad (id, modelo_ejecucion_id, tipo_finalidad_id, activo) VALUES (1, 1, 1, true);
INSERT INTO test.modelo_tipo_finalidad (id, modelo_ejecucion_id, tipo_finalidad_id, activo) VALUES (2, 2, 2, true);

-- TIPO_REGIMEN_CONCURRENCIA
INSERT INTO test.tipo_regimen_concurrencia (id,nombre,activo) VALUES (1,'nombre-1',true);
INSERT INTO test.tipo_regimen_concurrencia (id,nombre,activo) VALUES (2,'nombre-2',true);

-- TIPO AMBITO GEOGRAFICO
INSERT INTO test.tipo_ambito_geografico (id, nombre, activo) VALUES (1, 'nombre-001', true);
INSERT INTO test.tipo_ambito_geografico (id, nombre, activo) VALUES (2, 'nombre-002', true);

-- CONVOCATORIA
INSERT INTO test.convocatoria
(id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo)
VALUES(1, 'unidad-001', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-001', 'objeto-001', 'observaciones-001', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', true);
INSERT INTO test.convocatoria
(id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo)
VALUES(2, 'unidad-002', 1, 'codigo-002', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-002', 'objeto-002', 'observaciones-002', 1, 1, 'BORRADOR', 12, 1, 'COMPETITIVOS', true);

--TIPO FASE
INSERT INTO test.tipo_fase (id,nombre,descripcion,activo) VALUES (1,'nombre-1','descripcion-1',true);

-- MODELO TIPO FASE
INSERT INTO test.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (1, 1, 1, true, true, true, true);

--CONVOCATORIA FASE
INSERT INTO test.convocatoria_fase(id, convocatoria_id, tipo_fase_id, fecha_inicio, fecha_fin, observaciones) VALUES (1, 1, 1, '2020-10-18T00:00:00Z', '2020-11-01T23:59:59Z', 'observaciones-1')
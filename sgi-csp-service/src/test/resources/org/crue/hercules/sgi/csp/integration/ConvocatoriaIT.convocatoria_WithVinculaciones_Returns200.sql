-- MODELO EJECUCION
INSERT INTO csp.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-1', 'descripcion-1', true);

-- MODELO UNIDAD
INSERT INTO csp.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (1, 'unidad-001', 1, true);

-- TIPO_FINALIDAD
INSERT INTO csp.tipo_finalidad (id,nombre,descripcion,activo) VALUES (1,'nombre-1','descripcion-1',true);

-- MODELO TIPO FINALIDAD
INSERT INTO csp.modelo_tipo_finalidad (id, modelo_ejecucion_id, tipo_finalidad_id, activo) VALUES (1, 1, 1, true);

-- TIPO_REGIMEN_CONCURRENCIA
INSERT INTO csp.tipo_regimen_concurrencia (id,nombre,activo) VALUES (1,'nombre-1',true);

-- TIPO AMBITO GEOGRAFICO
INSERT INTO csp.tipo_ambito_geografico (id, nombre, activo) VALUES (1, 'nombre-001', true);

-- CONVOCATORIA
INSERT INTO csp.convocatoria
(id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, colaborativos, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo)
VALUES(1, 'unidad-001', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-001', 'objeto-001', 'observaciones-001', 1, 1, true, 'REGISTRADA', 12, 1, 'AYUDAS', true);

-- VINCULACIONES

--TIPO ENLACE
insert into csp.tipo_enlace (id,nombre,descripcion,activo) values (1,'nombre-1','descripcion-1',true);

-- MODELO TIPO ENLACE
INSERT INTO csp.modelo_tipo_enlace (id, tipo_enlace_id, modelo_ejecucion_id, activo) VALUES (1, 1, 1, true);

--CONVOCATORIA ENLACE
INSERT INTO csp.convocatoria_enlace (id, convocatoria_id, url, descripcion, tipo_enlace_id) VALUES(1, 1, 'www.url1.com','descripcion' ,1);

--TIPO FASE
INSERT INTO csp.tipo_fase (id,nombre,descripcion,activo) VALUES (1,'nombre-1','descripcion-1',true);

-- MODELO TIPO FASE
INSERT INTO csp.modelo_tipo_fase (id, tipo_fase_id, modelo_ejecucion_id, solicitud, convocatoria, proyecto, activo) VALUES (1, 1, 1, true, true, true, true);

--CONVOCATORIA FASE
INSERT INTO csp.convocatoria_fase(id, convocatoria_id, tipo_fase_id, fecha_inicio, fecha_fin, observaciones) VALUES (1, 1, 1, '2020-10-18T00:00:00Z', '2020-11-01T23:59:59Z', 'observaciones-1');

--TIPO HITO
insert into csp.tipo_hito (id,nombre,descripcion,activo) values (1,'nombre-1','descripcion-1',true);

-- MODELO TIPO HITO
INSERT INTO csp.modelo_tipo_hito (id, modelo_ejecucion_id, tipo_hito_id, solicitud, proyecto, convocatoria, activo) VALUES (1, 1, 1, true, true, true, true);

--CONVOCATORIA HITO
INSERT INTO csp.convocatoria_hito (id, convocatoria_id, tipo_hito_id,  fecha, comentario, genera_aviso ) values(1, 1, 1,'2021-10-22T00:00:00Z', 'comentario-1', true);

-- TIPO DOCUMENTO
INSERT INTO csp.tipo_documento (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);

-- MODELO TIPO DOCUMENTO
INSERT INTO csp.modelo_tipo_documento (id, tipo_documento_id, modelo_ejecucion_id, modelo_tipo_fase_id, activo) VALUES (1, 1, 1, 1, true);

-- CONVOCATORIA DOCUMENTO
INSERT INTO CSP.CONVOCATORIA_DOCUMENTO (ID, CONVOCATORIA_ID, TIPO_FASE_ID, TIPO_DOCUMENTO_ID, NOMBRE, PUBLICO, OBSERVACIONES, DOCUMENTO_REF) VALUES(1, 1, 1, 1, 'nombre doc-1', true, 'observacionesConvocatoriaDocumento-1', 'documentoRef-1');


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
(id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo)
VALUES(1, '2', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-001', 'objeto-001', 'observaciones-001', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', true);

-- TIPO FASE
INSERT INTO csp.tipo_fase (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);

--CONVOCATORIA FASE
INSERT INTO csp.convocatoria_fase(id, convocatoria_id, tipo_fase_id, fecha_inicio, fecha_fin, observaciones) VALUES (1, 1, 1, '2020-10-01T00:00:00Z', '2020-10-15T23:59:59Z', 'observaciones-1');

-- CONFIGURACION SOLICITUD
INSERT INTO csp.configuracion_solicitud 
(id, convocatoria_id, tramitacion_sgi, convocatoria_fase_id, importe_maximo_solicitud, formulario_solicitud) 
VALUES(1, 1, TRUE, 1, 12345, 'ESTANDAR');

-- PROGRAMA
INSERT INTO csp.programa (id, nombre, descripcion, programa_padre_id, activo) VALUES (1, 'nombre-001', 'descripcion-001', null, true);
INSERT INTO csp.programa (id, nombre, descripcion, programa_padre_id, activo) VALUES (2, 'nombre-002', 'descripcion-002', 1, true);
INSERT INTO csp.programa (id, nombre, descripcion, programa_padre_id, activo) VALUES (3, 'nombre-003', 'descripcion-003', 1, true);

-- CONVOCATORIA ENTIDAD CONVOCANTE
INSERT INTO csp.convocatoria_entidad_convocante (id,  convocatoria_id, entidad_ref, programa_id) VALUES (1, 1, 'entidad-001', 1);
INSERT INTO csp.convocatoria_entidad_convocante (id,  convocatoria_id, entidad_ref, programa_id) VALUES (2, 1, 'entidad-002', 1);

-- SOLICITUD
INSERT INTO csp.solicitud (id,titulo, codigo_externo, codigo_registro_interno, estado_solicitud_id, convocatoria_id, creador_ref, solicitante_ref, observaciones, convocatoria_externa, unidad_gestion_ref, formulario_solicitud, activo)
 VALUES (1, 'titulo',null, 'SGI_SLC1202011061027', null, 1, 'usr-001', 'usr-002', 'observaciones-001', null, '2', 'ESTANDAR', true);
INSERT INTO csp.solicitud (id,titulo, codigo_externo, codigo_registro_interno, estado_solicitud_id, convocatoria_id, creador_ref, solicitante_ref, observaciones, convocatoria_externa, unidad_gestion_ref, formulario_solicitud, activo)
 VALUES (2, 'titulo',null, 'SGI_SLC2202011061027', null, 1, 'usr-001', 'usr-002', 'observaciones-002', null, '2', 'ESTANDAR', true);
INSERT INTO csp.solicitud (id,titulo,codigo_externo, codigo_registro_interno, estado_solicitud_id, convocatoria_id, creador_ref, solicitante_ref, observaciones, convocatoria_externa, unidad_gestion_ref, formulario_solicitud, activo)
 VALUES (3, 'titulo',null, 'SGI_SLC3202011061027', null, 1, 'usr-001', 'usr-002', 'observaciones-003', null, '2', 'ESTANDAR', true);
INSERT INTO csp.solicitud (id,titulo,codigo_externo, codigo_registro_interno, estado_solicitud_id, convocatoria_id, creador_ref, solicitante_ref, observaciones, convocatoria_externa, unidad_gestion_ref, formulario_solicitud, activo)
 VALUES (4, 'titulo',null, 'SGI_SLC4202011061027', null, 1, 'usr-001', 'usr-002', 'observaciones-004', null, '2', 'ESTANDAR', false);
INSERT INTO csp.solicitud (id,titulo,codigo_externo, codigo_registro_interno, estado_solicitud_id, convocatoria_id, creador_ref, solicitante_ref, observaciones, convocatoria_externa, unidad_gestion_ref, formulario_solicitud, activo)
 VALUES (5,'titulo', null, 'SGI_SLC5202011061027', null, 1, 'usr-001', 'usr-002', 'observaciones-005', null, '1', 'ESTANDAR', true);
 
-- ESTADO SOLICITUD
INSERT INTO csp.estado_solicitud (id, solicitud_id, estado, fecha_estado, comentario) VALUES (1, 1, 'BORRADOR', '2020-11-17T00:00:00Z', 'comentario');
INSERT INTO csp.estado_solicitud (id, solicitud_id, estado, fecha_estado, comentario) VALUES (2, 2, 'BORRADOR', '2020-11-17T00:00:00Z', 'comentario');
INSERT INTO csp.estado_solicitud (id, solicitud_id, estado, fecha_estado, comentario) VALUES (3, 3, 'BORRADOR', '2020-11-17T00:00:00Z', 'comentario');
INSERT INTO csp.estado_solicitud (id, solicitud_id, estado, fecha_estado, comentario) VALUES (4, 4, 'BORRADOR', '2020-11-17T00:00:00Z', 'comentario');
INSERT INTO csp.estado_solicitud (id, solicitud_id, estado, fecha_estado, comentario) VALUES (5, 5, 'BORRADOR', '2020-11-17T00:00:00Z', 'comentario');

-- UPDATE SOLICITUD
UPDATE csp.solicitud SET estado_solicitud_id = 1 WHERE id = 1;
UPDATE csp.solicitud SET estado_solicitud_id = 2 WHERE id = 2;
UPDATE csp.solicitud SET estado_solicitud_id = 3 WHERE id = 3;
UPDATE csp.solicitud SET estado_solicitud_id = 4 WHERE id = 4;
UPDATE csp.solicitud SET estado_solicitud_id = 5 WHERE id = 5;

-- SOLICITUD PROYECTO DATOS
INSERT INTO csp.solicitud_proyecto (id, colaborativo, tipo_presupuesto) 
VALUES (1, true, 'GLOBAL');
INSERT INTO csp.solicitud_proyecto (id, colaborativo, tipo_presupuesto) 
VALUES (2, true, 'GLOBAL');


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
(id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, titulo, objeto, observaciones, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, activo, formulario_solicitud)
VALUES(1, '2', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-001', 'objeto-001', 'observaciones-001', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', true, 'PROYECTO');

-- TIPO FASE
INSERT INTO csp.tipo_fase (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);

--CONVOCATORIA FASE
INSERT INTO csp.convocatoria_fase(id, convocatoria_id, tipo_fase_id, fecha_inicio, fecha_fin, observaciones) VALUES (1, 1, 1, '2020-10-01T00:00:00Z', '2020-10-15T23:59:59Z', 'observaciones-1');

-- CONFIGURACION SOLICITUD
INSERT INTO csp.configuracion_solicitud 
(id, convocatoria_id, tramitacion_sgi, convocatoria_fase_id, importe_maximo_solicitud) 
VALUES(1, 1, TRUE, 1, 12345);

-- PROGRAMA
INSERT INTO csp.programa (id, nombre, descripcion, programa_padre_id, activo) VALUES (1, 'nombre-001', 'descripcion-001', null, true);

-- CONVOCATORIA ENTIDAD CONVOCANTE
INSERT INTO csp.convocatoria_entidad_convocante (id,  convocatoria_id, entidad_ref, programa_id) VALUES (1, 1, 'entidad-001', 1);
INSERT INTO csp.convocatoria_entidad_convocante (id,  convocatoria_id, entidad_ref, programa_id) VALUES (2, 1, 'entidad-002', 1);

-- SOLICITUD
INSERT INTO csp.solicitud (id, titulo,codigo_externo, codigo_registro_interno, estado_solicitud_id, convocatoria_id, creador_ref, solicitante_ref, observaciones, convocatoria_externa, unidad_gestion_ref, formulario_solicitud, activo)
  VALUES (1,'titulo', null, 'SGI_SLC1202011061027', null, 1, 'usr-001', 'personaRef-001', 'observaciones 1', null, '2', 'PROYECTO', true);
INSERT INTO csp.solicitud (id, titulo,codigo_externo, codigo_registro_interno, estado_solicitud_id, convocatoria_id, creador_ref, solicitante_ref, observaciones, convocatoria_externa, unidad_gestion_ref, formulario_solicitud, activo)
  VALUES (2, 'titulo',null, 'SGI_SLC2202011061027', null, 1, 'usr-001', 'usr-002', 'observaciones-002', null, '2', 'PROYECTO', true);

-- ESTADO SOLICITUD
INSERT INTO csp.estado_solicitud (id, solicitud_id, estado, fecha_estado, comentario) VALUES (1, 1, 'BORRADOR', '2020-11-17T00:00:00Z', 'comentario');
INSERT INTO csp.estado_solicitud (id, solicitud_id, estado, fecha_estado, comentario) VALUES (2, 2, 'BORRADOR', '2020-11-17T00:00:00Z', 'comentario');

-- UPDATE SOLICITUD
UPDATE csp.solicitud SET estado_solicitud_id = 1 WHERE id = 1;
UPDATE csp.solicitud SET estado_solicitud_id = 2 WHERE id = 2;

-- SOLICITUD PROYECTO DATOS
INSERT INTO csp.solicitud_proyecto (id, colaborativo, tipo_presupuesto ) 
  VALUES (1,true, 'GLOBAL');
INSERT INTO csp.solicitud_proyecto (id, colaborativo, tipo_presupuesto ) 
  VALUES (2,true, 'GLOBAL');

-- ROL PROYECTO
INSERT INTO csp.rol_proyecto (id, abreviatura, nombre, descripcion, rol_principal, orden, equipo, activo) 
VALUES(1, '001', 'nombre-001', 'descripcion-001', false, null, 'INVESTIGACION', true);

-- TIPO ORIGEN FUENTE FINANCIACION
INSERT INTO csp.tipo_origen_fuente_financiacion (id, nombre, activo) VALUES (1, 'nombre-001', true);

-- FUENTE FINANCIACION
INSERT INTO csp.fuente_financiacion (id, nombre, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (1, 'nombre-001', 'descripcion-001', true, 1, 1, true);

-- TIPO FINANCIACION
INSERT INTO csp.tipo_financiacion (id, nombre, descripcion, activo) VALUES (1, 'nombre-001', 'descripcion-001', true);

-- SOLICITUD PROYECTO ENTIDAD FINANCIADORA AJENA
INSERT INTO csp.solicitud_proyecto_entidad_financiadora_ajena (id, solicitud_proyecto_id, entidad_ref, fuente_financiacion_id, tipo_financiacion_id, porcentaje_financiacion, importe_financiacion) 
  VALUES (1, 1, 'entidad-001', 1, 1, 20, 1000);
INSERT INTO csp.solicitud_proyecto_entidad_financiadora_ajena (id, solicitud_proyecto_id, entidad_ref, fuente_financiacion_id, tipo_financiacion_id, porcentaje_financiacion, importe_financiacion) 
  VALUES (2, 1, 'entidad-002', null, null, 30, 1000);
INSERT INTO csp.solicitud_proyecto_entidad_financiadora_ajena (id, solicitud_proyecto_id, entidad_ref, fuente_financiacion_id, tipo_financiacion_id, porcentaje_financiacion, importe_financiacion) 
  VALUES (3, 1, 'entidad-003', null, null, 20, 1000);
INSERT INTO csp.solicitud_proyecto_entidad_financiadora_ajena (id, solicitud_proyecto_id, entidad_ref, fuente_financiacion_id, tipo_financiacion_id, porcentaje_financiacion, importe_financiacion) 
  VALUES (4, 2, 'entidad-004', null, null, 10, 1000);
INSERT INTO csp.solicitud_proyecto_entidad_financiadora_ajena (id, solicitud_proyecto_id, entidad_ref, fuente_financiacion_id, tipo_financiacion_id, porcentaje_financiacion, importe_financiacion) 
  VALUES (11, 1, 'entidad-011', null, null, 10, 1000);
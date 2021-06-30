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

-- AREA TEMATICA
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (1, 'nombre-001', 'descripcion-001', null, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (2, 'nombre-002', 'descripcion-002', null, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (3, 'nombre-003', 'descripcion-003', null, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (4, 'nombre-004', 'descripcion-004', null, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (5, 'nombre-005', 'descripcion-005', null, true);
INSERT INTO csp.area_tematica (id, nombre, descripcion, area_tematica_padre_id, activo) VALUES (6, 'nombre-006', 'descripcion-006', null, true);

-- CONVOCATORIA AREA TEMATICA
INSERT INTO csp.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (2, 1, 1, 'observaciones-002');
INSERT INTO csp.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (1, 1, 2, 'observaciones-001');
INSERT INTO csp.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (3, 1, 3, 'observaciones-003');
INSERT INTO csp.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (4, 1, 4, 'observaciones-4');
INSERT INTO csp.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (5, 2, 5, 'observaciones-001');
INSERT INTO csp.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (6, 2, 6, 'observaciones-002');
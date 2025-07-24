-- MODELO_EJECUCION
INSERT INTO test.modelo_ejecucion (id, activo) VALUES (1, true);
INSERT INTO test.modelo_ejecucion (id, activo) VALUES (2, true);

-- MODELO_EJECUCION_NOMBRE
INSERT INTO test.modelo_ejecucion_nombre (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'nombre-1');
INSERT INTO test.modelo_ejecucion_nombre (modelo_ejecucion_id, lang, value_) VALUES (2, 'es', 'nombre-2');

-- MODELO_EJECUCION_DESCRIPCION
INSERT INTO test.modelo_ejecucion_descripcion (modelo_ejecucion_id, lang, value_) VALUES (1, 'es', 'descripcion-1');
INSERT INTO test.modelo_ejecucion_descripcion (modelo_ejecucion_id, lang, value_) VALUES (2, 'es', 'descripcion-2');

-- MODELO UNIDAD
INSERT INTO test.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (1, 'unidad-001', 1, true);
INSERT INTO test.modelo_unidad (id, unidad_gestion_ref, modelo_ejecucion_id, activo) VALUES (2, 'unidad-002', 2, true);

-- TIPO_FINALIDAD
INSERT INTO test.tipo_finalidad (id, activo) VALUES (1, true);
INSERT INTO test.tipo_finalidad (id, activo) VALUES (2, true);

-- TIPO_FINALIDAD_NOMBRE
INSERT INTO test.tipo_finalidad_nombre (tipo_finalidad_id, lang, value_) VALUES(1, 'es', 'nombre-1');
INSERT INTO test.tipo_finalidad_nombre (tipo_finalidad_id, lang, value_) VALUES(2, 'es', 'nombre-2');

-- TIPO_FINALIDAD_DESCRIPCION
INSERT INTO test.tipo_finalidad_descripcion (tipo_finalidad_id, lang, value_) VALUES(1, 'es', 'descripcion-1');
INSERT INTO test.tipo_finalidad_descripcion (tipo_finalidad_id, lang, value_) VALUES(2, 'es', 'descripcion-2');

-- MODELO TIPO FINALIDAD
INSERT INTO test.modelo_tipo_finalidad (id, modelo_ejecucion_id, tipo_finalidad_id, activo) VALUES (1, 1, 1, true);
INSERT INTO test.modelo_tipo_finalidad (id, modelo_ejecucion_id, tipo_finalidad_id, activo) VALUES (2, 2, 2, true);

-- TIPO_REGIMEN_CONCURRENCIA
INSERT INTO test.tipo_regimen_concurrencia (id,activo) VALUES (1,true);
INSERT INTO test.tipo_regimen_concurrencia (id,activo) VALUES (2,true);

-- TIPO_REGIMEN_CONCURRENCIA_NOMBRE
INSERT INTO test.tipo_regimen_concurrencia_nombre (tipo_regimen_concurrencia_id, lang, value_) VALUES(1, 'es', 'nombre-1');
INSERT INTO test.tipo_regimen_concurrencia_nombre (tipo_regimen_concurrencia_id, lang, value_) VALUES(2, 'es', 'nombre-2');

-- TIPO AMBITO GEOGRAFICO
INSERT INTO test.tipo_ambito_geografico (id, activo) VALUES (1, true);
INSERT INTO test.tipo_ambito_geografico (id, activo) VALUES (2, true);

INSERT INTO test.tipo_ambito_geografico_nombre (tipo_ambito_geografico_id, lang, value_) VALUES(1, 'es', 'nombre-001');
INSERT INTO test.tipo_ambito_geografico_nombre (tipo_ambito_geografico_id, lang, value_) VALUES(2, 'es', 'nombre-002');

-- CONVOCATORIA
INSERT INTO test.convocatoria (id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, formulario_solicitud, activo)
VALUES (1, 'unidad-001', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', 'PROYECTO', true);
INSERT INTO test.convocatoria (id, unidad_gestion_ref, modelo_ejecucion_id, codigo, fecha_publicacion, fecha_provisional, fecha_concesion, tipo_finalidad_id, tipo_regimen_concurrencia_id, estado, duracion, tipo_ambito_geografico_id, clasificacion_cvn, formulario_solicitud, activo)
VALUES (2, 'unidad-002', 1, 'codigo-002', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 1, 1, 'BORRADOR', 12, 1, 'COMPETITIVOS', 'PROYECTO', true);

-- CONVOCATORIA TITULO
INSERT INTO test.convocatoria_titulo (convocatoria_id, lang, value_) VALUES (1, 'es', 'titulo-001');
INSERT INTO test.convocatoria_titulo (convocatoria_id, lang, value_) VALUES (2, 'es', 'titulo-002');

-- CONVOCATORIA OBJETO
INSERT INTO test.convocatoria_objeto (convocatoria_id, lang, value_) VALUES (1, 'es', 'objeto-001');
INSERT INTO test.convocatoria_objeto (convocatoria_id, lang, value_) VALUES (2, 'es', 'objeto-002');

-- CONVOCATORIA_OBSERVACIONES
INSERT INTO test.convocatoria_observaciones (convocatoria_id, lang, value_) VALUES (1, 'es', 'observaciones-001');
INSERT INTO test.convocatoria_observaciones (convocatoria_id, lang, value_) VALUES (2, 'es', 'observaciones-002');

-- AREA TEMATICA
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (1, null, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (2, null, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (3, null, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (4, null, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (5, null, true);
INSERT INTO test.area_tematica (id, area_tematica_padre_id, activo) VALUES (6, null, true);

-- AREA_TEMATICA_NOMBRE
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (2, 'es', 'nombre-002');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (3, 'es', 'nombre-003');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (4, 'es', 'nombre-004');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (5, 'es', 'nombre-005');
INSERT INTO test.area_tematica_nombre (area_tematica_id, lang, value_) VALUES (6, 'es', 'nombre-006');

-- AREA_TEMATICA_DESCRIPCION
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (1, 'es', 'nombre-001');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (2, 'es', 'nombre-002');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (3, 'es', 'nombre-003');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (4, 'es', 'nombre-004');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (5, 'es', 'nombre-005');
INSERT INTO test.area_tematica_descripcion (area_tematica_id, lang, value_) VALUES (6, 'es', 'nombre-006');


-- CONVOCATORIA AREA TEMATICA
INSERT INTO test.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (2, 1, 1, 'observaciones-002');
INSERT INTO test.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (1, 1, 2, 'observaciones-001');
INSERT INTO test.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (3, 1, 3, 'observaciones-003');
INSERT INTO test.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (4, 1, 4, 'observaciones-4');
INSERT INTO test.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (5, 2, 5, 'observaciones-001');
INSERT INTO test.convocatoria_area_tematica (id, convocatoria_id, area_tematica_id, observaciones) VALUES (6, 2, 6, 'observaciones-002');
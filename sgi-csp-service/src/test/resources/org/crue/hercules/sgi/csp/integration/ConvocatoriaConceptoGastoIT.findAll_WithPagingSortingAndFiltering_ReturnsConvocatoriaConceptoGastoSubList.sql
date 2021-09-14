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
VALUES(1, 'unidad-001', 1, 'codigo-001', '2021-10-15T23:59:59Z', '2021-10-16T23:59:59Z', '2021-10-17T23:59:59Z', 'titulo-001', 'objeto-001', 'observaciones-001', 1, 1, 'REGISTRADA', 12, 1, 'AYUDAS', true);

--CONCEPTO GASTO
insert into csp.concepto_gasto (id,nombre,descripcion,activo) values (1,'conceptoGasto-1','descripcion-1',true);

--CONVOCATORIA CONCEPTO GASTO
INSERT INTO csp.convocatoria_concepto_gasto(id, convocatoria_id, concepto_gasto_id, permitido) VALUES(1, 1, 1, true);
INSERT INTO csp.convocatoria_concepto_gasto(id, convocatoria_id, concepto_gasto_id, permitido) VALUES(2, 1, 1, true);
INSERT INTO csp.convocatoria_concepto_gasto(id, convocatoria_id, concepto_gasto_id, permitido) VALUES(3, 1, 1, false);
INSERT INTO csp.convocatoria_concepto_gasto(id, convocatoria_id, concepto_gasto_id, permitido) VALUES(4, 1, 1, true);
INSERT INTO csp.convocatoria_concepto_gasto(id, convocatoria_id, concepto_gasto_id, permitido) VALUES(5, 1, 1, false);
INSERT INTO csp.convocatoria_concepto_gasto(id, convocatoria_id, concepto_gasto_id, permitido) VALUES(6, 1, 1, true);

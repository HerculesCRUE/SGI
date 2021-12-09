-- MODELO EJECUCION
INSERT INTO test.modelo_ejecucion (id, nombre, descripcion, activo) VALUES (1, 'nombre-1', 'descripcion-1', true);

-- TIPO_FINALIDAD
INSERT INTO test.tipo_finalidad (id,nombre,descripcion,activo) VALUES (1,'nombre-1','descripcion-1',true);

-- TIPO AMBITO GEOGRAFICO
INSERT INTO test.tipo_ambito_geografico (id, nombre, activo) VALUES (1, 'nombre-001', true);

-- ESTADO PROYECTO
INSERT INTO test.estado_proyecto (id, proyecto_id, estado, fecha_estado, comentario) VALUES (1, 1, 'BORRADOR', '2020-11-17T00:00:00Z', 'comentario');

-- PROYECTO
INSERT INTO test.proyecto (id, titulo, codigo_externo, fecha_inicio, fecha_fin, unidad_gestion_ref, modelo_ejecucion_id, tipo_finalidad_id, tipo_ambito_geografico_id, confidencial, observaciones, estado_proyecto_id, activo)
  VALUES (1, 'PRO1', 'cod-externo-001', '2020-12-12T00:00:00Z', '2020-12-31T23:59:59Z', '2', 1, 1, 1, false, 'observaciones 1', 1, true);
INSERT INTO test.proyecto (id, titulo, codigo_externo, fecha_inicio, fecha_fin, unidad_gestion_ref, modelo_ejecucion_id, tipo_finalidad_id, tipo_ambito_geografico_id, confidencial, observaciones, estado_proyecto_id, activo)
  VALUES (2, 'PRO2', 'cod-externo-002', '2020-12-12T00:00:00Z', '2020-12-31T23:59:59Z', '2', 1, 1, 1, false, 'observaciones 2', 1, true);

-- ROL SOCIO
INSERT INTO test.rol_socio (id, abreviatura, nombre, descripcion, coordinador, activo) VALUES (1, '001', 'nombre-001', 'descripcion-001' , false, true);

-- PROYECTO SOCIO
INSERT INTO test.proyecto_socio (id, proyecto_id, empresa_ref, rol_socio_id, fecha_inicio, fecha_fin, num_investigadores, importe_concedido)
  VALUES (11, 1, 'empresa-001', 1, '2023-11-11T00:00:00Z', '2024-11-11T23:59:59Z', 55, 11000);
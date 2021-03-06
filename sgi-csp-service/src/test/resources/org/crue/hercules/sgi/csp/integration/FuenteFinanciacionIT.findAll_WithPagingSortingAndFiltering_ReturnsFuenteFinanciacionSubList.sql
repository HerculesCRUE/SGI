-- TIPO AMBITO GEOGRAFICO
INSERT INTO test.tipo_ambito_geografico (id, nombre, activo) VALUES (1, 'nombre-001', true);
INSERT INTO test.tipo_ambito_geografico (id, nombre, activo) VALUES (2, 'nombre-002', true);

-- TIPO ORIGEN FUENTE FINANCIACION
INSERT INTO test.tipo_origen_fuente_financiacion (id, nombre, activo) VALUES (1, 'nombre-001', true);
INSERT INTO test.tipo_origen_fuente_financiacion (id, nombre, activo) VALUES (2, 'nombre-002', true);

-- FUENTE FINANCIACION
INSERT INTO test.fuente_financiacion (id, nombre, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (1, 'nombre-001', 'descripcion-001', true, 1, 1, true);
INSERT INTO test.fuente_financiacion (id, nombre, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (2, 'nombre-002', 'descripcion-002', true, 2, 2, true);
INSERT INTO test.fuente_financiacion (id, nombre, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (3, 'nombre-003', 'descripcion-003', true, 2, 2, true);
INSERT INTO test.fuente_financiacion (id, nombre, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (4, 'nombre-004', 'descripcion-004', true, 2, 2, false);
INSERT INTO test.fuente_financiacion (id, nombre, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (11, 'nombre-011', 'descripcion-011', true, 1, 1, true);
INSERT INTO test.fuente_financiacion (id, nombre, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (12, 'nombre-012', 'descripcion-012', true, 2, 2, true);
  INSERT INTO test.fuente_financiacion (id, nombre, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (13, 'nombre-013', 'descripcion-013', true, 1, 1, true);
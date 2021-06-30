-- TIPO AMBITO GEOGRAFICO
INSERT INTO csp.tipo_ambito_geografico (id, nombre, activo) VALUES (1, 'nombre-001', true);
INSERT INTO csp.tipo_ambito_geografico (id, nombre, activo) VALUES (2, 'nombre-002', true);

-- TIPO ORIGEN FUENTE FINANCIACION
INSERT INTO csp.tipo_origen_fuente_financiacion (id, nombre, activo) VALUES (1, 'nombre-001', true);
INSERT INTO csp.tipo_origen_fuente_financiacion (id, nombre, activo) VALUES (2, 'nombre-002', true);

-- FUENTE FINANCIACION
INSERT INTO csp.fuente_financiacion (id, nombre, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (1, 'nombre-001', 'descripcion-001', true, 1, 1, true);
INSERT INTO csp.fuente_financiacion (id, nombre, descripcion, fondo_estructural, tipo_ambito_geografico_id, tipo_origen_fuente_financiacion_id, activo) 
  VALUES (2, 'nombre-002', 'descripcion-002', true, 2, 2, true);
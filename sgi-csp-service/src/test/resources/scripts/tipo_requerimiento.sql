INSERT INTO test.tipo_requerimiento 
  (id, activo) 
VALUES 
  (1, true),
  (2, true),
  (3, true),
  (4, true),
  (5, true),
  (6, true),
  (7, true),
  (8, false),
  (9, false);

-- TIPO_REQUERIMIENTO_NOMBRE
INSERT INTO test.tipo_requerimiento_nombre (tipo_requerimiento_id, lang, value_) VALUES(1, 'es', 'Requerimiento documental');
INSERT INTO test.tipo_requerimiento_nombre (tipo_requerimiento_id, lang, value_) VALUES(2, 'es', 'Resolución requerimiento documental');
INSERT INTO test.tipo_requerimiento_nombre (tipo_requerimiento_id, lang, value_) VALUES(3, 'es', 'Requerimiento subsanación');
INSERT INTO test.tipo_requerimiento_nombre (tipo_requerimiento_id, lang, value_) VALUES(4, 'es', 'Acuerdo de inicio de procedimiento de reintegro');
INSERT INTO test.tipo_requerimiento_nombre (tipo_requerimiento_id, lang, value_) VALUES(5, 'es', 'Resolución del procedimiento de reintegro');
INSERT INTO test.tipo_requerimiento_nombre (tipo_requerimiento_id, lang, value_) VALUES(6, 'es', 'Informe de cierre');
INSERT INTO test.tipo_requerimiento_nombre (tipo_requerimiento_id, lang, value_) VALUES(7, 'es', 'Resolución de recurso');
INSERT INTO test.tipo_requerimiento_nombre (tipo_requerimiento_id, lang, value_) VALUES(8, 'es', 'Dado de baja 1');
INSERT INTO test.tipo_requerimiento_nombre (tipo_requerimiento_id, lang, value_) VALUES(9, 'es', 'Dado de baja 2');

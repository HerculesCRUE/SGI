-- INVENCION
/*
  scripts = { 
    "classpath:scripts/tipo_proteccion.sql",
  }
*/
INSERT INTO test.invencion
(id, activo, comentarios, descripcion, fecha_comunicacion, proyecto_ref, titulo, tipo_proteccion_id)
VALUES 
(1, TRUE,  'comentarios-invencion-001', 'descripcion-invencion-001', '2020-01-12T00:00:00Z', null , 'titulo-invencion-001', 1),
(2, TRUE,  'comentarios-invencion-002', 'descripcion-invencion-002', '2020-01-13T00:00:00Z', null , 'titulo-invencion-002', 1),
(3, TRUE,  'comentarios-invencion-003', 'descripcion-invencion-003', '2020-01-14T00:00:00Z', null , 'titulo-invencion-003', 1),
(4, FALSE, 'comentarios-invencion-004', 'descripcion-invencion-004', '2020-01-14T00:00:00Z', null , 'titulo-invencion-004', 1);

ALTER SEQUENCE test.invencion_seq RESTART WITH 5;



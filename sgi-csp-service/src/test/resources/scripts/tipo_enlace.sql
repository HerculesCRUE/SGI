-- DEPENDENCIAS:
/*

*/

INSERT INTO test.tipo_enlace
(id, activo)
VALUES
(1, true),
(2, true),
(3, true);

-- TIPO_ENLACE_NOMBRE
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(1, 'es', 'Publicación convocatoria');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(2, 'es', 'Presentación solicitudes');
INSERT INTO test.tipo_enlace_nombre (tipo_enlace_id, lang, value_) VALUES(3, 'es', 'Portal de justificación');

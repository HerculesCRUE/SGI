
-- ESTADO SOLICITUD
INSERT INTO test.estado_solicitud (id, solicitud_id, estado, fecha_estado) VALUES (1, 1, 'BORRADOR', '2020-11-17T00:00:00Z');
INSERT INTO test.estado_solicitud (id, solicitud_id, estado, fecha_estado) VALUES (2, 1, 'PRESENTADA', '2020-11-20T00:00:00Z');
INSERT INTO test.estado_solicitud (id, solicitud_id, estado, fecha_estado) VALUES (3, 1, 'EXCLUIDA', '2020-11-17T00:00:00Z');
INSERT INTO test.estado_solicitud (id, solicitud_id, estado, fecha_estado) VALUES (4, 2, 'BORRADOR', '2020-11-17T00:00:00Z');
INSERT INTO test.estado_solicitud (id, solicitud_id, estado, fecha_estado) VALUES (5, 3, 'BORRADOR', '2020-11-18T00:00:00Z');
INSERT INTO test.estado_solicitud (id, solicitud_id, estado, fecha_estado) VALUES (6, 4, 'BORRADOR', '2020-11-19T00:00:00Z');
INSERT INTO test.estado_solicitud (id, solicitud_id, estado, fecha_estado) VALUES (7, 5, 'BORRADOR', '2020-11-20T00:00:00Z');

-- ESTADO SOLICITUD COMENTARIO
INSERT INTO test.estado_solicitud_comentario (estado_solicitud_id, lang, value_) VALUES (1, 'es', 'comentario borrador');
INSERT INTO test.estado_solicitud_comentario (estado_solicitud_id, lang, value_) VALUES (2, 'es', 'comentario presentada');
INSERT INTO test.estado_solicitud_comentario (estado_solicitud_id, lang, value_) VALUES (3, 'es', 'comentario excluida');
INSERT INTO test.estado_solicitud_comentario (estado_solicitud_id, lang, value_) VALUES (4, 'es', 'comentario borrador');
INSERT INTO test.estado_solicitud_comentario (estado_solicitud_id, lang, value_) VALUES (5, 'es', 'comentario borrador');
INSERT INTO test.estado_solicitud_comentario (estado_solicitud_id, lang, value_) VALUES (6, 'es', 'comentario borrador');
INSERT INTO test.estado_solicitud_comentario (estado_solicitud_id, lang, value_) VALUES (7, 'es', 'comentario borrador');

UPDATE test.solicitud SET estado_solicitud_id=1 WHERE id=1;
UPDATE test.solicitud SET estado_solicitud_id=4 WHERE id=2;
UPDATE test.solicitud SET estado_solicitud_id=5 WHERE id=3;
UPDATE test.solicitud SET estado_solicitud_id=6 WHERE id=4;
UPDATE test.solicitud SET estado_solicitud_id=7 WHERE id=5;

alter sequence test.estado_solicitud_seq restart with 8;
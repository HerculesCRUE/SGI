
INSERT INTO csp.estado_solicitud
  (id, solicitud_id, estado, fecha_estado, comentario)
VALUES
  (1, 1, 'BORRADOR', '2020-11-17T00:00:00Z', 'comentario borrador'),
  (2, 1, 'PRESENTADA', '2020-11-20T00:00:00Z', 'comentario presentada'),
  (3, 1, 'EXCLUIDA', '2020-11-17T00:00:00Z', 'comentario excluida'),
  (4, 2, 'BORRADOR', '2020-11-17T00:00:00Z', 'comentario borrador'),
  (5, 3, 'BORRADOR', '2020-11-18T00:00:00Z', 'comentario borrador'),
  (6, 4, 'BORRADOR', '2020-11-19T00:00:00Z', 'comentario borrador'),
  (7, 5, 'BORRADOR', '2020-11-20T00:00:00Z', 'comentario borrador');

UPDATE csp.solicitud SET estado_solicitud_id=1 WHERE id=1;
UPDATE csp.solicitud SET estado_solicitud_id=4 WHERE id=2;
UPDATE csp.solicitud SET estado_solicitud_id=5 WHERE id=3;
UPDATE csp.solicitud SET estado_solicitud_id=6 WHERE id=4;
UPDATE csp.solicitud SET estado_solicitud_id=7 WHERE id=5;
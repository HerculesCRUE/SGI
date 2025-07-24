INSERT INTO test.convocatoria_hito
(id, convocatoria_id, fecha, tipo_hito_id)
VALUES
(1, 1, '2022-01-27T11:11:11.000Z', 1),
(2, 1, '2022-01-28T11:11:11.000Z', 2),
(3, 2, '2022-01-29T11:11:11.000Z', 3),
(4, 3, '2022-01-30T11:11:11.000Z', 4),
(5, 4, '2022-01-31T11:11:11.000Z', 5);

INSERT INTO test.convocatoria_hito_comentario
(convocatoria_hito_id, lang, value_)
VALUES
(1, 'es', 'hito 1'),
(2, 'es', 'hito 2'),
(3, 'es', 'hito 3'),
(4, 'es', 'hito 4'),
(5, 'es', 'hito 5');

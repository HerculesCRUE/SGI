-- PRODUCCION_CIENTIFICA
-- Publicaciones
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(1, '060.010.010.000', 'publicacion-ref-001',  NULL);
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(2, '060.010.010.000', 'publicacion-ref-002',  NULL);
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(3, '060.010.010.000', 'publicacion-ref-003',  NULL);

INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(1, 1, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(2, 2, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(3, 3, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');

ALTER SEQUENCE test.estado_produccion_cientifica_seq RESTART WITH 4;

update test.produccion_cientifica set estado_produccion_cientifica_id = 1 where id=1;
update test.produccion_cientifica set estado_produccion_cientifica_id = 2 where id=2;
update test.produccion_cientifica set estado_produccion_cientifica_id = 3 where id=3;

-- Cómites editoriales
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(300, '060.030.030.000', 'comite-ref-001',  NULL);
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(301, '060.030.030.000', 'comite-ref-002',  NULL);
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(302, '060.030.030.000', 'comite-ref-003',  NULL);

INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(300, 300, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(301, 301, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(302, 302, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');

update test.produccion_cientifica set estado_produccion_cientifica_id = 300 where id=300;
update test.produccion_cientifica set estado_produccion_cientifica_id = 301 where id=301;
update test.produccion_cientifica set estado_produccion_cientifica_id = 302 where id=302;

-- Congresos
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(400, '060.010.020.000', 'congreso-ref-001',  NULL);
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(401, '060.010.020.000', 'congreso-ref-002',  NULL);
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(402, '060.010.020.000', 'congreso-ref-003',  NULL);

INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(400, 400, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(401, 401, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(402, 402, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');

update test.produccion_cientifica set estado_produccion_cientifica_id = 400 where id=400;
update test.produccion_cientifica set estado_produccion_cientifica_id = 401 where id=401;
update test.produccion_cientifica set estado_produccion_cientifica_id = 402 where id=402;

-- Direccion tesis
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(500, '030.040.000.000', 'direccion-tesis-ref-001',  NULL);
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(501, '030.040.000.000', 'direccion-tesis-ref-002',  NULL);
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(502, '030.040.000.000', 'direccion-tesis-ref-003',  NULL);

INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(500, 500, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(501, 501, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(502, 502, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');

update test.produccion_cientifica set estado_produccion_cientifica_id = 500 where id=500;
update test.produccion_cientifica set estado_produccion_cientifica_id = 501 where id=501;
update test.produccion_cientifica set estado_produccion_cientifica_id = 502 where id=502;

-- Obra artistica
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(600, '050.020.030.000', 'obra-artistica-ref-001',  NULL);
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(601, '050.020.030.000', 'obra-artistica-ref-002',  NULL);
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(602, '050.020.030.000', 'obra-artistica-ref-003',  NULL);

INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(600, 600, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(601, 601, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(602, 602, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');

update test.produccion_cientifica set estado_produccion_cientifica_id = 600 where id=600;
update test.produccion_cientifica set estado_produccion_cientifica_id = 601 where id=601;
update test.produccion_cientifica set estado_produccion_cientifica_id = 602 where id=602;

-- Organizacion de actividades
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(700, '060.020.030.000', 'organizacion_actividades-ref-001',  NULL);
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(701, '060.020.030.000', 'organizacion_actividades-ref-002',  NULL);
INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(702, '060.020.030.000', 'organizacion_actividades-ref-003',  NULL);

INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(700, 700, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(701, 701, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(702, 702, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');

update test.produccion_cientifica set estado_produccion_cientifica_id = 700 where id=700;
update test.produccion_cientifica set estado_produccion_cientifica_id = 701 where id=701;
update test.produccion_cientifica set estado_produccion_cientifica_id = 702 where id=702;

INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(800, '060.020.030.000', 'organizacion_actividades-ref-004',  NULL);
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(800, 800, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
update test.produccion_cientifica set estado_produccion_cientifica_id = 800 where id=800;

INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(801, '060.020.030.000', 'organizacion_actividades-ref-005',  NULL);
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(801, 801, NULL, 'PENDIENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
update test.produccion_cientifica set estado_produccion_cientifica_id = 801 where id=801;

INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(802, '060.020.030.000', 'organizacion_actividades-ref-006',  NULL);
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(802, 802, NULL, 'VALIDADO_PARCIALMENTE', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
update test.produccion_cientifica set estado_produccion_cientifica_id = 802 where id=802;

INSERT INTO test.produccion_cientifica (id, epigrafe_cvn, produccion_cientifica_ref, estado_produccion_cientifica_id) VALUES(803, '060.020.030.000', 'organizacion_actividades-ref-007',  NULL);
INSERT INTO test.estado_produccion_cientifica (id, produccion_cientifica_id, comentario, estado, fecha, last_modified_date) VALUES(803, 803, NULL, 'RECHAZADO', '2022-01-20T21:59:59Z', '2022-01-20T21:59:59Z');
update test.produccion_cientifica set estado_produccion_cientifica_id = 803 where id=803;

ALTER SEQUENCE test.produccion_cientifica_seq RESTART WITH 10000;
ALTER SEQUENCE test.estado_produccion_cientifica_seq RESTART WITH 10000;

INSERT INTO csp.programa (id, nombre, descripcion, programa_padre_id, activo) 
VALUES (1, 'r1', 'raiz-1', null, true);

INSERT INTO csp.programa (id, nombre, descripcion, programa_padre_id, activo) 
VALUES (2, 'r2', 'raiz-2', 1, false);

INSERT INTO csp.programa (id, nombre, descripcion, programa_padre_id, activo) 
VALUES (3, 'r3', 'raiz-3', 1, true);

INSERT INTO csp.programa (id, nombre, descripcion, programa_padre_id, activo) 
VALUES (4, 'r1h1', 'raiz-1-hijo-1', 1, true);

INSERT INTO csp.programa (id, nombre, descripcion, programa_padre_id, activo) 
VALUES (5, 'r1h2', 'raiz-1-hijo-2', 1, false);

INSERT INTO csp.programa (id, nombre, descripcion, programa_padre_id, activo) 
VALUES (6, 'r1h3', 'raiz-1-hijo-3', 1, true);

INSERT INTO csp.programa (id, nombre, descripcion, programa_padre_id, activo) 
VALUES (7, 'r2h1', 'raiz-2-hijo-1', 2, true);

INSERT INTO csp.programa (id, nombre, descripcion, programa_padre_id, activo) 
VALUES (8, 'r1h1h1', 'raiz-1-hijo-1-hijo-1', 4, true);


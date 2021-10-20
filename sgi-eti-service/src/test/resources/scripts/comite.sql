-- DEPENDENCIAS: COMITÉ
/*
  scripts = { 
    'classpath:scripts/formulario.sql',
  }
*/

-- COMITÉ
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, nombre_decreto, articulo, formulario_id, activo) VALUES (1, 'CEI', 'nombreSecretario', 'nombreInvestigacion', 'nombreDecreto', 'articulo', 1, true);
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, nombre_decreto, articulo, formulario_id, activo) VALUES (2, 'CEEA', 'nombreSecretario', 'nombreInvestigacion', 'nombreDecreto', 'articulo', 2, true);
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, nombre_decreto, articulo, formulario_id, activo) VALUES (3, 'CBE', 'nombreSecretario', 'nombreInvestigacion', 'nombreDecreto', 'articulo', 3, true);
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, nombre_decreto, articulo, formulario_id, activo) VALUES (4, 'Comite4', 'nombreSecretario', 'nombreInvestigacion', 'nombreDecreto', 'articulo', 4, true);
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, nombre_decreto, articulo, formulario_id, activo) VALUES (5, 'Comite5', 'nombreSecretario', 'nombreInvestigacion', 'nombreDecreto', 'articulo', 5, true);
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, nombre_decreto, articulo, formulario_id, activo) VALUES (6, 'Comite6', 'nombreSecretario', 'nombreInvestigacion', 'nombreDecreto', 'articulo', 6, true);
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, nombre_decreto, articulo, formulario_id, activo) VALUES (7, 'Comite7', 'nombreSecretario', 'nombreInvestigacion', 'nombreDecreto', 'articulo', 1, true);
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, nombre_decreto, articulo, formulario_id, activo) VALUES (8, 'Comite8', 'nombreSecretario', 'nombreInvestigacion', 'nombreDecreto', 'articulo', 2, true);

 ALTER SEQUENCE eti.comite_seq RESTART WITH 9;
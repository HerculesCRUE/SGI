-- DEPENDENCIAS: COMITÉ
/*
  scripts = { 
    'classpath:scripts/formulario.sql',
  }
*/

-- COMITÉ
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, genero, nombre_decreto, articulo, formulario_id, activo) VALUES (1, 'CEI', 'nombreSecretario', 'nombreInvestigacion', 'M', 'nombreDecreto', 'articulo', 1, true);
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, genero, nombre_decreto, articulo, formulario_id, activo) VALUES (2, 'CEEA', 'nombreSecretario', 'nombreInvestigacion', 'M', 'nombreDecreto', 'articulo', 2, true);
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, genero, nombre_decreto, articulo, formulario_id, activo) VALUES (3, 'CBE', 'nombreSecretario', 'nombreInvestigacion', 'M', 'nombreDecreto', 'articulo', 3, true);
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, genero, nombre_decreto, articulo, formulario_id, activo) VALUES (4, 'Comite4', 'nombreSecretario', 'nombreInvestigacion', 'M', 'nombreDecreto', 'articulo', 4, true);
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, genero, nombre_decreto, articulo, formulario_id, activo) VALUES (5, 'Comite5', 'nombreSecretario', 'nombreInvestigacion', 'M', 'nombreDecreto', 'articulo', 5, true);
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, genero, nombre_decreto, articulo, formulario_id, activo) VALUES (6, 'Comite6', 'nombreSecretario', 'nombreInvestigacion', 'M', 'nombreDecreto', 'articulo', 6, true);
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, genero, nombre_decreto, articulo, formulario_id, activo) VALUES (7, 'Comite7', 'nombreSecretario', 'nombreInvestigacion', 'M', 'nombreDecreto', 'articulo', 1, true);
INSERT INTO eti.comite (id, comite, nombre_secretario, nombre_investigacion, genero, nombre_decreto, articulo, formulario_id, activo) VALUES (8, 'Comite8', 'nombreSecretario', 'nombreInvestigacion', 'M', 'nombreDecreto', 'articulo', 2, true);

 ALTER SEQUENCE eti.comite_seq RESTART WITH 9;
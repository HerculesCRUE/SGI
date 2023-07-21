-- DEPENDENCIAS: COMITÉ
/*
  scripts = { 
    'classpath:scripts/formulario.sql',
  }
*/

-- COMITÉ
INSERT INTO test.comite (id, comite, nombre_investigacion, genero, formulario_id, activo) VALUES (1, 'CEI', 'nombreInvestigacion', 'M', 1, true);
INSERT INTO test.comite (id, comite, nombre_investigacion, genero, formulario_id, activo) VALUES (2, 'CEEA', 'nombreInvestigacion', 'M', 2, true);
INSERT INTO test.comite (id, comite, nombre_investigacion, genero, formulario_id, activo) VALUES (3, 'CBE', 'nombreInvestigacion', 'M', 3, true);
INSERT INTO test.comite (id, comite, nombre_investigacion, genero, formulario_id, activo) VALUES (4, 'Comite4', 'nombreInvestigacion', 'M', 4, true);
INSERT INTO test.comite (id, comite, nombre_investigacion, genero, formulario_id, activo) VALUES (5, 'Comite5', 'nombreInvestigacion', 'M', 5, true);
INSERT INTO test.comite (id, comite, nombre_investigacion, genero, formulario_id, activo) VALUES (6, 'Comite6', 'nombreInvestigacion', 'M', 6, true);
INSERT INTO test.comite (id, comite, nombre_investigacion, genero, formulario_id, activo) VALUES (7, 'Comite7', 'nombreInvestigacion', 'M', 1, true);
INSERT INTO test.comite (id, comite, nombre_investigacion, genero, formulario_id, activo) VALUES (8, 'Comite8', 'nombreInvestigacion', 'M', 2, true);

 ALTER SEQUENCE test.comite_seq RESTART WITH 9;
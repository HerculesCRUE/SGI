-- DEPENDENCIAS: COMITÉ
/*
  scripts = { 
    "classpath:scripts/formulario.sql",
  }
*/

-- COMITÉ
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (1, 'CEISH', 1, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (2, 'CEEA', 2, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (3, 'CEIAB', 3, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (4, 'Comite4', 4, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (5, 'Comite5', 5, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (6, 'Comite6', 6, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (7, 'Comite7', 1, true);
INSERT INTO eti.comite (id, comite, formulario_id, activo) VALUES (8, 'Comite8', 2, true);

 ALTER SEQUENCE eti.comite_seq RESTART WITH 9;
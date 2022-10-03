--DEPENDENCIES

INSERT INTO test.email 
(id, emailtpl_id) 
VALUES
(1, 1),
(2, 42),
(3, 42),
(4, 55),
(5, 42),
(6, 42),
(7, 56);

ALTER SEQUENCE test.email_seq RESTART WITH 8;

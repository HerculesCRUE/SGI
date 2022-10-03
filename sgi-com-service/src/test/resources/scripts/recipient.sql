INSERT INTO test.recipient
(id, address, email_id, name) 
VALUES
(1, 'hercules@treelogic.info', 1, 'Hercules'),
(2, 'fjalonso@um.es', 2, 'fjalonso@um.es'),
(3, 'fjalonso@um.es', 3, 'fjalonso@um.es'),
(4, 'fjalonso@um.es', 4, 'fjalonso@um.es'),
(5, 'fjalonso@um.es', 5, 'fjalonso@um.es'),
(6, 'fjalonso@um.es', 6, 'fjalonso@um.es'),
(7, 'fjalonso@um.es', 7, 'fjalonso@um.es');

ALTER SEQUENCE test.recipient_seq RESTART WITH 8;

-- Deterministic seed data for H2 tests (profile: test)
-- ---------------------------------------------------
-- In production, VW_USERS is an Oracle VIEW with an INSTEAD OF trigger.
-- For tests, Hibernate will create a physical table named VW_USERS based on the JPA entity mapping,
-- so we seed that table directly.

DELETE FROM VW_USERS;

INSERT INTO VW_USERS (ID, NAME, BIRTHDAY, LANGUAGE_CODE, LANGUAGE_DESCRIPTION)
VALUES (1, 'Pedro', DATE '2020-01-01', 'BR', 'Português');

INSERT INTO VW_USERS (ID, NAME, BIRTHDAY, LANGUAGE_CODE, LANGUAGE_DESCRIPTION)
VALUES (2, 'Peter', DATE '2020-01-02', 'EN', 'Inglês');

INSERT INTO VW_USERS (ID, NAME, BIRTHDAY, LANGUAGE_CODE, LANGUAGE_DESCRIPTION)
VALUES (3, 'Pablo', DATE '2020-01-03', 'SP', 'Espanhol');

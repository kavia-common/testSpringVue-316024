-- Deterministic seed data for H2 tests (profile: test)
-- ---------------------------------------------------
-- In production (Oracle), the app writes to VW_USERS and an INSTEAD OF trigger
-- persists into USERS and derives computed/joined fields from LANGUAGES.
--
-- In tests (H2), we recreate the same behavior using:
-- - Base tables: LANGUAGES, USERS
-- - View: VW_USERS
-- - INSTEAD OF triggers: TRG_VW_USERS_{INSERT,UPDATE,DELETE}
--
-- This file seeds LANGUAGES and then inserts into VW_USERS to exercise trigger semantics.

DELETE FROM USERS;
DELETE FROM LANGUAGES;

INSERT INTO LANGUAGES (ID, CODE, DESCRIPTION) VALUES (1, 'BR', 'Português');
INSERT INTO LANGUAGES (ID, CODE, DESCRIPTION) VALUES (2, 'EN', 'Inglês');
INSERT INTO LANGUAGES (ID, CODE, DESCRIPTION) VALUES (3, 'SP', 'Espanhol');

-- Insert via the VIEW (so triggers populate USERS, matching Oracle behavior).
-- BIRTHDAY is ignored by trigger and replaced with CURRENT_DATE (Oracle used SYSDATE).
INSERT INTO VW_USERS (NAME, BIRTHDAY, LANGUAGE_CODE) VALUES ('Pedro', DATE '2020-01-01', 'BR');
INSERT INTO VW_USERS (NAME, BIRTHDAY, LANGUAGE_CODE) VALUES ('Peter', DATE '2020-01-02', 'EN');
INSERT INTO VW_USERS (NAME, BIRTHDAY, LANGUAGE_CODE) VALUES ('Pablo', DATE '2020-01-03', 'SP');

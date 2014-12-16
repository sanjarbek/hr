# --- !Ups

CREATE TABLE contract_types (
  id        SERIAL PRIMARY KEY,
  name      TEXT,
  file_path TEXT
);

INSERT INTO contract_types (name, file_path)
VALUES ('Штатный', 'work_contract_unlimited'),
  ('Внештатный', 'work_contract_limited');

# -- !Downs
DROP TABLE IF EXISTS contract_types;
DROP SEQUENCE IF EXISTS contract_types_id_seq;

# --- !Ups

CREATE TABLE contract_types (
  id        SERIAL PRIMARY KEY,
  name      TEXT,
  file_path TEXT
);

INSERT INTO contract_types (name, file_path)
VALUES ('На неопределенный срок (с испытательным)', './uploaded/documents/work_contract_unlimited.doc'),
  ('На неопределенный срок (без испытательного)', './uploaded/documents/work_contract_unlimited.doc'),
  ('На определенный срок (с испытательным)', './uploaded/documents/work_contract_limited.doc'),
  ('На определенный срок (без испытательного)', './uploaded/documents/work_contract_limited.doc');

# -- !Downs
DROP TABLE IF EXISTS contract_types;
DROP SEQUENCE IF EXISTS contract_types_id_seq;

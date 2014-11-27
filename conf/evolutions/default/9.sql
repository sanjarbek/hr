# --- !Ups

CREATE TABLE contract_types (
  id        SERIAL PRIMARY KEY,
  name      TEXT,
  file_path TEXT
);

# -- !Downs
DROP TABLE IF EXISTS contract_types;
DROP SEQUENCE IF EXISTS contract_types_id_seq;

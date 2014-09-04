# --- !Ups

CREATE TABLE contract_types (
  id   SERIAL,
  name VARCHAR(50)
);

# -- !Downs
DROP TABLE IF EXISTS contract_types;
DROP SEQUENCE IF EXISTS contract_types_id_seq;

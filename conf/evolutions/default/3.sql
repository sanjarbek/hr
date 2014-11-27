# --- !Ups

CREATE TABLE relationship_types (
  id   SERIAL PRIMARY KEY,
  name TEXT
);

# -- !Downs
DROP TABLE IF EXISTS relationship_types;
DROP SEQUENCE IF EXISTS relationship_types_id_seq;

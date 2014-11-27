# --- !Ups

CREATE TABLE structure_types (
  id           SERIAL PRIMARY KEY,
  name         TEXT,
  has_children BOOL
);

# -- !Downs
DROP TABLE IF EXISTS structure_types;
DROP SEQUENCE IF EXISTS structure_types_id_seq;

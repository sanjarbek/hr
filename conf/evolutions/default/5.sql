# --- !Ups

CREATE TABLE structure_types (
  id           SERIAL,
  name         VARCHAR(40),
  has_children BOOL
);

# -- !Downs
DROP TABLE IF EXISTS structure_types;
DROP SEQUENCE IF EXISTS structure_types_id_seq;

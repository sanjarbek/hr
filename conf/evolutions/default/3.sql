# --- !Ups

CREATE TABLE relationship_types (
  id SERIAL,
  name varchar(30)
);

# -- !Downs
DROP TABLE if exists relationship_types;
DROP SEQUENCE if exists relationship_types_id_seq;

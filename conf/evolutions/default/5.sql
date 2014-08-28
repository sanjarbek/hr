# --- !Ups

CREATE TABLE office_types (
  id SERIAL,
  name varchar(40)
);

# -- !Downs
DROP TABLE if exists office_types;
DROP SEQUENCE if exists office_types_id_seq;

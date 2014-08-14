# --- !Ups

CREATE TABLE family (
  id BIGSERIAL,
  employee_id BIGINT NOT NULL,
  relationship int,
  surname varchar(20),
  firstname varchar(20),
  lastname varchar(20),
  birthday date
);

# -- !Downs
DROP TABLE if exists family;
DROP SEQUENCE if exists family_id_seq;

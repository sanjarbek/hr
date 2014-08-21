# --- !Ups

CREATE TABLE relationships (
  id BIGSERIAL,
  employee_id BIGINT NOT NULL,
  degree int,
  surname varchar(20),
  firstname varchar(20),
  lastname varchar(20),
  birthday date
);

# -- !Downs
DROP TABLE if exists relationships;
DROP SEQUENCE if exists relationships_id_seq;

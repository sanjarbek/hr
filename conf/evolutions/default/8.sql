# --- !Ups

CREATE TABLE departments (
  id        SERIAL,
  parent_id INTEGER,
  office_id INTEGER,
  name      VARCHAR(50)
);

# -- !Downs
DROP TABLE IF EXISTS departments;
DROP SEQUENCE IF EXISTS departments_id_seq;

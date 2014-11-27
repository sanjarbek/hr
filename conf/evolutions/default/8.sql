# --- !Ups

CREATE TABLE departments (
  id            SERIAL PRIMARY KEY,
  parent_id     INTEGER,
  office_id     INTEGER,
  name          TEXT,
  is_position   BOOL,
  position_type INT
);

# -- !Downs
DROP TABLE IF EXISTS departments;
DROP SEQUENCE IF EXISTS departments_id_seq;

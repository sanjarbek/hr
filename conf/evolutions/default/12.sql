# --- !Ups

CREATE TABLE educations (
  id               BIGSERIAL PRIMARY KEY,
  employee_id      BIGINT REFERENCES employees,
  institution_id   INT,
  serialnumber     TEXT,
  speciality       TEXT,
  qualification_id INT,
  end_date         DATE
);

CREATE TABLE qualification_types (
  id   SERIAL PRIMARY KEY,
  name TEXT UNIQUE
);


# -- !Downs
DROP TABLE IF EXISTS qualification_types CASCADE;
DROP SEQUENCE IF EXISTS qualification_types_id_seq;

DROP TABLE IF EXISTS educations CASCADE;
DROP SEQUENCE IF EXISTS educations_id_seq;

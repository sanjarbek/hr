# --- !Ups

CREATE TABLE educations (
  id             BIGSERIAL PRIMARY KEY,
  employee_id    BIGINT,
  institution_id INT,
  serialnumber   TEXT,
  speciality     TEXT,
  qualification  TEXT,
  start_date     DATE,
  end_date       DATE
);

# -- !Downs
DROP TABLE IF EXISTS educations;
DROP SEQUENCE IF EXISTS educations_id_seq;

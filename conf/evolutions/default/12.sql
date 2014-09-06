# --- !Ups

CREATE TABLE educations (
  id             BIGSERIAL,
  employee_id    BIGINT,
  institution_id INT,
  serialnumber   VARCHAR,
  speciality     VARCHAR,
  qualification  VARCHAR,
  start_date     DATE,
  end_date       DATE
);

# -- !Downs
DROP TABLE IF EXISTS educations;
DROP SEQUENCE IF EXISTS educations_id_seq;

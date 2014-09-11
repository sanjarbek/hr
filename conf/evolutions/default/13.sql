# --- !Ups

CREATE TABLE passports (
  id          BIGSERIAL,
  employee_id BIGINT,
  serial      VARCHAR,
  number      VARCHAR,
  organ       VARCHAR,
  open_date   DATE,
  end_date    DATE
);

# -- !Downs
DROP TABLE IF EXISTS passports;
DROP SEQUENCE IF EXISTS passports_id_seq;

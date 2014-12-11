# --- !Ups

CREATE TABLE passports (
  id          BIGSERIAL PRIMARY KEY,
  employee_id BIGINT UNIQUE REFERENCES employees,
  serial      TEXT,
  number      TEXT,
  organ       TEXT,
  reg_address TEXT,
  open_date   DATE,
  end_date    DATE
);

# -- !Downs
DROP TABLE IF EXISTS passports;
DROP SEQUENCE IF EXISTS passports_id_seq;

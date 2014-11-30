# --- !Ups

CREATE TABLE employees (
  id               BIGSERIAL PRIMARY KEY,
  surname          TEXT    NOT NULL,
  firstname        TEXT    NOT NULL,
  lastname         TEXT,
  birthday         DATE,
  citizenship      TEXT,
  insurance_number TEXT,
  tax_number       TEXT,
  sex              BOOLEAN NOT NULL,
  created_at  timestamp not null ,
  updated_at timestamp not null
);

# -- !Downs
DROP TABLE IF EXISTS employees;
DROP SEQUENCE IF EXISTS employees_id_seq;


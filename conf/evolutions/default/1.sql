# --- !Ups

CREATE TABLE employees (
  id               BIGSERIAL PRIMARY KEY,
  surname          TEXT      NOT NULL,
  firstname        TEXT      NOT NULL,
  lastname         TEXT,
  birthday date,
  citizenship      text,
  insurance_number text,
  tax_number       text,
  sex              boolean   not null,
  created_at       timestamp not null,
  updated_at       timestamp not null
);

# -- !Downs
DROP TABLE if exists employees;
DROP SEQUENCE if exists employees_id_seq;


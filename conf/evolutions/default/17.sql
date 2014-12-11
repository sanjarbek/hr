# --- !Ups

CREATE TABLE contact_informations (
  id             BIGSERIAL PRIMARY KEY,
  employee_id    BIGINT REFERENCES employees,
  home_address   TEXT,
  living_address TEXT,
  email          TEXT,
  home_phone     TEXT,
  mobile_phone   TEXT,
  created_at     TIMESTAMP,
  updated_at     TIMESTAMP
);

# -- !Downs
DROP TABLE IF EXISTS contact_informations CASCADE;
DROP SEQUENCE IF EXISTS contact_informations_id_seq;

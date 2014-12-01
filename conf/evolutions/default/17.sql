# --- !Ups

CREATE TABLE contact_informations (
  id           BIGSERIAL PRIMARY KEY,
  employee_id  BIGINT NOT NULL,
  home_address TEXT,
  reg_address  TEXT,
  email        TEXT,
  home_phone   TEXT,
  mobile_phone TEXT,
  created_at   TIMESTAMP,
  updated_at   TIMESTAMP
);

# -- !Downs
DROP TABLE IF EXISTS contact_informations;
DROP SEQUENCE IF EXISTS contact_informations_id_seq;

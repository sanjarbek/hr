# --- !Ups

CREATE TABLE military_infos (
  id                BIGSERIAL PRIMARY KEY,
  employee_id       BIGINT UNIQUE,
  category          TEXT,
  military_rank     TEXT,
  structure         TEXT,
  full_code         TEXT,
  validity_category TEXT,
  commissariat      TEXT,
  removal_mark      TEXT
);

# -- !Downs
DROP TABLE IF EXISTS military_infos;
DROP SEQUENCE IF EXISTS military_infos_id_seq;

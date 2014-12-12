# --- !Ups

CREATE TABLE military_infos (
  id                BIGSERIAL PRIMARY KEY,
  employee_id       BIGINT UNIQUE REFERENCES employees,
  category          TEXT,
  military_rank     TEXT,
  structure         TEXT,
  full_code         TEXT,
  validity_category TEXT,
  commissariat      TEXT,
  removal_mark      TEXT
);

CREATE TABLE seminars (
  id              BIGSERIAL PRIMARY KEY,
  employee_id     BIGINT REFERENCES employees,
  topic           TEXT,
  organizer       TEXT,
  event_date      DATE,
  has_certificate BOOLEAN,
  created_at      TIMESTAMP NOT NULL,
  updated_at      TIMESTAMP NOT NULL
);

# -- !Downs
DROP TABLE IF EXISTS military_infos;
DROP SEQUENCE IF EXISTS military_infos_id_seq;

DROP TABLE IF EXISTS seminars;
DROP SEQUENCE IF EXISTS seminars_id_seq;


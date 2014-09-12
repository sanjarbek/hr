# --- !Ups

CREATE TABLE military_infos (
  id                BIGSERIAL,
  employee_id       BIGINT,
  category          VARCHAR,
  military_rank     VARCHAR,
  structure         VARCHAR,
  full_code         VARCHAR,
  validity_category VARCHAR,
  commissariat      VARCHAR,
  removal_mark      VARCHAR,
  created           TIMESTAMP DEFAULT localtimestamp,
  updated           TIMESTAMP DEFAULT localtimestamp
);

# -- !Downs
DROP TABLE IF EXISTS military_infos;
DROP SEQUENCE IF EXISTS military_infos_id_seq;

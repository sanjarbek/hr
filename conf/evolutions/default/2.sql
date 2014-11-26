# --- !Ups

CREATE TABLE relationships (
  id          BIGSERIAL PRIMARY KEY,
  employee_id BIGINT NOT NULL,
  degree      INT    NOT NULL,
  surname     TEXT,
  firstname   TEXT,
  lastname    TEXT,
  birthday    DATE,
  created_at  TIMESTAMP,
  updated_at  TIMESTAMP
);

# -- !Downs
DROP TABLE if exists relationships;
DROP SEQUENCE if exists relationships_id_seq;

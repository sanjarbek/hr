# --- !Ups

CREATE TABLE relationships (
  id          BIGSERIAL PRIMARY KEY,
  employee_id BIGINT NOT NULL,
  degree      INT    NOT NULL,
  surname     TEXT,
  firstname   TEXT,
  lastname    TEXT,
  birthday    DATE
);

# -- !Downs
DROP TABLE IF EXISTS relationships;
DROP SEQUENCE IF EXISTS relationships_id_seq;

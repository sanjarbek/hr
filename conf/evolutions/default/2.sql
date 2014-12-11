# --- !Ups

CREATE TABLE relationships (
  id          BIGSERIAL PRIMARY KEY,
  employee_id BIGINT REFERENCES employees,
  degree      INT NOT NULL,
  surname     TEXT,
  firstname   TEXT,
  lastname    TEXT,
  birthday    DATE
);

# -- !Downs
DROP TABLE IF EXISTS relationships CASCADE;
DROP SEQUENCE IF EXISTS relationships_id_seq;

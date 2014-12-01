# --- !Ups

CREATE TABLE structures (
  id             SERIAL PRIMARY KEY,
  parent_id      INTEGER,
  name           TEXT NOT NULL,
  fullname       TEXT,
  salary         NUMERIC DEFAULT 0,
  bonus          NUMERIC DEFAULT 0,
  structure_type INT,
  position_type  INT,
  status         SMALLINT
);

# -- !Downs
DROP TABLE IF EXISTS structures;
DROP SEQUENCE IF EXISTS structures_id_seq;

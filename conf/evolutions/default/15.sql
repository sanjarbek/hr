# --- !Ups

CREATE TABLE structures (
  id             SERIAL,
  parent_id      INTEGER,
  name           VARCHAR(50),
  structure_type INT,
  position_type  INT,
  status         SMALLINT
);

# -- !Downs
DROP TABLE IF EXISTS structures;
DROP SEQUENCE IF EXISTS structures_id_seq;

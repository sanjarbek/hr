# --- !Ups

CREATE TABLE institutions (
  id        SERIAL PRIMARY KEY,
  longname  TEXT,
  shortname TEXT
);

# -- !Downs
DROP TABLE IF EXISTS institutions;
DROP SEQUENCE IF EXISTS institutions_id_seq;

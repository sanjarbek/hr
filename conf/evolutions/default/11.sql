# --- !Ups

CREATE TABLE institutions (
  id        SERIAL,
  longname  VARCHAR,
  shortname VARCHAR
);

# -- !Downs
DROP TABLE IF EXISTS institutions;
DROP SEQUENCE IF EXISTS institutions_id_seq;

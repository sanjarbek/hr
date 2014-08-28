# --- !Ups

CREATE TABLE positions (
  id SERIAL,
  category_id INTEGER,
  name varchar(30)
);

# -- !Downs
DROP TABLE if exists positions;
DROP SEQUENCE if exists positions_id_seq;

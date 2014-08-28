# --- !Ups

CREATE TABLE offices (
  id SERIAL,
  parent_id INTEGER,
  name varchar(40),
  address varchar(255),
  phone varchar(20),
  fax varchar(20),
  email varchar(30),
  type_id INT
);

# -- !Downs
DROP TABLE if exists offices;
DROP SEQUENCE if exists offices_id_seq;

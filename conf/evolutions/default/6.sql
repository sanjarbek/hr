# --- !Ups

CREATE TABLE offices (
  id        SERIAL PRIMARY KEY,
  parent_id INTEGER,
  name      VARCHAR(40),
  address   VARCHAR(255),
  phone     VARCHAR(20),
  fax       VARCHAR(20),
  email     VARCHAR(30),
  type_id   INT
);

# -- !Downs
DROP TABLE IF EXISTS offices;
DROP SEQUENCE IF EXISTS offices_id_seq;

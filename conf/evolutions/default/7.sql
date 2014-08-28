# --- !Ups

CREATE TABLE position_categories (
  id   SERIAL,
  name VARCHAR(50)
);

# -- !Downs
DROP TABLE IF EXISTS position_categories;
DROP SEQUENCE IF EXISTS position_categories_id_seq;

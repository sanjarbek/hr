# --- !Ups

CREATE TABLE position_categories (
  id   SERIAL PRIMARY KEY,
  name TEXT
);

# -- !Downs
DROP TABLE IF EXISTS position_categories;
DROP SEQUENCE IF EXISTS position_categories_id_seq;

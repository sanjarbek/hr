# --- !Ups

CREATE TABLE position_categories (
  id   SERIAL PRIMARY KEY,
  name TEXT
);

INSERT INTO position_categories (name) VALUES
  ('Руководитель'),
  ('Кассир'),
  ('СБ'),
  ('Специалист'),
  ('Главный специалист');

# -- !Downs
DROP TABLE IF EXISTS position_categories CASCADE;
DROP SEQUENCE IF EXISTS position_categories_id_seq;

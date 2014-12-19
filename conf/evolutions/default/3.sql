# --- !Ups

CREATE TABLE relationship_types (
  id   SERIAL PRIMARY KEY,
  name TEXT
);

INSERT INTO relationship_types (name)
VALUES ('Отец'),
  ('Мать'),
  ('Брат'),
  ('Сестра'),
  ('Сын'),
  ('Дочь');

# -- !Downs
DROP TABLE IF EXISTS relationship_types CASCADE;
DROP SEQUENCE IF EXISTS relationship_types_id_seq;

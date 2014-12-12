# --- !Ups
CREATE TABLE structure_types (
  id           SERIAL PRIMARY KEY,
  name         TEXT,
  has_children BOOL
);
INSERT INTO structure_types (name, has_children)
VALUES ('Филиал', TRUE),
  ('Сберегательная касса', TRUE),
  ('Организационная структура', TRUE),
  ('Должность', FALSE);

CREATE TABLE structures (
  id             SERIAL PRIMARY KEY,
  parent_id      INTEGER,
  name           TEXT NOT NULL,
  fullname       TEXT,
  salary         NUMERIC DEFAULT 0,
  bonus          NUMERIC DEFAULT 0,
  structure_type INT,
  position_type  INT,
  status         SMALLINT
);
INSERT INTO structures (parent_id, name, fullname, salary, bonus, structure_type, position_type, status)
VALUES (NULL, 'Головной', 'Головной филиал', 0, 0, 1, NULL, 1),
  (NULL, 'Филиал Берекет', 'Филиал Берекет', 0, 0, 1, NULL, 1),
  (NULL, 'Ошский филиал', 'Ошский филиал', 0, 0, 1, NULL, 1),
  (NULL, 'Джалалабадский филиал', 'Джалалабадский филиал', 0, 0, 1, NULL, 1),
  (NULL, 'Талаский филиал', 'Талаский филиал', 0, 0, 1, NULL, 1),
  (NULL, 'Нарынский филиал', 'Нарынский филиал', 0, 0, 1, NULL, 1),
  (NULL, 'Кочкорский филиал', 'Кочкорский филиал', 0, 0, 1, NULL, 1),
  (NULL, 'Чолпонатинский филиал', 'Чолпонатинский филиал', 0, 0, 1, NULL, 1),
  (NULL, 'Каракольский филиал', 'Каракольский филиал', 0, 0, 1, NULL, 1);

# -- !Downs
DROP TABLE IF EXISTS structures CASCADE;
DROP SEQUENCE IF EXISTS structures_id_seq;

DROP TABLE IF EXISTS structure_types CASCADE;
DROP SEQUENCE IF EXISTS structure_types_id_seq;
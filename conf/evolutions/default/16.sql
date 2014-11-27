# --- !Ups

CREATE TABLE orders (
  id             BIGSERIAL PRIMARY KEY,
  name           TEXT,
  order_category INT,
  nomer          INT,
  date_of_order  DATE,
  content        TEXT,
  tags           TEXT
);

CREATE TABLE order_tags (
  id    TEXT UNIQUE,
  count INT
)

  # -- !Downs
DROP TABLE IF EXISTS orders;
DROP SEQUENCE IF EXISTS orders_id_seq;
DROP TABLE IF EXISTS order_tags;

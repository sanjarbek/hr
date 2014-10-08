# --- !Ups

CREATE TABLE orders (
  id             BIGSERIAL,
  name           VARCHAR,
  order_category INT,
  nomer          INT,
  date_of_order  DATE,
  content        VARCHAR,
  tags           VARCHAR
);

# -- !Downs
DROP TABLE IF EXISTS orders;
DROP SEQUENCE IF EXISTS orders_id_seq;

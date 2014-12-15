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
);

CREATE TABLE employment_orders (
  id                 BIGSERIAL PRIMARY KEY,
  position_id        INT,
  contract_type_id   INT,
  employee_id        BIGINT,
  salary             NUMERIC(14, 2),
  calendar_type_id   SMALLINT,
  trial_period_start DATE,
  trial_period_end   DATE,
  start_date         DATE,
  end_date           DATE,
  close_date         DATE,
  created_at         TIMESTAMP NOT NULL,
  updated_at         TIMESTAMP NOT NULL
);

CREATE TABLE dismissal_orders (
  id                BIGSERIAL PRIMARY KEY,
  employee_id       BIGINT    NOT NULL,
  leaving_reason_id INT       NOT NULL,
  comment           TEXT,
  leaving_date      DATE,
  created_at        TIMESTAMP NOT NULL,
  updated_at        TIMESTAMP NOT NULL
);

CREATE TABLE positions (
  id                  BIGSERIAL PRIMARY KEY,
  employment_order_id BIGINT,
  position_id         INT,
  employee_id         BIGINT,
  dismissal_order_id  BIGINT,
  start_date          DATE,
  end_date            DATE,
  close_date          DATE,
  created_at          TIMESTAMP NOT NULL,
  updated_at          TIMESTAMP NOT NULL
);


  # -- !Downs
DROP TABLE IF EXISTS positions CASCADE;
DROP SEQUENCE IF EXISTS positions_id_seq;
DROP TABLE IF EXISTS orders CASCADE;
DROP SEQUENCE IF EXISTS orders_id_seq;
DROP TABLE IF EXISTS employment_orders CASCADE;
DROP SEQUENCE IF EXISTS employment_orders_id_seq;
DROP TABLE IF EXISTS dismissal_orders CASCADE;
DROP SEQUENCE IF EXISTS dismissal_orders_id_seq;
DROP TABLE IF EXISTS order_tags CASCADE;

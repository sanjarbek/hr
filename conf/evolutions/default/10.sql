# --- !Ups

CREATE TABLE contracts (
  id            BIGSERIAL,
  position_id   INT,
  contract_type INT,
  employee_id   BIGINT,
  open_date     DATE,
  end_date      DATE,
  close_date    DATE,
  status        SMALLINT
);

# -- !Downs
DROP TABLE IF EXISTS contracts;
DROP SEQUENCE IF EXISTS contracts_id_seq;

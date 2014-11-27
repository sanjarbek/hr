# --- !Ups

CREATE TABLE contracts (
  id                BIGSERIAL PRIMARY KEY,
  position_id       INT,
  contract_type     INT,
  employee_id       BIGINT,
  salary            NUMERIC(14, 2),
  working_time_type SMALLINT,
  trial_period_open DATE,
  trial_period_end  DATE,
  open_date         DATE,
  end_date          DATE,
  close_date        DATE,
  status            SMALLINT
);

# -- !Downs
DROP TABLE IF EXISTS contracts;
DROP SEQUENCE IF EXISTS contracts_id_seq;

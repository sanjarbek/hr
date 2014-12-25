# --- !Ups
CREATE TABLE relationship_statuses (
  id         SERIAL PRIMARY KEY,
  name       TEXT      NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

INSERT INTO relationship_statuses (name, created_at, updated_at)
VALUES ('Холост', localtimestamp, localtimestamp),
  ('Женат/Замужем', localtimestamp, localtimestamp),
  ('Разведен/Разведена', localtimestamp, localtimestamp);

CREATE TABLE nationalities (
  id         SERIAL PRIMARY KEY,
  name       TEXT      NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

INSERT INTO nationalities (name, created_at, updated_at)
VALUES ('Кыргыз', localtimestamp, localtimestamp),
  ('Русский', localtimestamp, localtimestamp);

CREATE TABLE employees (
  id                     BIGSERIAL PRIMARY KEY,
  surname                TEXT      NOT NULL,
  firstname              TEXT      NOT NULL,
  lastname               TEXT,
  birthday               DATE,
  citizenship            TEXT,
  insurance_number       TEXT,
  tax_number             TEXT,
  sex                    BOOLEAN   NOT NULL,
  relationship_status_id INTEGER REFERENCES relationship_statuses,
  nationality_id         INTEGER REFERENCES nationalities,
  position_history_id    BIGINT,
  created_at             TIMESTAMP NOT NULL,
  updated_at             TIMESTAMP NOT NULL
);

# -- !Downs
DROP TABLE IF EXISTS employees CASCADE;
DROP SEQUENCE IF EXISTS employees_id_seq;
DROP TABLE IF EXISTS relationship_statuses CASCADE;
DROP SEQUENCE IF EXISTS relationship_statuses_id_seq;
DROP TABLE IF EXISTS nationalities CASCADE;
DROP SEQUENCE IF EXISTS nationalities_id_seq;



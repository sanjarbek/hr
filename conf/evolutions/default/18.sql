# --- !Ups
CREATE TABLE regions (
  id         SERIAL PRIMARY KEY,
  name       TEXT UNIQUE,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE districts (
  id         SERIAL PRIMARY KEY,
  region_id  INT REFERENCES regions (id),
  name       TEXT UNIQUE NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

# -- !Downs
DROP TABLE IF EXISTS regions;
DROP SEQUENCE IF EXISTS regions_id_seq;
DROP TABLE IF EXISTS districts;
DROP SEQUENCE IF EXISTS districts_id_seq;

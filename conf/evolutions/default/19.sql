# --- !Ups
CREATE TABLE settlements (
  id          SERIAL PRIMARY KEY,
  district_id INT REFERENCES districts (id),
  name        TEXT UNIQUE NOT NULL,
  coefficient FLOAT       NOT NULL DEFAULT 0,
  type        BOOL,
  created_at  TIMESTAMP,
  updated_at  TIMESTAMP
);


# -- !Downs
DROP TABLE IF EXISTS settlements;
DROP SEQUENCE IF EXISTS settlements_id_seq;

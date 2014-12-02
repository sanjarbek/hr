# --- !Ups
create table day_types (
  id serial primary key ,
  name text unique ,
  created_at   TIMESTAMP,
  updated_at   TIMESTAMP
);

CREATE TABLE calendar (
  calendar_date date PRIMARY KEY,
  day_type  int ,
  created_at   TIMESTAMP,
  updated_at   TIMESTAMP
);

# -- !Downs
DROP TABLE IF EXISTS day_types;
DROP SEQUENCE IF EXISTS day_types_id_seq;
DROP TABLE IF EXISTS calendar;

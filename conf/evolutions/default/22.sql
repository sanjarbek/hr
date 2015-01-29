# --- !Ups

CREATE TABLE users (
  id serial primary key ,
  employee_id bigint not null ,
  username text not null unique ,
  password_hash text not null ,
  password_reset_token text  ,
  last_activity timestamp ,
  status smallint not null ,
  created_at timestamp ,
  updated_at timestamp
);

insert into users (employee_id, username, password_hash, last_activity, status, created_at, updated_at)
values(1, 'admin', '$2a$10$sVBaI4ExBTwbfZMzUbJgXur1ZsgK9FVztyEN9uyfzaQxsRotB20rK', localtimestamp, 1, localtimestamp, localtimestamp)


# -- !Downs
DROP TABLE IF EXISTS users cascade ;
DROP SEQUENCE IF EXISTS users_id_seq;
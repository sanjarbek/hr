# --- !Ups

CREATE TABLE employees (
  id BIGSERIAL,
  surname varchar(20),
  firstname varchar(20),
  lastname varchar(20),
  birthday date,
  citizenship varchar(30),
  insurance_number varchar(20) ,
  tax_number varchar(20) ,
  home_phone varchar(20) ,
  mobile_phone varchar(20) ,
  email varchar(20)
);

# -- !Downs
DROP TABLE if exists employees;
DROP SEQUENCE if exists employees_id_seq;


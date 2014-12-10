# --- !Ups
create table day_types (
  id serial primary key ,
  name text unique ,
  hours int not null default 0,
  type boolean not null default true,
  created_at   TIMESTAMP,
  updated_at   TIMESTAMP
);

insert into day_types(name, hours, type, created_at, updated_at)
values('Рабочий', 8, true, localtimestamp, localtimestamp),
('Выходной', 0, true, localtimestamp, localtimestamp),
('Праздник', 0, true, localtimestamp, localtimestamp);

create table calendar_types (
  id serial primary key ,
  name text not null unique,
  created_at   TIMESTAMP,
  updated_at   TIMESTAMP
);


create table calendars (
  id bigserial primary key ,
  calendar_type int not null ,
  calendar_date date not null ,
  day_type  int ,
  created_at   TIMESTAMP,
  updated_at   TIMESTAMP
);

create table holidays (
  id serial primary key ,
  name text unique not null ,
  created_at   TIMESTAMP,
  updated_at   TIMESTAMP
);

create table sheet_working_days (
  id serial primary key ,
  employee_id integer not null ,
  working_day date not null ,
  day_type int not null,
  hours int not null default 0,
  created_at   TIMESTAMP,
  updated_at   TIMESTAMP
);

create table sheet_working_months (
  id BIGSERIAL primary key ,
  employee_id integer not null ,
  working_month date not null ,
  calendar_days int not null default 0,
  working_hours int not null default 0,
  extra_hours1 int not null default 0,
  extra_hours2 int not null default 0,
  extra_hours3 int not null default 0,
  weekend_hours  int not null default 0,
  archive bool not null default false,
  created_at   TIMESTAMP,
  updated_at   TIMESTAMP
);

create table sheet_working_months_day_types (
  id bigserial primary key ,
  month_sheet_id INT not null references sheet_working_months(id),
  day_type int not null,
  day_counts int not null ,
  created_at   TIMESTAMP,
  updated_at   TIMESTAMP
)

# -- !Downs
DROP TABLE IF EXISTS sheet_working_days;
DROP SEQUENCE IF EXISTS sheet_working_days_id_seq;
DROP TABLE IF EXISTS sheet_working_months_day_types;
DROP SEQUENCE IF EXISTS sheet_working_months_day_types_id_seq;
DROP TABLE IF EXISTS sheet_working_months;
DROP SEQUENCE IF EXISTS sheet_working_months_id_seq;
drop table if exists holidays;
drop sequence if exists holidays_id_seq;
DROP TABLE IF EXISTS calendars;
DROP SEQUENCE IF EXISTS calendars_id_seq;
DROP TABLE IF EXISTS calendar_types;
DROP SEQUENCE IF EXISTS calendar_types_id_seq;
DROP TABLE IF EXISTS day_types;
DROP SEQUENCE IF EXISTS day_types_id_seq;





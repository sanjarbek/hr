# --- !Ups
create table holiday_types (
  id SERIAL PRIMARY key ,
  name text not null unique
);

CREATE TABLE holidays (
  employee_id bigint references employees(id),
  holiday_type_id INT references holiday_types(id),
  period_start        date,
  period_end        date,
  count_of_days int,
  start_date date,
  end_date date,
  comment text
) INHERITS (orders);

# -- !Downs
DROP TABLE IF EXISTS holiday_types cascade ;
DROP SEQUENCE IF EXISTS holiday_types_id_seq;
drop table if exists holidays;

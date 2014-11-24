# --- !Ups

CREATE TABLE test_tmp (
  id         BIGSERIAL,
  name       VARCHAR,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

# -- !Downs

DROP SEQUENCE IF EXISTS test_tmp_id_seq;
DROP TABLE IF EXISTS test_tmp;

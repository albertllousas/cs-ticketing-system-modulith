CREATE SCHEMA IF NOT EXISTS customer_mgmt;

CREATE TABLE IF NOT EXISTS customer_mgmt.customers (
  id            UUID                    NOT NULL,
  email         TEXT                    NOT NULL,
  full_name     TEXT                    NOT NULL,
  preferred_lang TEXT                    NOT NULL,
  tier          TEXT                    NOT NULL,
  version       BIGINT                  NOT NULL,
  created       TIMESTAMPTZ             NOT NULL DEFAULT clock_timestamp(),
  CONSTRAINT    pk_customers            PRIMARY KEY (id)
);

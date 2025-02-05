CREATE SCHEMA IF NOT EXISTS ticket_lifecycle;

CREATE TABLE IF NOT EXISTS ticket_lifecycle.tickets (
  id            UUID            NOT NULL,
  customer_id   UUID            NOT NULL,
  title         TEXT            NOT NULL,
  description   TEXT            NOT NULL,
  status        TEXT            NOT NULL,
  type          TEXT            NOT NULL,
  version       BIGINT          NOT NULL,
  created       TIMESTAMPTZ     NOT NULL DEFAULT clock_timestamp(),
  CONSTRAINT    pk_tickets      PRIMARY KEY (id)
);

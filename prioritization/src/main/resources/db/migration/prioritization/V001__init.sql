CREATE SCHEMA IF NOT EXISTS prioritization;

CREATE TABLE IF NOT EXISTS prioritization.ticket_priorities (
  id            UUID                    NOT NULL,
  ticket_id     UUID                    NOT NULL,
  priority      TEXT                    NOT NULL,
  version       BIGINT                  NOT NULL,
  created       TIMESTAMPTZ             NOT NULL DEFAULT clock_timestamp(),
  CONSTRAINT    pk_ticket_priorities    PRIMARY KEY (id)
);

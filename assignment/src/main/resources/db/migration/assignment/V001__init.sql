CREATE SCHEMA IF NOT EXISTS assignment;

CREATE TABLE IF NOT EXISTS assignment.agent_assignments (
  agent_id          UUID                    NOT NULL,
  tickets           JSONB                   NOT NULL DEFAULT '[]',
  version           BIGINT                  NOT NULL,
  created           TIMESTAMPTZ             NOT NULL DEFAULT clock_timestamp(),
  CONSTRAINT        pk_agent_assignments    PRIMARY KEY (agent_id)
);

CREATE INDEX IF NOT EXISTS idx_tickets_id ON assignment.agent_assignments USING gin (jsonb_path_query_array(tickets, '$[*].id'));

CREATE TABLE assignment.ticket_queue (
  ticket_id         UUID                    NOT NULL,
  priority          TEXT                    NOT NULL,
  created_at        TIMESTAMPTZ             NOT NULL DEFAULT clock_timestamp(),
  CONSTRAINT        pk_ticket_queue         PRIMARY KEY (ticket_id)
);

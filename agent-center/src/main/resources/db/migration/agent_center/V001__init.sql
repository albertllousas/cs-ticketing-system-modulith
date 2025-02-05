CREATE SCHEMA IF NOT EXISTS agent_center;

CREATE TABLE IF NOT EXISTS agent_center.agents (
    id          UUID        NOT NULL,
    email       TEXT        NOT NULL,
    full_name   TEXT        NOT NULL,
    skills      TEXT[]      NOT NULL,
    languages   TEXT[]      NOT NULL,
    status      TEXT        NOT NULL,
    version     BIGINT      NOT NULL,
    created     TIMESTAMPTZ NOT NULL DEFAULT clock_timestamp(),
    CONSTRAINT  pk_agents       PRIMARY KEY (id)
);


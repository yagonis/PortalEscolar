CREATE TABLE polls
(
    id                   UUID    DEFAULT uuid_generate_v4() NOT NULL,
    question             VARCHAR(500)                       NOT NULL,
    description          TEXT,
    status               VARCHAR(20)                        NOT NULL,
    opens_at             TIMESTAMP                          NOT NULL,
    closes_at            TIMESTAMP                          NOT NULL,
    allow_multiple_votes BOOLEAN DEFAULT FALSE              NOT NULL,
    created_at           TIMESTAMP                          NOT NULL,

    CONSTRAINT pk_polls PRIMARY KEY (id)
);

CREATE INDEX idx_polls_status ON polls (status);
CREATE INDEX idx_polls_closes_at ON polls (closes_at);
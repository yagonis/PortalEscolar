CREATE TABLE poll_options
(
    id            UUID DEFAULT uuid_generate_v4() NOT NULL,
    poll_id       UUID                            NOT NULL,
    text          VARCHAR(300)                    NOT NULL,
    display_order INTEGER                         NOT NULL,

    CONSTRAINT pk_poll_options PRIMARY KEY (id),
    CONSTRAINT fk_poll_options_poll FOREIGN KEY (poll_id) REFERENCES polls (id)
);

CREATE INDEX idx_poll_options_poll_id ON poll_options (poll_id);
CREATE TABLE poll_votes
(
    id        UUID DEFAULT uuid_generate_v4() NOT NULL,
    poll_id   UUID                            NOT NULL,
    option_id UUID                            NOT NULL,
    user_id   UUID                            NOT NULL,
    voted_at  TIMESTAMP                       NOT NULL,

    CONSTRAINT pk_poll_votes PRIMARY KEY (id),
    CONSTRAINT uq_poll_votes_poll_user UNIQUE (poll_id, user_id),
    CONSTRAINT fk_poll_votes_poll FOREIGN KEY (poll_id) REFERENCES polls (id),
    CONSTRAINT fk_poll_votes_option FOREIGN KEY (option_id) REFERENCES poll_options (id),
    CONSTRAINT fk_poll_votes_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_poll_votes_poll_id ON poll_votes (poll_id);
CREATE INDEX idx_poll_votes_user_id ON poll_votes (user_id);
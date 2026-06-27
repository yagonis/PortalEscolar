CREATE TABLE warnings (
                          id          UUID            DEFAULT uuid_generate_v4()  NOT NULL,
                          title       VARCHAR(150)                                NOT NULL,
                          content     TEXT                                        NOT NULL,
                          priority    VARCHAR(20)                                 NOT NULL,
                          pinned      BOOLEAN         DEFAULT FALSE               NOT NULL,
                          active      BOOLEAN         DEFAULT TRUE                NOT NULL,
                          created_at  TIMESTAMP                                   NOT NULL,

                          CONSTRAINT pk_warnings PRIMARY KEY (id)
);

CREATE INDEX idx_warnings_active     ON warnings (active);
CREATE INDEX idx_warnings_priority   ON warnings (priority);
CREATE INDEX idx_warnings_pinned     ON warnings (pinned);
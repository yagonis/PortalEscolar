CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
                       id          UUID            DEFAULT uuid_generate_v4()  NOT NULL,
                       nome        VARCHAR(100)                                NOT NULL,
                       email       VARCHAR(255)                                NOT NULL,
                       senha       VARCHAR(255)                                NOT NULL,
                       role        VARCHAR(20)                                 NOT NULL,
                       active      BOOLEAN         DEFAULT TRUE                NOT NULL,
                       created_at  TIMESTAMP                                   NOT NULL,
                       updated_at  TIMESTAMP,

                       CONSTRAINT pk_users PRIMARY KEY (id),
                       CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_role ON users (role);
CREATE INDEX idx_users_active ON users (active);
CREATE TABLE news (
                      id           UUID          DEFAULT uuid_generate_v4()  NOT NULL,
                      title        VARCHAR(200)                              NOT NULL,
                      subtitle     VARCHAR(300),
                      body         TEXT                                      NOT NULL,
                      image_url    VARCHAR(255),
                      status       VARCHAR(20)                               NOT NULL,
                      published_at TIMESTAMP,
                      created_at   TIMESTAMP                                 NOT NULL,
                      updated_at   TIMESTAMP,

                      CONSTRAINT pk_news PRIMARY KEY (id)
);

CREATE INDEX idx_news_status       ON news (status);
CREATE INDEX idx_news_published_at ON news (published_at DESC);
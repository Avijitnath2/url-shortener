CREATE TABLE short_urls (
    id          BIGSERIAL PRIMARY KEY,
    short_code  VARCHAR(10)  UNIQUE,
    original_url TEXT        NOT NULL,
    custom_alias VARCHAR(30),
    expires_at  TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    total_clicks INTEGER      NOT NULL DEFAULT 0
);

CREATE INDEX idx_short_code ON short_urls(short_code);

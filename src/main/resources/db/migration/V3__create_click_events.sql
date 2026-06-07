CREATE TABLE click_events (
    id           BIGSERIAL PRIMARY KEY,
    short_url_id BIGINT    NOT NULL REFERENCES short_urls(id) ON DELETE CASCADE,
    clicked_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    ip_address   VARCHAR(45),
    user_agent   TEXT,
    referrer     TEXT,
    country_code VARCHAR(2)
);

CREATE INDEX idx_clicked_at ON click_events(clicked_at);
CREATE INDEX idx_short_url_id ON click_events(short_url_id);
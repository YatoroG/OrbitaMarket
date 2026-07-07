CREATE SCHEMA IF NOT EXISTS payments_schema;

DROP TABLE IF EXISTS payments_schema.accounts CASCADE;
DROP TABLE IF EXISTS payments_schema.payments_inbox CASCADE;

CREATE TABLE payments_schema.accounts (
    user_id VARCHAR(64) PRIMARY KEY,
    balance INTEGER NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE payments_schema.payments_inbox (
    event_id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    amount INTEGER NOT NULL,
    new_balance INTEGER,
    error_message TEXT,
    processed_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_payments_inbox_order ON payments_schema.payments_inbox(order_id);

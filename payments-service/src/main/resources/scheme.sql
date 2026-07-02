DROP TABLE IF EXISTS accounts CASCADE;

CREATE TABLE accounts (
    user_id UUID PRIMARY KEY,
    balance NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE payments_inbox (
    event_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    new_balance NUMERIC(15, 2),
    error_message VARCHAR(255),
    processed_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_payments_inbox_order ON payments_inbox(order_id);

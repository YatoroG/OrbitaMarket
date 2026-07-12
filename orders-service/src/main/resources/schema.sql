CREATE SCHEMA IF NOT EXISTS orders_schema;

DROP TABLE IF EXISTS orders_schema.orders CASCADE;
DROP TABLE IF EXISTS orders_schema.orders_outbox CASCADE;

CREATE TABLE orders_schema.orders (
    id UUID PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    product_type VARCHAR(20) NOT NULL,
    payload JSONB NOT NULL,
    price INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    failure_reason VARCHAR(50),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE orders_schema.orders_outbox (
    event_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PAYMENT_PENDING',
    retry_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    processed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_orders_outbox_status ON orders_schema.orders_outbox(status) WHERE status = 'PAYMENT_PENDING';
CREATE INDEX idx_orders_user ON orders_schema.orders(user_id);

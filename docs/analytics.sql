-- orders

SELECT
    user_id,
    COUNT(*) AS paid_orders_count,
    SUM(price) AS total_spent_geocredits
FROM orders_schema.orders
WHERE status = 'PAID'
GROUP BY user_id
ORDER BY total_spent_geocredits DESC;

SELECT 
    failure_reason,
    COUNT(*) AS failure_count,
    CONCAT(ROUND(COUNT(*) * 100.0 / (
        SELECT COUNT(*) 
        FROM orders_schema.orders 
        WHERE status != 'PAID'), 
        0), '%') 
    AS share_of_failures_percent
FROM orders_schema.orders
WHERE status != 'PAID'
GROUP BY failure_reason
ORDER BY failure_count DESC;

-- payments

SELECT 
    COUNT(*) AS total_accounts,
    SUM(balance) AS total_geocredits,
    MAX(balance) AS richest_accounts_balance,
    ROUND(AVG(balance), 2) AS average_accounts_balance
FROM payments_schema.accounts;

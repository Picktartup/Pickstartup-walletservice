INSERT INTO wallet (wallet_id, user_id, address, keystore_filename, balance, status, created_at, updated_at)
VALUES
    (1, 1, '0x1234567890abcdef1234567890abcdef12345678', 'keystore1.json', 1000.50, 'ACTIVE', '2024-01-01 09:00:00', '2024-01-01 09:00:00'),
    (2, 2, '0xabcdef1234567890abcdef1234567890abcdef12', 'keystore2.json', 750.00, 'INACTIVE', '2024-01-02 11:00:00', '2024-01-02 11:00:00'),
    (3, 3, '0x7890abcdef1234567890abcdef1234567890abcd', 'keystore3.json', 500.25, 'SUSPENDED', '2024-01-03 15:00:00', '2024-01-03 15:00:00'),
    (4, 4, '0x34567890abcdef1234567890abcdef1234567890', 'keystore4.json', 1250.75, 'DELETED', '2024-01-04 18:00:00', '2024-01-04 18:00:00');

INSERT INTO token_transactions (id, user_id, order_id, payment_id, wallet_address, amount, token_amount, transaction_hash, status, type, completed_at, failure_reason, created_at)
VALUES
    (1, 1, 'ORD001', 'PAY001', '0x1234567890abcdef1234567890abcdef12345678', 100.00, 50.00, '0xhash001', 'COMPLETED', 'MINT', '2024-01-01 10:00:00', NULL, '2024-01-01 09:30:00'),
    (2, 2, 'ORD002', 'PAY002', '0xabcdef1234567890abcdef1234567890abcdef12', 200.00, 100.00, '0xhash002', 'PENDING', 'TRANSFER', NULL, 'Transaction awaiting approval', '2024-01-02 12:00:00'),
    (3, 3, 'ORD003', 'PAY003', '0x7890abcdef1234567890abcdef1234567890abcd', 150.00, 75.00, '0xhash003', 'FAILED', 'BURN', NULL, 'Insufficient token balance', '2024-01-03 14:00:00'),
    (4, 4, 'ORD004', 'PAY004', '0x34567890abcdef1234567890abcdef1234567890', 250.00, 125.00, '0xhash004', 'COMPLETED', 'TRANSFER', '2024-01-04 16:00:00', NULL

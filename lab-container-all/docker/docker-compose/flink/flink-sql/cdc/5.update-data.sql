--MySQL
INSERT INTO orders VALUES (default, '2020-07-30 15:22:00', 'Jark', 29.71, 109, false);

--MySQL
INSERT INTO products VALUES (default, '2020-07-30 15:22:00', 'Jark', 29.71, 109, false);

--PG
INSERT INTO shipments VALUES (default,10005,'Shanghai','Beijing',false);

--MySQL
UPDATE orders SET order_status = true WHERE order_id = 10004;
UPDATE products SET description = 'test' WHERE id = 109;

--PG
UPDATE shipments SET is_arrived = true WHERE shipment_id = 1004;

--MySQL
DELETE FROM orders WHERE order_id = 10004;
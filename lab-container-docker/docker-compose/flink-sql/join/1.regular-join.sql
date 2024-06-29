

-- 无需定义 process time 和 even time

CREATE TABLE product (
    id INT,
    name STRING,
    description STRING,
    update_time TIMESTAMP(3)
 ) WITH (
   'connector' = 'kafka',
   'topic' = 'orders',
   'properties.bootstrap.servers' = 'localhost:9092',
   'properties.group.id' = 'my',
   'scan.startup.mode' = 'latest-offset',
   'format' = 'json'
 );

CREATE TABLE orders (
    id INT,
    ts TIMESTAMP(3),
    product_id INT
 ) WITH (
   'connector' = 'kafka',
   'topic' = 'orders',
   'properties.bootstrap.servers' = 'localhost:9092',
   'properties.group.id' = 'my',
   'scan.startup.mode' = 'latest-offset',
   'format' = 'json'
 );


SELECT * FROM Orders
INNER JOIN Product
ON Orders.productId = Product.id
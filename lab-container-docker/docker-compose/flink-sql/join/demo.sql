CREATE TABLE orders (
    order_id INT,
    order_date TIMESTAMP(3),
    description STRING,
    proctime as PROCTIME(),
    order_date TIMESTAMP(3),
    product_id INT,
    WATERMARK FOR order_date AS order_date - INTERVAL '1' HOUR
 ) WITH (
   'connector' = 'kafka',
   'topic' = 'order',
   'properties.bootstrap.servers' = 'localhost:9092',
   'properties.group.id' = 'testGroup',
   'scan.startup.mode' = 'latest-offset',
   'format' = 'json'
 );


 CREATE TABLE orders (
    order_id INT,
    order_date TIMESTAMP(3),
    product_id INT,
    WATERMARK FOR order_date AS order_date - INTERVAL '1' HOUR,
    PRIMARY KEY (order_id) NOT ENFORCED
 ) WITH (
    'connector' = 'upsert-kafka',
    'topic' = 'order',
    'properties.bootstrap.servers' = 'localhost:9092',
    'properties.group.id' = 'testGroup',
    'properties.auto.offset.reset' = 'latest',
    'key.format' = 'raw',
    'value.format' = 'json'
 );

INSERT INTO enriched_orders
SELECT 
    o.order_id, 
    o.order_date, 
    o.product_id, 
    p.name as product_name, 
    p.description as product_description
FROM orders AS o
INNER JOIN product FOR SYSTEM_TIME AS OF o.proctime|o.order_date AS p 
ON o.product_id = p.id;


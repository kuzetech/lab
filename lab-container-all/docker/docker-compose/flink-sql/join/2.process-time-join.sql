-- docker-compose exec mysql mysql -uroot -p123456

-- MySQL
CREATE DATABASE mydb;
USE mydb;
CREATE TABLE product (
  id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512),
  update_time DATETIME NOT NULL
);
ALTER TABLE product AUTO_INCREMENT = 101;

INSERT INTO product
VALUES (default,"scooter","Small 2-wheel scooter", '2020-07-30 10:08:22'),
       (default,"car battery","12V car battery", '2020-07-30 10:08:22'),
       (default,"12-pack drill bits","12-pack of drill bits with sizes ranging from #40 to #3", '2020-07-30 10:08:22'),
       (default,"hammer","12oz carpenter's hammer", '2020-07-30 10:08:22'),
       (default,"hammer","14oz carpenter's hammer", '2020-07-30 10:08:22'),
       (default,"hammer","16oz carpenter's hammer", '2020-07-30 10:08:22'),
       (default,"rocks","box of assorted rocks", '2020-07-30 10:08:22'),
       (default,"jacket","water resistent black wind breaker", '2020-07-30 10:08:22'),
       (default,"spare tire","24 inch spare tire", '2020-07-30 10:08:22');


CREATE TEMPORARY TABLE product (
    id INT,
    name STRING,
    description STRING,
    update_time TIMESTAMP(3)
 ) WITH (
    'connector' = 'jdbc',
    'username' = 'root',
    'password' = '123456',
    'url' = 'jdbc:mysql://localhost:3306/mydb',
    'table-name' = 'product'
 );

 CREATE TABLE orders (
    id INT,
    ts TIMESTAMP(3),
    product_id INT,
    proctime as PROCTIME()
 ) WITH (
   'connector' = 'kafka',
   'topic' = 'orders',
   'properties.bootstrap.servers' = 'localhost:9092',
   'properties.group.id' = 'my',
   'scan.startup.mode' = 'latest-offset',
   'format' = 'json'
 );

CREATE TABLE enriched_orders (
    id INT,
    ts TIMESTAMP(3),
    product_id INT,
    name STRING,
    description STRING
 ) WITH (
   'connector' = 'kafka',
   'topic' = 'wide',
   'properties.bootstrap.servers' = 'localhost:9092',
   'format' = 'json'
 );

INSERT INTO enriched_orders
SELECT 
    o.id, 
    o.ts, 
    o.product_id, 
    p.name, 
    p.description
FROM orders AS o
INNER JOIN product FOR SYSTEM_TIME AS OF o.proctime AS p 
ON o.product_id = p.id;

-- ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic wide

-- ./kafka-console-producer.sh --broker-list localhost:9092 --topic orders
{"id":1, "ts":"2020-07-30 01:00:00", "product_id":99}
{"id":1, "ts":"2020-07-30 01:00:00", "product_id":101}

-- docker-compose exec mysql mysql -uroot -p123456
update product set name = 'test' where id = 101;

-- ./kafka-console-producer.sh --broker-list localhost:9092 --topic orders
{"id":1, "ts":"2020-07-30 01:00:00", "product_id":101}
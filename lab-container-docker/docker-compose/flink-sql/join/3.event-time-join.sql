
-- 如果加上了水印区间 - INTERVAL '1' HOUR ，任意一边下一个水印才会触发上一个区间计算
-- 没有加上水印区间的情况下，还未完全摸透


CREATE TABLE product (
    id INT,
    name STRING,
    description STRING,
    update_time TIMESTAMP(3),
    WATERMARK FOR update_time AS update_time,
    PRIMARY KEY (id) NOT ENFORCED
 ) WITH (
    'connector' = 'upsert-kafka',
    'topic' = 'product',
    'properties.bootstrap.servers' = 'localhost:9092',
    'properties.group.id' = 'my',
    'properties.auto.offset.reset' = 'latest',
    'key.format' = 'raw',
    'value.format' = 'json'
 );

CREATE TABLE orders (
    id INT,
    ts TIMESTAMP(3),
    product_id INT,
    WATERMARK FOR ts AS ts
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
INNER JOIN product FOR SYSTEM_TIME AS OF o.ts AS p 
ON o.product_id = p.id;

-- ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic wide

-- ./kafka-console-producer.sh --broker-list localhost:9092 --topic product
{"id":101, "name":"1", "description":"1", "update_time":"2020-07-29 01:00:00"}
{"id":101, "name":"2", "description":"2", "update_time":"2020-07-29 01:03:00"}
{"id":101, "name":"3", "description":"3", "update_time":"2020-07-29 03:00:00"}

-- ./kafka-console-producer.sh --broker-list localhost:9092 --topic orders
{"id":1, "ts":"2020-07-29 01:00:00", "product_id":101}
{"id":2, "ts":"2020-07-29 01:00:00", "product_id":101}
{"id":3, "ts":"2020-07-29 01:01:00", "product_id":101}
{"id":4, "ts":"2020-07-29 02:00:00", "product_id":101}

{"id":2, "ts":"2020-07-29 01:02:00", "product_id":101}
{"id":3, "ts":"2020-07-29 01:02:00", "product_id":101}

{"id":4, "ts":"2020-07-29 01:04:00", "product_id":101}
{"id":5, "ts":"2020-07-29 01:03:00", "product_id":101}
{"id":6, "ts":"2020-07-29 01:06:00", "product_id":101}

{"id":7, "ts":"2020-07-29 01:03:00", "product_id":101}
{"id":8, "ts":"2020-07-29 02:03:00", "product_id":102}
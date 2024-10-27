--Flink SQL
CREATE TABLE kafka_gmv (
   day_str STRING,
   gmv DECIMAL(10, 5)
) WITH (
    'connector' = 'kafka',
    'topic' = 'kafka_gmv',
    'scan.startup.mode' = 'earliest-offset',
    'properties.bootstrap.servers' = 'localhost:9092',
    'format' = 'changelog-json'
);

INSERT INTO kafka_gmv
SELECT DATE_FORMAT(order_date, 'yyyy-MM-dd') as day_str, SUM(price) as gmv
FROM orders
WHERE order_status = true
GROUP BY DATE_FORMAT(order_date, 'yyyy-MM-dd');

-- 读取 Kafka 的 changelog 数据，观察 materialize 后的结果
SELECT * FROM kafka_gmv;

docker-compose exec kafka bash -c 'kafka-console-consumer.sh --topic kafka_gmv --bootstrap-server kafka:9094 --from-beginning'


-- MySQL
UPDATE orders SET order_status = true WHERE order_id = 10001;
UPDATE orders SET order_status = true WHERE order_id = 10002;
UPDATE orders SET order_status = true WHERE order_id = 10003;

-- MySQL
INSERT INTO orders
VALUES (default, '2020-07-30 17:33:00', 'Timo', 50.00, 104, true);

-- MySQL
UPDATE orders SET price = 40.00 WHERE order_id = 10005;

-- MySQL
DELETE FROM orders WHERE order_id = 10005;

--结果数据样式如下
{"data":{"day_str":"2020-07-30","gmv":50.5},"op":"+I"}
{"data":{"day_str":"2020-07-30","gmv":50.5},"op":"-U"}  代表更新前的数据
{"data":{"day_str":"2020-07-30","gmv":65.5},"op":"+U"}  代表更新后的数据
{"data":{"day_str":"2020-07-30","gmv":65.5},"op":"-U"}
{"data":{"day_str":"2020-07-30","gmv":90.75},"op":"+U"}
{"data":{"day_str":"2020-07-30","gmv":90.75},"op":"-U"}
{"data":{"day_str":"2020-07-30","gmv":140.75},"op":"+U"}
{"data":{"day_str":"2020-07-30","gmv":140.75},"op":"-U"}
{"data":{"day_str":"2020-07-30","gmv":90.75},"op":"+U"}
{"data":{"day_str":"2020-07-30","gmv":90.75},"op":"-U"}
{"data":{"day_str":"2020-07-30","gmv":130.75},"op":"+U"}
{"data":{"day_str":"2020-07-30","gmv":130.75},"op":"-U"}
{"data":{"day_str":"2020-07-30","gmv":90.75},"op":"+U"}
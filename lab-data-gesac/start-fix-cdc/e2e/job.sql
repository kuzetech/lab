SET 'execution.runtime-mode' = 'streaming';
SET 'parallelism.default' = '1';
CREATE TABLE cdc_source (
    `识别码` INT NOT NULL,
    `客户名称` STRING NOT NULL,
    `状态_🚀` STRING,
    PRIMARY KEY (`识别码`) NOT ENFORCED
) WITH (
    'connector' = 'postgres-cdc',
    'hostname' = 'postgres',
    'port' = '5432',
    'username' = 'flink',
    'password' = 'flink',
    'database-name' = 'cdc',
    'schema-name' = '验证模式',
    'table-name' = '订单🚀',
    'slot.name' = 'flink_cdc_utf8_slot',
    'decoding.plugin.name' = 'pgoutput',
    'scan.startup.mode' = 'initial'
);
CREATE TABLE console_sink (
    `识别码` INT,
    `客户名称` STRING,
    `状态_🚀` STRING
) WITH ('connector' = 'print');
INSERT INTO console_sink SELECT * FROM cdc_source;

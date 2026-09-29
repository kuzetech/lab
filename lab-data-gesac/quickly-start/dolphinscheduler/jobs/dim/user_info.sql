CREATE TABLE `hive_catalog`.`gesac_lake`.`dim_user_info` (
    `id` BIGINT NOT NULL,
    `login_name` VARCHAR(200),
    `nick_name` VARCHAR(200),
    `user_level` VARCHAR(200),
    `birthday` DATE,
    `gender` VARCHAR(1),
    `create_time` TIMESTAMP,
    `operate_time` TIMESTAMP,
    `status` VARCHAR(200),
    CONSTRAINT `PK_id` PRIMARY KEY (`id`) NOT ENFORCED
) WITH (
    'path' = 'hdfs://namenode:9000/paimon/hive/gesac_lake.db/dim_user_info',
    'bucket' = '2',
    'changelog-producer' = 'input',
    'sink.parallelism' = '2',
    'tag.automatic-creation' = 'process-time',
    'tag.creation-period' = 'daily',
    'tag.creation-delay' = '5m',
    'tag.num-retained-max' = '365',
    'metastore.tag-to-partition'='dt',
    'metastore.tag-to-partition.preview'='process-time'
);

insert into dim_user_info
select    
    `id`,
    `login_name`,
    `nick_name`,
    `user_level`, 
    `birthday`, 
    `gender`,
    `create_time`, 
    `operate_time`,
    `status`
from `gesac_lake`.`ods_user_info_cdc`;
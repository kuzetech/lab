CREATE TABLE `hive_catalog`.`gmall_lake2024`.`dim_user_info` (
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
    'path' = 'hdfs://hadoop102:8020/paimon/hive/gmall_lake2024.db/dim_user_info',
    'bucket' = '2',
    'changelog-producer' = 'input',
    'sink.parallelism' = '2',
    'tag.automatic-creation' = 'process-time',
    'tag.creation-period' = 'daily',
    'tag.creation-delay' = '1 m',
    'tag.num-retained-max' = '30' ,
    'metastore.tag-to-partition'='dt',
    'metastore.tag-to-partition.preview'='process-time',
    'tag.num-retained-max'='365'
)
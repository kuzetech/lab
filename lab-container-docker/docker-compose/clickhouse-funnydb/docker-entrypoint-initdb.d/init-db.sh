#!/bin/bash
set -e

clickhouse client -n <<-EOSQL
    use default;

    CREATE TABLE IF NOT EXISTS event_log_local ON CLUSTER my
    (
        app_id          LowCardinality(String)      COMMENT '游戏ID',
        sdk_version     LowCardinality(String)      COMMENT 'SDK版本',
        log_id          String                      COMMENT '日志ID',
        time            UInt64                      COMMENT '事件时间戳',
        dt              DateTime                    MATERIALIZED toDateTime(time)       COMMENT '事件详细时间',
        event           LowCardinality(String)      COMMENT '事件名称',
        user_id         UInt32                      COMMENT 'SDK-ID',
        pid             UInt32                      COMMENT '游戏用户ID',
        duration        UInt32                      COMMENT '在线时常（秒）',
        amount          UInt64                      COMMENT '充值金额',
        currency        LowCardinality(String)      COMMENT '货币类型',
        step_name       LowCardinality(String)      COMMENT 'SDK步骤名称',
        new_device      UInt8                       COMMENT '是否为新设备，1代表新设备，反之为0',
        android_id      String                      COMMENT '安卓ID',
        advertising_id  String                      COMMENT '安卓广告ID',
        oaid            String                      COMMENT '安卓oaid',
        ios_idfa        String                      COMMENT 'IOS idfa',
        device_id       String                      COMMENT '自定义设备ID',
        model           LowCardinality(String)      COMMENT '手机型号',
        client_version  LowCardinality(String)      COMMENT '客户端版本',
        network_type    LowCardinality(String)      COMMENT '网络类型',
        ip              String                      COMMENT '登陆IP',
        longitude       Float32                     COMMENT '经度',
        latitude        Float32                     COMMENT '纬度',
        channel         LowCardinality(String)      COMMENT '渠道名称',
        resolution      LowCardinality(String)      COMMENT '分辨率',
        os              LowCardinality(String)      COMMENT '操作系统',
        os_version      LowCardinality(String)      COMMENT '操作系统版本',
        device_platform LowCardinality(String)      COMMENT '设备平台',
        carrier         LowCardinality(String)      COMMENT '网络运营商',
        country         LowCardinality(String)      COMMENT '国家',
        area            LowCardinality(String)      COMMENT '地区',
        subcontinents   LowCardinality(String)      COMMENT '次大洲'
    )
    ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/{shard}/event_log_local', '{replica}')
    PARTITION BY (toDate(time), event)
    ORDER BY (time, log_id);

    CREATE TABLE IF NOT EXISTS event_log ON CLUSTER my as event_log_local
    ENGINE = Distributed(my, default, event_log_local, rand());
EOSQL

clickhouse-client --query "INSERT INTO event_log_local FORMAT CSV" < /data/event.tbl
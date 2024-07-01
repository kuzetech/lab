#!/bin/bash
# 表不能使用 memory 类型，执行完该脚本后，服务器才启动，执行的插入语句数据都会丢失
set -e

clickhouse client -n <<-EOSQL

    create table default.action (
        uid     Int32,
        event   String,
        time    datetime
    ) 
    ENGINE = MergeTree()
    PARTITION BY uid
    ORDER BY xxHash32(uid)
    SAMPLE BY xxHash32(uid)
    SETTINGS index_granularity = 8192;

    insert into default.action values(1,'浏览','2020-01-02 11:00:00'); 
    insert into default.action values(1,'点击','2020-01-02 11:10:00'); 
    insert into default.action values(1,'下单','2020-01-02 11:20:00'); 
    insert into default.action values(1,'支付','2020-01-02 11:30:00'); 

    insert into default.action values(2,'下单','2020-01-02 11:00:00'); 
    insert into default.action values(2,'支付','2020-01-02 11:10:00'); 

    insert into default.action values(1,'浏览','2020-01-02 11:00:00'); 

    insert into default.action values(3,'浏览','2020-01-02 11:20:00'); 
    insert into default.action values(3,'点击','2020-01-02 12:00:00'); 

    insert into default.action values(4,'浏览','2020-01-02 11:50:00'); 
    insert into default.action values(4,'点击','2020-01-02 12:00:00'); 

    insert into default.action values(5,'浏览','2020-01-02 11:50:00'); 
    insert into default.action values(5,'点击','2020-01-02 12:00:00'); 
    insert into default.action values(5,'下单','2020-01-02 11:10:00'); 

    insert into default.action values(6,'浏览','2020-01-02 11:50:00'); 
    insert into default.action values(6,'点击','2020-01-02 12:00:00'); 
    insert into default.action values(6,'下单','2020-01-02 12:10:00'); 

EOSQL
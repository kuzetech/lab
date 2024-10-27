CREATE TABLE test_time
(
    id Int32,
    time UInt64
)
ENGINE = MergeTree()
ORDER BY id;

insert into test_time values (1, 1649730591);

select toDateTime(time) from test_time;

select toDateTime(time,'UTC') from test_time;

select toDateTime(time,'Asia/Shanghai') from test_time;
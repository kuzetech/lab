

CREATE TABLE test
(
    id UInt32,
    event LowCardinality(String),
    time UInt64
)
ENGINE = MergeTree()
ORDER BY (id, event)
SETTINGS index_granularity = 1;

insert into test VALUES (1,'test1', 100),(2,'test2', 200),(3,'test3', 300),(4,'test4', 400);


CREATE TABLE test_alis
(
    id Int32,
    time UInt64,
    pass_day UInt32 ALIAS dateDiff('day', toDateTime(time,'Europe/London'), now(), 'Europe/London')
)
ENGINE = MergeTree()
ORDER BY id;

CREATE TABLE test_alis2
(
    id Int32,
    time UInt64,
    kind String ALIAS multiIf(id=1, '分类1', '其他分类')
)
ENGINE = MergeTree()
ORDER BY id;

insert into test_alis2 values (1, 1),(2, 2);
insert into test_alis2 values (1, 1),(2, 2);
select kind from test_alis2;


-- 测试 ALIAS 能够使用 select 语句
CREATE TABLE test_alis3
(
    id Int32,
    test String ALIAS 'select 1'
)
ENGINE = MergeTree()
ORDER BY id;

insert into test_alis3 values (1);
-- 结论是不行，ALIAS 列的值 = 字符串字面值





-- 非精确去重
    --  uniq            近似去重里面精度最高
    --  uniqHLL12
    --  uniqCombined
    --  uniqCombined64
-- 精确去重
    --  uniqExact   支持任意类型
    --  groupBitmap 仅支持整形，特别优化整形去重
-- 近似去重算法
    -- HyperLog 算法
    -- BitMap 算法

create table test_distinct (
    uid      UInt32      COMMENT '用户ID',
    name     String      COMMENT '用户名'
) engine = Memory;

-- 根据uid去重

insert into test_distinct values 
(1, 'A'),(2, 'B'),(3, 'C'),(4, 'D'),
(3, 'C'),(1, 'A');

insert into test_distinct values (5, 'E');

-- 精确去重
select count(distinct uid) as total from user;
select countDistinct(uid) as total from user;

-- 精确去重
select uniqExact(uid) from test_distinct;
select groupBitmap(uid) from test_distinct;

-- 近似去重
select uniq(uid) from test_distinct;
select uniqHLL12(uid) from test_distinct;


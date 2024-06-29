CREATE TABLE test_rep_2s2r_local ON CLUSTER my2s2r (
    id UInt64,
    name String
)ENGINE = ReplicatedMergeTree('/clickhouse/tables/{shard}/{database}/{table}', '{replica}')
ORDER BY id 
PARTITION BY id;


CREATE TABLE test_rep_2s2r_all ON CLUSTER my2s2r as test_rep_2s2r_local
ENGINE = Distributed(my2s2r, default, test_rep_2s2r_local,rand());


INSERT INTO default.test_rep_2s2r_all VALUES
(1,'a'),
(2,'b'),
(3,'c'),
(4,'d'),
(5,'e'),
(6,'f'),
(7,'g'),
(8,'h'),
(9,'i');

select * from default.test_rep_2s2r_all;
select * from default.test_rep_2s2r_local;

-- 需要配置<internal_replication>true</internal_replication>

-- ReplicatedMergeTree支持新增列
ALTER TABLE test_rep_2s2r_local ON CLUSTER my2s2r ADD COLUMN OS String DEFAULT 'mac';
-- ReplicatedMergeTree支持删除数据
ALTER TABLE test_rep_2s2r_local ON CLUSTER my2s2r DELETE WHERE id = 1;
-- ReplicatedMergeTree支持更新数据
ALTER TABLE test_rep_2s2r_local ON CLUSTER my2s2r UPDATE name='gengxin' WHERE id = 2;

-- Distributed支持更新列定义
ALTER TABLE test_rep_2s2r_all ADD COLUMN OS String DEFAULT 'mac';
-- Distributed不支持删除
ALTER TABLE test_rep_2s2r_all DELETE WHERE id = 1;
-- Distributed不支持更新
ALTER TABLE test_rep_2s2r_all UPDATE name='gengxin' WHERE id = 1;

DROP TABLE test_rep_2s2r_local ON CLUSTER my2s2r;
DROP TABLE test_rep_2s2r_all ON CLUSTER my2s2r;
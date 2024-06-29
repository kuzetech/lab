CREATE TABLE test2s2r_local ON CLUSTER my2s2r (
    id UInt64
)ENGINE = MergeTree() ORDER BY id PARTITION BY id;


CREATE TABLE test2s2r_all ON CLUSTER my2s2r as test2s2r_local
ENGINE = Distributed(my2s2r, default, test2s2r_local,rand());


INSERT INTO default.test2s2r_all VALUES(1),(2),(3),(4),(5),(6),(7),(8),(9);

select * from default.test2s2r_all;

- 使用distributed + mergetree 可以实现列字段的更新
ALTER TABLE test2s2r_all ADD COLUMN OS String DEFAULT 'mac';
ALTER TABLE test2s2r_local ON CLUSTER my2s2r ADD COLUMN OS String DEFAULT 'mac';

ALTER TABLE test2s2r_all DROP COLUMN OS;
ALTER TABLE test2s2r_local DROP COLUMN OS;

DROP TABLE test2s2r_local ON CLUSTER my2s2r;
DROP TABLE test2s2r_all ON CLUSTER my2s2r;
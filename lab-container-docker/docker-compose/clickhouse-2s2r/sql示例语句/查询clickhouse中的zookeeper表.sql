SELECT * FROM system.zookeeper where path = '/clickhouse';

SELECT * FROM system.zookeeper where path = '/clickhouse/tables';

SELECT name, value, ctime, mtime, version, cversion, aversion FROM system.zookeeper where path = '/clickhouse/tables/01/test_rep_2s2r_local';


SELECT * FROM system.zookeeper where path = '/clickhouse/tables/01/test_rep_2s2r_local/replicas';
SELECT * FROM system.zookeeper where path = '/clickhouse/tables/01/test_rep_2s2r_local/quorum';

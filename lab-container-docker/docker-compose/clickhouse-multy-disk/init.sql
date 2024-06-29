ALTER TABLE test_local MODIFY SETTING storage_policy='hdd_in_order';

SELECT policy_name,
       volume_name,
       disks
FROM system.storage_policies;

SELECT
    name,
    data_paths,
    metadata_path,
    storage_policy
FROM system.tables
WHERE name = 'test_local';

select name, disk_name, path from system.parts where table = 'test_local';

INSERT INTO test VALUES(4),(5);
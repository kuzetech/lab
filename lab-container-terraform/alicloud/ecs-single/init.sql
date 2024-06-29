CREATE TABLE test (
    id UInt64
)ENGINE = MergeTree()
ORDER BY id
PARTITION BY id
SETTINGS storage_policy = 'policy_name_1';;

INSERT INTO test VALUES(1),(2),(3),(4),(5),(6),(7),(8),(9);
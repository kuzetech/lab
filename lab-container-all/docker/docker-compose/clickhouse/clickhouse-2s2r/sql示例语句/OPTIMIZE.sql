-- 指定 FINAL 即使所有数据已经在一个部分中，也会执行优化
OPTIMIZE TABLE drm.add_column_test FINAL; 

-- 指定 DEDUPLICATE 对完全相同的行进行重复数据删除（所有列进行比较），这仅适用于MergeTree引擎
OPTIMIZE TABLE drm.add_column_test DEDUPLICATE; 
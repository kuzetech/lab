-- 开启收集在 SQL 执行时所使用的资源情况
SET profiling=1;

-- 查询是否开启
SELECT @@profiling;

-- 查询当前会话的所有 profile
show profiles;

-- 显示指定 ID 的 profile
show profile for query 2;



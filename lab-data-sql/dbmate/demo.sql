-- pg 中没有 show tables 语句，只能通过查询元数据的规范方式
SELECT table_name
FROM information_schema.tables 
WHERE table_schema = 'public';

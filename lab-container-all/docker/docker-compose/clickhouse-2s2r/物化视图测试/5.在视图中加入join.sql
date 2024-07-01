CREATE MATERIALIZED VIEW mv1
TO dest
SELECT ...
FROM source left join some_dimension on (...)


-- 在视图中使用 join，会导致每写入一个 block 都需要重建 join ，十分浪费性能和内存
-- 可以使用 字典表+dictGet 或者 join引擎表+joinGet ，如下

CREATE MATERIALIZED VIEW mv1
TO dest
SELECT order.*, dictGet('dict_product_table_name', ('product_name','product_price'), product_id)
FROM order
## 准备工作

```
./images/build.sh
```

```
docker-compose up -d
```

## Iceberg Showcase

打开 Trino cli

```
docker-compose exec trino trino
```

通过 Trino 创建 Iceberg 表

> 现在必须在 Trino 上创建表, Trino 上的 Iceberg 实现与 Spark 上面的有一点兼容问题.

```sql
CREATE TABLE iceberg.default.table1 (
   id bigint,
   data varchar,
   updated_at timestamp(6) with time zone
)
WITH (
   format = 'PARQUET',
   partitioning = ARRAY['day(updated_at)']
)
```

> 更多详细的建表方式, 比如说分区: https://trino.io/docs/current/connector/iceberg.html


启动 Spark Shell

```
docker-compose exec spark spark-shell
```

在 Spark 写入一些数据

```scala
sql("insert into iceberg.default.table1 values(1, 'a', null), (2, 'b', null), (3, 'c', null)")
```

在 Spark 查询写入的数据

```scala
sql("select * from iceberg.default.table1 order by id").show
/*
+---+----+----------+
| id|data|updated_at|
+---+----+----------+
|  1|   a|      null|
|  2|   b|      null|
|  3|   c|      null|
+---+----+----------+
*/
```

在 Trino 查询写入的数据

```sql
select * from iceberg.default.table1 order by id;
/*
 id | data | updated_at
----+------+------------
  2 | b    | NULL
  1 | a    | NULL
  3 | c    | NULL
(3 rows)

Query 20210226_062100_00044_9wgc5, FINISHED, 1 node
Splits: 19 total, 19 done (100.00%)
0.70 [3 rows, 2.93KB] [4 rows/s, 4.17KB/s]
*/
```

在 Spark 写入一些更新

```scala
spark.range(3, 10).select($"id", lit("update 5").as("data"), lit("2021-02-28 12:00:00").cast("timestamp").as("updated_at")).createOrReplaceTempView("updates")
sql("merge into iceberg.default.table1 t using updates u on t.id = u.id when matched then update set t.data = u.data, t.updated_at = u.updated_at when not matched then insert *")
```

你可以同时在 Spark 和 Trino 看到这些更新

```scala
sql("select * from iceberg.default.table1 order by id").show
/*
+---+--------+-------------------+
| id|    data|         updated_at|
+---+--------+-------------------+
|  1|       a|               null|
|  2|       b|               null|
|  3|update 5|2021-02-28 12:00:00|
|  4|update 5|2021-02-28 12:00:00|
|  5|update 5|2021-02-28 12:00:00|
|  6|update 5|2021-02-28 12:00:00|
|  7|update 5|2021-02-28 12:00:00|
|  8|update 5|2021-02-28 12:00:00|
|  9|update 5|2021-02-28 12:00:00|
+---+--------+-------------------+
*/
```

```sql
select * from iceberg.default.table1 order by id;
/*
 id |   data   |           updated_at
----+----------+--------------------------------
  1 | a        | NULL
  2 | b        | NULL
  3 | update 5 | 2021-02-28 12:00:00.000000 UTC
  4 | update 5 | 2021-02-28 12:00:00.000000 UTC
  5 | update 5 | 2021-02-28 12:00:00.000000 UTC
  6 | update 5 | 2021-02-28 12:00:00.000000 UTC
  7 | update 5 | 2021-02-28 12:00:00.000000 UTC
  8 | update 5 | 2021-02-28 12:00:00.000000 UTC
  9 | update 5 | 2021-02-28 12:00:00.000000 UTC
(9 rows)

Query 20210226_062255_00045_9wgc5, FINISHED, 1 node
Splits: 27 total, 27 done (100.00%)
0.91 [9 rows, 9.83KB] [9 rows/s, 10.8KB/s]
*/
```

我们还可以查询一些表的元数据, 在 Spark 和 Trino 上的查询方法略微有一点不同

Spark:

```scala
sql("select * from iceberg.default.table1.partitions").show
```

Trino:

```sql
select * from iceberg.default."table1$partitions";
```

注意到我们通过在表的引用中加入后缀来查询相关的元数据

你可以尝试替换后缀查看其他的信息, 下面是支持的后缀:

+ snapshots
+ files
+ history
+ manifests


更多的 SQL 语句可以参考 Iceberg 和 Trino 的文档:

+ http://iceberg.apache.org/spark-ddl/
+ https://trino.io/docs/current/connector/iceberg.html

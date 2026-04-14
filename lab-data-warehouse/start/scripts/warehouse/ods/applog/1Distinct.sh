#!/bin/bash

dt=$(date -d'-1 day' +%Y-%m-%d)

# 如果脚本调用者传入了指定日期，则将统计日志赋值为传入的日期
if [ $1 ];then
dt=$1
fi


metabus_host="doit03:8080"


# 请求元总线查询日志服务器上的日志行总数
logserver_cnt=$(curl http://${metabus_host}/api/get/applog?date=${dt})
echo "请求元总线查询到的日志行数为: $logserver_cnt "

# 统计hdfs上的行总数
hdfds_cnt=0
for f in $(hdfs dfs -ls /logdata/applog/${dt}/ | awk 'NR==1{next}{print $NF}')
do
  i=$(hdfs dfs -text $f | wc -l)
  hdfds_cnt=$((hdfds_cnt+i))
  echo "计算hdfs上的日志行数为: $hdfds_cnt "
done

# create table tmp.log_raw_tmp(json string) partitioned by(dt string);

sql1="alter table tmp.log_raw_tmp add partition(dt='${dt}') location '/logdata/applog/${dt}'"

# distinct mapreduce 只有单并行度工作，使用 groupby 更快
sql2="
set hive.exec.compress.output = true;
set mapred.output.compression.codec = org.apache.hadoop.io.compress.GzipCodec;
insert overwrite directory '/tmp/distinct_task/applog/${dt}'
select
  line
from tmp.log_raw_tmp where dt='${dt}'
group by line
"


# 判断，如果hdfs上的行数>日志服务器上的行数，则启动去重计算
if [ ${hdfds_cnt} -gt ${logserver_cnt} ];then
  echo "检测到${dt}日志采集目录中的数据行数>日支服务器上的日志行数,准备执行去重计算"
  # 启动去重计算
  /opt/hive/bin/beeline \
    -u jdbc:hive2://localhost:10000 \
    -n hive \
    -e "$sql1"
  echo "加载日志数据到临时表"
  /opt/hive/bin/beeline \
    -u jdbc:hive2://localhost:10000 \
    -n hive \
    -e "$sql2"
  echo "执行了去重计算"
fi


if [ $? -eq 0 ];then
  echo "数据重复检测及去重处理,成功"
  exit 0
else 
  echo "数据重复检测及去重处理,失败"
  exit 1
fi

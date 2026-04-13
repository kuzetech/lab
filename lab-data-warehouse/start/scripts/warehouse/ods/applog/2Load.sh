#!/bin/bash

##
#
# @author : deep as the sea
# @department: doitedu
# @date : 2022-02-14
# @desc : app端行为日志加载入库
#

export HIVE_HOME=/opt/hive/

# 获取脚本运行时的前一日日期
dt=$(date -d'-1 day' +%Y-%m-%d)

# 如果脚本调用者传入了指定日期，则执行指定日期数据的导入
if [ $1 ];then
dt=$1
fi

# 判断指定日期的数据是否有做过去重处理
datapath=/logdata/applog/${dt}
hdfs dfs -test -e /tmp/distinct_task/applog/${dt}
if [ $? -eq 0 ];then
  echo "检测到${dt}日期的数据有做去重,准备加载去重后的数据"
  datapath=/tmp/distinct_task/applog/${dt}
else
  echo "检测到${dt}日期的数据没有做去重处理,准备加载flume所采集的数据"
fi

echo "加载的路径为： ${datapath}"


${HIVE_HOME}/bin/hive -e "load data inpath '${datapath}' overwrite into table ods.mall_app_log partition(dt='${dt}')"

# 判断上一条命令执行的返回码(判断hive导入是否成功)
if [ $? -eq 0 ];then
  echo "数仓任务执行报告：app行为日志,日期：$dt ,加载入库任务成功"
  exit 0
else
  echo "数仓任务执行报告：app行为日志,日期：$dt ,加载入库任务失败"
  exit 1
fi

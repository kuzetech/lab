#!/bin/bash

dt=$(date -d'-1 day' +%Y-%m-%d)


metabus_host="doit03:8080"

# 如果脚本调用者传入了指定日期，则将统计日志赋值为传入的日期
if [ $1 ];then
dt=$1
fi



# 统计指定日期的日志文件的行数

# 先查看日志目录中该日期的文件是否只有1个
file_cnt=$(ls -l /root/moni_data/log/applog/*${dt}* | wc -l)
if [ $file_cnt -eq 1 ];then
cnt=$(wc -l /root/moni_data/log/applog/*${dt}* | cut -d' ' -f 1)
else 
cnt=$(echo $(wc -l /root/moni_data/log/applog/*${dt}*  | grep total) | cut -d' ' -f 1)
fi

# 获取本日志服务器是主机名
servername=$(hostname)

# 请求meta bus，提交本机的日志行数
curl -H "Content-type: application/json" -X POST -d"{\"logServerName\":\"${servername}\",\"logType\":\"applog\",\"logDate\":\"${dt}\",\"lineCnt\":${cnt} }" http://${metabus_host}/api/commit


if [ $? -eq 0 ];then
    echo "日志服务器${servername},日期${dt},统计到日志行数：${cnt},提交到meta bus 成功"
    echo "日志服务器${servername},日期${dt},统计到日志行数：${cnt},提交到meta bus 成功" | mail -s '数仓平台,任务成功通知' 83544844@qq.com
    exit 0
else 
    echo "日志服务器${servername},日期${dt},统计到日志行数：${cnt},提交到meta bus 失败"
    echo "日志服务器${servername},日期${dt},统计到日志行数：${cnt},提交到meta bus 失败" | mail -s '数仓平台,任务失败通知' 83544844@qq.com
    exit 1
fi


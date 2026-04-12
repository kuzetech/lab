#!/bin/bash

dt=$(date -d'-1 day' +%Y-%m-%d)

cnt=$(wc -l /root/moni_data/log/applog/*.${dt}  | cut -d' ' -f 1)

servername=$(hostname)

curl -H "Content-type: application/json" -X POST -d"{\"logServerName\":\"${servername}\",\"logType\":\"applog\",\"logDate\":\"${dt}\",\"lineCnt\":${cnt} }" http://192.168.5.3:8080/api/commit
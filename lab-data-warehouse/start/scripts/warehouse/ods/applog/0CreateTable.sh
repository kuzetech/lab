#!/bin/bash

sql="
drop table if exists ods.app_event_log;
create external table ods.app_event_log
(
  account string,
  appId   string,
  appVersion    string,
  carrier     string,
  deviceId     string,
  deviceType    string,
  eventId     string,
  ip  string,
  latitude    double,
  longitude    double,
  netType     string,
  osName      string,
  osVersion    string,
  properties    map<string,string>,
  releaseChannel  string,
  resolution    string,
  sessionId    string,
  `timestamp`    bigint
)  
partitioned by (dt string)
row format serde 'org.apache.hive.hcatalog.data.JsonSerDe' 
stored as textfile;   
"

hive -e "$sql"


if [ $? -eq 0 ];then
  echo "成功"
  exit 0
else 
  echo "失败"
  exit 1
fi

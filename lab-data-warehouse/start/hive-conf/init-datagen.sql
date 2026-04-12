create database if not exists tmp;
create database if not exists ods;
create database if not exists dwd;
create database if not exists dws;
create database if not exists ads;

use dwd;

create external table if not exists flume_datagen_raw(
  line string
)
partitioned by (dt string)
stored as textfile
location '/tmp/flume/datagen';

alter table flume_datagen_raw
  add if not exists partition (dt='${hivevar:dt}')
  location '/tmp/flume/datagen/dt=${hivevar:dt}';

alter table flume_datagen_raw
  partition (dt='${hivevar:dt}')
  set location '/tmp/flume/datagen/dt=${hivevar:dt}';

set hive.msck.path.validation=ignore;

msck repair table flume_datagen_raw;


/Users/huangsw/soft/flink-1.13.2/bin/sql-client.sh \
-i /Users/huangsw/script/docker-compose/flink-cdc/join-cdc-event-time/init.sql \
-f /Users/huangsw/script/docker-compose/flink-cdc/join-cdc-event-time/execute.sql


/Users/huangsw/soft/flink-1.13.2/bin/flink stop \
--savepointPath /tmp/flink-savepoints \
a855f57451d5ccedd871f3257ad70bd2
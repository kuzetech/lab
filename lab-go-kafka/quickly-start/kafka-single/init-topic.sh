#!/bin/bash

set +x

are_brokers_available() {
    expected_num_brokers=1 # replace this with a proper value
    brokers_str="$(zookeeper-shell.sh zookeeper:2181 ls /brokers/ids 2>/dev/null | grep "\[")"
    echo $brokers_str
    array=(${brokers_str//,/ })
    num_brokers=${#array[*]}
    echo $num_brokers
    if [ $num_brokers -ge $expected_num_brokers ]
    then
        return 1
    else
        return 0
    fi
}

while are_brokers_available 
do
    echo "brokers not available yet"
    sleep 1
done

echo "Kafka custom init"

/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic demo --partitions 3 --replication-factor 1

echo "Kafka custom init done"
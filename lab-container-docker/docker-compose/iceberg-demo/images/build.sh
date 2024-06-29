#!/bin/bash
set -eu

GOALS=""

_build() {
    if [ "$GOALS" = "${GOALS/$1/x}" ]; then
        GOALS="$GOALS $1"
    fi
}

build_alluxio() {
    _build alluxio
}

build_hadoop() {
    _build hadoop
}

build_hive() {
    build_hadoop

    _build hive
}

build_kafka() {
    _build kafka
}

build_zookeeper() {
    _build zookeeper
}

build_spark() {
    build_hadoop

    _build spark
}

build_presto() {
    build_alluxio

    _build presto
}

build_trino() {
    build_alluxio

    _build trino
}

ARGS=$@
if [ -z "$ARGS" ]; then
    ARGS="$(sed -n -E 's/build_(.+)\(\).*/\1/p' ${BASH_SOURCE[0]})"
fi

for ARG in $ARGS; do
    build_$ARG
done

cd $(dirname ${BASH_SOURCE[0]})
for GOAL in $GOALS; do
    docker build -t dpp/$GOAL $GOAL
done

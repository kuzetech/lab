#!/bin/bash

dt=`date -d'-1 day' +%Y-%m-%d`
if [ $1 ];then
dt=$1
fi

echo $dt

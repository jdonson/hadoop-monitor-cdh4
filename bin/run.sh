#!/bin/bash
SERVICE=hadoop-monitor-cdh4
VERSION=1.0.0
DATE=$(date +%Y%m%d%H%M%S)

function usage() {
    echo "sh run.sh"
}

cd ..
CLASSPATH=${CLASSPATH}:./conf
CLASSPATH=${CLASSPATH}:./$SERVICE-$VERSION.jar
export CLASSPATH
mkdir ./logs >/dev/null 2>&1
nohup java -Xms128m -Xmx256m com.wandoujia.hadoop.monitor.MonitorMain >logs/hadoop-monitor.out 2>&1 &


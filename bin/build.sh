#!/bin/bash

SERVICE=hadoop-monitor-cdh4
VERSION=1.0.0

function usage() {
    echo "Usage: ./build.sh"
}

INSTALL_DIR=$SERVICE-$VERSION
cd ..
mvn clean
mvn assembly:assembly
mkdir -p ../$INSTALL_DIR
mkdir -p ../$INSTALL_DIR/logs
mkdir -p ../$INSTALL_DIR/conf

cp target/$SERVICE-$VERSION-jar-with-dependencies.jar ../$INSTALL_DIR/$SERVICE-$VERSION.jar
cp -r bin ../$INSTALL_DIR/
cp src/main/resources/* ../$INSTALL_DIR/conf
if [ ! -d ../$INSTALL_DIR/logs ];then
    mkdir ../$INSTALL_DIR/logs
fi


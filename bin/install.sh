#!/bin/bash

SERVICE=hadoop-monitor-cdh4
VERSION=1.0.0

function usage() {
    echo "Usage: ./install.sh"
}

INSTALL_DIR=$SERVICE-$VERSION
cd ..

PROFILE=online
if [ $# -eq 1 ];then
    PROFILE=$1
fi

mvn clean
mvn assembly:assembly -P ${PROFILE}
mkdir -p ../$INSTALL_DIR
mkdir -p ../$INSTALL_DIR/logs
mkdir -p ../$INSTALL_DIR/conf

cp target/$SERVICE-$VERSION-jar-with-dependencies.jar ../$INSTALL_DIR/$SERVICE-$VERSION.jar
cp -r bin ../$INSTALL_DIR/
cp target/classes/*.xml ../$INSTALL_DIR/conf/
cp target/classes/*.properties ../$INSTALL_DIR/conf/
if [ ! -d ../$INSTALL_DIR/logs ];then
    mkdir ../$INSTALL_DIR/logs
fi


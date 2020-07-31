#!/bin/bash
#停止服务脚本
echo "Stopping  eurekaserver-0.0.1-SNAPSHOT"
pid=`ps -ef | grep eurekaserver-0.0.1-SNAPSHOT.jar | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ]

then
   echo "kill -9 的pid:" $pid
   kill -9 $pid
fi


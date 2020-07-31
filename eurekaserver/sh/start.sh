#!/bin/bash
#重启脚本
echo "Starting eurekaserver Application"
pid=`ps -ef | grep eurekaserver-0.0.1-SNAPSHOT.jar | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ]
then
    echo "eurekaserver-0.0.1-SNAPSHOT is running"
    echo "kill -9 的pid:" $pid
    kill -9 $pid
	echo "关闭进程："$pid
fi
	sleep 3
	
	echo "授予当前用户权限"
	chmod 777 /springcloud/eurekaserver/eurekaserver-0.0.1-SNAPSHOT.jar
	echo "starting  eurekaserver-0.0.1-SNAPSHOT "
    nohup java -jar  eurekaserver-0.0.1-SNAPSHOT.jar >> eurekaserver.log 2>&1 &
	
echo "部署完毕！"
	


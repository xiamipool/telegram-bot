#!/bin/bash

SELF=$(cd $(dirname $0); pwd -P)/$(basename $0)
null_buffer=/dev/null
pid_file=./flag.pid
wait_time=10
application='.:../lib/* com.xiamipool.bot.Application'
server_name='bot'

#等待进程退出,$1为进程PID，$2为最长等待时间单位s
wait_stop() {
    local time=0
    kill -0 $1 &> ${null_buffer}
    while ( [[ $? = 0 ]] && [[ ${time} -lt $2 ]] ); do
        sleep 1s
        time=$((time+1))
        kill -0 $1 &> ${null_buffer}
    done

    kill -0 $1 &> ${null_buffer}
    if [[ $? -ne 0 ]]; then
        return 0
    else
        return 1
    fi
}

#启动服务程序
start() {
    if [[ 1 -eq $# ]]; then
        echo "start ${server_name} by remote port = "$2
        nohup java -Dspring.profiles.active=$1 -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=$1 -cp ${application} > ${null_buffer} 2>&1 &
    else
        echo "start ${server_name}"
        nohup java -Dspring.profiles.active=$1 -cp ${application} > ${null_buffer} 2>&1 &
    fi
}

#关闭服务程序
stop() {
    echo "stop ${server_name}"

    #查看进程文件
    if [[ ! -f ${pid_file} ]]; then
        echo "${server_name} has no process file"
        return 1
    fi

    #查看进程是否存在
    local PID=$(cat "${pid_file}")
    kill -0 ${PID} &> ${null_buffer}
    if [[ $? -ne 0 ]]; then
        echo "${server_name} has stopped"
        return 0
    fi

    #删除进程文件，让系统安全退出
    rm -f ${pid_file} &> ${null_buffer}

    #等待进程退出
    wait_stop ${PID} ${wait_time}
    if [[ $? -ne 0 ]]; then
        echo "${server_name} does't stop, try to kill"
        kill -9 ${PID} &> ${null_buffer}
    fi

    echo "${server_name} stop"
}

#服务程序状态
status() {
    #查看进程文件
    if [[ ! -f ${pid_file} ]]; then
        echo "${server_name} has no process file"
        return 1
    fi

    #查看进程是否存在
    local PID=$(cat "${pid_file}")
    kill -0 ${PID} &> ${null_buffer}
    if [[ $? -ne 0 ]]; then
        echo "${server_name} is not running"
    else
        echo "${server_name} is running"
    fi

    return 0
}

case "${1:-''}" in
    "start")
        start $2 $3
        ;;
    "stop")
        stop
        ;;
    "restart")
        stop
        start $2 $3
        ;;
    "state")
        status
        ;;
    *)
        echo "Usage: $SELF start|start test|start test r|stop|restart|state|"
        exit 1
        ;;
esac

exit 0

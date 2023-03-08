#!/bin/bash

help(){
    echo "    logcat2     # 抓日志" | grep --color -E  ".*?#"
}

logcat3(){
    logname=$(date +%Y%m%d_%H%M%S)
    echo 日志文件名 $logname.log

    # 调用清除缓存
    if [[ $1 = "c" ]] || [[ $1 = "clear" ]];then
        adb logcat -c
        echo "clear buffer."
    fi
    # adb logcat -c;
    echo "open $logname.log" | pbcopy
    adb logcat -D -v threadtime >> $logname.log
}

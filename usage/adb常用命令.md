获取指定包名对应的版本信息
adb shell pm dump <packageName> | grep "version"

列出所有的包名，加-f 可以获取安装位置
adb shell pm list packages -f

dumpsys package <pkgname> 查看 apk 位置等详细信息


查看焦点抢占情况
logcat | grep AudioFocus


分辨率，dpi
adb shell dumpsys window displays

分辨率
adb shell wm size

adb shell wm density 获取屏幕像素密度，以决定调用哪个图片


root@aston-p1:/ # am stack boxes


获取手机型号与设备信息
cat /system/build.prop

查看Android设备的CPU架构信息
adb shell
cat  /proc/cpuinfo
​	
发送广播
adb shell am broadcast -a android.intent.action.BOOT_COMPLETED

adb shell am broadcast -a MusicControl_To_Service_Action --ei musiccommand 2

启动 activity
adb shell am start -n com.sys.plugin/.MainActivity



启动 service
adb shell am startservice -n <pkgname>/<service name>
adb shell am startservice -n <pkgname>/<service name> -a <action>

清除app应用数据
adb shell pm clear <pkgname>

杀死(停止)app进程
adb shell am kill(force-stop) <pkgname>

查看所有 APP 的名称
adb shell pm list packages


截屏	
adb shell screencap -p /sdcard/1.png
录屏	
adb shell screenrecord /sdcard/test.mp4

多媒体按键
adb shell input keyevent 87 

KEYCODE_MEDIA_PLAY_PAUSE 85
KEYCODE_MEDIA_PAUSE 127
KEYCODE_MEDIA_NEXT 87
KEYCODE_MEDIA_PREVIOUS 88
KEYCODE_MEDIA_PLAY 126

KEYCODE_HOME 3
KEYCODE_BACK 4
KEYCODE_DPAD_UP 19
KEYCODE_DPAD_DOWN 20
KEYCODE_DPAD_LEFT 21
KEYCODE_DPAD_RIGHT 22
KEYCODE_DPAD_CENTER 23



浮窗隐藏
手机管家 --> 通知管理 --> 悬浮窗管理


------------------------- 
测试
adb shell monkey -p your.package.name -vvv 500 > monkeytest.txt  

p 包名
-vvv 输出1-3级
500 500次事件


MonkeyRunner

-------------------------

CPU%：CPU占用率  RSS：实际占用的物理内存数，单位KB

top -d 1 %包名

procrank -u | grep %包名


dumpsys [options]
​	meminfo 显示内存信息
​	cpuinfo 显示CPU信息
​	account 显示accounts信息
​	activity 显示所有的activities的信息
​	window 显示键盘，窗口和它们的关系
​	wifi 显示wifi信息
​	

查看所有服务	
adb shell dumpsys activity services
查看所有activity	
adb shell dumpsys activity
查看apk使用内存情况	
adb shell dumpsys meminfo package_name


dumpsys meminfo cn.kuwo.kwmusichd
procrank | grep cn.kuwo


查看当前界面处于哪个activity
adb shell dumpsys activity | findstr "mFocusedActivity"
adb shell "dumpsys window |grep mCurrentFocus"


​	
过滤系统日志
adb  logcat -v threadtime > xxx.txt



查看内存
adb shell busybox free m

adb shell procrank

adb shell dumpsys meminfo com.android.launcher


-------------------------

adb root

adb connect 192.168.1.115

adb remount

adb pull /mnt/oem/Equipmentdata.xml d:/

adb shell mount -o rw,remount -t ext4 /system



-------------------------
查看 keystore 的签名
在 <java_home>\bin
keytool -list -v -keystore D:\Desktop\app_key
keytool -list -v -keystore E:\debug.keystore -storepass xxx(密匙)

查看三方应用或是系统应用签名
解压缩 apk 获取 CERT.RSA
keytool -printcert -file META-INF/CERT.RSA

-------------------------





















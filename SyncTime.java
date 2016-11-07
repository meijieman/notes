import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 同步时间
 */
public class SyncTime {

    private static int sleepMinutes = 0;
    private static final long EPOCH_OFFSET_MILLIS;
    private static final String[] hostName = {"time-a.nist.gov", "time-nw.nist.gov", "time.nist.gov"};

    static {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        // Java使用的参照标准是1970年，而时间服务器返回的秒是相当1900年的，算一下偏移
        calendar.set(1900, Calendar.JANUARY, 1, 0, 0, 0);
        EPOCH_OFFSET_MILLIS = Math.abs(calendar.getTime().getTime());
    }

    public static void main(String[] args) {
        GetWebTime();
    }

    private static Date getNetDate(String hostName) {
        try {
            Socket socket = new Socket(hostName, 37);
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream(),
                    socket.getReceiveBufferSize());
            int b1 = bis.read();
            int b2 = bis.read();
            int b3 = bis.read();
            int b4 = bis.read();
            if ((b1 | b2 | b3 | b4) < 0) {
                return null;
            }
            long result = (((long) b1) << 24) + (b2 << 16) + (b3 << 8) + b4;
            Date date = new Date(result * 1000 - EPOCH_OFFSET_MILLIS);
            socket.close();
            print("原子钟时间 " + date + ", hostName " + hostName);
            return date;
        } catch (IOException ex) {
            print(ex.toString());
            return null;
        }
    }

    /**
     * 通过ping命令判断是否离线
     *
     * @return
     */
    public static boolean isOffline() {
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec("ping www.hao123.com");
            InputStream s = process.getInputStream();
            BufferedReader bis = new BufferedReader(new InputStreamReader(s));
            String str = bis.readLine();
            if (str != null) {
                print("网站返回的数据 " + str);
//                if (str.startsWith("Reply from")) {
                return false;
//                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException ex) {
            print(ex.toString());
        }
        return true;
    }

    /**
     * 通过调用本地命令date和time修改计算机时间
     *
     * @param date
     */
    private static void setComputeDate(Date date) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        c.setTime(new Date());
        int year_c = c.get(Calendar.YEAR);
        int month_c = c.get(Calendar.MONTH) + 1;
        int day_c = c.get(Calendar.DAY_OF_MONTH);
        int hour_c = c.get(Calendar.HOUR_OF_DAY);
        int minute_c = c.get(Calendar.MINUTE);

        String ymd = year + "-" + month + "-" + day;
        String time = hour + ":" + minute + ":" + second;
        try {
            // 日期不一致就修改一下日期
            if (year != year_c || month != month_c || day != day_c) {
                String cmd = "cmd /c date " + ymd;
                Process process = Runtime.getRuntime().exec(cmd);
                process.waitFor();
            }

            // 时间不一致就修改一下时间
            if (hour != hour_c || minute != minute_c) {
                String cmd = "cmd  /c  time " + time;
                Process process = Runtime.getRuntime().exec(cmd);
                process.waitFor();
            }
        } catch (IOException | InterruptedException ex) {
            print("修改失败 " + ex.toString());
        }
    }

    public static void GetWebTime() {
        // 检测电脑是否在线
        if (isOffline()) {
            // 重试
            while (sleepMinutes < 30) {
                try {
                    Thread.sleep(1000 * 5 * 60);
                    sleepMinutes += 2;
                    GetWebTime();
                } catch (InterruptedException ex) {
                    print(ex.toString());
                }
            }

            // 30分钟还没有联线，表示就不上网了，退出吧
            System.exit(0);
        } else {
            // 从网络上获取时间
            for (String name : hostName) {
                Date date = getNetDate(name);
                if (date != null) {
                    setComputeDate(date);
                    break;
                }
            }
        }
    }

    static void print(String msg) {
        System.out.println("------ " + msg);
    }
}

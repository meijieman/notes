package com.hongfans.musicdemo;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/29.
 */
public class SDUtils {

    private static final String TAG = "SDUtils";

    /**
     * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
     *
     * @return
     */
    public static ArrayList<String> getDevMountList() throws IOException {
        File file = new File("/etc/vold.fstab");
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len= -1;
        if ((len = fis.read(buffer)) != -1) {
            baos.write(buffer,0,len);
        }
        fis.close();
        String str = baos.toString();

        Log.e(TAG, "getDevMountList: str" + str);
        String[] toSearch = str.split(" ");
        ArrayList<String> out = new ArrayList<String>();
        for (int i = 0; i < toSearch.length; i++) {
            if (toSearch[i].contains("dev_mount")) {
                if (new File(toSearch[i + 2]).exists()) {
                    out.add(toSearch[i + 2]);
                }
            }
        }
        return out;
    }
}

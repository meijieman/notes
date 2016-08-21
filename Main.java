package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by major on 16-8-21.
 */

public class Main {
    public static void main(String[] args) throws IOException {

        copyFile("d:/as/as笔记");
    }

    private static void copyFile(String path) throws IOException {
        File root = new File(path);
        if (!root.exists()) {
            throw new IllegalArgumentException("文件路径非法");
        }
        if (root.isDirectory()) {
            File copyRoot =  new File(root.getParent(), "copy_" + root.getName());
            copyRoot.mkdir();
            File[] files = root.listFiles();
            for (File file : files) {
                copyFile(file.getAbsolutePath());
            }
        } else {
            File parentFile = root.getParentFile();
            //
            File dst = new File(parentFile.getParent() + File.separator + "copy_" + parentFile.getName(), "copy_" + root.getName());
            copyFile1(path, dst.getAbsolutePath());
        }
    }

    private static void copyFile1(String src, String dst) throws IOException {
        System.out.println("------ src " + src + " dst " + dst);
        InputStream is = new FileInputStream(src);
        OutputStream os = new FileOutputStream(dst);
        int len;
        byte[] buffer = new byte[1024];
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        os.flush();

        is.close();
        os.close();
    }
}

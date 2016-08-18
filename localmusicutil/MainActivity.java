package com.hongfans.musicdemo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.system.StructUtsname;
import android.util.Log;
import android.view.View;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.btn_main);
        View viewPath = findViewById(R.id.btn_main_path);
        assert  view != null;
        assert  viewPath != null;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<Song> allSongs = AudioUtils.getAllSongs(MainActivity.this);
                for (Song allSong : allSongs) {
                    Log.e(TAG, "onCreate: " + allSong.getFileName() + " " + allSong.getFileUrl());
                }
            }
        });
        viewPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                Environment.getExternalStorageDirectory() 返回的是当前设备厂商所认为的“外部存储”，
                有可能返回外置的SD卡目录（Micro SD Card），也可能返回内置的存储目录（eMMC）。
                 */
                // 获取的是内部存储（内置存储） /storage/emulated/0
                File file = Environment.getExternalStorageDirectory();
                Log.e(TAG, "onClick:  " + " file " + file.getAbsolutePath());
                String[] list = file.list();
                Log.e(TAG, "onClick: list " + Arrays.toString(list));

                File directory = Environment.getExternalStorageDirectory();
                Log.e(TAG, "onClick: dir " + directory.getAbsolutePath());

                String state = Environment.getExternalStorageState();

                Log.e(TAG, "onClick: state " + state);

                try {
                    SDUtils.getDevMountList();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}

package com.hongfans.musicdemo;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * @Desc 音频文件帮助类
 */
public class AudioUtils {

    /**
     * 获取sd卡所有的音乐文件
     */
    public static ArrayList<Song> getAllSongs(Context context) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.YEAR,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA},
                MediaStore.Audio.Media.MIME_TYPE + "=? or "
                        + MediaStore.Audio.Media.MIME_TYPE + "=?",
                new String[]{"audio/mpeg", "audio/x-ms-wma"}, null);

        ArrayList<Song> songs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            Song song;
            do {
                song = new Song();
                song.setFileName(cursor.getString(1)); // 文件名
                song.setTitle(cursor.getString(2));        // 歌曲名
                song.setDuration(cursor.getInt(3));   // 时长
                song.setSinger(cursor.getString(4));// 歌手名
                song.setAlbum(cursor.getString(5)); // 专辑名
                if (cursor.getString(6) != null) {
                    song.setYear(cursor.getString(6));  // 年代
                } else {
                    song.setYear("未知");
                }
                if ("audio/mpeg".equals(cursor.getString(7).trim())) {
                    song.setType("mp3");   // 歌曲格式
                } else if ("audio/x-ms-wma".equals(cursor.getString(7).trim())) {
                    song.setType("wma");
                }

                if (cursor.getString(8) != null) {
                    // 文件大小
                    float size = cursor.getInt(8) / 1024f / 1024f;
                    song.setSize((size + "").substring(0, 4) + "M");
                } else {
                    song.setSize("未知");
                }
                if (cursor.getString(9) != null) { // 文件路径
                    song.setFileUrl(cursor.getString(9));
                }
                songs.add(song);
            } while (cursor.moveToNext());

            cursor.close();
        }
        return songs;
    }
}
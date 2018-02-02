package com.infisight.hudprojector.util;

import java.util.ArrayList;
import java.util.List;

import com.infisight.hudprojector.data.DataMusicClass;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;


/**
 * 项目名称 : FRMusic
 * 
 * @author 作者 : fengrui
 * @date 创建时间：2015年8月5日 下午1:37:01
 * @version 1.0
 */
public class MusicList {
	public static List<DataMusicClass> getMusicData(Context context) {

		List<DataMusicClass> musicList = new ArrayList<DataMusicClass>();
		ContentResolver cr = context.getContentResolver();
		if (cr != null) {
			// 获取所有歌曲
			Cursor cursor = cr.query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
					null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
			if (null == cursor) {
				return null;
			}
			while (cursor.moveToNext()) {
				DataMusicClass m = new DataMusicClass();
				String title = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.TITLE));
				String singer = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.ARTIST));
				if ("<unknown>".equals(singer)) {
					singer = "未知艺术家";
				}
				String album = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.ALBUM));
				long size = cursor.getLong(cursor
						.getColumnIndex(MediaStore.Audio.Media.SIZE));
				long time = cursor.getLong(cursor
						.getColumnIndex(MediaStore.Audio.Media.DURATION));
				String url = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.DATA));
				String name = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
				String sbr = name.substring(name.length() - 3, name.length());

				if ("m4a".equals(sbr) || "mp3".equals(sbr) || "aac".equals(sbr)) {
					m.setTitle(title);
					m.setSinger(singer);
					m.setAlbum(album);
					m.setSize(size);
					m.setTime(time);
					m.setUrl(url);
					m.setName(name);
					musicList.add(m);
				}
			}

		}
		return musicList;
	}

	public static List<DataMusicClass> getMusicBySinger(String singer,
			List<DataMusicClass> listMusic) {
		if (singer == null || singer.length() <= 0) {
			return null;
		}
		List<DataMusicClass> musicSinger = new ArrayList<DataMusicClass>();
		for (DataMusicClass music : listMusic) {
			if (singer.equals(music.getSinger())) {
				musicSinger.add(music);
			}
		}
		return musicSinger;
	}

	public static List<DataMusicClass> getMusicBySong(String song, List<DataMusicClass> listMusic) {
		if (song == null || song.length() <= 0) {
			return null;
		}
		List<DataMusicClass> musicSong = new ArrayList<DataMusicClass>();
		for (DataMusicClass music : listMusic) {
			if (song.equals(music.getTitle())) {
				musicSong.add(music);
			}
		}
		return musicSong;
	}

}

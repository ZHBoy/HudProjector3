package com.infisight.hudprojector.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * 项目名称 : FRMusic 处理歌词文件的类
 * 
 * @author 作者 : fengrui
 * @date 创建时间：2015年8月7日 上午11:22:02
 * @version 1.0
 */

public class LrcProcess {

	private List<LrcContent> LrcList;

	private LrcContent mLrcContent;

	public LrcProcess() {

		mLrcContent = new LrcContent();
		LrcList = new ArrayList<LrcContent>();
	}

	/**
	 * 读取歌词文件的内容
	 */
	@SuppressLint("NewApi")
	public String readLRC(String song_path) {
		// public void Read(String file){

		StringBuilder stringBuilder = new StringBuilder();

		if (song_path != null && song_path.length() > 0) {
			String songpath = song_path.substring(0, song_path.indexOf("."));

			if (songpath != null && songpath.length() > 0) {
				File f = new File(songpath + ".lrc");
				try {
					FileInputStream fis = new FileInputStream(f);
					InputStreamReader isr = new InputStreamReader(fis, "GB2312");
					BufferedReader br = new BufferedReader(isr);
					String s = "";
					while ((s = br.readLine()) != null) {
						// if ((s.indexOf("[ar:") != -1) || (s.indexOf("[ti:")
						// !=
						// -1)
						// || (s.indexOf("[by:") != -1)) {
						// s = s.substring(s.indexOf(":") + 1, s.indexOf("]"));
						// } else {
						// try {
						// String ss = s.substring(s.indexOf("["),
						// s.indexOf("]") + 1);
						// s = s.replace(ss, "");
						// } catch (Exception e) {
						// s = "     ";
						// }
						// }
						// stringBuilder.append(s + "\n");

						// 替换字符
						s = s.replace("[", "");
						s = s.replace("]", "@");

						// 分离"@"字符
						String splitLrc_data[] = s.split("@");
						if (splitLrc_data.length > 1) {

							mLrcContent.setLrc(splitLrc_data[1]);
							// 处理歌词取得歌曲时间
							int LrcTime = TimeStr(splitLrc_data[0]);

							mLrcContent.setLrc_time(LrcTime);

							// 添加进列表数组
							LrcList.add(mLrcContent);

							// 创建对象
							mLrcContent = new LrcContent();

						}

					}
					br.close();
					isr.close();
					fis.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					stringBuilder.append("木有歌词文件，赶紧去下载！...");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					stringBuilder.append("木有读取到歌词啊！");
				}
			}
		}

		// File f = new File(song_path.replace(".mp3", ".lrc"));

		return stringBuilder.toString();
	}

	/**
	 * 解析歌曲时间处理类
	 */
	public int TimeStr(String timeStr) {

		try {
			timeStr = timeStr.replace(":", ".");
			timeStr = timeStr.replace(".", "@");

			String timeData[] = timeStr.split("@");

			// 分离出分、秒并转换为整型
			int minute = Integer.parseInt(timeData[0]);
			int second = Integer.parseInt(timeData[1]);
			int millisecond = Integer.parseInt(timeData[2]);

			// 计算上一行与下一行的时间转换为毫秒数
			int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;

			return currentTime;
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public List<LrcContent> getLrcContent() {

		return LrcList;
	}

	/**
	 * 获得歌词和时间并返回的类
	 */
	public class LrcContent {
		private String Lrc;
		private int Lrc_time;

		public String getLrc() {
			return Lrc;
		}

		public void setLrc(String lrc) {
			Lrc = lrc;
		}

		public int getLrc_time() {
			return Lrc_time;
		}

		public void setLrc_time(int lrc_time) {
			Lrc_time = lrc_time;
		}
	}

}

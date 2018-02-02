package com.infisight.hudprojector.adapter;

import java.util.ArrayList;
import java.util.List;

import com.infisight.hudprojector.R;
import com.infisight.hudprojector.data.DataMusicClass;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 项目名称 : FRMusic
 * 
 * @author 作者 : fengrui
 * @date 创建时间：2015年8月5日 下午4:12:25
 * @version 1.0
 */
public class MusicAdapter extends BaseAdapter {

	private List<DataMusicClass> listMusic = new ArrayList<DataMusicClass>();
	private Context context;

	public MusicAdapter(List<DataMusicClass> listMusic, Context context) {

		this.listMusic = listMusic;
		this.context = context;
	}

	public List<DataMusicClass> getListMusic() {
		return listMusic;
	}

	public void setListMusic(List<DataMusicClass> listMusic) {
		this.listMusic = listMusic;
	}

	@Override
	public int getCount() {
		return listMusic.size();
	}

	@Override
	public Object getItem(int position) {
		return listMusic.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void AddNewItem(DataMusicClass newItem) {
		listMusic.add(newItem);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.music_item, null);
		}
		DataMusicClass m = listMusic.get(position);
		// 歌曲列表位置
		TextView music_item_num = (TextView) convertView
				.findViewById(R.id.music_item_num);
		// if (position == 0) {
		// music_item_image.setBackgroundResource(R.drawable.number1);
		// } else if (position == 1) {
		// music_item_image.setBackgroundResource(R.drawable.number2);
		// } else if (position == 2) {
		// music_item_image.setBackgroundResource(R.drawable.number3);
		// } else if (position == 3) {
		// music_item_image.setBackgroundResource(R.drawable.number4);
		// } else if (position == 4) {
		// music_item_image.setBackgroundResource(R.drawable.number5);
		// }
		// 音乐名
		TextView tv_music_name = (TextView) convertView
				.findViewById(R.id.music_item_name);
		Log.i("TAG", "音乐名字的长度" + m.getTitle().length());
		String songname = m.getTitle();
		if (m.getTitle().length() > 11) {
			songname = m.getTitle().substring(0, 10) + "...";
		}
		Log.i("TAG", "截取后音乐名字的长度" + songname.length());
		tv_music_name.setText(songname);
		// 歌手
		TextView tv_music_singer = (TextView) convertView
				.findViewById(R.id.music_item_singer);
		tv_music_singer.setText(m.getSinger());
		music_item_num.setText(position + 1 + "");
		// 歌曲总时间
		// TextView tv_music_time = (TextView) convertView
		// .findViewById(R.id.music_item_time);
		// tv_music_time.setText(toTime((int) m.getTime()));

		return convertView;
	}

	/**
	 * 时间格式转换
	 * 
	 * @param time
	 * @return
	 */
	public String toTime(int time) {
		time /= 1000;
		int minute = time / 60;
		int hour = minute / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

	// 删除本次歌曲列表
	public boolean setListMusicIsNull() {
		if (listMusic.size() > 0) {
			listMusic.clear();
			return true;
		} else {
			return false;
		}
	}

}

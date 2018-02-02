package com.infisight.hudprojector.service;

import java.io.IOException;
import java.util.List; 

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager; 
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log; 

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken; 
import com.infisight.hudprojector.common.HudProjectApplication;
import com.infisight.hudprojector.data.DataMusicClass; 
import com.infisight.hudprojector.fragment.MusicFragment;
import com.infisight.hudprojector.util.Constants; 
import com.infisight.hudprojector.util.MusicList;

/**
 * 项目名称 : FRMusic
 * 
 * @author 作者 : fengrui
 * @date 创建时间：2015年8月7日 下午4:04:32
 * @version 1.0
 */
public class MusicService extends Service implements Runnable {

	private MediaPlayer mediaPlayer;
	private List<DataMusicClass> lists;
	// 当前播放位置
	public static int mid = 0;  
	public static int playing_id; 
	Gson gson = null;
	private static final String TAG = "MusicService"; 
	private AudioManager mAm; 
	
	@Override
	public void onCreate() {

		lists = MusicList.getMusicData(getApplicationContext());
		SeekBarBroadcastReceiver receiver = new SeekBarBroadcastReceiver();
		IntentFilter filter = new IntentFilter("com.example.frmusic.seekBar");
		this.registerReceiver(receiver, filter);
		new Thread(this).start(); 
		mAm = (AudioManager) getApplicationContext().getSystemService(
				Context.AUDIO_SERVICE);

		super.onCreate(); 
	}

	// 进度条状态的广播接收
	private class SeekBarBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int seekBarPosition = intent.getIntExtra("seekBarPosition", 0);
			mediaPlayer.seekTo(seekBarPosition * mediaPlayer.getDuration()
					/ 100);
			mediaPlayer.start(); 
		}

	}

	@Override
	public void onStart(Intent intent, int startId) { 
		gson = new Gson(); 
		if (intent != null) {
			String play = intent.getStringExtra("play");
			mid = intent.getIntExtra("id", 0);
			if (intent.getStringExtra("listType") != null) {
				lists = gson.fromJson(intent.getStringExtra("listType"),
						new TypeToken<List<DataMusicClass>>() {
						}.getType()); 
			}
			if (play != null) {
				if (play.equals("play")) {
					if (null != mediaPlayer) {
						mediaPlayer.release();
						mediaPlayer = null;
					}
					playMusic(mid); 
				} else if (play.equals("pause")) {
					if (null != mediaPlayer) {
						mediaPlayer.pause();
						Intent hud_music = new Intent(
								Constants.MUSIC_AUDIOFOCUS_ACTION);
						hud_music.putExtra("hud_music", false);
						MusicService.this.sendBroadcast(hud_music);
						Log.d(TAG, "play.equals(pause");
						HudProjectApplication.isMusicPlaying = false; 
						HudProjectApplication.doubanOrMusic = "music"; 
					}

				} else if (play.equals("playing")) {
					if (null != mediaPlayer) {
						mediaPlayer.start();  
						Intent hud_music = new Intent(
								Constants.MUSIC_AUDIOFOCUS_ACTION);
						hud_music.putExtra("hud_music", true);
						MusicService.this.sendBroadcast(hud_music);
						HudProjectApplication.isMusicPlaying = true;
						HudProjectApplication.doubanOrMusic = "music";
						Log.i("123", "isMusicPlaying is true");
					} else {
						playMusic(mid);
					}
				}  else if (play.equals("next")) {
					int id = intent.getIntExtra("id", 0);
					playMusic(id);
				} else if (play.equals("last")) {
					int id = intent.getIntExtra("id", 0);
					playMusic(id);
				}
			}
		} 
		super.onStart(intent, startId);
		
	}

	 
	@Override
	public void run() { 
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (null != mediaPlayer) {
			try {
				int position = mediaPlayer.getCurrentPosition();
				int total = mediaPlayer.getDuration();
				Intent intent = new Intent("com.example.frmusic.progress");
				intent.putExtra("position", position);
				intent.putExtra("total", total);
				sendBroadcast(intent);
				if (mediaPlayer.isPlaying()) {
					HudProjectApplication.isMusicPlaying = true;
				} else {
					HudProjectApplication.isMusicPlaying = false;
				}
			} catch (Exception e) { 
				mediaPlayer = null;
				mediaPlayer = new MediaPlayer();
				e.printStackTrace();
			}
		} 
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
 
	private void playMusic(int id) {
		try {  
			if (null != mediaPlayer) {
				mediaPlayer.release();
				mediaPlayer = null;
			}
			if (id > lists.size() - 1) {
				mid = lists.size() - 1;
			} else if (id < 0) {
				mid = 0;
			}
			DataMusicClass m = lists.get(mid);
			String url = m.getUrl();
			Uri uri = Uri.parse(url);
//			mediaPlayer = new MediaPlayer();
			mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
			mediaPlayer.reset();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); 
			mediaPlayer.setDataSource(getApplicationContext(), uri);
			mediaPlayer.prepare(); 
			mediaPlayer.start();  
			int position = mediaPlayer.getCurrentPosition();
			int total = mediaPlayer.getDuration();
			Intent intent = new Intent("com.example.frmusic.progress");
			intent.putExtra("position", position);
			intent.putExtra("total", total);
			sendBroadcast(intent);
			HudProjectApplication.isMusicPlaying = true;
			HudProjectApplication.doubanOrMusic = "music"; 
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					if (mp.isPlaying()) {

					} 
					//播放完成后 下一首 
					mp.reset();
					if (MusicFragment.model == 1) {
						mid = (int) (Math.random() * lists.size() + 1);
					} else if (MusicFragment.model == 2) {
						mid = mid + 1;
						if (MusicFragment.musicLsts == mid) {// 到最后一条
							mid = 0;
						}
					} else if (MusicFragment.model == 3) {
						mid = mid;
					}
					if (mid > lists.size() - 1) {
						mid = lists.size() - 1;
					} else if (mid < 0) {
						mid = 0;
					}
					playMusic(mid);
					Intent intent = new Intent(
							"com.example.frmusic.completion");
					sendBroadcast(intent);  
				}
			});
			mediaPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if (null != mediaPlayer) {
						mediaPlayer.release();
						mediaPlayer = null;
					}
					DataMusicClass m = lists.get(mid);
					String url = m.getUrl();
					Uri uri = Uri.parse(url);
					mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
					mediaPlayer.reset();
					mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					try {
						mediaPlayer.setDataSource(getApplicationContext(), uri);
						mediaPlayer.prepare();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					mediaPlayer.start(); 
					return false;
				}
			});

			try {
				Intent hud_music = new Intent(Constants.MUSIC_AUDIOFOCUS_ACTION);
				hud_music.putExtra("hud_music", true);
				hud_music.putExtra("music_name", m.getTitle());
				MusicService.this.sendBroadcast(hud_music);
			} catch (Exception e) { 
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();

		} 
		
	} 
}

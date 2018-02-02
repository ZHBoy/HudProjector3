package com.infisight.hudprojector.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.infisight.hudprojector.MainActivity;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.adapter.MusicAdapter;
import com.infisight.hudprojector.common.HudProjectApplication;
import com.infisight.hudprojector.data.DataMusicClass;
import com.infisight.hudprojector.data.SpeechVoiceData;
import com.infisight.hudprojector.data.VoiceDataClass;
import com.infisight.hudprojector.service.MusicService;
import com.infisight.hudprojector.util.Constants;
import com.infisight.hudprojector.util.MusicList;

/**
 * 项目名称 : FRMusic2
 * 
 * @author 作者 : fengrui
 * @date 创建时间：2015年8月18日 下午3:27:18
 * @version 1.0
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MusicFragment extends Fragment implements OnClickListener {

	SharedPreferences sp = null;
	protected static final String TAG = "MusicFragment";
	// 歌曲列表
	public static List<DataMusicClass> listMusic = new ArrayList<DataMusicClass>();
	// 歌曲列表
	public List<DataMusicClass> listMusicAll = new ArrayList<DataMusicClass>();
	Animation operatingAnim = null;
	// 音乐图片
	// public ImageView im_music_picture;
	// 音乐名
	private TextView tv_music_name;
	// 歌手
	private TextView tv_music_singer;
	// 上一曲
	private ImageView iv_music_last;
	// 暂停和播放
	public static ImageView iv_music_play;
	// 下一曲
	private ImageView iv_music_next;
	private ImageView iv_music_play_model;
	private LinearLayout ll_no_music;
	private LinearLayout ll_music_play;
	// 歌曲项的适配器
	private MusicAdapter adapter;
	private int choosedMusicId = 0;
	private Boolean replaying = false;
	private MyCompletionListner myCompletionListner;
	private MyProgressBroadCastReceiver receiver;
	// 播放模式: 1.随机播放 2.顺序播放 3.单曲循环
	public static int model = 1;
	public static int musicLsts = 0;
	Gson gson = null;
	MainActivity mainActivity = null;
	private boolean isDoubanplaying = false;
	// 注册焦点监听器
	private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = null;
	private AudioManager mAudioMgr;
	// 隐藏按钮
	LinearLayout ll_music_dismiss_view;

	/**
	 * 创造唯一实例
	 * 
	 * @param musicInfo
	 * @return
	 */
	public static MusicFragment newInstance(String musicInfo) {
		MusicFragment fragment = new MusicFragment();
		Bundle args = new Bundle();
		args.putString("musicInfo", musicInfo);
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * 构造函数
	 */
	public MusicFragment() {
	}

	public class MyProgressBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			int position = arg1.getIntExtra("position", 0);
			int total = arg1.getIntExtra("total", 1);
			if (total == 0) {
				total = 1;
			}
			int progress = position * 100 / total;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		sp = getActivity()
				.getSharedPreferences(Constants.FILE_BG_MUSIC_INFO, 0);
		init();
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * 初始化控件
	 */
	private void init() {

		ll_no_music = (LinearLayout) getActivity().findViewById(
				R.id.ll_no_music);
		ll_music_play = (LinearLayout) getActivity().findViewById(
				R.id.ll_music_play);

		ll_no_music = (LinearLayout) getActivity().findViewById(
				R.id.ll_no_music);
		ll_music_play = (LinearLayout) getActivity().findViewById(
				R.id.ll_music_play);

		tv_music_name = (TextView) getActivity().findViewById(
				R.id.tv_music_name);
		tv_music_singer = (TextView) getActivity().findViewById(
				R.id.tv_music_singer);
		iv_music_last = (ImageView) getActivity().findViewById(
				R.id.iv_music_last);
		iv_music_next = (ImageView) getActivity().findViewById(
				R.id.iv_music_next);
		iv_music_play = (ImageView) getActivity().findViewById(
				R.id.iv_music_play);
		iv_music_play_model = (ImageView) getActivity().findViewById(
				R.id.iv_music_play_model);
		ll_music_dismiss_view = (LinearLayout) getActivity().findViewById(
				R.id.ll_music_dismiss_view);
		iv_music_last.setOnClickListener(new MyListener());
		iv_music_next.setOnClickListener(new MyListener());
		iv_music_play.setOnClickListener(new MyListener());
		iv_music_play_model.setOnClickListener(new MyListener());
		operatingAnim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.music_icon_anim);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		// 获取音乐列表
		listMusic = MusicList.getMusicData(getActivity()
				.getApplicationContext());
		if (listMusic == null || listMusic.size() <= 0) {
			ll_no_music.setVisibility(View.VISIBLE);
			ll_music_play.setVisibility(View.GONE);
			return;
		}
		// 电话状态监听
		TelephonyManager telephonyManager = (TelephonyManager) getActivity()
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new MobliePhoneStateListener(),
				PhoneStateListener.LISTEN_CALL_STATE);
		myCompletionListner = new MyCompletionListner();
		IntentFilter filter = new IntentFilter("com.example.frmusic.completion");
		getActivity().registerReceiver(myCompletionListner, filter);

	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i("123", "MusicFragment:onStart");
		MainActivity.svr.setAudioVol(MainActivity.svr.getMaxAlarmCurrentVol(),
				MainActivity.svr.getMaxMusicCurrentVol());// 音量恢复正常
		gson = new Gson();
		// 获取service中发过来的信息
		receiver = new MyProgressBroadCastReceiver();
		IntentFilter filter = new IntentFilter("com.example.frmusic.progress");
		getActivity().registerReceiver(receiver, filter);
		DataMusicClass m = null;
		if (listMusic == null || listMusic.size() <= 0) {
			return;
		}
		choosedMusicId = sp.getInt(Constants.KEY_CURRENTMUSIC, 0);
		if (sp.getString(Constants.KEY_MUSICPLAYING, null) != null) {
			m = gson.fromJson(sp.getString(Constants.KEY_MUSICPLAYING, null),
					DataMusicClass.class); 
			String songname = m.getTitle();
			tv_music_name.setText(songname);
			tv_music_singer.setText(m.getSinger());
			iv_music_play.setImageResource(R.drawable.pause11);
			Intent intent = new Intent(getActivity(), MusicService.class);
			intent.putExtra("play", "playing");
			intent.putExtra("id", choosedMusicId);
			intent.putExtra("listType", gson.toJson(listMusic));
			if (!HudProjectApplication.isDoubanplaying) {
				getActivity().startService(intent);
				HudProjectApplication.isMusicPlaying = true;
				HudProjectApplication.doubanOrMusic = "music";
				replaying = true;
			}

		} else { 
			m = listMusic.get(choosedMusicId);
			String songname = m.getTitle();
			tv_music_name.setText(songname);
			tv_music_singer.setText(m.getSinger());
			iv_music_play.setImageResource(R.drawable.pause11);
			Intent intent = new Intent(getActivity(), MusicService.class);
			intent.putExtra("play", "playing");
			intent.putExtra("id", choosedMusicId);
			intent.putExtra("listType", gson.toJson(listMusic));
			if (!HudProjectApplication.isDoubanplaying) {
				getActivity().startService(intent);
				HudProjectApplication.isMusicPlaying = true;
				HudProjectApplication.doubanOrMusic = "music";
				replaying = true;
			}
		}
		sp.edit().putInt(Constants.KEY_CURRENTMUSIC, choosedMusicId)
				.putString(Constants.KEY_MUSICPLAYING, gson.toJson(m)).commit();// 首次保存播放歌曲
		Log.i("123", "choosedMusicId  " + choosedMusicId);

	}

	/**
	 * 电话状态的监听类
	 * 
	 * @author fengrui
	 * 
	 *         2015年8月10日
	 */
	private class MobliePhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:// 无任何状态时
				Intent intent = new Intent(getActivity(), MusicService.class);
				intent.putExtra("play", "playing");
				intent.putExtra("id", choosedMusicId);
				intent.putExtra("listType", gson.toJson(listMusic));
				if (!HudProjectApplication.isDoubanplaying) {
					getActivity().startService(intent);
					HudProjectApplication.isMusicPlaying = true;
					HudProjectApplication.doubanOrMusic = "music";
					iv_music_play.setImageResource(R.drawable.pause11);
					replaying = true;
				}
				sp.edit().putInt(Constants.KEY_CURRENTMUSIC, choosedMusicId)
						.commit(); 
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 接听电话时

			case TelephonyManager.CALL_STATE_RINGING:// 电话进来时
				Intent intent2 = new Intent(getActivity(), MusicService.class);
				intent2.putExtra("play", "puase");
				intent2.putExtra("listType", gson.toJson(listMusic));
				getActivity().startService(intent2);
				HudProjectApplication.isMusicPlaying = false;
				HudProjectApplication.doubanOrMusic = "music";
				iv_music_play.setImageResource(R.drawable.play11);
				replaying = false;
				break;
			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainActivity = (MainActivity) getActivity();
		return inflater.inflate(R.layout.fragment_music, container, false);
	}

	@Override
	public void onResume() {
		getActivity().registerReceiver(musicPlayReceiver,
				makeNewMsgIntentFilter());
		super.onResume();
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(musicPlayReceiver);
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (myCompletionListner != null) {
			getActivity().unregisterReceiver(myCompletionListner);
		}

		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	BroadcastReceiver musicPlayReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			gson = new Gson();
			final String action = intent.getAction();
			SpeechVoiceData svd;
			VoiceDataClass vdc;
			if ((Constants.HUD_MENU_SHOW_ACTION).equals(action)) {
				if (intent.getIntExtra("keycode", -1) == KeyEvent.KEYCODE_ENTER) {// 遥控器确定键
					// 显示音乐控制按钮条
					if (ll_music_dismiss_view.getVisibility() == View.GONE) {
						ll_music_dismiss_view.setVisibility(View.VISIBLE);
					}
				} else {// 遥控器返回键
					// 隐藏音乐控制按钮条
					if (ll_music_dismiss_view.getVisibility() == View.VISIBLE) {
						ll_music_dismiss_view.setVisibility(View.GONE);
					}
				}
			}
			if (Constants.MAIN_MUSIC_ACTION.equals(action)
					|| Constants.COMMON_UTIL_ACTION.equals(action)) {
				if (listMusic == null || listMusic.size() <= 0) {
					return;
				}
				svd = gson.fromJson(intent.getStringExtra(action),
						SpeechVoiceData.class);
				vdc = gson.fromJson(svd.getValue(), VoiceDataClass.class);
				Intent intent2 = new Intent(getActivity(), MusicService.class);
				intent2.putExtra("listType", gson.toJson(listMusic));

				// 继续播放
				if (vdc.getType().equals(Constants.F_R_MUSIC_CONTINUE_PLAY[0])) {
					intent2.putExtra("play", "playing");
					intent2.putExtra("id", choosedMusicId);
					if (!HudProjectApplication.isDoubanplaying) {
						getActivity().startService(intent2);
						HudProjectApplication.isMusicPlaying = true;
						HudProjectApplication.doubanOrMusic = "music";
						iv_music_play.setImageResource(R.drawable.pause11);
						replaying = true;
					}
					sp.edit()
							.putInt(Constants.KEY_CURRENTMUSIC, choosedMusicId)
							.commit();
					Log.i("123", "choosedMusicId  " + choosedMusicId);
				}
				// 单曲循环
				else if (vdc.getType().equals(
						Constants.F_R_MUSIC_JUST_ONE_PLAY[0])) {
					if (!HudProjectApplication.isDoubanplaying) {
						iv_music_play_model
								.setImageResource(R.drawable.music_play_single_1);
						model = 3;
					}
				}// 随机播放
				else if (vdc.getType().equals(
						Constants.F_R_MUSIC_RANDOM_PLAY[0])) {
					if (!HudProjectApplication.isDoubanplaying) {
						iv_music_play_model
								.setImageResource(R.drawable.music_play_random_1);
						model = 1;
					}
				}
				// 顺序播放
				else if (vdc.getType()
						.equals(Constants.F_R_MUSIC_ORDER_PLAY[0])) {
					if (!HudProjectApplication.isDoubanplaying) {
						iv_music_play_model
								.setImageResource(R.drawable.music_play_order_1);
						model = 2;
					}
				}

				// 上一首
				else if (vdc.getType().equals(
						Constants.F_R_MUSIC_LAST_MUSIC_PLAY[0])) {
					if (!HudProjectApplication.isDoubanplaying) {
						// 前一首
						int choosedMusicId = 0;
						if (model == 1) {
							choosedMusicId = MusicService.mid
									- (int) (Math.random() * listMusic.size() + 1);
						} else if (model == 2) {
							choosedMusicId = MusicService.mid - 1;
						} else if (model == 3) {
							choosedMusicId = MusicService.mid - 1;
						}
						if (choosedMusicId > listMusic.size() - 1) {
							choosedMusicId = 0;
						} else if (choosedMusicId < 0) {
							choosedMusicId = listMusic.size() - 1;
						}
						DataMusicClass m = listMusic.get(choosedMusicId);
						String songname = m.getTitle();
						tv_music_name.setText(songname);
						tv_music_singer.setText(m.getSinger());
						iv_music_play.setImageResource(R.drawable.pause11);
						intent2.putExtra("play", "last");
						intent2.putExtra("id", choosedMusicId);
						if (!HudProjectApplication.isDoubanplaying) {
							getActivity().startService(intent2);
							HudProjectApplication.isMusicPlaying = true;
							HudProjectApplication.doubanOrMusic = "music";
						}
						sp.edit()
								.putInt(Constants.KEY_CURRENTMUSIC,
										choosedMusicId)
								.putString(Constants.KEY_MUSICPLAYING,
										gson.toJson(m)).commit();
						Log.i("123", "choosedMusicId  " + choosedMusicId);
					}
				}
				// 下一首
				else if (vdc.getType().equals(
						Constants.F_R_MUSIC_NEXT_MUSIC_PLAY[0])) {
					if (!HudProjectApplication.isDoubanplaying) {
						// 下一首
						int choosedMusicId = 0;
						if (model == 1) {
							choosedMusicId = MusicService.mid
									+ (int) (Math.random() * listMusic.size() + 1);
						} else if (model == 2) {
							choosedMusicId = MusicService.mid + 1;
						} else if (model == 3) {
							choosedMusicId = MusicService.mid + 1;
						}
						if (choosedMusicId > listMusic.size() - 1) {
							choosedMusicId = 0;
						} else if (choosedMusicId < 0) {
							choosedMusicId = listMusic.size() - 1;
						}
						DataMusicClass m = listMusic.get(choosedMusicId);
						String songname = m.getTitle();
						tv_music_name.setText(songname);
						tv_music_singer.setText(m.getSinger());
						iv_music_play.setImageResource(R.drawable.pause11);
						intent2.putExtra("play", "next");
						intent2.putExtra("id", choosedMusicId);
						if (!HudProjectApplication.isDoubanplaying) {
							getActivity().startService(intent2);
							HudProjectApplication.isMusicPlaying = true;
							HudProjectApplication.doubanOrMusic = "music";
						}
						sp.edit()
								.putInt(Constants.KEY_CURRENTMUSIC,
										choosedMusicId)
								.putString(Constants.KEY_MUSICPLAYING,
										gson.toJson(m)).commit(); 
					}
				}
				// 停止播放
				else if (vdc.getType().equals(Constants.F_R_MUSIC_STOP_PLAY[0])) {
					intent2.putExtra("play", "pause");
					getActivity().startService(intent2);
					if (!HudProjectApplication.isDoubanplaying) {
						HudProjectApplication.isMusicPlaying = false;
						HudProjectApplication.doubanOrMusic = "music";
						iv_music_play.setImageResource(R.drawable.play11);
						replaying = false;
					}
					sp.edit()
							.putInt(Constants.KEY_CURRENTMUSIC, choosedMusicId)
							.commit();
					Log.i("123", "choosedMusicId  " + choosedMusicId);
				}
				// 打开音乐
				else if (vdc.getType().equals(Constants.F_R_MUSIC_ON[0])) { 
					MusicFragment hf = new MusicFragment();
					getFragmentManager().beginTransaction()
							.replace(R.id.info_container, hf).commit();  
				} 
				// 播放音乐
				else if (vdc.getType().equals(
						Constants.F_R_MUSIC_MUSIC_PLAY[0])) {
					intent2.putExtra("play", "playing");
					intent2.putExtra("id", sp.getInt("currentMusic", 0));
					getActivity().startService(intent2);
					HudProjectApplication.isMusicPlaying = true;
					HudProjectApplication.doubanOrMusic = "music"; 
				}
			}
		}
	};

	/**
	 * 广播过滤器
	 * 
	 * @return
	 */
	private static IntentFilter makeNewMsgIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.COMMON_UTIL_ACTION);
		intentFilter.addAction(Constants.MAIN_MUSIC_ACTION);
		intentFilter.addAction(Constants.HUD_MENU_SHOW_ACTION);
		return intentFilter;
	}

	@Override
	public void onClick(View v) {

	}

	private class MyListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v == iv_music_last) {
				// 前一首
				int choosedMusicId = 0;
				if (model == 1) {
					choosedMusicId = (int) (Math.random() * listMusic.size());
				} else if (model == 2) {
					choosedMusicId = MusicService.mid - 1;
				} else if (model == 3) {
					choosedMusicId = MusicService.mid - 1;
				}
				if (choosedMusicId > listMusic.size() - 1) {
					choosedMusicId = 0;
				} else if (choosedMusicId < 0) {
					choosedMusicId = listMusic.size() - 1;
				}
				DataMusicClass m = listMusic.get(choosedMusicId);
				String songname = m.getTitle();
				tv_music_name.setText(songname);
				tv_music_singer.setText(m.getSinger());
				iv_music_play.setImageResource(R.drawable.pause11); 
				Intent intent = new Intent(getActivity(), MusicService.class);
				intent.putExtra("play", "last");
				intent.putExtra("id", choosedMusicId);
				intent.putExtra("listType", gson.toJson(listMusic));
				if (!HudProjectApplication.isDoubanplaying) {
					getActivity().startService(intent);
					HudProjectApplication.isMusicPlaying = true;
					HudProjectApplication.doubanOrMusic = "music";
				}
				sp.edit().putInt(Constants.KEY_CURRENTMUSIC, choosedMusicId)
						.putString(Constants.KEY_MUSICPLAYING, gson.toJson(m))
						.commit();
				Log.i("123", "choosedMusicId  " + choosedMusicId);
			} else if (v == iv_music_play) {
				// 正在播放
				if (HudProjectApplication.isMusicPlaying) {
					Intent intent = new Intent(getActivity(),
							MusicService.class);
					intent.putExtra("play", "pause");
					intent.putExtra("id", choosedMusicId);
					intent.putExtra("listType", gson.toJson(listMusic));
					if (!HudProjectApplication.isDoubanplaying) {
						getActivity().startService(intent);
						HudProjectApplication.isMusicPlaying = false;
						HudProjectApplication.doubanOrMusic = "music";
						iv_music_play.setImageResource(R.drawable.play11);
						replaying = false;
					}
					sp.edit()
							.putInt(Constants.KEY_CURRENTMUSIC, choosedMusicId)
							.commit();
					Log.i("123", "choosedMusicId  " + choosedMusicId);
				} else {
					Intent intent = new Intent(getActivity(),
							MusicService.class);
					intent.putExtra("play", "playing");
					intent.putExtra("id", choosedMusicId);
					intent.putExtra("listType", gson.toJson(listMusic));
					if (!HudProjectApplication.isDoubanplaying) {
						getActivity().startService(intent);
						HudProjectApplication.isMusicPlaying = true;
						HudProjectApplication.doubanOrMusic = "music";
						iv_music_play.setImageResource(R.drawable.pause11);
						replaying = true;
					}
					sp.edit()
							.putInt(Constants.KEY_CURRENTMUSIC, choosedMusicId)
							.commit();
					Log.i("123", "choosedMusicId  " + choosedMusicId);
				}
			} else if (v == iv_music_next) {
				// 下一首
				int choosedMusicId = 0;
				if (model == 1) {
					choosedMusicId = MusicService.mid
							+ (int) (Math.random() * listMusic.size() + 1);
				} else if (model == 2) {
					choosedMusicId = MusicService.mid + 1;
				} else if (model == 3) {
					choosedMusicId = MusicService.mid + 1;
				}
				if (choosedMusicId > listMusic.size() - 1) {
					choosedMusicId = 0;
				} else if (choosedMusicId < 0) {
					choosedMusicId = listMusic.size() - 1;
				}
				DataMusicClass m = listMusic.get(choosedMusicId);
				String songname = m.getTitle();
				tv_music_name.setText(songname);
				tv_music_singer.setText(m.getSinger());
				iv_music_play.setImageResource(R.drawable.pause11);
				Intent intent = new Intent(getActivity(), MusicService.class);
				intent.putExtra("play", "next");
				intent.putExtra("id", choosedMusicId);
				intent.putExtra("listType", gson.toJson(listMusic));
				Log.i("123", "下一首   " + "播放的id" + choosedMusicId + "歌曲名"
						+ songname);
				if (!HudProjectApplication.isDoubanplaying) {
					getActivity().startService(intent);
					HudProjectApplication.isMusicPlaying = true;
					HudProjectApplication.doubanOrMusic = "music";
				}
				sp.edit().putInt(Constants.KEY_CURRENTMUSIC, choosedMusicId)
						.putString(Constants.KEY_MUSICPLAYING, gson.toJson(m))
						.commit();
				Log.i("123", "choosedMusicId  " + choosedMusicId);
			} else if (v == iv_music_play_model) {
				if (model == 1) {
					iv_music_play_model
							.setImageResource(R.drawable.music_play_order_1);
					model = 2;
				} else if (model == 2) {
					iv_music_play_model
							.setImageResource(R.drawable.music_play_single_1);
					model = 3;
				} else if (model == 3) {
					iv_music_play_model
							.setImageResource(R.drawable.music_play_random_1);
					model = 1;
				}
			}
		}

	}

	public class MyCompletionListner extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				DataMusicClass m = listMusic.get(MusicService.mid);
				String songname = m.getTitle();
				tv_music_name.setText(songname);
				tv_music_singer.setText(m.getSinger());
				iv_music_play.setImageResource(R.drawable.pause11);
				sp.edit().putInt(Constants.KEY_CURRENTMUSIC, choosedMusicId)
						.putString(Constants.KEY_MUSICPLAYING, gson.toJson(m))
						.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public String toTime(int time) {
		time /= 1000;
		int minute = time / 60;
		int hour = minute / 60;
		int second = time % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

}

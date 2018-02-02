package com.infisight.hudprojector;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.model.NaviLatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infisight.hudprojector.common.HudProjectApplication;
import com.infisight.hudprojector.data.DataLocationInfo;
import com.infisight.hudprojector.data.DataMoreLevelClass;
import com.infisight.hudprojector.data.HotSpotInfo;
import com.infisight.hudprojector.data.MenuClass;
import com.infisight.hudprojector.data.MsgInfoClass;
import com.infisight.hudprojector.data.NavDataClass;
import com.infisight.hudprojector.data.PhoneInfoDataClass;
import com.infisight.hudprojector.data.PhoneInfoDataListClass;
import com.infisight.hudprojector.data.SpeechVoiceData;
import com.infisight.hudprojector.data.StartNaviInfo;
import com.infisight.hudprojector.data.VoiceDataClass;
import com.infisight.hudprojector.fragment.AllPlanSeeFragment;
import com.infisight.hudprojector.fragment.GNaviFragment;
import com.infisight.hudprojector.fragment.HomePJFragment;
import com.infisight.hudprojector.fragment.HudFragment;
import com.infisight.hudprojector.fragment.MusicFragment;
import com.infisight.hudprojector.fragment.NewMsgFragment;
import com.infisight.hudprojector.fragment.OBDFragment;
import com.infisight.hudprojector.fragment.PhoneFragment;
import com.infisight.hudprojector.fragment.RecorderFragment;
import com.infisight.hudprojector.fragment.SettingFragment;
import com.infisight.hudprojector.hotspot.WifiAdmin;
import com.infisight.hudprojector.hotspot.WifiApAdmin;
import com.infisight.hudprojector.kdxfspeech.SpeechVoiceRecognition;
import com.infisight.hudprojector.offlinemap.OfflineMapFragment;
import com.infisight.hudprojector.player.PicturePlayer;
import com.infisight.hudprojector.player.RecorderPlayer;
import com.infisight.hudprojector.remote.BluetoothLeService;
import com.infisight.hudprojector.service.MusicService;
import com.infisight.hudprojector.service.ProcessMsgService;
import com.infisight.hudprojector.service.TrafficService;
import com.infisight.hudprojector.util.BluetoolsPhoneUtil;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;
import com.infisight.hudprojector.util.RecordUtils;
import com.infisight.hudprojector.util.Utils;
import com.infisight.hudprojector.util.WordDataSet;
import com.infisight.hudprojector.versionupdate.UpdateCallback;
import com.infisight.hudprojector.versionupdate.UpdateManager;
import com.infisight.hudprojector.widget.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 */
@SuppressLint("NewApi")
public class MainActivity extends Activity implements View.OnClickListener {
	public static String hsName;// 热点名
	public static String hsPwd;// 热点密码
	String TAG = "MainActivity";
	// 与路径规划相关的内容
	WifiP2pManager manager;
	Channel channel;
	WifiApAdmin wifihot = null;
	boolean isWifiP2pEnabled = false;
	final IntentFilter intentFilter = new IntentFilter();
	List<MenuClass> lstMC = new ArrayList<MenuClass>();
	LinearLayout ll_menus, main_ll_key;
	RelativeLayout main_ll_top;
	int selectPosition = 0;
	HashMap<Integer, Integer> hmControlsPosition = new HashMap<Integer, Integer>();
	private boolean mIsEngineInitSuccess = false;
	// FrameLayout nav_container;
	FrameLayout info_container;
	int OperFragmentFlag = 2;// 如果在nav_container，则为0，如果在info_container,则为1，如果在菜单项，则为2；
								// 向左减，向右加，最小为0，最大为2
	// 想定popupwindow 相关的数据
	private PopupWindow popupWindow;
	StartNaviInfo sni;
	public static SpeechVoiceRecognition svr = null;
	// TextView main_tv_volume;
	TextView tv_curspeed;
	SharedPreferences sploc, continue_navi;
	String navi_address = null;
	Fragment mFragment = null;
	ImageView iv_remote, iv_obd, iv_hotspot, iv_network;
	SharedPreferences sp, sp_is_phone;// sp_is_phone用于电话在各种情况显示的
	AlertDialog mAlertDialog = null;
	Gson gson = new Gson();
	Intent intentBluetoothLeService = new Intent();
	PhoneInfoDataClass pidc = null;// 拨打电话的联系人信息
	boolean isConnect = true;
	// 检测震动相关对象（加速度传感器）
	private SensorManager sensorManager;
	// 版本更新
	private UpdateManager updateMan;
	// private ProgressDialog updateProgressDialog;
	public static CustomProgressDialog progressDialog;

	private Handler handler = new Handler();
	SharedPreferences locationlistsp;
	ArrayList<String> datalist;
	String msg_content = null;
	Animation mShowAction, mHiddenAction;// 控件显示隐藏的动画
	LinearLayout callLayout, msgLayout;

	String con_navi_str = null;
	AnimationDrawable animationDrawable;
	ImageView iv_speechicon;
	LinearLayout ll_speechcontentcontainer;
	BluetoothManager bluetoothManager;
	BluetoothAdapter mBluetoothAdapter;
	int maxSpeakV, currSpeakV, maxMusicV, currMusicV;// 音量控制
	AudioManager audioManager;
	String dialogType = null;// 用来判断是退出导航还是新的导航的dialog
	RelativeLayout rl_icon_tip;// 左侧提示框的动画

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BluetoolsPhoneUtil.startLedLight();// Bluetooth
		// 版本升级

		// if(Utils.isNetworkAvailable(MainActivity.this)){
		// // 检查是否有更新
		// // 如果有更新提示下载
		// updateMan = new UpdateManager(MainActivity.this, appUpdateCb);
		// updateMan.checkUpdate();
		// }

		datalist = new ArrayList<String>();
		try {
			locationlistsp = MainActivity.this.getSharedPreferences(
					Constants.FILE_LOCATIONLIST, 0);
			String string = locationlistsp.getString(
					Constants.KEY_LOCATIONLIST, "0");
			datalist = gson.fromJson(string,
					new TypeToken<ArrayList<String>>() {
					}.getType());
		} catch (Exception e) {
			datalist = new ArrayList<String>();
		}

		svr = SpeechVoiceRecognition.getInstance(this);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// int height = this.getWindowManager().getDefaultDisplay().getHeight();
		// getSharedPreferences("screen_height", 0).edit()
		// .putInt("screen_height", height).commit();

		// Log.i(TAG, height + "-----");
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 设置不息屏
		InitView();
		sploc = this.getSharedPreferences(Constants.LOCATION_INFO, 0);
		sp = this.getSharedPreferences(Constants.FILE_BG_MUSIC_INFO, 0);
		continue_navi = this.getSharedPreferences(
				Constants.FILE_HUD_CONTIUE_NAVI, 0);
		sp_is_phone = this.getSharedPreferences(Constants.FILE_SP_IS_PHONE, 0);
		progressDialog = CustomProgressDialog.createDialog(MainActivity.this);
		progressDialog.setMessage(MainActivity.this.getResources().getString(
				R.string.delay_search));
		// creatHotSpot(MainActivity.this);

		// 创建悬浮窗口
		// if (continue_navi != null) {
		// Log.d(TAG, "继续导航" + continue_navi.toString());
		// }
		con_navi_str = continue_navi.getString(
				Constants.KEY_CONTIUE_NAVI_ADDRESS, null);
		if (con_navi_str != null) {
			Bundle bundle = new Bundle();
			bundle.putString("con_navi_str", con_navi_str);
			HudFragment hf = new HudFragment();
			hf.setArguments(bundle);
			getFragmentManager().beginTransaction()
					.replace(R.id.info_container, hf).commit();
		}
		Intent intentService = new Intent(MainActivity.this,
				ProcessMsgService.class);

		MainActivity.this.startService(intentService);
		audioManager = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		maxSpeakV = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		maxMusicV = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		bluetoothManager = (BluetoothManager) MainActivity.this
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
			Log.i(TAG, "启动蓝牙服务");
			intentBluetoothLeService = new Intent(MainActivity.this,
					BluetoothLeService.class);
			startService(intentBluetoothLeService);
		}

		Intent intent = new Intent(MainActivity.this, TrafficService.class);
		startService(intent);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// showMsgPopupWindow(null);
		// new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// if (main_ll_key.getVisibility() == View.VISIBLE
		// && !mFragment.toString().contains("HudFragment")) {
		// info_container.getBackground().setAlpha(120);
		// } else {
		// info_container.getBackground().setAlpha(255);
		// }
		// }
		// });
	}

	// 创建一个热点
	public static HotSpotInfo creatHotSpot(Context context) {
		WifiApAdmin wifiAp = new WifiApAdmin(context);
		WifiAdmin wifiA = new WifiAdmin(context) {

			@Override
			public void onNotifyWifiConnected() {

			}

			@Override
			public void onNotifyWifiConnectFailed() {

			}

			@Override
			public void myUnregisterReceiver(BroadcastReceiver receiver) {

			}

			@Override
			public Intent myRegisterReceiver(BroadcastReceiver receiver,
					IntentFilter filter) {
				return null;
			}
		};
		HotSpotInfo hsi = new HotSpotInfo();
		hsName = "HudFengrui";
		hsPwd = "66666666";
		wifiAp.startWifiAp(hsName, hsPwd);
		hsi.setHsName(hsName);
		hsi.setHsPwd(hsPwd);
		return hsi;
	}

	/**
	 * 初始化空间，这个还需要再考虑是不是要直接开起Wifi-Direct功能
	 */
	private void InitView() {
		// iv_rui_state.setImageResource(R.drawable.test_sleeping);
		if (!CommonUtil.isGPSOpen(MainActivity.this)) {
			CommonUtil.openGPS(MainActivity.this);
		}

		main_ll_top = (RelativeLayout) findViewById(R.id.main_ll_top);
		// nav_container = (FrameLayout) findViewById(R.id.nav_container);
		info_container = (FrameLayout) findViewById(R.id.info_container);
		// LoadMenu();
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		// 以下部分用于测试功能
		manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		UpdateOperAction();
		iv_remote = (ImageView) findViewById(R.id.iv_remote);
		iv_obd = (ImageView) findViewById(R.id.iv_obd);
		iv_hotspot = (ImageView) findViewById(R.id.iv_hotspot);
		iv_network = (ImageView) findViewById(R.id.iv_network);
		main_ll_key = (LinearLayout) findViewById(R.id.main_ll_key);

		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) main_ll_key
				.getLayoutParams();
		params.width = this.getWindowManager().getDefaultDisplay().getWidth() / 5;
		main_ll_key.setLayoutParams(params);
		// main_tv_key = (TextView) findViewById(R.id.main_tv_key);

		mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		mShowAction.setDuration(800);
		iv_speechicon = (ImageView) findViewById(R.id.iv_speechicon);
		iv_speechicon.setImageResource(R.drawable.speech_icons);
		mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f);
		mHiddenAction.setDuration(800);
		ll_speechcontentcontainer = (LinearLayout) findViewById(R.id.ll_speechcontentcontainer);
		rl_icon_tip = (RelativeLayout) findViewById(R.id.rl_icon_tip);
	}

	/**
	 * 显示语音输入动画
	 * 
	 * @param isShow
	 */
	private void LoadSpeechIcon(boolean isShow) {
		if (isShow) {
			animationDrawable = (AnimationDrawable) iv_speechicon.getDrawable();
			animationDrawable.start();
		} else {
			animationDrawable = (AnimationDrawable) iv_speechicon.getDrawable();
			animationDrawable.stop();
			;
		}

	}

	/**
	 * 销毁PopWindow
	 */
	public void dismissPopupWindow() {
		// 要做的事情
		if (popupWindow != null && popupWindow.isShowing())
			popupWindow.dismiss();
		popupWindow = null;
		backgroundAlpha(1f);
	}

	// 隐藏标题栏控件
	public void SetLayoutGone() {

		main_ll_top.setVisibility(View.GONE);
	}

	// 显示标题栏控件
	public void SetLayoutVisible() {

		main_ll_top.setVisibility(View.VISIBLE);
	}

	// /**
	// * 加载菜单功能
	// */
	// private void LoadMenu() {
	// String strMenu = getSharedPreferences(Constants.MENU_INFO, 0)
	// .getString(Constants.MENUDATA, "");
	// Gson gson = new Gson();
	// LinearLayout.LayoutParams vlp = new LinearLayout.LayoutParams(
	// LinearLayout.LayoutParams.MATCH_PARENT, 60);
	// vlp.topMargin = 5;
	// if (strMenu.length() > 1) {
	// try {
	// int position = 0;
	// lstMC = gson.fromJson(strMenu,
	// new TypeToken<List<MenuClass>>() {
	// }.getType());
	// // int[] arrMC = new int[lstMC.size()];
	// // 假设是有序的
	// for (MenuClass mc : lstMC) {
	// Button btn = new Button(this);
	// btn.setLayoutParams(vlp);
	// btn.setTextSize(14);
	// btn.setId(mc.getMenuid());
	// btn.setText(mc.getMenuname());
	// btn.setBackgroundDrawable(getResources().getDrawable(
	// R.drawable.menu_bg));
	// // hmDP.put(ID_BUTTON_START, dp);
	// btn.setOnClickListener(new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // 直接在这里显示提示框
	//
	// }
	//
	// });
	// ll_menus.addView(btn);
	// hmControlsPosition.put(position, mc.getMenuid());
	// position++;
	// }
	// } catch (Exception e) {
	// }
	// } else {
	// int position = 0;
	// MenuClass mc1 = new MenuClass();
	// mc1.setMenuid(1);
	// mc1.setOrdernum(1);
	// mc1.setMenuname("HUD");
	// mc1.setMenuicon(R.drawable.icon_alarm_clock);
	// mc1.setFragmentname("");
	// lstMC.add(mc1);
	// MenuClass mc2 = new MenuClass();
	// mc2.setMenuid(2);
	// mc2.setOrdernum(2);
	// mc2.setMenuname("电话");
	// mc2.setMenuicon(R.drawable.icon_alarm_clock);
	// mc2.setFragmentname("");
	// lstMC.add(mc2);
	// MenuClass mc3 = new MenuClass();
	// mc3.setMenuid(3);
	// mc3.setOrdernum(3);
	// mc3.setMenuname("短信");
	// mc3.setMenuicon(R.drawable.icon_alarm_clock);
	// mc3.setFragmentname("");
	// lstMC.add(mc3);
	// MenuClass mc4 = new MenuClass();
	// mc4.setMenuid(4);
	// mc4.setOrdernum(4);
	// mc4.setMenuname("OBD");
	// mc4.setMenuicon(R.drawable.icon_alarm_clock);
	// mc4.setFragmentname("");
	// lstMC.add(mc4);
	// String strMC = gson.toJson(lstMC);
	// getSharedPreferences(Constants.MENU_INFO, 0).edit().putString(
	// Constants.MENUDATA, strMC);
	// // for (MenuClass mc : lstMC) {
	// // Button btn = new Button(this);
	// // btn.setLayoutParams(vlp);
	// // btn.setTextSize(14);
	// // btn.setId(mc.getMenuid());
	// // btn.setText(mc.getMenuname());
	// // btn.setBackgroundDrawable(getResources().getDrawable(
	// // R.drawable.menu_bg));
	// // // hmDP.put(ID_BUTTON_START, dp);
	// // btn.setOnClickListener(new View.OnClickListener() {
	// //
	// // @Override
	// // public void onClick(View v) {
	// //
	// // }
	// // });
	// // ll_menus.addView(btn);
	// // hmControlsPosition.put(position, mc.getMenuid());
	// // position++;
	// // }
	// }
	// }

	@Override
	protected void onResume() {
		super.onResume();
		svr.startSpeaking(Constants.F_C_WHAT_YOU_DO);
		if (!mFragment.toString().contains("HudFragment")) {
			// 初始化应用提示一次左侧菜单i
			String[] keys = WordDataSet.mainKeyShow.get("HomePJFragment");
			if (keys != null && keys.length > 0) {
				main_ll_key.startAnimation(mShowAction);
				main_ll_key.setVisibility(View.VISIBLE);
				// 加载动画
				LoadSpeechIcon(true);
				ll_speechcontentcontainer.removeAllViews();
				for (String key : keys) {
					View convertView = LayoutInflater.from(MainActivity.this)
							.inflate(R.layout.speech_content_item_template,
									null);
					TextView tvTip = (TextView) convertView
							.findViewById(R.id.tv_speech_item);
					tvTip.setText(key);
					ll_speechcontentcontainer.addView(convertView);
				}
			}
		}
		wifihot = new WifiApAdmin(this);
		registerReceiver(mNewMsgReceiver, makeNewMsgIntentFilter());
		if (sensorManager != null) {// 注册监听器
			sensorManager.registerListener(sensorEventListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
			// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
		}
	}

	@Override
	protected void onPause() {
		if (sensorManager != null) {// 取消监听器
			sensorManager.unregisterListener(sensorEventListener);
		}
		super.onPause();
		unregisterReceiver(mNewMsgReceiver);
	}

	/**
	 *  电话 短信 移除哪一个view
	 * @param layout
	 */
	private void handle_remove_phone_info(LinearLayout layout) {
		Intent intentAction = new Intent(Constants.MAIN_KEY_SHOW_ACTION);
		intentAction.putExtra("isShow", false);
		MainActivity.this.sendBroadcast(intentAction);
		ll_speechcontentcontainer.setVisibility(View.VISIBLE);
		rl_icon_tip.setVisibility(View.VISIBLE);
		try {
			if (layout != null) {
				main_ll_key.removeView(layout);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(TAG, "移除layout的时候出现异常了、、、、、、");
		}
	}

	// 新电话打进来的
	Runnable newPhoneRunnable = new Runnable() {

		public void run() {
			if (popupWindow != null && popupWindow.isShowing()) {
				CommonUtil.sendMsg(Constants.MODEL_PHONE_HANG_UP_COM,
						Constants.TELEPHONY_IDLE, MainActivity.this,
						Constants.MODEL_PHONE_COM);
				if (callLayout != null) {
					handle_remove_phone_info(callLayout);
				}

			}
		}

	};

	/**
	 * 显示新电话的提示框
	 */
	private void showCallPopupWindow() {
		// 先移除
		handler.removeCallbacks(newPhoneRunnable);
		svr.controlToWake(false);
		callLayout = (LinearLayout) LayoutInflater.from(MainActivity.this)
				.inflate(R.layout.popupwindow_newcall, null);
		main_ll_key.addView(callLayout);
		rl_icon_tip.setVisibility(View.GONE);

		// 接听
		ImageView pp_call_im_answer = (ImageView) callLayout
				.findViewById(R.id.pp_call_im_answer);
		pp_call_im_answer.setFocusable(true);
		pp_call_im_answer.setFocusableInTouchMode(true);
		pp_call_im_answer.requestFocus();
		pp_call_im_answer.requestFocusFromTouch();
		pp_call_im_answer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CommonUtil.sendMsg(Constants.MODEL_PHONE_ANSWER_COM,
						Constants.TELEPHONY_OFFHOOK, MainActivity.this,
						Constants.MODEL_PHONE_COM);
				handle_remove_phone_info(callLayout);
				if (mFragment.toString().contains("HudFragment")) {
					Intent in = new Intent(Constants.HUD_TITLE_SHOW_ACTION);
					in.putExtra(Constants.handle_info_command,
							Constants.handle_answer);
					in.putExtra(Constants.handle_info_content,
							gson.toJson(pidc));
					sendBroadcast(in);
					return;
				}
				Bundle bundle = new Bundle();
				bundle.putString("call_in_phone", gson.toJson(pidc));
				PhoneFragment hf = new PhoneFragment();
				hf.setArguments(bundle);
				getFragmentManager().beginTransaction()
						.replace(R.id.info_container, hf).commit();

			}
		});

		// 挂断
		ImageView pp_call_im_hang_up = (ImageView) callLayout
				.findViewById(R.id.pp_call_im_hang_up);
		pp_call_im_hang_up.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CommonUtil.sendMsg(Constants.MODEL_PHONE_HANG_UP_COM,
						Constants.TELEPHONY_IDLE, MainActivity.this,
						Constants.MODEL_PHONE_COM);
				handle_remove_phone_info(callLayout);

			}
		});
		if (pidc.getPhoneName() != null) {
			TextView pp_call_tv_username = (TextView) callLayout
					.findViewById(R.id.pp_call_tv_username);
			pp_call_tv_username.setText(pidc.getPhoneName());
		}
		if (pidc.getPhoneNumber() != null) {
			TextView pp_call_tv_phonenumber = (TextView) callLayout
					.findViewById(R.id.pp_call_tv_phonenumber);
			pp_call_tv_phonenumber.setText(pidc.getPhoneNumber());
		}
		// popupWindow = new PopupWindow(MainActivity.this);
		// popupWindow.setWidth(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		// popupWindow.setHeight(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		// popupWindow.setOutsideTouchable(true);
		// popupWindow.setFocusable(true);
		// popupWindow.setContentView(layout);
		// popupWindow.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.road_bg));
		// // popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
		// popupWindow.showAtLocation(findViewById(R.id.info_container),
		// Gravity.CENTER_VERTICAL, 0, 0);// 需要指定Gravity，默认情况是center.
		// backgroundAlpha(0.5f);
		String tel_text = null;
		if (pidc.getPhoneName() != null && !pidc.getPhoneName().equals("未知号码")) {
			tel_text = pidc.getPhoneName() + "来电话了,请及时处理";
		} else if (pidc.getPhoneName().equals("未知号码")
				&& pidc.getPhoneNumber() != null) {
			tel_text = pidc.getPhoneName() + pidc.getPhoneNumber()
					+ "来电话了,请及时处理";
		}
		if (tel_text != null) {
			svr.setAudioVol(svr.getMaxAlarmCurrentVol(),
					svr.getMaxMusicCurrentVol());
			svr.startSpeaking(tel_text);
			Log.i(TAG, "svr.startSpeaking(tel_text)");
		} else {
			return;
		}
		// 自动挂断电话（后期做成用户可调整为，默认接听或者挂断。还有提示时间）
		handler.postDelayed(newPhoneRunnable, 15000);
	}

	// 新短信
	Runnable newMsgRunnable = new Runnable() {

		public void run() {
			if (msgLayout != null) {
				handle_remove_phone_info(msgLayout);
			}
		}
	};

	/**
	 * 显示新消息的提示框
	 */
	private void showMsgPopupWindow(MsgInfoClass milu) {
		svr.controlToWake(false);
		msgLayout = (LinearLayout) LayoutInflater.from(MainActivity.this)
				.inflate(R.layout.popupwindow_newmsg, null);
		ll_speechcontentcontainer.setVisibility(View.GONE);
		rl_icon_tip.setVisibility(View.GONE);
		main_ll_key.addView(msgLayout);

		// if (milu.getMsg_Photo() != null && milu.getMsg_Photo() != "") {
		// CircularImage ci_headphoto = (CircularImage) layout
		// .findViewById(R.id.ci_headphoto);
		// try {
		//
		// } catch (Exception e) {
		// }
		// }
		if (milu.getMsg_Content() != null) {
			msg_content = milu.getMsg_Content();
		}
		// 读取
		TextView tv_read_now = (TextView) msgLayout
				.findViewById(R.id.tv_read_now);
		tv_read_now.setFocusable(true);
		tv_read_now.setFocusableInTouchMode(true);
		tv_read_now.requestFocus();
		tv_read_now.requestFocusFromTouch();
		tv_read_now.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (msg_content != null) {
					svr.startSpeaking(msg_content);
				}
			}
		});
		// 忽略
		TextView tv_ignore_now = (TextView) msgLayout
				.findViewById(R.id.tv_ignore_now);
		tv_ignore_now.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				handle_remove_phone_info(msgLayout);
			}
		});
		if (milu.getMsg_Content() != null) {
			TextView tv_msgcontent = (TextView) msgLayout
					.findViewById(R.id.tv_msgcontent);
			tv_msgcontent.setText(milu.getMsg_Content());
		}
		if (milu.getMsg_DisplayName() != null) {
			TextView tv_username = (TextView) msgLayout
					.findViewById(R.id.tv_username);
			tv_username.setText(milu.getMsg_DisplayName());
		}
		if (milu.getMsg_TelNum() != null) {
			TextView tv_phonenumber = (TextView) msgLayout
					.findViewById(R.id.tv_phonenumber);
			tv_phonenumber.setText(milu.getMsg_TelNum());
		}

		// popupWindow = new PopupWindow(MainActivity.this);
		// popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
		// popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		// popupWindow.setOutsideTouchable(true);
		// popupWindow.setFocusable(true);
		// popupWindow.setContentView(layout);
		// popupWindow.setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.road_bg));
		// // popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
		// popupWindow.showAtLocation(findViewById(R.id.info_container),
		// Gravity.CENTER_VERTICAL, 0, 0);// 需要指定Gravity，默认情况是center.
		// backgroundAlpha(0.5f);
		handler.postDelayed(newMsgRunnable, 12000);
		String msg_test = null;
		if (milu.getMsg_DisplayName() != null) {
			msg_test = "您有新的消息，发送人，" + milu.getMsg_DisplayName() + ",请及时处理";
		} else {
			// msg_test = "未知号码" + milu.getMsg_TelNum() + "，发来短信，请及时处理";
		}
		if (msg_test != null) {
			svr.startSpeaking(msg_test);
		}
	}

	/**
	 * 设置添加屏幕的背景透明度
	 * 
	 * @param bgAlpha
	 */
	public void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		getWindow().setAttributes(lp);
	}

	/*
	 * 确定按钮点击逻辑
	 */
	public void isOkNavi() {
		// 导航去陆家嘴的时候
		svr.setAudioVol(svr.getMaxAlarmCurrentVol(),
				svr.getMaxMusicCurrentVol());// 音量恢复正常

		Log.i(TAG, "确定结束当前导航-------+------------------");
		// 清除保留熄火后可以继续导航的路径信息
		continue_navi.edit().clear().commit();
		Bundle bundle = new Bundle();
		bundle.putString("destination", navi_address);
		GNaviFragment hf = new GNaviFragment();
		hf.setArguments(bundle);
		getFragmentManager().beginTransaction()
				.replace(R.id.info_container, hf).commit();
		CommonUtil.dismissDialogC(mAlertDialog);
		mAlertDialog = null;

	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		}
	}

	public void startNaviHud(StartNaviInfo info) {

		Bundle bundle = new Bundle();
		bundle.putSerializable("navi_point_info", info);
		HudFragment hf = new HudFragment();
		hf.setArguments(bundle);
		getFragmentManager().beginTransaction()
		// .replace(R.id.nav_container, gf)
				.replace(R.id.info_container, hf).commit();
		Log.d(TAG, "跳转到------hudfragment");
	}

	public void onAttachFragment(Fragment fragment) {
		Log.d(TAG, fragment.toString());
		mFragment = fragment;
	};

	// 退出导航和开启新的导航的diolog
	Runnable stopNaviRunnable = new Runnable() {

		public void run() {
			CommonUtil.dismissDialogC(mAlertDialog);
			svr.setAudioVol(svr.getMaxAlarmCurrentVol(),
					svr.getMaxMusicCurrentVol());// 音量恢复正常
		}

	};
	// 退出导航和开启新的导航的diolog
	Runnable naviRunnable = new Runnable() {

		public void run() {
			CommonUtil.dismissDialogC(mAlertDialog);
			svr.setAudioVol(svr.getMaxAlarmCurrentVol(),
					svr.getMaxMusicCurrentVol());// 音量恢复正常
		}

	};
	/**
	 * 广播接收器,需要对控件进行编号，然后按照顺序选择 ，先选择上下，在选择左右 ，比较复杂的手势操作，涉及到数据派发的问题
	 * 判断当前显示内容，在决定向哪里发送数据
	 */
	BroadcastReceiver mNewMsgReceiver = new BroadcastReceiver() {// 如果是地图页面的显示，就实现数据的分发。
		@Override
		public void onReceive(Context context, Intent intent) {
			Gson gson = new Gson();
			final String action = intent.getAction();
			// 蓝牙服务的开启时机放到广播中避免报错
			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)
					|| action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				if (mBluetoothAdapter.isEnabled()) {
					Log.i(TAG, "启动蓝牙服务");
					intentBluetoothLeService = new Intent(MainActivity.this,
							BluetoothLeService.class);
					startService(intentBluetoothLeService);
				}
			}
			if (action.equals(Constants.CONNECTIVITY_CHANGE_ACTION)) {
				svr.setAudioVol(svr.getMaxAlarmCurrentVol(),
						svr.getMaxMusicCurrentVol());// 音量恢复正常
				int connType = CommonUtil.connectedType(MainActivity.this);
				// 没有网的时候
				if (!CommonUtil.isConnect(MainActivity.this)) {
					isConnect = false;
					svr.startSpeaking(Constants.F_C_DISCONNECTED);
					iv_network.setVisibility(View.INVISIBLE);
					// iv_network.setImageResource(R.drawable.icon_network_5_disable);
				}
				// 2g网络时
				else if (connType == 2 || connType == 4 || connType == 7
						|| connType == 11) {
					isConnect = false;
					svr.startSpeaking(Constants.F_C_UNBECOMING_TYPE);
					iv_network.setVisibility(View.INVISIBLE);
				} else {
					iv_network.setVisibility(View.VISIBLE);
					iv_hotspot.setVisibility(View.INVISIBLE);
					// iv_network.setImageResource(R.drawable.icon_network_5);
					// iv_hotspot.setImageResource(R.drawable.icon_wifi_disable);
					if (isConnect == false) {
						isConnect = true;
						svr.controlToWake(true);
					}
				}

				Log.d(TAG, "android.net.conn.CONNECTIVITY_CHANGE");
			}
			// 控制提示词的显示隐藏
			if (action.equals(Constants.MAIN_KEY_SHOW_ACTION)) {
				Log.d(TAG, "MAIN_KEY_SHOW_ACTION:");
				boolean isShow = intent.getBooleanExtra("isShow", false);
				if (isShow == false) {
					if (main_ll_key.getVisibility() == View.VISIBLE) {
						main_ll_key.startAnimation(mHiddenAction);
						LoadSpeechIcon(false);
						main_ll_key.setVisibility(View.GONE);
					}
					return;
				}
				if (isShow == true) {
					// if (!mFragment.toString().contains("HudFragment")) {
					// backgroundAlpha(0.5f);
					// main_ll_key.getBackground().setAlpha(255);
					// }
					main_ll_key.startAnimation(mShowAction);
					main_ll_key.setVisibility(View.VISIBLE);
					LoadSpeechIcon(true);
					String[] keys = null;
					if (mFragment.toString().contains("HomePJFragment")) {
						keys = WordDataSet.mainKeyShow.get("HomePJFragment");
					} else if (mFragment.toString().contains("HudFragment")) {
						keys = WordDataSet.mainKeyShow.get("HudFragment");
					} else if (mFragment.toString().contains("DoubanFragment")) {
						keys = WordDataSet.mainKeyShow.get("DoubanFragment");
					} else if (mFragment.toString().contains("GNaviFragment")) {
						switch (HudProjectApplication.gnavi_page_state) {
						case 0:// 打开导航或者点击导航进入，只有地图的界面
							keys = WordDataSet.mainKeyShow
									.get("GNaviFragment0");
							break;
						case 1:// 进行条目选择的界面
							keys = WordDataSet.mainKeyShow
									.get("GNaviFragment1");
							break;
						case 2:// 路径规划成功，准备导航的界面
							keys = WordDataSet.mainKeyShow
									.get("GNaviFragment2");
							break;

						default:
							break;
						}

					} else if (mFragment.toString().contains("MusicFragment")) {
						keys = WordDataSet.mainKeyShow.get("MusicFragment");
					} else if (mFragment.toString().contains("PhoneFragment")) {
						keys = WordDataSet.mainKeyShow.get("PhoneFragment");
					} else if (mFragment.toString().contains("SettingFragment")) {
						keys = WordDataSet.mainKeyShow.get("SettingFragment");
					} else if (mFragment.toString()
							.contains("RecorderFragment")) {
						keys = WordDataSet.mainKeyShow.get("RecorderFragment");
					} else if (mFragment.toString().contains("TrafficFragment")) {
						keys = WordDataSet.mainKeyShow.get("TrafficFragment");
					}
					ll_speechcontentcontainer.removeAllViews();
					if (keys != null && keys.length > 0) {
						for (String key : keys) {
							View convertView = LayoutInflater
									.from(MainActivity.this)
									.inflate(
											R.layout.speech_content_item_template,
											null);
							TextView tvTip = (TextView) convertView
									.findViewById(R.id.tv_speech_item);
							tvTip.setText(key);
							ll_speechcontentcontainer.addView(convertView);
						}
					}

				}
			}
			if (action.equals("onVolumeChanged")) {
				int volumeValue = intent.getIntExtra("onVolumeChanged", 0);
				int volumedata = volumeValue / 5;
			}

			// 首页导航
			if (action.equals(Constants.MAIN_NAVI_ACTION))// 如果是语音指令，就进入语音指令消息分发界面
			{
				SpeechVoiceData svd = gson.fromJson(
						intent.getStringExtra(Constants.MAIN_NAVI_ACTION),
						SpeechVoiceData.class);
				VoiceDataClass vdc = null;
				if (svd.getValue() != null) {
					// 遥控器
					if (svd.getValue().equals(Constants.CONTROL_MAIN_KEY)) {
						if (continue_navi.getString(
								Constants.KEY_CONTIUE_NAVI_ADDRESS, null) != null) {
							try {
								AMapNavi.getInstance(getApplicationContext())
										.destroy();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							HudFragment hf = new HudFragment();
							getFragmentManager().beginTransaction()
									.replace(R.id.info_container, hf).commit();
							return;
						}
						Bundle bundle = new Bundle();
						bundle.putString("destination", svd.getValue());
						GNaviFragment hf = new GNaviFragment();
						hf.setArguments(bundle);
						getFragmentManager().beginTransaction()
								.replace(R.id.info_container, hf).commit();
					}
					// 语音
					else {
						vdc = gson.fromJson(svd.getValue(),
								VoiceDataClass.class);
						// 收藏当前位置
						if (vdc.getType().equals(
								Constants.F_R_SAVE_MY_LOCATION[0])) {
							DataLocationInfo dlts = new DataLocationInfo();
							String lat = sploc.getString(Constants.LATITUDE,
									null);
							String lon = sploc.getString(Constants.LONGITUDE,
									null);
							String addr = sploc.getString(Constants.ADDR, null);
							String city = sploc.getString(Constants.CITYNAME,
									null);

							if (lat != null && lon != null && addr != null
									&& city != null) {
								NaviLatLng mNaviLatLng = new NaviLatLng(
										Double.parseDouble(lat),
										Double.parseDouble(lon));
								DataLocationInfo dlts2 = gson
										.fromJson(
												getSharedPreferences(
														Constants.FILE_SAVE_LOCATION_INFO,
														0)
														.getString(addr, null),
												DataLocationInfo.class);
								if (dlts2 != null) {
									svr.startSpeaking(Constants.F_C_SAVED_AGO);
									getSharedPreferences(
											Constants.FILE_SAVE_LOCATION_INFO,
											0)
											.edit()
											.putString(
													"地铁站",
													gson.toJson(new DataLocationInfo(
															mNaviLatLng, "地铁站",
															"上海"))).commit();
									return;
								}
								dlts.setAddressString(addr);
								dlts.setCityString(city);
								dlts.setmNaviLatLng(mNaviLatLng);
								// 把收藏位置列表保存起来
								datalist.add(gson.toJson(dlts));
								CommonUtil.sendMsg(
										Constants.MODEL_NAVI_LOCATION_COM,
										gson.toJson(datalist),
										MainActivity.this,
										Constants.MODEL_NAVI_COM);
								SharedPreferences locationlistsp = MainActivity.this
										.getSharedPreferences(
												Constants.FILE_LOCATIONLIST, 0);
								locationlistsp
										.edit()
										.putString(Constants.KEY_LOCATIONLIST,
												gson.toJson(datalist)).commit();

								CommonUtil.sendMsg(
										Constants.MODEL_NAVI_LOCATION_COM,
										gson.toJson(datalist),
										MainActivity.this,
										Constants.MODEL_NAVI_COM);
								Log.i("locationlist", gson.toJson(datalist));
								getSharedPreferences(
										Constants.FILE_SAVE_LOCATION_INFO, 0)
										.edit()
										.putString(addr, gson.toJson(dlts))
										.commit();
								svr.startSpeaking("当前位置：" + addr
										+ "，保存成功,您可以在手机端整理");
							} else {
								svr.startSpeaking("定位失败，有可能是您网络不好，或者位置太偏僻。");
							}

						}

						// 退出导航
						else if (vdc.getType().equals(
								Constants.F_R_STOP_NAVI[0])) {
							dialogType = Constants.F_C_STOP_NAVI;
							// 导航还没开始
							if (continue_navi.getString(
									Constants.KEY_CONTIUE_NAVI_ADDRESS, null) == null) {
								MainActivity.svr
										.startSpeaking(Constants.F_C_NAVI_NOT_ON);
								return;
							}
							CommonUtil.dismissDialogC(mAlertDialog);
							mAlertDialog = CommonUtil.showDialogAlert(
									MainActivity.this, Constants.F_C_STOP_NAVI);

							MainActivity.svr
									.startSpeaking(Constants.F_C_STOP_NAVI);
							handler.removeCallbacks(naviRunnable);
							handler.postDelayed(naviRunnable, 10000);
						}

						else if (!mFragment.toString()
								.contains("GNaviFragment")) {

							Log.d(TAG, vdc.getValue() + ":::::::::::");
							// 导航去哪哪哪
							if (vdc.getType() != null
									&& vdc.getType().equals(
											Constants.F_R_NAV_GOTO[0])) {
								navi_address = vdc.getValue();
								// 两种弹框的情况：1，导航正在进行2，当前处于导航界面
								if (continue_navi.getString(
										Constants.KEY_CONTIUE_NAVI_ADDRESS,
										null) != null
										|| mFragment.toString().contains(
												"HudFragment")) {
									dialogType = Constants.F_C_STOP_CURRENT_NAVI;

									CommonUtil.dismissDialogC(mAlertDialog);
									mAlertDialog = CommonUtil.showDialogAlert(
											MainActivity.this,
											Constants.F_C_STOP_CURRENT_NAVI);
									svr.startSpeaking(Constants.F_C_STOP_CURRENT_NAVI);
									handler.removeCallbacks(naviRunnable);
									handler.postDelayed(naviRunnable, 10000);
								} else {
									Bundle bundle = new Bundle();
									bundle.putString("destination",
											vdc.getValue());
									GNaviFragment hf = new GNaviFragment();
									hf.setArguments(bundle);
									getFragmentManager().beginTransaction()
											.replace(R.id.info_container, hf)
											.commit();
									svr.startSpeaking(vdc.getPromptKey());
								}
							}
							// 打开导航
							else if (vdc.getType() != null
									&& vdc.getType().equals(
											Constants.F_R_OPEN_NAVI[0])) {
								if (continue_navi.getString(
										Constants.KEY_CONTIUE_NAVI_ADDRESS,
										null) != null) {
									// 在hud界面说导航去什么地方时也会调用这里，所以做处理，当检测到是HudFragment时就是不让弹框
									if (mFragment.toString().contains(
											"HudFragment")) {
										Bundle bundle = new Bundle();
										bundle.putString("destination",
												vdc.getValue());
										GNaviFragment hf = new GNaviFragment();
										hf.setArguments(bundle);
										getFragmentManager()
												.beginTransaction()
												.replace(R.id.info_container,
														hf).commit();
										// svr.startSpeaking(vdc.getPromptKey());
										return;
									}
									try {
										AMapNavi.getInstance(
												getApplicationContext())
												.destroy();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									HudFragment hf = new HudFragment();
									getFragmentManager().beginTransaction()
											.replace(R.id.info_container, hf)
											.commit();
								} else {
									Bundle bundle = new Bundle();
									bundle.putString("destination",
											vdc.getValue());
									GNaviFragment hf = new GNaviFragment();
									hf.setArguments(bundle);
									getFragmentManager().beginTransaction()
											.replace(R.id.info_container, hf)
											.commit();
									svr.startSpeaking(vdc.getPromptKey());
								}
							}
						}
					}
				}

			}
			// 需要现在fragment中处理然后交给mainactivity转发的action
			else if (action.equals(Constants.FRAG_TO_MAIN_ACTION)) {
				SpeechVoiceData svd = gson.fromJson(
						intent.getStringExtra(Constants.FRAG_TO_MAIN_ACTION),
						SpeechVoiceData.class);
				Log.d(TAG, svd.getValue() + "----");
				if (svd.getValue() != null) {
					VoiceDataClass vdc = gson.fromJson(svd.getValue(),
							VoiceDataClass.class);
					Log.d(TAG, vdc.getType() + "--333--");
					// 开始导航
					if (vdc.getType() != null
							&& vdc.getType()
									.equals(Constants.F_R_START_NAVI[0])) {
						if (mFragment.toString().contains("GNaviFragment")) {
							if (vdc.getValue() != null) {
								sni = gson.fromJson(vdc.getValue(),
										StartNaviInfo.class);
								Log.i(TAG, "startNaviHud(sni)");
								startNaviHud(sni);
								return;
							}
						}
					}
					// 路线全览
					else if (vdc.getType() != null
							&& vdc.getType().equals(
									Constants.F_R_NAVI_ROUTE_PLAN_All_SEE[0])) {
						if (mFragment.toString().contains("HudFragment")) {
							if (continue_navi.getString(
									Constants.KEY_CONTIUE_NAVI_ADDRESS, null) != null) {
								AllPlanSeeFragment hf = new AllPlanSeeFragment();
								getFragmentManager().beginTransaction()
										.replace(R.id.info_container, hf)
										.commit();
							} else {
								svr.startSpeaking(Constants.F_C_NAVI_NOT_START);
							}
						}
					}
				}

			}

			// 打电话
			else if (action.equals(Constants.MAIN_PHONE_ACTION)) {
				if (!mFragment.toString().contains("PhoneFragment")) {
					SpeechVoiceData svd = gson.fromJson(
							intent.getStringExtra(Constants.MAIN_PHONE_ACTION),
							SpeechVoiceData.class);
					if (svd.getValue() != null) {
						// 遥控器
						if (svd.getValue().equals(Constants.CONTROL_MAIN_KEY)) {
							// 如果和手机端尚未连接
							PhoneFragment hf = new PhoneFragment();
							getFragmentManager().beginTransaction()
									.replace(R.id.info_container, hf).commit();
						}
						// 语音指令
						else {
							VoiceDataClass vdc = gson.fromJson(svd.getValue(),
									VoiceDataClass.class);
							// 当前正在通话
							if (sp_is_phone.getString(Constants.KEY_RING_STATE,
									null) != null) {
								svr.startSpeaking(Constants.F_C_CAN_NOT_PHONE);
								return;
							}
							// 当前是hudFragment
							if (mFragment.toString().contains("HudFragment")) {
								CommonUtil.sendMsg(
										Constants.MODEL_PHONE_NAME_COM,
										vdc.getValue(), MainActivity.this,
										Constants.MODEL_PHONE_COM);
								return;
							}
							Bundle bundle = new Bundle();
							bundle.putString("phoneInfos", vdc.getValue());
							PhoneFragment hf = new PhoneFragment();
							hf.setArguments(bundle);
							getFragmentManager().beginTransaction()
									.replace(R.id.info_container, hf).commit();
						}
					}
				}

			}
			// 打电话model:接听、挂断
			else if (action.equals(Constants.MODEL_PHONE_ACTION)) {
				SpeechVoiceData svd = gson.fromJson(
						intent.getStringExtra(Constants.MODEL_PHONE_ACTION),
						SpeechVoiceData.class);
				VoiceDataClass vdc = null;
				if (svd != null && svd.getValue() != null) {
					vdc = gson.fromJson(svd.getValue(), VoiceDataClass.class);
				}
				// 语音操作
				if (svd != null && svd.getValue() != null) {
					// 语音挂断
					if (vdc.getValue() != null
							&& vdc.getValue().equals(
									Constants.F_R_PHONE_HANG_UP[1])) {
						// 挂断操作
						CommonUtil.sendMsg(Constants.MODEL_PHONE_HANG_UP_COM,
								Constants.TELEPHONY_IDLE, MainActivity.this,
								Constants.MODEL_PHONE_COM);
						// 清理保存的信息
						sp_is_phone.edit().clear().commit();

						// 外部电话打进来，移除电话layout
						if (callLayout != null
								&& callLayout.getVisibility() == View.VISIBLE) {
							handle_remove_phone_info(callLayout);
						}
						// 如果当前是导航中界面，只移除hudFragment顶部条
						if (mFragment.toString().contains("HudFragment")) {
							Intent in = new Intent(
									Constants.HUD_TITLE_SHOW_ACTION);
							in.putExtra(Constants.handle_info_command,
									Constants.handle_hang_up);
							sendBroadcast(in);
							return;
						}
						// 表明不是HudFragment也不是HomePJFragment，则返回首页
						if (mFragment.toString().contains("HomePJFragment")) {
							CommonUtil.retHomeFragment(MainActivity.this);
							return;
						}
					}
					// 语音接听
					else if (vdc.getValue() != null
							&& vdc.getValue().equals(
									Constants.F_R_PHONE_ANSWER[1])) {
						// 接听操作
						CommonUtil.sendMsg(Constants.MODEL_PHONE_ANSWER_COM,
								Constants.TELEPHONY_OFFHOOK, MainActivity.this,
								Constants.MODEL_PHONE_COM);
						// 移除电话layout
						if (callLayout != null
								&& callLayout.getVisibility() == View.VISIBLE) {
							handle_remove_phone_info(callLayout);
						}
						// 保存接听后的联系人信息
						if (pidc.getPhoneName() != null
								&& pidc.getPhoneNumber() != null) {
							Log.i(TAG, pidc.toString() + "---电话的信息");
							sp_is_phone
									.edit()
									.putString(Constants.KEY_RING_STATE,
											gson.toJson(pidc)).commit();
						}
						// 当前是导航中界面，则不跳转
						if (mFragment.toString().contains("HudFragment")) {
							Intent in = new Intent(
									Constants.HUD_TITLE_SHOW_ACTION);
							in.putExtra(Constants.handle_info_command,
									Constants.handle_answer);
							in.putExtra(Constants.handle_info_content,
									gson.toJson(pidc));
							sendBroadcast(in);
							return;
						}
						// 当前不是导航中也不是电话界面，则跳转到电话界面
						if (!mFragment.toString().contains("PhoneFragment")) {
							Bundle bundle = new Bundle();
							bundle.putString("call_in_phone", gson.toJson(pidc));
							PhoneFragment hf = new PhoneFragment();
							hf.setArguments(bundle);
							getFragmentManager().beginTransaction()
									.replace(R.id.info_container, hf).commit();
							return;
						}
					}
					// 读取信息
					else if (vdc.getValue() != null
							&& vdc.getValue().equals(
									Constants.F_R_READ_MSG_INFO[1])) {
						if (msgLayout != null
								&& msgLayout.getVisibility() == View.VISIBLE) {
							if (msg_content != null) {
								svr.startSpeaking(msg_content);
							} else {
								svr.startSpeaking("内容为空");
								handle_remove_phone_info(msgLayout);
							}
						}
						return;

					}
					// 忽略信息
					else if (vdc.getValue() != null
							&& vdc.getValue().equals(
									Constants.F_R_IGNORE_MSG_INFO[1])) {
						if (msgLayout != null
								&& msgLayout.getVisibility() == View.VISIBLE) {
							handle_remove_phone_info(msgLayout);
						}
						return;
					}
				}
				// 手机端信息回传
				if (!mFragment.toString().contains("PhoneFragment")) {
					// 手机端回传信息
					String strExtra = intent
							.getStringExtra(Constants.MSG_PARAM);
					DataMoreLevelClass dmlc = gson.fromJson(strExtra,
							DataMoreLevelClass.class);
					if (strExtra != null && dmlc != null) {
						switch (dmlc.getCommand()) {
						case Constants.MODEL_PHONE_NAME_COM:// 说联系人
							PhoneInfoDataListClass lsts = gson.fromJson(
									dmlc.getContent(),
									PhoneInfoDataListClass.class);
							Log.i(TAG, "MODEL_PHONE_NAME_COM:"
									+ Constants.MODEL_PHONE_NAME_COM);
							if (lsts.getPhoneInfoDataClass() != null
									&& lsts.getPhoneInfoDataClass().size() == 1) {
								PhoneInfoDataClass pidc2 = lsts
										.getPhoneInfoDataClass().get(0);
								Intent in2 = new Intent(
										Constants.HUD_TITLE_SHOW_ACTION);
								in2.putExtra(Constants.handle_info_command,
										Constants.handle_answer);
								in2.putExtra(Constants.handle_info_content,
										gson.toJson(pidc2));
								sendBroadcast(in2);
							} else {
								svr.startSpeaking("没有找到相关联系人");
							}
							break;
						case Constants.MODEL_PHONE_HANG_UP_COM:// 挂断电话
							Log.i(TAG,
									"case Constants.MODEL_PHONE_HANG_UP_COM:// 挂断电话");
							Intent in = new Intent(
									Constants.HUD_TITLE_SHOW_ACTION);
							in.putExtra(Constants.handle_info_command,
									Constants.handle_hang_up);
							sendBroadcast(in);
							handle_remove_phone_info(callLayout);
							sp_is_phone.edit().clear().commit();

							break;
						case Constants.MODEL_PHONE_ANSWER_COM:// 接听电话
							Log.i(TAG, "接听电话");
							handle_remove_phone_info(callLayout);
							Intent in2 = new Intent(
									Constants.HUD_TITLE_SHOW_ACTION);
							in2.putExtra(Constants.handle_info_command,
									Constants.handle_answer);
							in2.putExtra(Constants.handle_info_content,
									gson.toJson(pidc));
							sendBroadcast(in2);
							sp_is_phone
									.edit()
									.putString(Constants.KEY_RING_STATE,
											gson.toJson(pidc)).commit();
							break;
						default:
							break;
						}
					}

				}

			}
			// 打开设置
			else if (action.equals(Constants.MAIN_SETTING_ACTION)) {
				if (!mFragment.toString().contains("SettingFragment")) {
					SpeechVoiceData svd = gson.fromJson(intent
							.getStringExtra(Constants.MAIN_SETTING_ACTION),
							SpeechVoiceData.class);

					VoiceDataClass vdc = gson.fromJson(svd.getValue(),
							VoiceDataClass.class);
					if (!mFragment.toString().contains("SettingFragment")) {
						// 放大音量
						if (vdc.getType().equals(
								Constants.F_R_SETTING_VOICEUP[0])) {
							Toast.makeText(MainActivity.this, "放大音量",
									Toast.LENGTH_SHORT).show();
							// 获得当前的两种音量
							getSystemVoiceLevel();
							if (currSpeakV + 1 <= maxSpeakV) {
								audioManager.adjustStreamVolume(
										AudioManager.STREAM_ALARM,
										AudioManager.ADJUST_LOWER,
										AudioManager.FX_FOCUS_NAVIGATION_UP);
								currSpeakV += 1;
								audioManager.setStreamVolume(
										AudioManager.STREAM_ALARM, currSpeakV,
										0);
							}
							if (currMusicV + 1 <= maxMusicV) {
								audioManager.adjustStreamVolume(
										AudioManager.STREAM_MUSIC,
										AudioManager.ADJUST_LOWER,
										AudioManager.FX_FOCUS_NAVIGATION_UP);
								currMusicV += 1;
								audioManager.setStreamVolume(
										AudioManager.STREAM_MUSIC, currMusicV,
										0);
							}
							saveSystemVoiceLevel();

						}// 缩小音量
						else if (vdc.getType().equals(
								Constants.F_R_SETTING_VOICEDOWN[0])) {
							Toast.makeText(MainActivity.this, "缩小音量",
									Toast.LENGTH_SHORT).show();
							getSystemVoiceLevel();
							if (currSpeakV - 1 >= 0) {
								audioManager.adjustStreamVolume(
										AudioManager.STREAM_ALARM,
										AudioManager.ADJUST_RAISE,
										AudioManager.FX_FOCUS_NAVIGATION_UP);
								currSpeakV -= 1;
								audioManager.setStreamVolume(
										AudioManager.STREAM_ALARM, currSpeakV,
										0);
							}
							if (currMusicV - 1 >= 0) {
								audioManager.adjustStreamVolume(
										AudioManager.STREAM_MUSIC,
										AudioManager.ADJUST_RAISE,
										AudioManager.FX_FOCUS_NAVIGATION_UP);
								currMusicV -= 1;
								audioManager.setStreamVolume(
										AudioManager.STREAM_MUSIC, currMusicV,
										0);
							}
							saveSystemVoiceLevel();
						} else if (vdc.getType().equals(
								Constants.F_R_SETTING[0])) {
							Bundle bundle = new Bundle();
							SettingFragment sf = new SettingFragment();
							sf.setArguments(bundle);
							getFragmentManager().beginTransaction()
									.replace(R.id.info_container, sf).commit();
						}

					}
				}
			}

			// 首页obd显示
			else if (action.equals(Constants.MAIN_OBD_ACTION)) {
				Log.d(TAG, "首页obd显示：" + action);
				if (!mFragment.toString().contains("OBDFragment")) {
					SpeechVoiceData svd = gson.fromJson(
							intent.getStringExtra(Constants.MAIN_OBD_ACTION),
							SpeechVoiceData.class);
					if (svd.getValue() != null) {
						OBDFragment hf = new OBDFragment();
						getFragmentManager().beginTransaction()
								.replace(R.id.info_container, hf).commit();
					}
				}

			}
			// 音乐功能
			else if (action != null
					&& Constants.MAIN_MUSIC_ACTION.equals(action)) {
				SpeechVoiceData svd = gson.fromJson(
						intent.getStringExtra(Constants.MAIN_MUSIC_ACTION),
						SpeechVoiceData.class);
				if (svd.getValue().equals(Constants.CONTROL_MAIN_KEY)) {
					if (!mFragment.toString().contains("MusicFragment")) {

						MusicFragment hf = new MusicFragment();
						getFragmentManager().beginTransaction()
								.replace(R.id.info_container, hf).commit();
						return;
					}
				}
				VoiceDataClass vdc = gson.fromJson(svd.getValue(),
						VoiceDataClass.class);
				Intent intent2 = new Intent(MainActivity.this,
						MusicService.class);
				if (!mFragment.toString().contains("MusicFragment")) {
					// 停止播放
					if (vdc.getType().equals(Constants.F_R_MUSIC_STOP_PLAY[0])) {
						if (!HudProjectApplication.isDoubanplaying) {
							intent2.putExtra("play", "pause");
							MainActivity.this.startService(intent2);
						}
					}
					// 播放音乐
					else if (vdc.getType().equals(
							Constants.F_R_MUSIC_MUSIC_PLAY[0])) {
						intent2.putExtra("play", "playing");
						intent2.putExtra("id", sp.getInt("currentMusic", 0));
						MainActivity.this.startService(intent2);
						HudProjectApplication.isMusicPlaying = true;
						HudProjectApplication.doubanOrMusic = "music";
					}
					// 继续播放
					else if (vdc.getType().equals(
							Constants.F_R_MUSIC_CONTINUE_PLAY[0])) {
						if (!HudProjectApplication.isDoubanplaying) {
							intent2.putExtra("play", "playing");
							intent2.putExtra("id", sp.getInt("currentMusic", 0));
							MainActivity.this.startService(intent2);
						}
					}
					// 下一首
					else if (vdc.getType().equals(
							Constants.F_R_MUSIC_NEXT_MUSIC_PLAY[0])) {
						if (!HudProjectApplication.isDoubanplaying
								&& HudProjectApplication.isMusicPlaying) {
							intent2.putExtra("play", "next");
							intent2.putExtra("id",
									sp.getInt("currentMusic", 0) + 1);
							MainActivity.this.startService(intent2);
						}
					}
					// 上一首
					else if (vdc.getType().equals(
							Constants.F_R_MUSIC_LAST_MUSIC_PLAY[0])) {
						int index = sp.getInt("currentMusic", 0) - 1;
						if (index < 0) {
							index = 0;
						}
						if (!HudProjectApplication.isDoubanplaying) {
							intent2.putExtra("play", "last");
							intent2.putExtra("id", index);
							MainActivity.this.startService(intent2);
						}
					}
					// 打开音乐
					else if (vdc.getType().equals(Constants.F_R_MUSIC_ON[0])) {
						if (!mFragment.toString().contains("MusicFragment")) {
							MusicFragment hf = new MusicFragment();
							getFragmentManager().beginTransaction()
									.replace(R.id.info_container, hf).commit();
						}
					}
				}
			}
			// 打开行车记录仪
			else if (action.equals(Constants.MAIN_GORECORDER_ACTION)) {
				// if (!mFragment.toString().contains("RecorderFragment")) {
				// SpeechVoiceData svd = gson.fromJson(intent
				// .getStringExtra(Constants.MAIN_GORECORDER_ACTION),
				// SpeechVoiceData.class);
				// if (svd.getValue() != null) {
				// Intent service = new Intent(MainActivity.this,
				// BackgroundRecorder.class);
				// stopService(service);
				// RecorderFragment ra = new RecorderFragment();
				// getFragmentManager().beginTransaction()
				// .replace(R.id.info_container, ra).commit();
				// }
				// }

			}
			// 公共指令
			else if (action.equals(Constants.COMMON_UTIL_ACTION)) {
				SpeechVoiceData svd2 = gson.fromJson(
						intent.getStringExtra(Constants.COMMON_UTIL_ACTION),
						SpeechVoiceData.class);
				if (svd2 == null || svd2.getValue() == null) {// 遥控是第二种判断
					return;
				}
				VoiceDataClass vdc = gson.fromJson(svd2.getValue(),
						VoiceDataClass.class);
				// 确定结束当前导航
				if (vdc.getType().equals(Constants.F_R_IS_TRUE_STOP_NAVI[0])) {
					if (mAlertDialog != null && mAlertDialog.isShowing()) {

						if (dialogType == null) {
							return;
						}
						// 表明是开启新导航的dialog
						else if (dialogType
								.equals(Constants.F_C_STOP_CURRENT_NAVI)) {
							isOkNavi();
						}
						// 表明是退出导航dialog
						else if (dialogType.equals(Constants.F_C_STOP_NAVI)) {
							CommonUtil.dismissDialogC(mAlertDialog);
							AMapNavi.getInstance(getApplicationContext())
									.destroy();
							continue_navi.edit().clear().commit();
							// 如果是hudFragment就返回首页
							if (mFragment.toString().contains("HudFragment")) {
								HomePJFragment hf = new HomePJFragment();
								getFragmentManager().beginTransaction()
										.replace(R.id.info_container, hf)
										.commit();
							}
						}
						MainActivity.svr.setAudioVol(
								MainActivity.svr.getMaxAlarmCurrentVol(),
								MainActivity.svr.getMaxMusicCurrentVol());// 音量恢复正常
						return;
					}

				}
				// 取消结束当前导航，则进入hud界面
				else if (vdc.getType().equals(
						Constants.F_R_NOT_IS_TRUE_STOP_NAVI[0])) {
					// 只有是导航去陆家嘴的时候
					svr.setAudioVol(svr.getMaxAlarmCurrentVol(),
							svr.getMaxMusicCurrentVol());// 音量恢复正常
					CommonUtil.dismissDialogC(mAlertDialog);
				}
				// 返回首页
				else if (vdc.getType().equals(Constants.F_R_RETURN_HOME[0])) {
					if (!mFragment.toString().contains("HomePJFragment")) {
						HomePJFragment hf = new HomePJFragment();
						getFragmentManager().beginTransaction()
								.replace(R.id.info_container, hf).commit();
						// if (CommonUtil.isConnect(MainActivity.this)) {
						// svr.controlToWake(false);
						// }

					}
				}
			}
			// 界面图标的显示与隐藏
			else if (action.equals(Constants.MAIN_ICON_STATE_ACTION)) {
				// int iconType = intent.getIntExtra("iconType", 0);
				// int iconState = intent.getIntExtra("iconState", 0);
				// switch (iconType) {
				// case Constants.MAIN_ICON_RUI_STATE:
				// switch (iconState) {
				// // 0:打招呼 1:听 2：思考 3：识别不出来 4：休息
				// case 0:
				// iv_rui_state.setImageResource(R.drawable.test_hello);
				// rui_isSleep = false;
				// break;
				// case 1:
				// iv_rui_state
				// .setImageResource(R.drawable.test_listening);
				// rui_isSleep = false;
				// break;
				// case 2:
				// iv_rui_state.setImageResource(R.drawable.test_thinking);
				// rui_isSleep = false;
				// break;
				// case 3:
				// iv_rui_state.setImageResource(R.drawable.test_thinking);
				// rui_isSleep = false;
				// break;
				// case 4:
				// iv_rui_state.setImageResource(R.drawable.test_sleeping);
				// rui_isSleep = true;
				// hmVolumeImage.put(0, R.drawable.volume_1);
				// break;
				// default:
				// break;
				// }
				// break;
				// default:
				// break;
				// }
			}
			// 退出应用
			else if (action.equals(Constants.MAIN_EXIT_PROJECT_ACTION)) {
				MainActivity.this.finish();
				int id = android.os.Process.myPid();
				android.os.Process.killProcess(id);
			}
			// 每分钟更新一次
			// else if (action.equals(Intent.ACTION_TIME_TICK)) {
			// // main_tv_time.setText(CommonUtil.timeShow());
			// }
			// 电话提醒
			else if (action.equals(Constants.TELEPHONY_NEW)) {
				String strMsg = intent.getStringExtra(Constants.MSG_PARAM);
				Log.i(TAG, "有电话进来了" + strMsg);
				// 先将内容解析出来，再放到对应的数据中，再弹框处理
				pidc = new PhoneInfoDataClass();
				try {
					pidc = gson.fromJson(strMsg, PhoneInfoDataClass.class);
					showCallPopupWindow();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (action.equals(Constants.SMS_NEW)) {// 短信来了
				MsgInfoClass milu = new MsgInfoClass();
				String strMsg = intent.getStringExtra(Constants.MSG_PARAM);
				Log.i(TAG, "有短信进来了" + strMsg);
				try {
					milu = gson.fromJson(strMsg, MsgInfoClass.class);
					// 先将内容解析出来，再放到对应的数据中，再弹框处理
					showMsgPopupWindow(milu);
				} catch (Exception e) {
					Log.w(TAG, "接收短信出现异常" + e);
				}

			} else if (Constants.MSG_NEW.equals(action)) {
				String strCmd = intent.getStringExtra(Constants.MSG_CMD);
				String strMsg = intent.getStringExtra(Constants.MSG_PARAM);
				Log.d(TAG, "strCmd:" + strCmd + "=+="
						+ Constants.MODEL_PHONE_ACTION);
				Log.d(TAG, "strMsg:" + strMsg);
				// 呼出电话信息回传
				if (Constants.MODEL_PHONE_ACTION.equals(strCmd)) {
					Log.i(TAG, TAG + "呼出电话信息回传");
					BroadCastNewMsg(Constants.MODEL_PHONE_ACTION, strMsg);
				}
			}
			// 直接进行新的导航，包括手机端导航、附近的地点导航
			else if (Constants.NEW_NAVI_PLAN_ACTION.equals(action)) {
				if (intent.getStringExtra(Constants.NAIV_TO_SEARCH_POI_LAT) != null) {
					NaviLatLng mNaviLatlan = gson.fromJson(intent
							.getStringExtra(Constants.NAIV_TO_SEARCH_POI_LAT),
							NaviLatLng.class);
					if (mNaviLatlan != null) {
						sni = new StartNaviInfo();
						sni.setNavi_end(mNaviLatlan);
						sni.setRoute_plan(4);
						// 清除保留熄火后可以继续导航的路径信息
						continue_navi.edit().clear().commit();
						HudProjectApplication.isStopNavi = true;
						startNaviHud(sni);
					}
					return;
				}
				String strMsg = intent.getStringExtra(Constants.MSG_PARAM);
				NavDataClass ndc = gson.fromJson(strMsg, NavDataClass.class);
				sni = new StartNaviInfo();
				sni.setNavi_end(new NaviLatLng(
						ndc.getPnEnd().getLatLng().latitude, ndc.getPnEnd()
								.getLatLng().longitude));
				Log.i(TAG, TAG + ndc.getPnEnd().getLatLng().latitude
						+ "========" + ndc.getPnEnd().getLatLng().longitude);
				sni.setRoute_plan(ndc.getDriveMode());

				// 清除保留熄火后可以继续导航的路径信息
				continue_navi.edit().clear().commit();
				startNaviHud(sni);

			} else if (Constants.ACTION_GATT_DISCONNECTED.equals(action)) {
				iv_remote.setVisibility(View.INVISIBLE);
				// iv_remote.setImageResource(R.drawable.icon_controlpad_disable);
			} else if (Constants.ACTION_GATT_CONNECTED.equals(action)) {
				iv_remote.setVisibility(View.VISIBLE);
				// iv_remote.setImageResource(R.drawable.icon_controlpad);
			} else if (Constants.ACTION_HOTSPOT_AVAILABLE.equals(action)) {
				iv_hotspot.setVisibility(View.VISIBLE);
				// iv_hotspot.setImageResource(R.drawable.icon_wifi);
			} else if (Constants.ACTION_HOTSPOT_UNAVAILABLE.equals(action)) {
				iv_hotspot.setVisibility(View.INVISIBLE);
				// iv_network.setVisibility(View.INVISIBLE);
				// iv_hotspot.setImageResource(R.drawable.icon_wifi_disable);
				// iv_network.setImageResource(R.drawable.icon_network_5_disable);
			}
			// 这里是dialog点击效果在这里加逻辑
			else if (Constants.CERTAIN_ACTION.equals(action)) {
				String control = intent.getStringExtra("control");
				if (control.equals("certain")) {
					if (dialogType == null) {
						return;
					}
					// 表明是开启新导航的dialog
					else if (dialogType.equals(Constants.F_C_STOP_CURRENT_NAVI)) {
						isOkNavi();
					}
					// 表明是退出导航dialog
					else if (dialogType.equals(Constants.F_C_STOP_NAVI)) {
						CommonUtil.dismissDialogC(mAlertDialog);
						AMapNavi.getInstance(getApplicationContext()).destroy();
						continue_navi.edit().clear().commit();
						// 如果是hudFragment就返回首页
						if (mFragment.toString().contains("HudFragment")) {
							HomePJFragment hf = new HomePJFragment();
							getFragmentManager().beginTransaction()
									.replace(R.id.info_container, hf).commit();
						}
					}
					MainActivity.svr.setAudioVol(
							MainActivity.svr.getMaxAlarmCurrentVol(),
							MainActivity.svr.getMaxMusicCurrentVol());// 音量恢复正常
					return;
				} else if (control.equals("cancel")) {
					svr.setAudioVol(svr.getMaxAlarmCurrentVol(),
							svr.getMaxMusicCurrentVol());// 音量恢复正常
					CommonUtil.dismissDialogC(mAlertDialog);
					return;
				}
			}
		}

		/**
		 * 保存音量数据
		 */
		private void saveSystemVoiceLevel() {
			Utils.put(MainActivity.this, Constants.FILE_VOICEMANAGER,
					Constants.KEY_MANAGER_VOICE_SPEAK, currSpeakV);
			Utils.put(MainActivity.this, Constants.FILE_VOICEMANAGER,
					Constants.KEY_MANAGER_VOICE_MUSIC, currMusicV);
		}

		/**
		 * 获得音量数据
		 */
		private void getSystemVoiceLevel() {
			try {
				currSpeakV = (Integer) Utils
						.get(MainActivity.this,
								Constants.FILE_VOICEMANAGER,
								Constants.KEY_MANAGER_VOICE_SPEAK,
								audioManager
										.getStreamVolume(AudioManager.STREAM_ALARM));
				currMusicV = (Integer) Utils
						.get(MainActivity.this,
								Constants.FILE_VOICEMANAGER,
								Constants.KEY_MANAGER_VOICE_MUSIC,
								audioManager
										.getStreamVolume(AudioManager.STREAM_MUSIC));
			} catch (Exception e) {
				e.printStackTrace();
				currSpeakV = audioManager
						.getStreamVolume(AudioManager.STREAM_ALARM);
				currMusicV = audioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);
			}

		}
	};

	/**
	 * 广播过滤器
	 */
	private static IntentFilter makeNewMsgIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.MSG_NEW);
		intentFilter.addAction(Constants.SMS_NEW);
		intentFilter.addAction(Constants.TELEPHONY_NEW);
		intentFilter.addAction(Constants.FRAG_TO_MAIN_ACTION);
		intentFilter.addAction(Constants.MAIN_NAVI_ACTION);
		intentFilter.addAction(Constants.MAIN_MUSIC_ACTION);// 音乐列表(即首页音乐)
		intentFilter.addAction(Constants.MAIN_GORECORDER_ACTION);// 行车记录仪
		// intentFilter.addAction(Constants.MAIN_MUSIC_PLAY_COM);// 音乐播放
		intentFilter.addAction(Constants.MAIN_PHONE_ACTION);// 电话主页
		intentFilter.addAction(Constants.MODEL_PHONE_ACTION);// 具体操作接听、挂断
		intentFilter.addAction(Constants.MAIN_OBD_ACTION);
		intentFilter.addAction(Constants.MAIN_EXIT_PROJECT_ACTION);
		intentFilter.addAction(Intent.ACTION_TIME_TICK);// 每分钟发送一次
		intentFilter.addAction(Constants.MAIN_ICON_STATE_ACTION);// 用于控制界面图标的显示、隐藏
		intentFilter.addAction(Constants.CONNECTIVITY_CHANGE_ACTION);// 用于控制界面图标的显示、隐藏
		intentFilter.addAction(Constants.ON_VOLUME_CHANGED_ACTION);//
		intentFilter.addAction(Constants.COMMON_UTIL_ACTION);// 公共的action
		intentFilter.addAction(Constants.MAIN_SETTING_ACTION);// 设置的action
		intentFilter.addAction(Constants.ACTION_GATT_DISCONNECTED);// 遥控器断开连接
		intentFilter.addAction(Constants.ACTION_GATT_CONNECTED);// 遥控器连接成功
		intentFilter.addAction(Constants.NEW_NAVI_PLAN_ACTION);// 接收手机端的导航信息
		intentFilter.addAction(Constants.CERTAIN_ACTION);// dialog弹出框点击操作action
		intentFilter.addAction(Constants.ACTION_HOTSPOT_AVAILABLE);// 热点可用
		intentFilter.addAction(Constants.ACTION_HOTSPOT_UNAVAILABLE);// 热点不可用
		intentFilter.addAction(Constants.MAIN_DOUBAN_ACTION);// 豆瓣FM
		intentFilter.addAction(Constants.MAIN_KEY_SHOW_ACTION);// 提示词的显示隐藏
		intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);// 蓝牙状态改变
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		return intentFilter;
	}

	/**
	 * 依据命令更新按钮的背景
	 * 
	 */
	// private void UpdateButtonState(boolean isClear) {
	// for (int i : hmControlsPosition.keySet()) {
	// findViewById(hmControlsPosition.get(i)).setBackgroundDrawable(
	// getResources().getDrawable(R.drawable.menu_normal));
	// }
	// if (!isClear)
	// findViewById(hmControlsPosition.get(selectPosition))
	// .setBackgroundDrawable(
	// getResources().getDrawable(R.drawable.menu_pressed));
	// }
	// /**
	// * 依据命令更新按钮的背景
	// *
	// */
	// private void UpdateButtonState(boolean isClear) {
	// for (int i : hmControlsPosition.keySet()) {
	// findViewById(hmControlsPosition.get(i)).setBackgroundDrawable(
	// getResources().getDrawable(R.drawable.menu_normal));
	// }
	// if (!isClear)
	// findViewById(hmControlsPosition.get(selectPosition))
	// .setBackgroundDrawable(
	// getResources().getDrawable(R.drawable.menu_pressed));
	// }

	/**
	 * 依据相应的命令更新操作
	 */
	private void UpdateOperAction() {
		switch (selectPosition) {
		case 0:
			// 判断，如果Nav存在，就直接显示Nav，如果是Hud存在，就直接显示HUD。
			// nav_container.setVisibility(View.VISIBLE);
			// info_container.setVisibility(View.GONE);
			// 再说吧
			// Fragment f = getFragmentManager().findFragmentById(
			// R.id.nav_container);// 向上手势
			// if (!(f instanceof HudFragment)) {
			// HudFragment nf = new HudFragment();//
			// 如果没有的话，就加入，如果有的话，就不用加入了。因为这个是唯一的？再考虑一下怎么做
			// getFragmentManager().beginTransaction()
			// .replace(R.id.nav_container, nf).commit();
			// }
			// nav_container.setVisibility(View.VISIBLE);
			// info_container.setVisibility(View.GONE);
			// AllPlanSeeFragment HomePJFragment
			Fragment f = getFragmentManager().findFragmentById(
					R.id.info_container);
			if (!(f instanceof HomePJFragment)) {
				OfflineMapFragment nf = new OfflineMapFragment();//
				// 如果没有的话，就加入，如果有的话，就不用加入了。因为这个是唯一的？再考虑一下怎么做
				getFragmentManager().beginTransaction()
						.replace(R.id.info_container, nf).commit();
			}
			break;
		case 1:
			break;
		case 2:
			// nav_container.setVisibility(View.GONE);
			// info_container.setVisibility(View.VISIBLE);
			NewMsgFragment nmf = new NewMsgFragment();
			getFragmentManager().beginTransaction()
					.replace(R.id.info_container, nmf).commit();
			break;
		}
	}

	// 初始化导航引擎
	private void InitBNData() {
		// 初始化导航引擎
		// BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
		// mNaviEngineInitListener, ACCESS_KEY, mKeyVerifyListener);
		// BaiduNaviManager.getInstance().initEngine(MainActivity.this,
		// getSdcardDir(), mNaviEngineInitListener,
		// new LBSAuthManagerListener() {
		// @Override
		// public void onAuthResult(int status, String msg) {
		// String str = null;
		// if (0 == status) {
		// str = "key校验成功!";
		// } else {
		// str = "key校验失败, " + msg;
		// }
		// Toast.makeText(MainActivity.this, str,
		// Toast.LENGTH_LONG).show();
		// }
		// });
	}

	/**
	 * 发送新消息到本应用程序中
	 * 
	 * @param directionMsg
	 * @param param
	 */
	private void BroadCastNewMsg(String directionMsg, String param) {
		Intent intentActionNav = new Intent(directionMsg);
		intentActionNav.putExtra(Constants.MSG_PARAM, param);
		MainActivity.this.sendBroadcast(intentActionNav);
	}

	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 全程只有一个语音对象，在工程销毁时，猜销毁对象。
		if (svr != null) {
			svr.destroy();
			svr.destroy();
		}
		sp_is_phone.edit().clear().commit();

		// 退出时释放连接
		SettingFragment.hsIsOpen = false;
		Utils.put(MainActivity.this, Constants.FILE_HS_INFO,
				Constants.KEY_HSINFO, SettingFragment.hsIsOpen);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Intent intent = new Intent(Constants.HUD_MENU_SHOW_ACTION);
		if (keyCode == KeyEvent.KEYCODE_ENTER
				&& (mFragment.toString().contains("HudFragment")
						|| mFragment.toString().contains("MusicFragment")
						|| mFragment.toString().contains("DoubanFragment") || mFragment
						.toString().contains("RecorderFragment"))) {
			Log.i("123", "首页发送确定键广播");
			intent.putExtra("keycode", KeyEvent.KEYCODE_ENTER);
			sendBroadcast(intent);
			return true;
		}
		// if (keyCode == KeyEvent.KEYCODE_DPAD_UP &&
		// mFragment.toString().contains("DoubanFragment")) {
		// Log.i("123", "首页发送上键广播");
		// intent.putExtra("keycode", KeyEvent.KEYCODE_DPAD_UP);
		// sendBroadcast(intent);
		// return true;
		// }
		// if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN &&
		// mFragment.toString().contains("DoubanFragment")) {
		// Log.i("123", "首页发送下键广播");
		// intent.putExtra("keycode", KeyEvent.KEYCODE_DPAD_DOWN);
		// sendBroadcast(intent);
		// return true;
		// }

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (main_ll_key.getVisibility() == View.VISIBLE) {
				svr.comeToWake();
				main_ll_key.startAnimation(mHiddenAction);
				LoadSpeechIcon(false);
				main_ll_key.setVisibility(View.GONE);
				return true;
			}

			if (mFragment.toString().contains("HudFragment")) {
				// 地图版底部条
				int ll_hud_dismiss_view = getFragmentManager()
						.findFragmentById(R.id.info_container).getView()
						.findViewById(R.id.ll_hud_dismiss_view).getVisibility();
				// 简版底部条
				int ll_hud_dismiss_view_simple = getFragmentManager()
						.findFragmentById(R.id.info_container).getView()
						.findViewById(R.id.ll_hud_dismiss_view_simple)
						.getVisibility();
				// 简版地图
				int ll_hud_simple_view = getFragmentManager()
						.findFragmentById(R.id.info_container).getView()
						.findViewById(R.id.ll_hud_simple_view).getVisibility();
				if (ll_hud_dismiss_view == View.VISIBLE
						|| ll_hud_dismiss_view_simple == View.VISIBLE
						|| ll_hud_simple_view == View.VISIBLE) {
					intent.putExtra("keycode", KeyEvent.KEYCODE_BACK);
					sendBroadcast(intent);
					return true;
				}

			}

			if (mFragment.toString().contains("MusicFragment")) {
				int ll_music_dismiss_view = getFragmentManager()
						.findFragmentById(R.id.info_container).getView()
						.findViewById(R.id.ll_music_dismiss_view)
						.getVisibility();
				if (ll_music_dismiss_view == View.VISIBLE) {
					intent.putExtra("keycode", KeyEvent.KEYCODE_BACK);
					sendBroadcast(intent);
					return true;
				}

			}
			if (mFragment.toString().contains("RecorderFragment")) {
				int ll_hud_record_view = getFragmentManager()
						.findFragmentById(R.id.info_container).getView()
						.findViewById(R.id.ll_hud_record_view).getVisibility();
				if (ll_hud_record_view == View.VISIBLE) {
					intent.putExtra("keycode", KeyEvent.KEYCODE_BACK);
					sendBroadcast(intent);
					return true;
				}
			}
			if (mFragment.toString().contains("DoubanFragment")) {
				// 底部隐藏栏
				int ll_douban_dismiss_view = getFragmentManager()
						.findFragmentById(R.id.info_container).getView()
						.findViewById(R.id.ll_douban_dismiss_view)
						.getVisibility();
				if (ll_douban_dismiss_view == View.VISIBLE) {
					intent.putExtra("keycode", KeyEvent.KEYCODE_BACK);
					sendBroadcast(intent);
					return true;
				}

			}

			// 先判断是否是Home，如果是，就不用返回为Home了
			Fragment f = getFragmentManager().findFragmentById(
					R.id.info_container);
			if (f instanceof RecorderPlayer || f instanceof PicturePlayer) {
				getFragmentManager().popBackStack();
				return true;
			}
			if (!(f instanceof HomePJFragment)) {
				HomePJFragment nf = new HomePJFragment();//
				// 如果没有的话，就加入，如果有的话，就不用加入了。因为这个是唯一的？再考虑一下怎么做
				getFragmentManager().beginTransaction()
						.replace(R.id.info_container, nf).commit();
			}

			return true;
		}

		return false;
	}

	/**
	 * 加速度传感器监听
	 */
	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			float[] values = event.values;
			float x = values[0]; // x轴方向的重力加速度，向右为正
			float y = values[1]; // y轴方向的重力加速度，向前为正
			float z = values[2]; // z轴方向的重力加速度，向上为正
			// Log.i("Sensor", "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y +
			// "；z轴方向的重力加速度" + z);
			int medumValue = 20;
			Fragment f = getFragmentManager().findFragmentById(
					R.id.info_container);
			if (Math.abs(x) > medumValue || Math.abs(y) > medumValue
					|| Math.abs(z) > medumValue) {
				if (!(f instanceof RecorderFragment)) {
					RecordUtils.spaceNotEnoughDeleteTempFile(MainActivity.this);
					RecorderFragment rf = new RecorderFragment();
					getFragmentManager().beginTransaction()
							.replace(R.id.info_container, rf).commit();
				}
			}
		}

	};

	/**
	 * 收藏当前地点
	 */
	public void save_current_adrress() {
		DataLocationInfo dlts = new DataLocationInfo();
		String lat = sploc.getString(Constants.LATITUDE, null);
		String lon = sploc.getString(Constants.LONGITUDE, null);
		String addr = sploc.getString(Constants.ADDR, null);
		String city = sploc.getString(Constants.CITYNAME, null);

		if (lat != null && lon != null && addr != null && city != null) {
			NaviLatLng mNaviLatLng = new NaviLatLng(Double.parseDouble(lat),
					Double.parseDouble(lon));
			DataLocationInfo dlts2 = gson.fromJson(
					getSharedPreferences(Constants.FILE_SAVE_LOCATION_INFO, 0)
							.getString(addr, null), DataLocationInfo.class);
			if (dlts2 != null) {
				svr.startSpeaking(Constants.F_C_SAVED_AGO);
				getSharedPreferences(Constants.FILE_SAVE_LOCATION_INFO, 0)
						.edit()
						.putString(
								"地铁站",
								gson.toJson(new DataLocationInfo(mNaviLatLng,
										"地铁站", "上海"))).commit();
				return;
			}
			dlts.setAddressString(addr);
			dlts.setCityString(city);
			dlts.setmNaviLatLng(mNaviLatLng);

			getSharedPreferences(Constants.FILE_SAVE_LOCATION_INFO, 0).edit()
					.putString(addr, gson.toJson(dlts)).commit();
			svr.startSpeaking("当前位置：" + addr + "，保存成功，您可以在设置中查看");
		} else {
			svr.startSpeaking("定位失败，有可能是您网络不好，或者位置太偏僻。");
		}
	}

	// 版本更新
	// 自动更新回调函数
	UpdateCallback appUpdateCb = new UpdateCallback() {

		public void downloadProgressChanged(int progress) {
			// if (updateProgressDialog != null
			// && updateProgressDialog.isShowing()) {
			// updateProgressDialog.setProgress(progress);
			// }
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog
						.createDialog(MainActivity.this);
				progressDialog.setMessage("正在更新客户端...");
			}
			progressDialog.show();
		}

		public void downloadCompleted(Boolean sucess, CharSequence errorMsg) {
			// if (updateProgressDialog != null
			// && updateProgressDialog.isShowing()) {
			// updateProgressDialog.dismiss();
			// }
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			if (sucess) {
				Log.i("UpdateManager", "update");
				updateMan.update();
			} else {
				// DialogHelper.Confirm(MainActivity.this,
				// R.string.dialog_error_title,
				// R.string.dialog_downfailed_msg,
				// R.string.dialog_downfailed_btnnext,
				// new DialogInterface.OnClickListener() {
				//
				// public void onClick(DialogInterface dialog,
				// int which) {
				// updateMan.downloadPackage();
				//
				// }
				// }, R.string.dialog_downfailed_cancel, null);
			}
		}

		public void downloadCanceled() {

		}

		public void checkUpdateCompleted(Boolean hasUpdate,
				CharSequence updateInfo) {
			if (hasUpdate) {
				// DialogHelper.Confirm(
				// MainActivity.this,
				// getText(R.string.dialog_update_title),
				// getText(R.string.dialog_update_msg).toString()
				// CustomProgressDialog.java + updateInfo
				// + getText(R.string.dialog_update_msg2)
				// .toString(),
				// getText(R.string.dialog_update_btnupdate),
				// new DialogInterface.OnClickListener() {
				//
				// public void onClick(DialogInterface dialog,
				// int which) {
				// updateProgressDialog = new ProgressDialog(
				// MainActivity.this);
				// updateProgressDialog
				// .setMessage(getText(R.string.dialog_downloading_msg));
				// updateProgressDialog.setIndeterminate(false);
				// updateProgressDialog
				// .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				// updateProgressDialog.setMax(100);
				// updateProgressDialog.setProgress(0);
				// updateProgressDialog.show();

				updateMan.downloadPackage();
				// }
				// }, getText(R.string.dialog_update_btnnext), null);
			}

		}
	};

	/**
	 * 检索手机发来的信息自动连接wifi
	 */
	// public void connectWifi(){
	// Log.i(TAG, wifiAdapter.listWifi.size() + "");
	// Log.i(TAG, getwifiInfo + "");
	// for (int i = 0; i < wifiAdapter.listWifi.size(); i++) {
	// if (getwifiInfo.keySet().contains(
	// wifiAdapter.listWifi.get(i).getSSID())) {
	// String SSID = wifiAdapter.listWifi.get(i).getSSID();
	// String pwd = getwifiInfo.get(SSID);
	// boolean addNetwork = wifiAdmin.addNetwork(wifiAdmin
	// .createWifiInfo(SSID, pwd, wifiAdmin.TYPE_WPA));
	// if (!addNetwork) {
	// Toast.makeText(this, "wifi连接失败",
	// Toast.LENGTH_SHORT).show();
	// continue;
	// }
	// break;
	// }
	// }
	// }
}

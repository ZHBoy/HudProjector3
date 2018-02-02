package com.infisight.hudprojector.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infisight.hudprojector.MainActivity;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.data.HotSpotInfo;
import com.infisight.hudprojector.data.ScanWifiClass;
import com.infisight.hudprojector.data.SpeechVoiceData;
import com.infisight.hudprojector.data.VoiceDataClass;
import com.infisight.hudprojector.hotspot.WifiAdapter;
import com.infisight.hudprojector.hotspot.WifiAdmin;
import com.infisight.hudprojector.hotspot.WifiApAdmin;
import com.infisight.hudprojector.remote.BluetoothLeService;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;
import com.infisight.hudprojector.util.Utils;
import com.infisight.hudprojector.versionupdate.UpdateUtils;
import com.infisight.hudprojector.widget.CustomProgressDialog;

@SuppressLint("NewApi")
public class SettingFragment extends Fragment implements OnClickListener {
	private static String TAG = "SettingFragment";
	RelativeLayout rl_managefavorite, rl_managewifi, rl_managehotspot,
			rl_remotesconnect, rl_traffic, rl_managevoice;
	LinearLayout ll_managehotspot_content;
	TextView tv_nohotspot, tv_remotesconnectname, tv_voicevolume_value,
			tv_musicvolume_value;
	TextView hs_name, hs_pwd, tv_wifiinfor, tv_versioname;
	HashMap<String, String> getwifiInfo = new HashMap<String, String>();// 储存从手机发来的wifi信息（SSID和密码）
	CustomProgressDialog progressDialog;
	// SpeechVoiceRecognition svr = null;
	Gson gson = null;
	SharedPreferences sp_wifiInfo;
	SharedPreferences prefs = null;
	PopupWindow popupWindow;// 弹出Wifi连接的界面
	private final Timer timer = new Timer();
	private DismissPopupWindowTask task;
	ListView lv_wifi, lv_remote;
	WifiAdapter wifiAdapter;
	List<ScanResult> lstScanResult = new ArrayList<ScanResult>();
	WifiAdmin wifiAdmin;
	WifiApAdmin wifiApAdmin;
	public static boolean hsIsOpen = false;

	boolean mScanning;
	boolean msgFromUser = false;
	long SCAN_PERIOD = 10000;// 一次扫描时间设置
	LeDeviceListAdapter mLeDeviceListAdapter;
	BluetoothAdapter mBluetoothAdapter;
	int REQUEST_ENABLE_BT = 1;
	Intent intentBluetoothLeService = new Intent();

	AudioManager audioManager;
	int maxSpeakV, currSpeakV, maxMusicV, currMusicV;
	SeekBar speakingVoice, musicVoice;
	String hsname;// 热点名称
	String hspwd;// 热点 密码
	String pwd;// wifi密码
	String SSID;// 当前连接的wifi名字
	WifiManager mWifiManager;
	SharedPreferences sp_wifi;// 储存wifi名字，密码的SP

	public SettingFragment newInstance(String settinginfo) {
		SettingFragment fragment = new SettingFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public SettingFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_setting, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		gson = new Gson();
		InitView();
		InitClickListener();
		initDate();
		if (Utils.isWifiEnabled(getActivity())) {
			SSID = Utils.getWifiSSID(getActivity());
			tv_wifiinfor.setText("已连接：" + SSID);
		} else {
			tv_wifiinfor.setText("未连接");
		}
	}

	@Override
	public void onStop() {
		Utils.put(getActivity(), Constants.FILE_VOICEMANAGER,
				Constants.KEY_MANAGER_VOICE_SPEAK, currSpeakV);
		Utils.put(getActivity(), Constants.FILE_VOICEMANAGER,
				Constants.KEY_MANAGER_VOICE_MUSIC, currMusicV);
		super.onStop();
	}

	private void initDate() {
		getwifiInfo = getwifiInformation();
		audioManager = (AudioManager) getActivity().getSystemService(
				Context.AUDIO_SERVICE);
		maxSpeakV = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
		maxMusicV = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		try {
			currSpeakV = (Integer) Utils.get(getActivity(),
					Constants.FILE_VOICEMANAGER,
					Constants.KEY_MANAGER_VOICE_SPEAK,
					audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
			currMusicV = (Integer) Utils.get(getActivity(),
					Constants.FILE_VOICEMANAGER,
					Constants.KEY_MANAGER_VOICE_MUSIC,
					audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
		} catch (Exception e) {
			currSpeakV = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
			currMusicV = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		}

		mWifiManager = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);
		Message mes = new Message();
		mes.what = 2;
		wifiHandler.sendMessageDelayed(mes, 2300);
	}


	private void InitView() {
		// 初始化蓝牙部分，检测蓝牙是否支持
		if (!getActivity().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(getActivity(), "你的设备不支持BLE功能", Toast.LENGTH_SHORT)
					.show();
			// finish();
		}
		// 获取Adapeter
		final BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
				.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// 检测是否获取到蓝牙Adapter
		if (mBluetoothAdapter == null) {
			Toast.makeText(getActivity(), "获取蓝牙功能失败", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		tv_wifiinfor = (TextView) getActivity().findViewById(R.id.tv_wifiinfor);
		tv_wifiinfor.setText("未连接");
		tv_versioname = (TextView) getActivity().findViewById(
				R.id.tv_versioname);
		String verName = UpdateUtils.getVerName(getActivity());
		tv_versioname.setText(verName);
		rl_managefavorite = (RelativeLayout) getActivity().findViewById(
				R.id.rl_managefavorite); // 收藏管理的功能
		rl_managewifi = (RelativeLayout) getActivity().findViewById(
				R.id.rl_managewifi);// WIFI管理的功能
		rl_managehotspot = (RelativeLayout) getActivity().findViewById(
				R.id.rl_managehotspot);// 热点管理的功能
		rl_remotesconnect = (RelativeLayout) getActivity().findViewById(
				R.id.rl_remotesconnect);
		rl_traffic = (RelativeLayout) getActivity().findViewById(
				R.id.rl_traffic);
		rl_managevoice = (RelativeLayout) getActivity().findViewById(
				R.id.rl_managevoice);
		ll_managehotspot_content = (LinearLayout) getActivity().findViewById(
				R.id.ll_managehotspot_content);
		tv_nohotspot = (TextView) getActivity().findViewById(R.id.tv_nohotspot);
		tv_nohotspot.setVisibility(View.GONE);
		tv_remotesconnectname = (TextView) getActivity().findViewById(
				R.id.tv_remotesconnectname);
		tv_remotesconnectname.setText("未连接");
		tv_voicevolume_value = (TextView) getActivity().findViewById(
				R.id.tv_voicevolume_value);
		tv_musicvolume_value = (TextView) getActivity().findViewById(
				R.id.tv_musicvolume_value);
		ll_managehotspot_content.setVisibility(View.VISIBLE);
		hs_name = (TextView) getActivity().findViewById(R.id.hs_name);
		hs_pwd = (TextView) getActivity().findViewById(R.id.hs_pwd);
		try {
			hsIsOpen = (Boolean) Utils.get(getActivity(),
					Constants.FILE_HS_INFO, Constants.KEY_HSINFO, false);
		} catch (Exception e) {
			hsIsOpen = false;
			return;
		}
		try {
			sp_wifi = getActivity().getSharedPreferences(
					Constants.FILE_PASSWORD, Activity.MODE_PRIVATE);
			hsname = sp_wifi.getString(Constants.KEY_WIFINAME, "未找到");
			hspwd = sp_wifi.getString(Constants.KEY_WIFIPASSWORD, "未找到");
		} catch (Exception e) {
			hsname = "Exception";
			hspwd = "Exception";
		}
		hs_name.setText("用户名:" + hsname);
		hs_pwd.setText("密码:" + hspwd);
		if (hsIsOpen) {
			tv_nohotspot.setVisibility(View.GONE);
			ll_managehotspot_content.setVisibility(View.VISIBLE);
		} else {
			tv_nohotspot.setVisibility(View.VISIBLE);
			ll_managehotspot_content.setVisibility(View.GONE);
		}

		wifiApAdmin = new WifiApAdmin(getActivity());
		wifiAdapter = new WifiAdapter(getActivity());
		wifiAdmin = new WifiAdmin(getActivity()) {
			@Override
			public Intent myRegisterReceiver(BroadcastReceiver receiver,
					IntentFilter filter) {
				Log.d(TAG, "myRegisterReceiver");
				getActivity().registerReceiver(receiver, filter);
				return null;
			}

			@Override
			public void myUnregisterReceiver(BroadcastReceiver receiver) {
				getActivity().unregisterReceiver(receiver);
			}

			@Override
			public void onNotifyWifiConnected() {

			}

			@Override
			public void onNotifyWifiConnectFailed() {

			}
		};
		// svr = SpeechVoiceRecognition.getInstance(getActivity());
	}

	private void InitClickListener() {
		rl_managefavorite.setOnClickListener(this);
		rl_managewifi.setOnClickListener(this);
		rl_managehotspot.setOnClickListener(this);
		rl_remotesconnect.setOnClickListener(this);
		rl_traffic.setOnClickListener(this);
		rl_managevoice.setOnClickListener(this);
	}

	@Override
	public void onPause() {
		getActivity().unregisterReceiver(settingReceiver);
		getActivity().unregisterReceiver(mWifiReceiver);
		getActivity().unregisterReceiver(NetworkConnectChangedReceiver);
		super.onPause();
		// scanLeDevice(false);
	}

	@Override
	public void onResume() {
		getActivity().registerReceiver(settingReceiver, makeIntentFilter());
		getActivity().registerReceiver(mWifiReceiver, makeIntentFilter());
		getActivity().registerReceiver(NetworkConnectChangedReceiver,
				makeIntentFilter());
		super.onResume();
		getData();
	}

	/**
	 * 读取音量等数据
	 */
	private void getData() {
		String voicevolume_value = currSpeakV + "/" + maxSpeakV;
		String musicvolume_value = currMusicV + "/" + maxMusicV;
		tv_voicevolume_value.setText(voicevolume_value);
		tv_musicvolume_value.setText(musicvolume_value);
		boolean isRemoteConnect = prefs.getBoolean(
				Constants.REMOTE_CONNECT_STATUS, false);
		if (isRemoteConnect) {
			tv_remotesconnectname.setText("已连接");
		} else {
			tv_remotesconnectname.setText("未连接");
		}
	}

	BroadcastReceiver settingReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			final String action = intent.getAction();
			SpeechVoiceData svd;
			VoiceDataClass vdc;
			if (Constants.MAIN_SETTING_ACTION.equals(action)
					|| Constants.COMMON_UTIL_ACTION.equals(action)) {
				svd = gson.fromJson(intent.getStringExtra(action),
						SpeechVoiceData.class);
				vdc = gson.fromJson(svd.getValue(), VoiceDataClass.class);
				// 连接wifi
				if (vdc.getType().equals(Constants.F_R_SETTING_WIFI[0])) {
					if (Utils.isWifiEnabled(getActivity())) {
						if (mWifiManager.isWifiEnabled()) {
							mWifiManager.setWifiEnabled(false);
						}
					} else {
						if (CommonUtil.isWifiApEnabled(mWifiManager)
								|| hsIsOpen == true) {
							Toast.makeText(getActivity(), "请先关闭热点再连接wifi",
									Toast.LENGTH_SHORT).show();
							return;
						}
						if (!mWifiManager.isWifiEnabled()) {
							mWifiManager.setWifiEnabled(true);
							showProgressDialog();
						} else {
							wifiAdapter.listWifi.clear();
							showWifiListPopupWindow();
						}
					}
				}
				// 打开热点

				else if (vdc.getType()
						.equals(Constants.F_R_SETTING_HOTPOINT[0])) {
					// 打开热点
					Log.i("SettingFragment", hsIsOpen + "");
					if (hsIsOpen == false) {
						HotSpotInfo creatHotSpot = MainActivity
								.creatHotSpot(getActivity());
						try {
							sp_wifi = getActivity().getSharedPreferences(
									Constants.FILE_PASSWORD,
									Activity.MODE_PRIVATE);
							hsname = creatHotSpot.getHsName();
							hspwd = creatHotSpot.getHsPwd();
							Editor editor = sp_wifi.edit();
							editor.putString(Constants.KEY_WIFINAME, hsname);
							editor.putString(Constants.KEY_WIFIPASSWORD, hspwd);
							editor.commit();
						} catch (Exception e) {
							hsname = "HudpQYL";
							hspwd = "66666666";
						}
						hs_name.setText("用户名:" + hsname);
						hs_pwd.setText("密码:" + hspwd);
						tv_nohotspot.setVisibility(View.GONE);
						ll_managehotspot_content.setVisibility(View.VISIBLE);
						hsIsOpen = true;
						Utils.put(getActivity(), Constants.FILE_HS_INFO,
								Constants.KEY_HSINFO, hsIsOpen);
						Toast.makeText(getActivity(), "打开热点",
								Toast.LENGTH_SHORT).show();
					}
					// 关闭热点（打开wifi）
					else {
						// 用打开wifi的方法来关闭热点不太妥当，在有些手机会出现权限提示对话框（HUD上无法用点触来选择），so替换为仅关闭热点
						WifiApAdmin.closeWifiAp(mWifiManager);
						tv_nohotspot.setVisibility(View.VISIBLE);
						ll_managehotspot_content.setVisibility(View.GONE);
						hsIsOpen = false;
						Utils.put(getActivity(), Constants.FILE_HS_INFO,
								Constants.KEY_HSINFO, hsIsOpen);
						Toast.makeText(getActivity(), "关闭热点",
								Toast.LENGTH_SHORT).show();
					}
				}
				// 连接遥控器
				else if (vdc.getType().equals(Constants.F_R_SETTING_REMOTE[0])) {
					try {
						if (!mBluetoothAdapter.isEnabled()) {
							// Intent enableBtIntent = new Intent(
							// BluetoothAdapter.ACTION_REQUEST_ENABLE);
							// startActivityForResult(enableBtIntent,
							// REQUEST_ENABLE_BT);
							mBluetoothAdapter.enable();
						}
					} catch (Exception e) {
						Toast.makeText(getActivity(), "本机没有找到蓝牙硬件或驱动！",
								Toast.LENGTH_SHORT).show();
					}
					showRemotesListPopupWindow();
				}
				// 流量监控
				else if (vdc.getType().equals(Constants.F_R_SETTING_TRAFFIC[0])) {
					TrafficFragment tf = new TrafficFragment();
					getFragmentManager().beginTransaction()
							.replace(R.id.info_container, tf).commit();
				}
				// 音量管理
				else if (vdc.getType().equals(Constants.F_R_SETTING_VOICE[0])) {
					if (popupWindow == null || !popupWindow.isShowing()) {
						showVoiceManagePopupWindow();
					}
				}
				// 放大音量
				else if (vdc.getType().equals(Constants.F_R_SETTING_VOICEUP[0])) {
					if (popupWindow == null || !popupWindow.isShowing()) {
						showVoiceManagePopupWindow();
					}
					if (currSpeakV + 1 <= maxSpeakV) {
//						audioManager.adjustStreamVolume(
//								AudioManager.STREAM_ALARM,
//								AudioManager.ADJUST_LOWER,
//								AudioManager.FX_FOCUS_NAVIGATION_UP);
						currSpeakV += 1;
						audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
								currSpeakV, 0);
						speakingVoice.setProgress(currSpeakV);
					}
					if (currMusicV + 1 <= maxMusicV) {
//						audioManager.adjustStreamVolume(
//								AudioManager.STREAM_MUSIC,
//								AudioManager.ADJUST_LOWER,
//								AudioManager.FX_FOCUS_NAVIGATION_UP);
						currMusicV += 1;
						audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
								currMusicV, 0);
						musicVoice.setProgress(currMusicV);
					}
				}
				// 缩小音量
				else if (vdc.getType().equals(
						Constants.F_R_SETTING_VOICEDOWN[0])) {
					if (popupWindow == null || !popupWindow.isShowing()) {
						showVoiceManagePopupWindow();
					}
					if (currSpeakV - 1 >= 0) {
//						audioManager.adjustStreamVolume(
//								AudioManager.STREAM_ALARM,
//								AudioManager.ADJUST_RAISE,
//								AudioManager.FX_FOCUS_NAVIGATION_UP);
						currSpeakV -= 1;
						audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
								currSpeakV, 0);
						speakingVoice.setProgress(currSpeakV);
					}
					if (currMusicV - 1 >= 0) {
//						audioManager.adjustStreamVolume(
//								AudioManager.STREAM_MUSIC,
//								AudioManager.ADJUST_RAISE,
//								AudioManager.FX_FOCUS_NAVIGATION_UP);
						currMusicV -= 1;
						audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
								currMusicV, 0);
						musicVoice.setProgress(currMusicV);
					}
				}
			} else if (Constants.ACTION_GATT_DISCONNECTED.equals(action)) {
				getData();
			} else if (Constants.ACTION_GATT_CONNECTED.equals(action)) {
				getData();
			}
		};

	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 判断，如果当前没有连接成功，就重新连接遥控器；否则，不再连接遥控器
	 */
	private void reconnectRemote() {
		SharedPreferences spObdInfo = getActivity().getSharedPreferences(
				Constants.REMOTE_DEVICE_INFO, 0);
		String obdName = spObdInfo.getString(Constants.REMOTE_CUR_NAME, "");
		String obdAddr = spObdInfo.getString(Constants.REMOTE_CURR_ADDR, "");
		Log.d(TAG, "obdName:" + obdName);
		if (obdName.equals("")) {// 未连接，需要进行搜索和连接
			if (mLeDeviceListAdapter != null)
				mLeDeviceListAdapter.clear();
			Log.d(TAG, "scanLeDevice:scanLeDevice(true)");
			scanLeDevice(true);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		resultCode = Activity.RESULT_OK;
		if (requestCode == 1 && resultCode == Activity.RESULT_CANCELED) {
			Log.i(TAG, "CANCELED");
			return;
		} else if (requestCode == REQUEST_ENABLE_BT) {
			Log.i(TAG, "down");
			if (mLeDeviceListAdapter.getCount() == 0) {
				Toast.makeText(getActivity(), "没有找到遥控器", Toast.LENGTH_SHORT)
						.show();
				return;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 查找设备
	 * 
	 * @param enable
	 */
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, SCAN_PERIOD);
			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
	}

	/**
	 * 没有找到设备消息处理
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (mLeDeviceListAdapter.getCount() == 0) {
					Toast.makeText(getActivity(), "没有找到OBD设备!",
							Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	};

	/**
	 * 连接BLE设备
	 * 
	 * @param device
	 */
	private void connectBleDevice(BluetoothDevice device) {
		SharedPreferences spOBDDevice = getActivity().getSharedPreferences(
				Constants.REMOTE_DEVICE_INFO, 0);
		spOBDDevice.edit()
				.putString(Constants.REMOTE_CURR_ADDR, device.getAddress())
				.putString(Constants.REMOTE_CUR_NAME, device.getName())
				.commit();
		intentBluetoothLeService.setClass(getActivity(),
				BluetoothLeService.class);
		if (mScanning) {
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mScanning = false;
		}
		getActivity().startService(intentBluetoothLeService);
		Log.i("DeviceOnClick", device.getAddress());
	}

	private IntentFilter makeIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.MAIN_SETTING_ACTION);
		intentFilter.addAction(Constants.MAIN_NAVI_ACTION);
		intentFilter.addAction(Constants.MAIN_PHONE_ACTION);
		intentFilter.addAction(Constants.MAIN_OBD_ACTION);
		intentFilter.addAction(Constants.MAIN_MUSIC_ACTION);
		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		intentFilter.addAction(Constants.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(Constants.ACTION_GATT_CONNECTED);
		return intentFilter;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.rl_managefavorite:

			break;
		case R.id.rl_managewifi:
			WifiApAdmin.closeWifiAp(mWifiManager);
			// hs_info.setVisibility(View.GONE);
			hsIsOpen = false;
			ll_managehotspot_content.setVisibility(View.GONE);
			tv_nohotspot.setVisibility(View.VISIBLE);
			Utils.put(getActivity(), Constants.FILE_HS_INFO,
					Constants.KEY_HSINFO, hsIsOpen);
			// Toast.makeText(getActivity(), "关闭热点",
			// Toast.LENGTH_SHORT).show();
			if (Utils.isWifiEnabled(getActivity())) {
				if (mWifiManager.isWifiEnabled()) {
					mWifiManager.setWifiEnabled(false);
				}
			} else {
				if (CommonUtil.isWifiApEnabled(mWifiManager)) {
					Toast.makeText(getActivity(), "请先关闭热点再连接wifi",
							Toast.LENGTH_SHORT).show();
					return;
				}
				msgFromUser = true;
				if (!mWifiManager.isWifiEnabled()) {
					mWifiManager.setWifiEnabled(true);
					showProgressDialog();
				} else {
					wifiAdapter.listWifi.clear();
					showWifiListPopupWindow();
				}
			}
			break;
		case R.id.rl_managehotspot:
			// 打开热点
			if (hsIsOpen == false) {
				HotSpotInfo creatHotSpot = MainActivity
						.creatHotSpot(getActivity());
				try {
					sp_wifi = getActivity().getSharedPreferences(
							Constants.FILE_PASSWORD, Activity.MODE_PRIVATE);
					hsname = creatHotSpot.getHsName();
					hspwd = creatHotSpot.getHsPwd();
					Editor editor = sp_wifi.edit();
					editor.putString(Constants.KEY_WIFINAME, hsname);
					editor.putString(Constants.KEY_WIFIPASSWORD, hspwd);
					editor.commit();
				} catch (Exception e) {
					hsname = "Exception";
					hspwd = "Exception";
				}
				hs_name.setText("用户名:" + hsname);
				hs_pwd.setText("密码:" + hspwd);
				ll_managehotspot_content.setVisibility(View.VISIBLE);
				tv_nohotspot.setVisibility(View.GONE);
				hsIsOpen = true;
				Utils.put(getActivity(), Constants.FILE_HS_INFO,
						Constants.KEY_HSINFO, hsIsOpen);
				Toast.makeText(getActivity(), "打开热点", Toast.LENGTH_SHORT)
						.show();
			}
			// 关闭热点（打开wifi）
			else {
				// 用打开wifi的方法来关闭热点不太妥当，在有些手机会出现权限提示对话框（HUD上无法用点触来选择），so替换为仅关闭热点
				WifiApAdmin.closeWifiAp(mWifiManager);
				ll_managehotspot_content.setVisibility(View.GONE);
				tv_nohotspot.setVisibility(View.VISIBLE);
				hsIsOpen = false;
				Utils.put(getActivity(), Constants.FILE_HS_INFO,
						Constants.KEY_HSINFO, hsIsOpen);
				Toast.makeText(getActivity(), "关闭热点", Toast.LENGTH_SHORT)
						.show();
			}

			break;
		case R.id.rl_remotesconnect:// send a broadcast
			try {
				if (!mBluetoothAdapter.isEnabled()) {
					// Intent enableBtIntent = new Intent(
					// BluetoothAdapter.ACTION_REQUEST_ENABLE);
					// startActivityForResult(enableBtIntent,
					// REQUEST_ENABLE_BT);
					mBluetoothAdapter.enable();
				}
			} catch (Exception e) {
				Toast.makeText(getActivity(), "本机没有找到蓝牙硬件或驱动！",
						Toast.LENGTH_SHORT).show();
				return;
			}
			intentBluetoothLeService = new Intent(getActivity(),
					BluetoothLeService.class);
			getActivity().startService(intentBluetoothLeService);
			showRemotesListPopupWindow();
			break;
		case R.id.rl_traffic:
			TrafficFragment tf = new TrafficFragment();
			getFragmentManager().beginTransaction()
					.replace(R.id.info_container, tf).commit();
			break;
		case R.id.rl_managevoice:
			showVoiceManagePopupWindow();
			break;
		}
	}

	/**
	 * 获取所有可以连接的WIFI列表
	 */
	public void getAllNewWorkList() {
		// 开始扫描网络
		wifiAdmin.startScan();
		lstScanResult = wifiAdmin.getWifiList();
		List<ScanWifiClass> lstScanWifi = new ArrayList<ScanWifiClass>();
		if (lstScanResult != null) {
			for (ScanResult sr : lstScanResult) {
				ScanWifiClass swc = new ScanWifiClass();
				swc.setBSSID(sr.BSSID);
				swc.setCapabilities(sr.capabilities);
				swc.setLevel(sr.level);
				swc.setSSID(sr.SSID);
				lstScanWifi.add(swc);
			}
			Collections.sort(lstScanWifi);
			for (ScanWifiClass swc : lstScanWifi) {
				wifiAdapter.AddNewItem(swc);
			}
			// allNetWork.setText("扫描到的wifi网络：\n"+sb.toString());
		}
	}

	/**
	 * 显示遥控器连接的弹框
	 */
	private void showRemotesListPopupWindow() {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity())
				.inflate(R.layout.remotepopupwindow, null);
		lv_remote = (ListView) layout.findViewById(R.id.lv_remote);
		lv_remote.setAdapter(mLeDeviceListAdapter);
		lv_remote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BluetoothDevice device = mLeDeviceListAdapter
						.getDevice(position);
				if (device == null)
					return;
				connectBleDevice(device);
				// ArrayList<BlueDevice> blueDevice=new ArrayList<BlueDevice>();
				// BlueDevice bd=new BlueDevice();
				// bd.setName(device.getName());
				// bd.setImei(device.getAddress());
				// bd.setImsi(device.getAddress());
				// blueDevice.add(bd);
				// SaveUtils.saveArray(getActivity(), blueDevice);
			}
		});
		popupWindow = new PopupWindow(getActivity());
		popupWindow.setWidth(getActivity().getWindowManager()
				.getDefaultDisplay().getWidth() / 2);
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout);
		popupWindow.showAtLocation(
				getActivity().findViewById(R.id.rl_remotesconnect),
				Gravity.CENTER_VERTICAL, 0, 0);
		scanLeDevice(true);
		task = new DismissPopupWindowTask();
		timer.schedule(task, 10000, 10000); // 4秒钟后销毁
	}

	/**
	 * 显示WIFI连接的弹框
	 */
	private void showWifiListPopupWindow() {
		showProgressDialog();
		Message mes = new Message();
		mes.what = 3;
		wifiHandler.sendMessageDelayed(mes, 15000);
		// pwd = "";
		// LinearLayout layout = (LinearLayout)
		// LayoutInflater.from(getActivity())
		// .inflate(R.layout.wifipopupwindow, null);
		// lv_wifi = (ListView) layout.findViewById(R.id.lv_wifi);
		// lv_wifi.setOnItemClickListener(new AdapterView.OnItemClickListener()
		// {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// // 如果从手机端发来的wifi信息SSID包含所选wifi的SSID，使用已设置好的wifi密码
		// String SSID = wifiAdapter.listWifi.get(position).getSSID();
		// if (getwifiInfo.keySet().contains(SSID)) {
		// pwd = getwifiInfo.get(SSID);
		// boolean addNetwork = wifiAdmin.addNetwork(wifiAdmin
		// .createWifiInfo(wifiAdapter.listWifi.get(position)
		// .getSSID(), pwd, wifiAdmin.TYPE_WPA));
		// if (!addNetwork) {
		// Toast.makeText(getActivity(), "wifi连接失败",
		// Toast.LENGTH_SHORT).show();
		// }
		// } else {
		// Toast.makeText(getActivity(), "请先连接手机端设置相应的wifi密码",
		// Toast.LENGTH_SHORT).show();
		// }
		// }
		// });
		getAllNewWorkList();
		// lv_wifi.setAdapter(wifiAdapter);
		/**
		 * 检索手机发来的信息自动连接wifi
		 */
		Log.i(TAG, wifiAdapter.listWifi.size() + "");
		Log.i(TAG, getwifiInfo + "");
		for (int i = 0; i < wifiAdapter.listWifi.size(); i++) {
			Log.i(TAG, wifiAdapter.listWifi.get(i).getSSID());
			if (getwifiInfo.keySet().contains(
					wifiAdapter.listWifi.get(i).getSSID())) {
				String SSID = wifiAdapter.listWifi.get(i).getSSID();
				String pwd = getwifiInfo.get(SSID);
				boolean addNetwork = wifiAdmin.addNetwork(wifiAdmin
						.createWifiInfo(SSID, pwd, wifiAdmin.TYPE_WPA));
				Log.i(TAG, addNetwork + "");
				if (!addNetwork) {
					Toast.makeText(getActivity(), "wifi连接失败",
							Toast.LENGTH_SHORT).show();
					continue;
				}
				break;
			}
		}
		// popupWindow = new PopupWindow(getActivity());
		// popupWindow.setWidth(getActivity().getWindowManager()
		// .getDefaultDisplay().getWidth() / 2);
		// popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		// popupWindow.setOutsideTouchable(true);
		// popupWindow.setFocusable(true);
		// popupWindow.setContentView(layout);
		// popupWindow.showAtLocation(
		// getActivity().findViewById(R.id.rl_managewifi),
		// Gravity.CENTER_VERTICAL, 0, 0);
		// task = new DismissPopupWindowTask();
		// timer.schedule(task, 10000, 10000); // 4秒钟后销毁
	}

	/**
	 * 获得从手机端发送过来的wifi设置信息
	 * 
	 * @return
	 */
	private HashMap<String, String> getwifiInformation() {
		HashMap<String, String> wifiinfoMap = new HashMap<String, String>();
		sp_wifiInfo = getActivity().getSharedPreferences(
				Constants.FILE_HUDWIFI, Activity.MODE_PRIVATE);
		String wifiInfoStr = sp_wifiInfo.getString(Constants.KEY_WIFIINFO, "");
		Log.i("wifiInfo", wifiInfoStr);
		if (null != wifiInfoStr && "" != wifiInfoStr) {
			wifiinfoMap = gson.fromJson(wifiInfoStr,
					new TypeToken<HashMap<String, String>>() {
					}.getType());
		}
		Log.i("wifiInfo", wifiinfoMap + "");
		//添加一个默认的连接对象
		wifiinfoMap.put("Hudprojector", "88888888");
		return wifiinfoMap;
	}

	class DismissPopupWindowTask extends TimerTask {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			dismissPopupWindowhandler.sendMessage(message);
		}
	}

	/**
	 * 销毁PopWindow的线程
	 */
	Handler dismissPopupWindowhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 要做的事情
			if (popupWindow != null && popupWindow.isShowing())
				popupWindow.dismiss();
			if (task != null) {
				task.cancel();
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 遥控器设备的列表适配器
	 */
	private class LeDeviceListAdapter extends BaseAdapter {

		public List<BluetoothDevice> mLeDevices = new ArrayList<BluetoothDevice>();
		private LayoutInflater mInflator;

		public LeDeviceListAdapter() {
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = getActivity().getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device) {
			if (!mLeDevices.contains(device)) {
				mLeDevices.add(device);
			}
		}

		public BluetoothDevice getDevice(int position) {
			return mLeDevices.get(position);
		}

		/**
		 * 通过地址，从列表中删除
		 * 
		 * @param addr
		 * @return
		 */
		public boolean removeItemByAddr(String addr) {
			List<BluetoothDevice> lstTempDevices = new ArrayList<BluetoothDevice>();
			try {
				for (BluetoothDevice bd : lstTempDevices) {
					if (!bd.getAddress().equals(addr)) {
						lstTempDevices.add(bd);
					}
				}
				mLeDevices = lstTempDevices;
				return true;
			} catch (Exception e) {
				return false;
			}

		}

		public void clear() {
			mLeDevices.clear();
		}

		@Override
		public int getCount() {
			return mLeDevices.size();
		}

		@Override
		public Object getItem(int i) {
			return mLeDevices.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null) {
				view = mInflator.inflate(R.layout.listitem_remote_device, null);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view
						.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view
						.findViewById(R.id.device_name);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDevices.get(i);
			final String deviceName = device.getName();
			if (deviceName != null && deviceName.length() > 0)
				viewHolder.deviceName.setText(deviceName);
			else
				viewHolder.deviceName.setText("未知设备");
			viewHolder.deviceAddress.setText(device.getAddress());
			return view;
		}
	}

	/**
	 * 检索到的遥控器容器
	 * 
	 * @author Administrator
	 * 
	 */
	static class ViewHolder {
		TextView deviceName;
		TextView deviceAddress;
	}

	// 设备查找回调
	BluetoothAdapter.LeScanCallback mLeScanCallback = new LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {

			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mLeDeviceListAdapter.addDevice(device);
					mLeDeviceListAdapter.notifyDataSetChanged();
					// String deviceName = "KeyCtrl";
					// Log.d(TAG, "find deviceName:"+device.getName());
					// if(device.getName().equals(deviceName))
					// {
					// connectBleDevice(device);//随便连接一个
					// }
				}
			});
		}
	};

	/**
	 * 显示音量管理的弹框
	 */
	private void showVoiceManagePopupWindow() {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(getActivity())
				.inflate(R.layout.voicepopupwindow, null);
		speakingVoice = (SeekBar) layout.findViewById(R.id.lv_speaking_voice);
		musicVoice = (SeekBar) layout.findViewById(R.id.lv_music_voice);
		ImageButton upSpeakVoice, downSpeakVoice, upMusicVoice, downMusicVoice;
		upSpeakVoice = (ImageButton) layout.findViewById(R.id.btn_upspeak);
		downSpeakVoice = (ImageButton) layout.findViewById(R.id.btn_downspeak);
		upMusicVoice = (ImageButton) layout.findViewById(R.id.btn_upmusic);
		downMusicVoice = (ImageButton) layout.findViewById(R.id.btn_downmusic);

		upSpeakVoice.setOnClickListener(voiceListener);
		downSpeakVoice.setOnClickListener(voiceListener);
		upMusicVoice.setOnClickListener(voiceListener);
		downMusicVoice.setOnClickListener(voiceListener);
		speakingVoice.setMax(maxSpeakV);
		speakingVoice.setProgress(currSpeakV);
		musicVoice.setMax(maxMusicV);
		musicVoice.setProgress(currMusicV);
		OnSeekBarChangeListener speakChanegeListener = new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
						progress, 0);
				currSpeakV = audioManager
						.getStreamVolume(AudioManager.STREAM_ALARM);
				seekBar.setProgress(currSpeakV);
				getData();
			}
		};
		OnSeekBarChangeListener musicChangeListener = new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress, 0);
				currMusicV = audioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				seekBar.setProgress(currMusicV);
				getData();
			}
		};
		speakingVoice.setOnSeekBarChangeListener(speakChanegeListener);
		musicVoice.setOnSeekBarChangeListener(musicChangeListener);
		popupWindow = new PopupWindow(getActivity());
		popupWindow.setWidth(getActivity().getWindowManager()
				.getDefaultDisplay().getWidth() / 2);
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout);
		popupWindow.showAtLocation(
				getActivity().findViewById(R.id.rl_managevoice),
				Gravity.CENTER_VERTICAL, 0, 0);
		task = new DismissPopupWindowTask();
		timer.schedule(task, 10000, 10000); // 4秒钟后销毁
	}

	OnClickListener voiceListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_upspeak:
				if (currSpeakV + 1 <= maxSpeakV) {
					currSpeakV += 1;
					audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
							currSpeakV, 0);
					speakingVoice.setProgress(currSpeakV);
				}
				break;
			case R.id.btn_downspeak:
				if (currSpeakV - 1 >= 0) {
					currSpeakV -= 1;
					audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
							currSpeakV, 0);
					speakingVoice.setProgress(currSpeakV);

				}
				break;
			case R.id.btn_upmusic:
				if (currMusicV + 1 <= maxMusicV) {
					currMusicV += 1;
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							currMusicV, 0);
					musicVoice.setProgress(currMusicV);
				}
				break;
			case R.id.btn_downmusic:
				if (currMusicV - 1 >= 0) {
					currMusicV -= 1;
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
							currMusicV, 0);
					musicVoice.setProgress(currMusicV);
				}
				break;

			default:
				break;
			}

		}
	};

	private final BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i(TAG, action);
			if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
				if (mWifiManager.isWifiEnabled() && msgFromUser == true) {
					if (!Utils.isWifiEnabled(getActivity())) {
						getAllNewWorkList();
						Message mes = new Message();
						mes.what = 1;
						wifiHandler.sendMessageDelayed(mes, 2000);
					}
				}
			}
		}
	};

	private BroadcastReceiver NetworkConnectChangedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 网络连接状态改变时，收到该广播
			Log.i("H3c", "收到网络状态改变广播");
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent
					.getAction())) {
				Parcelable parcelableExtra = intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (null != parcelableExtra) {
					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
					State state = networkInfo.getState();
					boolean isConnected = state == State.CONNECTED;// 当然，这边可以更精确的确定状态
					Log.e("H3c", "isConnected" + isConnected);
					if (isConnected) {
						dissmissProgressDialog();
						SSID = Utils.getWifiSSID(context);
						tv_wifiinfor.setText("已连接：" + SSID);
						msgFromUser = false;
					} else {
						tv_wifiinfor.setText("未连接");
						msgFromUser = false;
					}
				}
			}
		}
	};

	Handler wifiHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				dissmissProgressDialog();
				wifiAdapter.listWifi.clear();
				if (!Utils.isWifiEnabled(getActivity())) {
					showWifiListPopupWindow();
				}
				Log.i("wifiInfo", "showPOPWINDOW");
				break;
			// case 2:
			// if (popupWindow != null && popupWindow.isShowing()) {
			// popupWindow.dismiss();
			// }
			// break;
			case 3:
				dissmissProgressDialog();
				Toast.makeText(getActivity(), "wifi连接超时", Toast.LENGTH_SHORT)
						.show();
				msgFromUser = false;
				// if (null != dialog1 && dialog1.isShowing()) {
				// dialog1.dismiss();
				// new AlertDialog.Builder(getActivity()).setTitle("提示")
				// .setMessage("wifi连接失败")
				// .setPositiveButton("确定", null).show();
				// }
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(getActivity());
			progressDialog.setMessage(getActivity().getResources().getString(
					R.string.delay_search));
		}
		progressDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

}

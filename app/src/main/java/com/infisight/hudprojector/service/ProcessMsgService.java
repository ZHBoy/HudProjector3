package com.infisight.hudprojector.service;

import java.util.HashMap;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.infisight.hudprojector.MainActivity;
import com.infisight.hudprojector.data.DataCmdMessageClass;
import com.infisight.hudprojector.hotspot.MyTimerCheck;
import com.infisight.hudprojector.kdxfspeech.SpeechVoiceRecognition;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

/**
 * 消息处理中心的Service
 * 
 * @author Administrator
 * 
 */
public class ProcessMsgService extends Service {
	String TAG = "ProcessMsgService";
	MyTimerTask task;
	HashMap<String, Integer> hmGestureInfo = new HashMap<String, Integer>();
	public WifiManager mWifiManager = null;
	// SpeechVoiceRecognition svr = null;
	// private WakeLock mWakeLock;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		CheckAPState();
		// svr = SpeechVoiceRecognition.getInstance(this);
		// PowerManager pm = (PowerManager)
		// getSystemService(Context.POWER_SERVICE);
		// mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "XYTEST"); //
		// WakeLock
		// if (!mWakeLock.isHeld())
		// mWakeLock.acquire();
	}
	/**
	 * 检测热点是否开启
	 */
	private void CheckAPState()
	{
		mWifiManager = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		MyTimerCheck timerCheck = new MyTimerCheck() {

			@Override
			public void doTimerCheckWork() {
				// TODO Auto-generated method stub
				if (CommonUtil.isWifiApEnabled(mWifiManager)) {
					String action = Constants.ACTION_HOTSPOT_AVAILABLE;
					Intent intent = new Intent(action);
					ProcessMsgService.this.sendBroadcast(intent);
					this.exit();
				} else {
					String action = Constants.ACTION_HOTSPOT_UNAVAILABLE;
					Intent intent = new Intent(action);
					ProcessMsgService.this.sendBroadcast(intent);
					Log.v(TAG, "Wifi enabled failed!");
				}
			}

			@Override
			public void doTimeOutWork() {
				// TODO Auto-generated method stub
				this.exit();
			}
		};
		timerCheck.start(15, 1000);
	}
	
	private void InitData() {
		hmGestureInfo.put(Constants.GETSTURE_BACK, 0);
		hmGestureInfo.put(Constants.GESTURE_TOUP, 1);
		hmGestureInfo.put(Constants.GESTURE_TODOWN, 2);
		hmGestureInfo.put(Constants.GESTURE_TOLEFT, 3);
		hmGestureInfo.put(Constants.GESTURE_TORIGHT, 4);
		hmGestureInfo.put(Constants.GESTURE_SINGLETAP, 5);
		hmGestureInfo.put(Constants.GESTURE_DOUBLECLICK, 6);
		hmGestureInfo.put(Constants.GETSTURE_LONGPRESS, 7);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(gMsgReceiver);
		super.onDestroy();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		registerReceiver(gMsgReceiver, makeNewMsgIntentFilter());
		InitData();
		new Thread(new MsgServer(ProcessMsgService.this)).start();
		return super.onStartCommand(intent, flags, startId);
	};

	BroadcastReceiver gMsgReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			try {
				Gson gson = new Gson();
				final String action = intent.getAction();
				Log.d(TAG, "action:" + action);
				Log.d(TAG,"StringExtra:"+ intent.getStringExtra(Constants.MSG_PARAM));
				String gestureCode = intent.getStringExtra(Constants.MSG_PARAM);
				if (action.equals(Constants.GESTURE_INFO)
						&& hmGestureInfo.keySet().contains(gestureCode)) {
					// DataCmdMessageClass dcmc = new DataCmdMessageClass();
					// dcmc =
					// gson.fromJson(intent.getStringExtra(Constants.MSG_PARAM),
					// DataCmdMessageClass.class);
					int operFlag = hmGestureInfo.get(gestureCode);
					Message msg = handler.obtainMessage(operFlag);
					msg.what = operFlag;
					msg.sendToTarget();
				}
			} catch (Exception e) {

			}
		}

	};

	/**
	 * 广播过滤器
	 */
	private static IntentFilter makeNewMsgIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.GESTURE_INFO);
		return intentFilter;
	}

	/**
	 * 接收到按键操作
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				CommonUtil.simulateKeystroke(KeyEvent.KEYCODE_BUTTON_Y);
				CommonUtil.simulateKeystroke(KeyEvent.META_SYM_ON);
				break;
			case 1:
				CommonUtil.simulateKeystroke(KeyEvent.KEYCODE_DPAD_UP);
				break;
			case 2:
				CommonUtil.simulateKeystroke(KeyEvent.KEYCODE_DPAD_DOWN);
				break;
			case 3:
				CommonUtil.simulateKeystroke(KeyEvent.KEYCODE_DPAD_LEFT);
				break;
			case 4:
				// CommonUtil.simulateKeystroke(KeyEvent.KEYCODE_ENTER);
				CommonUtil.simulateKeystroke(KeyEvent.KEYCODE_DPAD_RIGHT);
				break;
			case 5:
				CommonUtil.simulateKeystroke(KeyEvent.KEYCODE_ENTER);
				break;
			case 6:
				 MainActivity.svr.controlToWake(true);
				break;
			}
		}
	};

	/**
	 * 接收到按键操作，这个不一定要，但是发送命令的需要存在
	 */
	class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = handler.obtainMessage(4);
			msg.what = 4;
			msg.sendToTarget();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}

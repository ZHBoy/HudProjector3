package com.infisight.hudprojector.hotspot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import com.infisight.hudprojector.MainActivity;
import com.infisight.hudprojector.service.ProcessMsgService;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * 创建热点
 * 
 */
public class WifiApAdmin {
	public static final String TAG = "WifiApAdmin";

	public static void closeWifiAp(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		closeWifiAp(wifiManager);
	}

	public WifiManager mWifiManager = null;

	public Context mContext = null;

	public WifiApAdmin(Context context) {
		mContext = context;

		mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);

//		closeWifiAp(mWifiManager);
	}

	public String mSSID = "";
	public String mPasswd = "";

	public void startWifiAp(String ssid, String passwd) {
		mSSID = ssid;
		mPasswd = passwd;

		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}

		stratWifiAp();

		MyTimerCheck timerCheck = new MyTimerCheck() {

			@Override
			public void doTimerCheckWork() {
				// TODO Auto-generated method stub

				if (CommonUtil.isWifiApEnabled(mWifiManager)) {
					Log.v(TAG, "Wifi enabled success!");
					Intent intentService = new Intent(mContext,
							ProcessMsgService.class);
					mContext.startService(intentService);
					String action = Constants.ACTION_HOTSPOT_AVAILABLE;
					Intent intent = new Intent(action);
					mContext.sendBroadcast(intent);
					this.exit();
				} else {
					String action = Constants.ACTION_HOTSPOT_UNAVAILABLE;
					Intent intent = new Intent(action);
					mContext.sendBroadcast(intent);
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

	public void stratWifiAp() {
		Method method1 = null;
		try {
			method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
					WifiConfiguration.class, boolean.class);
			WifiConfiguration netConfig = new WifiConfiguration();

			netConfig.SSID = mSSID;
			netConfig.preSharedKey = mPasswd;

			netConfig.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			netConfig.allowedKeyManagement
					.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			netConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			netConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			netConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
			netConfig.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);

			method1.invoke(mWifiManager, netConfig, true);

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void closeWifiAp(WifiManager wifiManager) {
		if (CommonUtil.isWifiApEnabled(wifiManager)) {
			try {
				Method method = wifiManager.getClass().getMethod(
						"getWifiApConfiguration");
				method.setAccessible(true);

				WifiConfiguration config = (WifiConfiguration) method
						.invoke(wifiManager);

				Method method2 = wifiManager.getClass().getMethod(
						"setWifiApEnabled", WifiConfiguration.class,
						boolean.class);
				method2.invoke(wifiManager, config, false);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	

}
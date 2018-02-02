package com.infisight.hudprojector.common;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.WindowManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.navi.model.AMapNaviPath;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.util.Constants;

import java.net.Socket;
import java.sql.Date;
import java.util.concurrent.ExecutorService;

public class HudProjectApplication extends Application implements
// AMapLocationListener,Thread.UncaughtExceptionHandler{
		AMapLocationListener {
	public static String TAG = "HudProjectApplication";
	private WakeLock mWakeLock;
	private AMapLocation aMapLocation;// 用于判断定位超时
	public static int totalDis = 0;
	ExecutorService cachedThreadPool;
	public static int gnavi_page_state = 0;
	public static AMapNaviPath naviPath = null;
	public static boolean isStopNavi = false;
	// public static float zoom = -1000;
	public static boolean is_show_hud_music = false;
	public static String music_name = null;

	private static WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

	public static WindowManager.LayoutParams getMywmParams() {
		return wmParams;
	}

	public static String doubanOrMusic = null;
	public static boolean isDoubanplaying = false;
	public static boolean isMusicPlaying = false;
	// 豆瓣FM部分
	public static final int WARNING_SIZE = 1;
	public static boolean isPauseByUser = false;
	private int currentChannelId;// 频道ID
	private static HudProjectApplication instance;
	private AMapLocationClient mMaplocationClient = null;
	private AMapLocationClientOption mMapLocationOption = null;

	// 前任车主
	public static Socket carBeforeSocket = null;

	// 当前车主
	public static Socket carSocket = null;
	@Override
	public void onCreate() {
		// 语音
		StringBuffer param = new StringBuffer();
		param.append("appid=" + getString(R.string.app_id));
		param.append(",");
		// 设置使用v5+
		param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
		SpeechUtility.createUtility(HudProjectApplication.this,
				param.toString());
		super.onCreate();
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		if (aMapLocation == null) {
			startLocation();
			Log.d(TAG, "aMapLocation");
		}

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP, "XYTEST"); // WakeLock
		mWakeLock.acquire();


		instance = this;
	}

	public static HudProjectApplication getInstance() {
		return instance;
	}

	private void startLocation() {
		AMapLocationClient.setApiKey("62a8554483a2cce1877ab1c1d8d20a39");
		mMaplocationClient = new AMapLocationClient(getApplicationContext());
		// 初始化定位参数
		mMapLocationOption = new AMapLocationClientOption();
		// 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mMapLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		// 设置是否返回地址信息（默认返回地址信息）
		mMapLocationOption.setNeedAddress(true);
		// 设置是否只定位一次,默认为false
		mMapLocationOption.setOnceLocation(false);
		// 设置是否强制刷新WIFI，默认为强制刷新
		mMapLocationOption.setWifiActiveScan(false);
		// 设置是否允许模拟位置,默认为false，不允许模拟位置
		mMapLocationOption.setMockEnable(false);
		// 设置定位间隔,单位毫秒,默认为2000ms
		mMapLocationOption.setInterval(2000);
		// 给定位客户端对象设置定位参数
		mMaplocationClient.setLocationOption(mMapLocationOption);
		// 设置定位监听
		mMaplocationClient.setLocationListener(this);
		// 启动定位
		mMaplocationClient.startLocation();
	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (mMaplocationClient != null) {
			mMaplocationClient.stopLocation();// 停止定位
			mMaplocationClient.onDestroy();// 销毁定位客户端。
		}
		mMaplocationClient = null;
	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		// TODO Auto-generated method stub

		if (location != null) {
			Log.d(TAG, "location:" + location);
			this.aMapLocation = location;// 判断超时机制
			double geoLat = location.getLatitude();
			double geoLng = location.getLongitude();
			String cityCode = "";
			String desc = "";
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}
			// 保存经纬度数据
			getSharedPreferences(Constants.LOCATION_INFO, 0).edit()
					.putString(Constants.LATITUDE, String.valueOf(geoLat))
					.putString(Constants.LONGITUDE, String.valueOf(geoLng))
					.putString(Constants.CITYNAME, location.getCity())
					.putString(Constants.ADDR, desc)
					.putFloat(Constants.SPEED, location.getSpeed()).commit();
			String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
					+ "\n精    度    :" + location.getAccuracy() + "米"
					+ "\n定位方式:" + location.getProvider() + "\n定位时间:"
					+ new Date(location.getTime()).toLocaleString() + "\n城市编码:"
					+ cityCode + "\n位置描述:" + desc + "\n省:"
					+ location.getProvince() + "\n市:" + location.getCity()
					+ "\n区(县):" + location.getDistrict() + "\n区域编码:" + location
					.getAdCode());
			Log.d(TAG, "str:" + str);
		}
	}

	// @Override
	// public void uncaughtException(Thread thread, Throwable ex) {
	// // TODO Auto-generated method stub
	// // System.exit(0);
	// Intent intent = new Intent(this,MainActivity.class);
	// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
	// Intent.FLAG_ACTIVITY_NEW_TASK);
	// startActivity(intent);
	// }

}

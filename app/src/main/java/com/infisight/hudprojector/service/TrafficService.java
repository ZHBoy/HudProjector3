package com.infisight.hudprojector.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson; 
import com.infisight.hudprojector.data.TrafficInfo;
import com.infisight.hudprojector.db.DbManager;
import com.infisight.hudprojector.fragment.TrafficFragment;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;
import com.infisight.hudprojector.util.Utils;

/**
 * 问题点：
 * 1.如果服务被强制关闭，则不能统�?
 * 2.�?��自动启动服务
 * 3.如果要做某天的流量统计，则需要定时在每晚十二点前进行�?��已用流量统计
 * 4.数据库相关操作比较�?时，要�?虑异�?
 * 
 */
public class TrafficService extends Service {
	private static final String TAG = "TrafficService";
	private static final String SERVICE_SP = "service_sp";
	private static final String SERVICE_LAST_TRAFFIC = "service_last_traffic";
	private ArrayList<TrafficInfo> infosGPRS = new ArrayList<TrafficInfo>();
	private ArrayList<TrafficInfo> infosWIFI = new ArrayList<TrafficInfo>();
	private Gson gson;
	
	private final int REFRESH_MESSAGE = 1;

	// private TrafficReceiver tReceiver;
	private ConnectivityManager connManager;
	private DbManager dbManager;


	private Handler refreshHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			logRecord();
			Log.e("#################", "record");
			sendEmptyMessageDelayed(REFRESH_MESSAGE, 60000);
		}

	};

	public class MyBinder extends Binder {
		TrafficService getService() {
			return TrafficService.this;
		}
	}


	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		return super.onUnbind(intent);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "TrafficService is go");
		// 获得数据库连接服务
		dbManager = new DbManager(this);
		gson =new Gson();
		// 获得网络连接服务
		connManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		refreshHandler.sendEmptyMessageDelayed(REFRESH_MESSAGE, 1000);
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		logRecord();
		dbManager.close();
		refreshHandler.removeMessages(REFRESH_MESSAGE);
		super.onDestroy();
	}

	public synchronized void logRecord() {
		long traffic = 0;
		long lastTraffic = 0;
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		String networkType = null;
		if (networkInfo == null) {
			networkType = null;
			return;
		} else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			networkType = DbManager.NETWORK_TYPE_WIFI;
		} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			networkType = DbManager.NETWORK_TYPE_MOBILE;
		}
		if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			long ret = (long) 0.0;
			try {
				lastTraffic = (Long) Utils.get(getApplicationContext(), networkType
						+ SERVICE_SP, SERVICE_LAST_TRAFFIC, ret);
			} catch (Exception e) {
				e.printStackTrace();
			}
			long rx = TrafficStats.getTotalRxBytes()
					- TrafficStats.getMobileRxBytes();//接收的wifi字节数
			long tx = TrafficStats.getTotalTxBytes()
					- TrafficStats.getMobileTxBytes();//发送的wifi字节数
			traffic = rx + tx;
//			wifiTraffic=switchUnit(lastTraffic+traffic);
			Log.i("Qyl", switchUnit(traffic));
			Utils.put(getApplicationContext(), networkType + SERVICE_SP,
					SERVICE_LAST_TRAFFIC, traffic);

		} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
			long ret = (long) 0.0;
			try {
				lastTraffic = (Long) Utils.get(getApplicationContext(), networkType
						+ SERVICE_SP, SERVICE_LAST_TRAFFIC, ret);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// int uid = getApplicationInfo().uid;
			long rx = TrafficStats.getMobileRxBytes();
			long tx = TrafficStats.getMobileTxBytes();
			traffic = rx + tx;
//			gprsTraffic=switchUnit(lastTraffic+traffic);
			Log.i("Qyl", switchUnit(traffic));
			Utils.put(getApplicationContext(), networkType + SERVICE_SP,
					SERVICE_LAST_TRAFFIC, traffic);
		}
		//发送数据到手机端
		String dateMonth=Utils.getCurrentTime("yyyy MM");
		long totalwifi = dbManager.queryTotalTraffic(DbManager.NETWORK_TYPE_WIFI, dateMonth);
		long totalgprs = dbManager.queryTotalTraffic(DbManager.NETWORK_TYPE_MOBILE, dateMonth);
		CommonUtil.sendMsg(Constants.MODEL_TRAFFIC_WIFI_COM, switchUnit(totalwifi), TrafficService.this, Constants.MODEL_TRAFFIC_COM);
		CommonUtil.sendMsg(Constants.MODEL_TRAFFIC_GPRS_COM, switchUnit(totalgprs), TrafficService.this, Constants.MODEL_TRAFFIC_COM);
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy MM/dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String date = formatter.format(curDate);
		Log.i("curDate", date);
//		String date = "04-" + System.currentTimeMillis() % 30;
		dbManager.everydayTrafficCreated(date, DbManager.NETWORK_TYPE_WIFI);
		dbManager.everydayTrafficCreated(date, DbManager.NETWORK_TYPE_MOBILE);
		if (traffic < lastTraffic)
			dbManager.updateTraffic(traffic, date, networkType);
		else
			dbManager.updateTraffic(traffic - lastTraffic, date, networkType);
		//折线柱形图数据发送
		infosGPRS = dbManager.queryTotal(DbManager.NETWORK_TYPE_MOBILE);
		String infosGsonG= gson.toJson(infosGPRS);
		CommonUtil.sendMsg(Constants.MODEL_TRAFFIC_GPRS_P_COM, infosGsonG, TrafficService.this, Constants.MODEL_TRAFFIC_COM);
		Log.i("TrafficService", infosGsonG);
		infosWIFI =dbManager.queryTotal(DbManager.NETWORK_TYPE_WIFI);
		String infosGsonW =gson.toJson(infosWIFI);
		CommonUtil.sendMsg(Constants.MODEL_TRAFFIC_WIFI_P_COM, infosGsonW, TrafficService.this, Constants.MODEL_TRAFFIC_COM);
		Log.i("TrafficService", infosGsonW);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public String switchUnit(long max) {
		String textString = "0b";
		if (max / 1024 > 0) {
			float value = 1f;
			value = 1024;
			String unit = "k";
			if (max / (1024 * 1024) > 0) {
				value = 1024 * 1024;
				unit = "M";
				if (max / (1024 * 1024 * 1024) > 0) {
					value = 1024 * 1024 * 1024;
					unit = "G";
				}
			}
			float result = max / value;
			BigDecimal b = new BigDecimal(result);// 新建一个BigDecimal
			float displayVar = b.setScale(1, BigDecimal.ROUND_HALF_UP)
					.floatValue();// 进行小数点一位保留处理现实在坐标系上的数值
			textString = displayVar + unit;
		} else {
			textString = max + "b";
		}
		return textString;
	}
}

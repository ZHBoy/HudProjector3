package com.infisight.hudprojector.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.data.DataMoreLevelClass;
import com.infisight.hudprojector.data.DialogShowData;
import com.infisight.hudprojector.data.PMessage;
import com.infisight.hudprojector.data.SpeechVoiceData;
import com.infisight.hudprojector.data.UserInfoClass;
import com.infisight.hudprojector.data.VoiceDataClass;
import com.infisight.hudprojector.kdxfspeech.SpeechVoiceRecognition;
import com.infisight.hudprojector.widget.TimerCountDownView;
import com.infisight.hudprojector.widget.TimerCountDownView.CountdownTimerListener;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
	static String TAG = "CommonUtil";
	static WifiLock wifiLock = null;
	static SpeechVoiceRecognition svr = null;

	/**
	 * 打开P2P
	 * 
	 * @param view
	 */
	public static void enableP2p(Context context, WifiP2pManager manager,
			Channel channel, View view) {
		try {
			Method m = WifiP2pManager.class.getMethod("enableP2p",
					WifiP2pManager.Channel.class);
			m.invoke(manager, channel);
			Log.d("wifitest", "enableP2p打开P2P");
		} catch (NoSuchMethodException e) {
			// Log.e("NoSuchMethod", "No Such Method Exception!");
			Log.d("wifitest", "wifiManager.setWifiEnabled(enabled)");
			// 在这里打开和关闭Wifi
			toggleWiFi(context, true);

			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Log.e("InvocationExcept", "Invocation Exception");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.e("IllegalAccessExcept", "IllegalAccessException");
			e.printStackTrace();
		}
	}

	/**
	 * 关闭P2P
	 * 
	 * @param view
	 */
	public static void disableP2p(Context context, WifiP2pManager manager,
			Channel channel, View view) {
		try {
			Method m = WifiP2pManager.class.getMethod("disableP2p",
					WifiP2pManager.Channel.class);
			m.invoke(manager, channel);
		} catch (NoSuchMethodException e) {
			Log.e("NoSuchMethod", "No Such Method Exception!");
			toggleWiFi(context, false);
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Log.e("InvocationExcept", "Invocation Exception");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.e("IllegalAccessExcept", "IllegalAccessException");
			e.printStackTrace();
		}
	}

	/**
	 * 打开和关闭Wifi
	 * 
	 * @param context
	 * @param enabled
	 */
	private static void toggleWiFi(Context context, boolean enabled) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled() != enabled)
			wifiManager.setWifiEnabled(enabled);
		Log.d("wifitest", "wifiManager.setWifiEnabled(enabled)");
	}

	/**
	 * 重启wifi
	 * 
	 * @param context
	 */
	public static void RestartWiFi(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled() == true) {
			wifiManager.setWifiEnabled(false);
			wifiManager.setWifiEnabled(true);
		} else {
			wifiManager.setWifiEnabled(true);
		}
		Log.d("wifitest", "wifiManager.setWifiEnabled(enabled)");
	}

	// 判断GPS是否打开
	public static boolean isGPSOpen(Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		boolean gps = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean network = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps)
			return true;
		else
			return false;

	}

	// 开启GPS
	public static void openGPS(Context context) {
		Intent intentGPS = new Intent();
		intentGPS.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		intentGPS.addCategory("android.intent.category.ALTERNATIVE");
		intentGPS.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(context, 0, intentGPS, 0).send();
		} catch (Exception e) {
		}
	}

	/**
	 * 对Wifi进行加锁
	 * 
	 * @param context
	 * @param on
	 */
	public static void keepWiFiOn(Context context, boolean on) {
		if (wifiLock == null) {
			WifiManager wm = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (wm != null) {
				wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL,
						"infisight_wifilock");
				wifiLock.setReferenceCounted(true);
				Log.d("wifitest", "Released WiFi lock");
			}
		}
		if (wifiLock != null && !wifiLock.isHeld()) { // May be null if wm is
														// null
			if (on) {
				wifiLock.acquire();
				// Log.d(TAG, "Adquired WiFi lock");
				Log.d("wifitest", "Released WiFi acquire");
			} else if (wifiLock.isHeld()) {
				wifiLock.release();
				// Log.d(TAG, "Released WiFi lock");
				Log.d("wifitest", "Released WiFi release");
			}
		}
	}

	/**
	 * 获取终点关键字
	 * 
	 * @param keyWords
	 * @return
	 */
	public static String getTerminalAddr(String keyWords) {
		String strRet = "";
		try {
			for (String k : WordDataSet.hmVoiceNav.get(Constants.K_SPLITS)) {
				if (keyWords.contains(k)) {
					strRet = keyWords.substring(keyWords.indexOf(k));
				}
			}
		} catch (Exception e) {
			strRet = "";
		}
		return strRet.trim();
	}

	/**
	 * 判断地图拖拽方向
	 * 
	 * @param keyWords
	 * @return
	 */
	public static String getDirectOper(String keyWords) {
		String strRet = "";
		try {
			for (String k : WordDataSet.hmVoiceNav
					.get(Constants.K_MAP_ORPERCMD_DOWN)) {
				if (keyWords.contains(k)) {
					strRet = Constants.K_MAP_ORPERCMD_DOWN;
					break;
				}
			}
			for (String k : WordDataSet.hmVoiceNav
					.get(Constants.K_MAP_ORPERCMD_UP)) {
				if (keyWords.contains(k)) {
					strRet = Constants.K_MAP_ORPERCMD_UP;
					break;
				}
			}
			for (String k : WordDataSet.hmVoiceNav
					.get(Constants.K_MAP_ORPERCMD_LEFT)) {
				if (keyWords.contains(k)) {
					strRet = Constants.K_MAP_ORPERCMD_LEFT;
					break;
				}
			}
			for (String k : WordDataSet.hmVoiceNav
					.get(Constants.K_MAP_ORPERCMD_RIGHT)) {
				if (keyWords.contains(k)) {
					strRet = Constants.K_MAP_ORPERCMD_RIGHT;
					break;
				}
			}
			for (String k : WordDataSet.hmVoiceNav
					.get(Constants.K_MAP_ORPERCMD_ENLARGE)) {
				if (keyWords.contains(k)) {
					strRet = Constants.K_MAP_ORPERCMD_ENLARGE;
					break;
				}
			}
			for (String k : WordDataSet.hmVoiceNav
					.get(Constants.K_MAP_ORPERCMD_SMALLERE)) {
				if (keyWords.contains(k)) {
					strRet = Constants.K_MAP_ORPERCMD_SMALLERE;
					break;
				}
			}
		} catch (Exception e) {
			strRet = "";
		}
		return strRet.trim();
	}

	/**
	 * 联想词相对应操作
	 * 
	 * @param keyWords
	 * @return
	 */
	public static String getDirectLstViewDirect(String keyWords) {
		String strRet = "";
		try {
			// 向下
			for (String k : WordDataSet.hmVoiceNav
					.get(Constants.K_MAP_LSTVIEW_DIRECT_DOWN)) {
				if (keyWords.contains(k)) {
					strRet = Constants.K_MAP_LSTVIEW_DIRECT_DOWN;
					break;
				}
			}
			// 向上
			for (String k : WordDataSet.hmVoiceNav
					.get(Constants.K_MAP_LSTVIEW_DIRECT_UP)) {
				if (keyWords.contains(k)) {
					strRet = Constants.K_MAP_LSTVIEW_DIRECT_UP;
					break;
				}
			}
			// 选中第几条
			for (int i = 0; i < WordDataSet.hmVoiceNav
					.get(Constants.K_MAP_LSTVIEW_DIRECT_AUTO2).length; i++) {
				if (keyWords.contains("第"
						+ WordDataSet.hmVoiceNav
								.get(Constants.K_MAP_LSTVIEW_DIRECT_AUTO2)[i])) {
					strRet = String.valueOf(i);
					break;
				}
			}
			// 确认选中指定条目
			for (String k : WordDataSet.hmVoiceNav
					.get(Constants.K_MAP_LSTVIEW_DIRECT_CHOOSE)) {
				if (keyWords.contains(k)) {
					strRet = Constants.K_MAP_LSTVIEW_DIRECT_CHOOSE;
					break;
				}
			}
		} catch (Exception e) {
			strRet = "";
		}
		return strRet.trim();
	}

	public static int getIndex(String str) {
		HashMap<String, String> hm = new HashMap<String, String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put("一", "0");
				put("二", "1");
				put("三", "2");
				put("四", "3");
				put("五", "4");
				put("六", "5");
				put("七", "6");
				put("八", "7");
				put("九", "8");
				put("十", "9");

			}
		};
		if (hm.get(str) == null) {
			return -1;
		}
		return Integer.parseInt(hm.get(str));
	}

	/**
	 * 依据电话号码获取用户信息
	 * 
	 * @param phonenumber
	 * @return
	 */
	public static UserInfoClass getUserInfoByPhoneNumber(Context mContext,
			String phonenumber) {
		UserInfoClass uic = new UserInfoClass();
		try {
			Cursor cursor = mContext.getContentResolver().query(
					Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
							phonenumber),
					new String[] { PhoneLookup._ID, PhoneLookup.NUMBER,
							PhoneLookup.DISPLAY_NAME, PhoneLookup.PHOTO_ID,
							PhoneLookup.TYPE, PhoneLookup.LABEL }, null, null,
					null);

			if (cursor.getCount() == 0) {
				// 没找到电话号码
			} else if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				uic.setDisplayName(cursor.getString(2));
				uic.setPhoneNumber(phonenumber);
				// uic.setPhoto(cursor.getString(3));//可以将String转为photo流
				uic.setPhoto("");
			}
		} catch (Exception e) {
		}
		return uic;
	}

	/**
	 * 获取联系人信息
	 * @param mContext
	 * @return
	 */
	public static List<UserInfoClass> getPhoneContacts(Context mContext) {
		List<UserInfoClass> lsts = new ArrayList<UserInfoClass>();
		String name = null;
		String num = null;
		try {
			Cursor phone = mContext.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					null, null, null);
			if (phone != null) {
				while (phone.moveToNext()) {
					UserInfoClass userInfo = new UserInfoClass();
					// 得到手机号码
					num = phone
							.getString(phone
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					// 当手机号码为空的或者为空字段 跳过当前循环
					int nameFieldColumnIndex = phone
							.getColumnIndex(PhoneLookup.DISPLAY_NAME);
					name = phone.getString(nameFieldColumnIndex);
					userInfo.setDisplayName(name);
					userInfo.setPhoneNumber(num);
					lsts.add(userInfo);
				}
				phone.close();
				phone = null;
			}
			return lsts;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lsts;
	}

	/**
	 * 根据名字中的某一个字进行模糊查询
	 * 
	 * @param key
	 */
	public static void getFuzzyQueryByName(Context mContext, String key) {

		StringBuilder sb = new StringBuilder();
		ContentResolver cr = mContext.getContentResolver();
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER };
		Cursor cursor = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
				ContactsContract.Contacts.DISPLAY_NAME + " like " + "'%" + key
						+ "%'", null, null);
		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			String number = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			sb.append(name + " (").append(number + ")").append("\r\n");
		}
		cursor.close();

		if (!sb.toString().isEmpty()) {
			Log.d(TAG, "查询联系人:\r\n" + sb.toString());
		}
	}
//
//	// 获取手机号码的归属地
//	public String getResultForHttpGet(String tel)
//			throws ClientProtocolException, IOException {
//		// 服务器 ：服务器项目 ：servlet名称
//		String path = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel="
//				+ tel;
//		String result = "";
//		HttpGet httpGet = new HttpGet(path);// 编者按：与HttpPost区别所在，这里是将参数在地址中传递
//		HttpResponse response = new DefaultHttpClient().execute(httpGet);
//		if (response.getStatusLine().getStatusCode() == 200) {
//			HttpEntity entity = response.getEntity();
//			result = EntityUtils.toString(entity, HTTP.UTF_8);
//		}
//		return result;
//	}

	// 将一个秒转换成，时：分：秒的格式
	public static String secToTime(int time) {
		String timeStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (time <= 0)
			return "00:00";
		else {
			minute = time / 60;
			if (minute < 60) {
				second = time % 60;
				timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				hour = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":"
						+ unitFormat(second);
			}
		}
		return timeStr;
	}

	// 将Drawable转化为Bitmap
	public static Bitmap drawableToBitmap(Drawable drawable) {
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}

	//
	public static String unitFormat(int i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = "" + i;
		return retStr;
	}

	/**
	 * 操作确定按钮通过dialog显示显示
	 * @param mContext
	 * @param layout
	 * @return
	 */
	public static AlertDialog.Builder isDoThisStep(Context mContext, View layout) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext,
				AlertDialog.THEME_DEVICE_DEFAULT_DARK);// 浅色
		layout.setLayoutParams(new LayoutParams(426, 336));
		dialog.setView(layout);

		return dialog;
	}

	// dialog几秒钟之后自动关闭
	public static void closeDialog(final Dialog dialog) {
		final Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				dialog.dismiss();
				t.cancel();
			}
		}, 2000);
	}

	/**
	 * 发送广播，在界面上进行修改mainActivity
	 */
	public static void processBroadcast(Context context, SpeechVoiceData svd)
			throws Exception {
		Gson gson = new Gson();
		Intent intentAction = new Intent(svd.getCommand());
		intentAction.putExtra(svd.getCommand(), gson.toJson(svd));
		context.sendBroadcast(intentAction);
	}

	/**
	 * 发送广播，更改界面图标状态 iconType:图标类型 ：音乐 、小瑞状态等 iconState 0:隐藏 1：显示
	 */
	public static void processBroadcast(Context context, int iconType,
			int iconState) {
		try {
			Intent intentAction = new Intent(Constants.MAIN_ICON_STATE_ACTION);
			intentAction.putExtra("iconType", iconType);
			intentAction.putExtra("iconState", iconState);
			context.sendBroadcast(intentAction);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 返回首页
	public static void retHomeFragment(Context context) {
		try {
			SpeechVoiceData svd = packSpeechCommandInfo(new VoiceDataClass(
					null, Constants.F_R_RETURN_HOME[0], null, null),
					Constants.COMMON_UTIL_ACTION, null);
			processBroadcast(context, svd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 装载语音指令信息
	 */
	public static SpeechVoiceData packSpeechCommandInfo(VoiceDataClass vdc,
			String action, String value) {
		Gson gson = new Gson();
		VoiceDataClass vdcData = new VoiceDataClass();// 装载语音指令信息
		vdcData.setType(vdc.getType());
		vdcData.setValue(value);// value用来装载实体类对象信息

		SpeechVoiceData svdData = new SpeechVoiceData();// 装载action
		svdData.setCommand(action);
		svdData.setValue(gson.toJson(vdcData));
		return svdData;
	}

	// 返回当前时间
	public static String timeShow() {
		// 通过SimpleDateFormat获取24小时制时间
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",
				Locale.getDefault());
		String currentTime = sdf.format(new Date());
		return currentTime;
	}

	/**
	 * @param index
	 * @return 距离+单位
	 */
	public static String[] meterTurnKm(double index) {

		DecimalFormat df = new DecimalFormat("######0.0");
		String distance = null;
		String util = null;
		if (index >= 1000) {
			distance = df.format(index / 1000).toString();
			util = "公里";

		} else {
			distance = String.valueOf((int) Math.round(index));
			util = "米";
		}
		String str[] = { distance, util };
		return str;
	}

	// 秒转成小时和分钟
	public static String[] getTimeFormat(double second) {
		String hour = null;
		String minute = null;
		DecimalFormat df = new DecimalFormat("######0.00");
		String time = df.format(second / 3600) + "";
		String[] strArr = time.split("\\.");
		if (time.contains(".")) {
			if (!strArr[0].equals("0")) {
				hour = time.split("\\.")[0] + "";
			} else {
				hour = null;
			}
			if (!strArr[1].equals("0")) {
				minute = Integer.parseInt(time.split("\\.")[1]) * 60 / 100 + "";
			} else {
				minute = null;
			}
			String str[] = { hour, minute };
			return str;
		} else {
			String str2[] = { time, null };
			return str2;
		}
	}

	/*
	 * 根据屏幕获取字体大小，用于listview铺满全屏 size 屏幕大小； /7*6:自定义的标题栏占了一个比重；num是条目个数；3是像素转sp
	 */
	public static int getTextSizes(int size, int num) {
		return size / 7 * 5 / num / 3;
	}

	/**
	 * 发送虚拟按键
	 * 
	 * @param KeyCode
	 */
	public static void simulateKeystroke(final int KeyCode) {
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				try {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(KeyCode);
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

		}).start();

	}

	/*
	 * 唤醒词识别
	 */
	public static boolean recognitionKeyWake(String text, String[] strArray) {
		boolean isTrue = false;
		for (String str : strArray) {
			if (text.contains(str)) {
				isTrue = true;
				break;
			}
		}
		return isTrue;
	}

	/*
	 * 语音识别词(非截取)contains
	 */
	public static String recognitionKey(String text, String[] strArray) {
		String key = null;
		for (String str : strArray) {
			if (text.contains(str)) {
				key = str;
				break;
			}
		}
		return key;
	}

	/*
	 * 语音识别词(非截取)equals
	 */
	public static boolean recognitionKey_Eaqual(String text, String[] strArray) {
		boolean isTrue = false;
		for (String str : strArray) {
			if (text.equals(str)) {
				isTrue = true;
				break;
			}
		}
		return isTrue;
	}

	/*
	 * 语音识别词(截取)
	 */
	public static String recognitionKeySplit(String text, String[] strArray) {
		String recSplit = null;
		for (String str : strArray) {
			if (text.contains(str)) {
				String strs = text.replaceAll(str, "o");
				if (strs.split("o").length == 2) {
					recSplit = strs.split("o")[1];
					Log.i("TAG", recSplit);
				}
				break;
			}
		}
		return recSplit;
	}

	// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
	public static boolean isConnect(Context context) {

		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.v("error", e.toString());
		}
		return false;
	}

	/**
	 * @param context
	 * @return 当前网络类型
	 */
	public static int connectedType(Context context) {

		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return info.getSubtype();
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.v("error", e.toString());
		}
		return -1;
	}

	// 保存路径全览信息
	public static void save_plan_all_see(Context mComtext, String navipath) {
		SharedPreferences sp = mComtext.getSharedPreferences(
				Constants.FILE_PLAN_ALL_SEE, 0);
		sp.edit().putString(Constants.KEY_NAVIPATH, navipath).commit();
	}

	// 判断字符串是否为数字
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	// 生成随机不重复的字符串
	public static String randomValue() {
		String str = String.valueOf(System.currentTimeMillis());
		return str;

	}

	/*
	 * dialog确定和取消, is_ok用来将确定操作传给其他地方
	 */
	public static AlertDialog showDialogAlert(final Context mContext,
			String tv_show) {

		svr = SpeechVoiceRecognition.getInstance(mContext);
		final AlertDialog dlg = new AlertDialog.Builder(mContext).create();
		final Intent intent = new Intent(Constants.CERTAIN_ACTION);
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		window.setContentView(R.layout.dialog_customer);
		TextView tv_end_dialog = (TextView) window
				.findViewById(R.id.tv_end_dialog);
		tv_end_dialog.setText(tv_show);
		TimerCountDownView timeCount = (TimerCountDownView) window
				.findViewById(R.id.dialog_counttime);
		final TextView tv_timeCount = (TextView) window
				.findViewById(R.id.dialog_timer);

		CountdownTimerListener litener = new CountdownTimerListener() {

			@Override
			public void onCountDown(String time) {
				tv_timeCount.setText(time);
			}

			@Override
			public void onTimeArrive(boolean isArrive) {
				if (isArrive) {
					intent.putExtra("control", "cancel");
					mContext.sendBroadcast(intent);
					dlg.cancel();
				}
			}
		};

		timeCount.setMaxTime(10);
		timeCount.updateView();
		timeCount.addCountdownTimerListener(litener);

		// 为确认按钮添加事件,执行退出应用操作
		final ImageButton ok = (ImageButton) window.findViewById(R.id.btn_ok);
		ok.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					ok.setImageResource(R.drawable.t_certain_down);
				} else {
					ok.setImageResource(R.drawable.t_certain);
				}
			}
		});
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				intent.putExtra("control", "certain");
				mContext.sendBroadcast(intent);
				dlg.cancel();
			}
		});

		// 关闭alert对话框架
		final ImageButton cancel = (ImageButton) window
				.findViewById(R.id.btn_cancel);

		cancel.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					cancel.setImageResource(R.drawable.t_cancel_down);
				} else {
					cancel.setImageResource(R.drawable.t_cancel);
				}
			}
		});
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				intent.putExtra("control", "cancel");
				mContext.sendBroadcast(intent);
				dlg.cancel();
			}
		});

		new Handler().postDelayed(new Runnable() {

			public void run() {
				svr.setAudioVol(0, 0);
			}

		}, 2500);

		return dlg;
	}

	/*
	 * dialog确定和取消, is_ok用来将确定操作传给其他地方
	 */
	public static AlertDialog showDialogAlert(final Context mContext,
			final DialogShowData dsd) {

		svr = SpeechVoiceRecognition.getInstance(mContext);
		final AlertDialog dlg = new AlertDialog.Builder(mContext).create();
		final Intent intent = new Intent(Constants.CERTAIN_ACTION);
		dlg.show();
		Window window = dlg.getWindow();
		// *** 主要就是在这里实现这种效果的.
		window.setContentView(R.layout.dialog_customer);
		// 为确认按钮添加事件,执行退出应用操作
		ImageButton ok = (ImageButton) window.findViewById(R.id.btn_ok);
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				intent.putExtra("control", "certain");
				mContext.sendBroadcast(intent);
				dlg.cancel();
			}
		});

		// 关闭alert对话框架
		ImageButton cancel = (ImageButton) window.findViewById(R.id.btn_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				intent.putExtra("control", "cancel");
				mContext.sendBroadcast(intent);
				dlg.cancel();
			}
		});

		new Handler().postDelayed(new Runnable() {

			public void run() {
				svr.setAudioVol(0, 0);
			}

		}, 2500);

		return dlg;
	}

	/*
	 * 移除dialog
	 */
	public static void dismissDialogC(AlertDialog mAlertDialog) {

		if (mAlertDialog != null && mAlertDialog.isShowing()) {
			mAlertDialog.cancel();
			mAlertDialog = null;
			svr.setAudioVol(svr.getMaxAlarmCurrentVol(),
					svr.getMaxMusicCurrentVol());// 音量恢复正常
		}
	}

	/**
	 * 发送广播，在界面上进行修改mainActivity
	 */
	public static void processBroadcast(Context context, String str) {
		Intent intentAction = new Intent(Constants.MSG_SEND);
		intentAction.putExtra("NMESSAGE", str);
		context.sendBroadcast(intentAction);
	}

	/**
	 * @param command
	 *            具体指令
	 * @param content
	 *            指令内容
	 * @param mContext
	 *            上下文对象
	 * @param action
	 *            外层action
	 */
	public static void sendMsg(int command, String content, Context mContext,
			int action) {
		Gson gson = new Gson();
		DataMoreLevelClass dmlc = new DataMoreLevelClass();

		dmlc.setCommand(command);
		dmlc.setContent(content);

		String str = gson.toJson(dmlc);
		PMessage pm = new PMessage();
		pm.command = action;// 在手机端进行识别
		pm.message = str;
		pm.fromname = "a";
		pm.toname = "b";
		String str2 = gson.toJson(pm);
		processBroadcast(mContext, str2);
	}

	/**
	 * 判断热点是否被开启
	 * 
	 * @param wifiManager
	 * @return
	 */
	public static boolean isWifiApEnabled(WifiManager wifiManager) {
		try {
			Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
			method.setAccessible(true);
			return (Boolean) method.invoke(wifiManager);

		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	// public static Bitmap r_t_bitmap(Context mContext, int resId) {
	// BitmapFactory.Options opt = new BitmapFactory.Options();
	//
	// opt.inPreferredConfig = Bitmap.Config.RGB_565;
	//
	// opt.inPurgeable = true;
	//
	// opt.inInputShareable = true;
	//
	// InputStream is = mContext.getResources().openRawResource(resId);
	//
	// Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
	//
	// BitmapDrawable bd = new BitmapDrawable(getResources(), bm);
	//
	// holder.iv.setBackgroundDrawable(bd);
	// }

	/**
	 * 根据速度显示简版hud动态图
	 * 
	 * @param mContext
	 * @param speed
	 * @return
	 */
	public static int rt_hud_simple_image(Context mContext, float speed) {
		int rt_res_simple = 0;
		if (speed > 0 && speed <= 30) {
			rt_res_simple = R.anim.spinner_30;
		} else if (speed > 30 && speed <= 40) {
			rt_res_simple = R.anim.spinner_40;
		} else if (speed > 40 && speed <= 50) {
			rt_res_simple = R.anim.spinner_50;
		} else if (speed > 50 && speed <= 60) {
			rt_res_simple = R.anim.spinner_60;
		} else if (speed > 60 && speed <= 70) {
			rt_res_simple = R.anim.spinner_70;
		} else if (speed > 70 && speed <= 80) {
			rt_res_simple = R.anim.spinner_80;
		} else if (speed > 80 && speed <= 90) {
			rt_res_simple = R.anim.spinner_90;
		} else if (speed > 90 && speed <= 100) {
			rt_res_simple = R.anim.spinner_100;
		} else if (speed > 100 && speed <= 110) {
			rt_res_simple = R.anim.spinner_110;
		} else if (speed > 110 && speed <= 120) {
			rt_res_simple = R.anim.spinner_120;
		} else {
			rt_res_simple = R.anim.spinner_130_t;
		}

		return rt_res_simple;

	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getCurrentDateTime() {
		SimpleDateFormat sDateFormat2 = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String currenttime = sDateFormat2.format(new java.util.Date());
		return currenttime;
	}

	/**
	 * @param lastTime
	 * @param nextTime
	 * @return 通话时长
	 */
	public static int ringTime(String lastTime, String nextTime) {
		long time = 0;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d1 = df.parse(nextTime);
			Date d2 = df.parse(lastTime);
			long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别
			long days = diff / (1000 * 60 * 60 * 24);

			long hours = (diff - days * (1000 * 60 * 60 * 24))
					/ (1000 * 60 * 60);
			long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours
					* (1000 * 60 * 60))
					/ (1000 * 60);
			time = hours * 3600 + minutes * 60;

		} catch (Exception e) {
			time = 0;
		}
		return (int) time;
	}

	public static int retNearby_loc(Context context, int index) {
		int ret = R.drawable.nearby_01;
		switch (index) {
		case 0:
			ret = R.drawable.nearby_01;
			break;
		case 1:
			ret = R.drawable.nearby_02;
			break;
		case 2:
			ret = R.drawable.nearby_03;
			break;
		case 3:
			ret = R.drawable.nearby_04;
			break;
		case 4:
			ret = R.drawable.nearby_05;
			break;

		default:
			ret = R.drawable.nearby_01;
			break;
		}
		return ret;

	}

	/**
	 * 判断当前是否有车主
	 * 
	 * @return
	 */
	public static boolean CarClientIsConnect(HashMap<Socket, String> map) {
		Iterator<Entry<Socket, String>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object val = entry.getValue();
			if (val != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断当前是否有车主
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Socket carClient(HashMap<Socket, String> map) {
		Iterator<Entry<Socket, String>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object val = entry.getValue();
			if (val != null) {

				return (Socket) entry.getKey();
			}
		}
		return null;
	}

	/**
	 * 读取asset目录下文件。
	 * 
	 * @return content
	 */
	public static String readFile(Context mContext, String file, String code) {
		int len = 0;
		byte[] buf = null;
		String result = "";
		try {
			InputStream in = mContext.getAssets().open(file);
			len = in.available();
			buf = new byte[len];
			in.read(buf, 0, len);

			result = new String(buf, code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}

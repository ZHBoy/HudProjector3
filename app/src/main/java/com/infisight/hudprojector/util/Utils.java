package com.infisight.hudprojector.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class Utils {
	public static final String DAY_NIGHT_MODE = "daynightmode";
	public static final String DEVIATION = "deviationrecalculation";
	public static final String JAM = "jamrecalculation";
	public static final String TRAFFIC = "trafficbroadcast";
	public static final String CAMERA = "camerabroadcast";
	public static final String SCREEN = "screenon";
	public static final String THEME = "theme";
	public static final String ISEMULATOR = "isemulator";

	public static final String ACTIVITYINDEX = "activityindex";

	public static final int SIMPLEHUDNAVIE = 0;
	public static final int EMULATORNAVI = 1;
	public static final int SIMPLEGPSNAVI = 2;
	public static final int SIMPLEROUTENAVI = 3;

	public static final boolean DAY_MODE = false;
	public static final boolean NIGHT_MODE = true;
	public static final boolean YES_MODE = true;
	public static final boolean NO_MODE = false;
	public static final boolean OPEN_MODE = true;
	public static final boolean CLOSE_MODE = false;

	/**
	 * 以秒换算小时和分钟
	 * 
	 * @return
	 */
	public static String getHourMinuteBySecond(int second) {
		int h = 0;
		int d = 0;
		int s = 0;
		int temp = second % 3600;
		if (second > 3600) {
			h = second / 3600;
			if (temp != 0) {
				if (temp > 60) {
					d = temp / 60;
					if (temp % 60 != 0) {
						s = temp % 60;
					}
				} else {
					s = temp;
				}
			}
		} else {
			d = second / 60;
			if (second % 60 != 0) {
				s = second % 60;
			}
		}
		String strformat = String.format("%02d:%02d:%02d", h, d, s);
		return strformat;
	}

	/**
	 * 换算千米
	 * 
	 * @return
	 */
	public static String getKileMeters(int mile) {
		String dataret = "0.0";
		String unitret = "千米";
		dataret = new DecimalFormat("0.000").format(mile * 0.001);
		return dataret + unitret;
	}

	/**
	 * 
	 * @param second
	 *            还剩余的秒数
	 * @return
	 */
	public static String getArriveTime(int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, second);
		Log.d("Utils", "second:" + second);
		SimpleDateFormat FORMAT2 = new SimpleDateFormat("MM-dd HH:mm:ss");
		return FORMAT2.format(calendar.getTime());
	}

	/**
	 * 获取当前时间 format:yyyy-MM-dd HH:mm:ss
	 * */
	public static String getCurrentTime(String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(new Date());
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 保存数据的方法，我们�?要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 * 
	 * @param context
	 * @param key
	 * @param object
	 */
	public static void put(Context context, String spname, String key,
			Object object) {

		SharedPreferences sp = context.getSharedPreferences(spname,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		if (object instanceof String) {
			editor.putString(key, (String) object);
		} else if (object instanceof Integer) {
			editor.putInt(key, (Integer) object);
		} else if (object instanceof Boolean) {
			editor.putBoolean(key, (Boolean) object);
		} else if (object instanceof Float) {
			editor.putFloat(key, (Float) object);
		} else if (object instanceof Long) {
			editor.putLong(key, (Long) object);
		} else {
			editor.putString(key, object.toString());
		}

		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取�?
	 * 
	 * @param context
	 * @param spname
	 * @param key
	 * @param defaultObject
	 * @return
	 */
	public static Object get(Context context, String spname, String key,
			Object defaultObject)throws Exception {
		SharedPreferences sp = context.getSharedPreferences(spname,
				Context.MODE_PRIVATE);

		if (defaultObject instanceof String) {
			return sp.getString(key, (String) defaultObject);
		} else if (defaultObject instanceof Integer) {
			return sp.getInt(key, (Integer) defaultObject);
		} else if (defaultObject instanceof Boolean) {
			return sp.getBoolean(key, (Boolean) defaultObject);
		} else if (defaultObject instanceof Float) {
			return sp.getFloat(key, (Float) defaultObject);
		} else if (defaultObject instanceof Long) {
			return sp.getLong(key, (Long) defaultObject);
		}

		return null;
	}

	/**
	 * 创建�?个解决SharedPreferencesCompat.apply方法的一个兼容类
	 * 
	 * @author zhy
	 * 
	 */
	private static class SharedPreferencesCompat {
		private static final Method sApplyMethod = findApplyMethod();

		/**
		 * 反射查找apply的方�?
		 * 
		 * @return
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private static Method findApplyMethod() {
			try {
				Class clz = SharedPreferences.Editor.class;
				return clz.getMethod("apply");
			} catch (NoSuchMethodException e) {
			}

			return null;
		}

		/**
		 * 如果找到则使用apply执行，否则使用commit
		 * 
		 * @param editor
		 */
		public static void apply(SharedPreferences.Editor editor) {
			try {
				if (sApplyMethod != null) {
					sApplyMethod.invoke(editor);
					return;
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			editor.commit();
		}
	}

	/**
	 * 判断网络是否可用
	 */
	public static boolean isNetworkAvailable(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					System.out.println(i + "===状态==="
							+ networkInfo[i].getState());
					System.out.println(i + "===类型==="
							+ networkInfo[i].getTypeName());
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断当前wifi是否已连接上（注意：不是判断wifi接口是否打开）
	 * @param myContext
	 * @return 已成功连接上wifi返回true
	 */
	public static boolean isWifiEnabled(Context myContext) {
        if (myContext == null) {
            throw new NullPointerException("Global context is null");
        }
        WifiManager wifiMgr = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            ConnectivityManager connManager = (ConnectivityManager) myContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiInfo.isConnected();
        } else {
            return false;
        }
    }
	
	/**
	 * 获得当前连接上的wifi名字也就是SSID（注：请先确认wifi已连接上）
	 * @param context
	 * @return
	 */
	public static String getWifiSSID(Context context){
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
		WifiInfo info = wifi.getConnectionInfo();
		String ssid = info.getSSID();
		return ssid;
	}
	
	
}

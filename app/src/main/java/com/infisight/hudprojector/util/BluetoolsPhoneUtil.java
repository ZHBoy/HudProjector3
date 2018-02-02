package com.infisight.hudprojector.util;

import java.io.FileOutputStream;

import android.util.Log;

public class BluetoolsPhoneUtil {
	static String TAG = "MainActivity";
	private static String filePath = "/sys/class/gpio_ctrl_class/gpio_ctrl_device/gpiopd28";

	/**
	 * 操作Bluetooth
	 */
	public static void ctrlLedLight(String flag) {
		try {
			FileOutputStream outputStream = new FileOutputStream(filePath);
			outputStream.write(flag.getBytes());
			outputStream.flush();
			outputStream.close();
		} catch (Exception e) {
			Log.d(TAG, "Exception:" + e.getMessage());
		} finally {
		}
	}

	/**
	 * 开机的时候打开Bluetooth
	 */
	public static void startLedLight() {
		try {
			ctrlLedLight("1");
			Thread.sleep(5000);
			ctrlLedLight("0");
		} catch (Exception e) {
		}
	}

	/**
	 * 接听/挂断电话时 改变Bluetooth的状态
	 */
	public static void changeLedLight() {
		try {
			ctrlLedLight("1");
			Thread.sleep(200);
			ctrlLedLight("0");
		} catch (Exception e) {
		}
	}
}

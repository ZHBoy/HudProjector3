package com.infisight.hudprojector.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	public static void show(Context context, String info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}

	public static void show(Context context, int info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}

	public static void show2(Context context, String info) {
		Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
	}
}
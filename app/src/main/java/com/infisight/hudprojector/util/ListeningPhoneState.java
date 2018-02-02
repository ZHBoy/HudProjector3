package com.infisight.hudprojector.util;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class ListeningPhoneState {

	// 监听电话
	public static void MonitorPshone(Context mContext) {
		TelephonyManager tManager;
		// 获取系统的TelephonyManager对象,需要从
		tManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		// 创建一个通话状态监听器
		PhoneStateListener pListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String number) {
				// TODO Auto-generated method stub
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:// 无任何状态，或者为挂机状态，这个状态捕捉不到电话号码
					String stringIntentAction = Constants.MSG_SEND;
					Intent intentAction = new Intent(stringIntentAction);
					intentAction.putExtra(Constants.MSG_CMD,
							Constants.TELEPHONY_IDLE);
					intentAction.putExtra(Constants.MSG_PARAM, "");
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:// 接听来电 2 这个状态捕捉不到电话号码
					String stringIntentOffHookAction = Constants.MSG_SEND;
					Intent intentOffHookAction = new Intent(
							stringIntentOffHookAction);
					intentOffHookAction.putExtra(Constants.MSG_CMD,
							Constants.TELEPHONY_OFFHOOK);
					intentOffHookAction.putExtra(Constants.MSG_PARAM, "");
					break;
				case TelephonyManager.CALL_STATE_RINGING:// 来电 1，这个有电话

				default:
					break;
				}
				super.onCallStateChanged(state, number);
			}
		};
		// 为tManager添加监听器
		tManager.listen(pListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
}

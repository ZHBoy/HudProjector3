package com.infisight.hudprojector.receiver;

import com.infisight.hudprojector.service.TrafficService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class StrartingReceiver extends BroadcastReceiver {
//	static final String action_boot="android.intent.action.BOOT_COMPLETED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
//		if (intent.getAction().equals(action_boot)) {
			Intent service = new Intent(context, TrafficService.class);
			context.startService(service);
//		}
	}
}

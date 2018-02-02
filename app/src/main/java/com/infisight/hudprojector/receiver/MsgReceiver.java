package com.infisight.hudprojector.receiver;

import com.infisight.hudprojector.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

/**
 * 广播接收中心
 * 
 * @author Administrator
 * 
 */
public class MsgReceiver extends BroadcastReceiver {
	private WifiP2pManager manager;
	private Channel channel;
	private MainActivity activity;

	public MsgReceiver() {
	}

	public MsgReceiver(WifiP2pManager manager, Channel channel,
			MainActivity activity) {
		this.manager = manager;
		this.channel = channel;
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {// 开机启动该项目，到时候先进入什么页面再考虑
			Intent bootActivityIntent = new Intent(context, MainActivity.class);
			bootActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(bootActivityIntent);
		}
		// else if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
		// {
		// int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
		// if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
		// // Wifi Direct mode is enabled
		// activity.setIsWifiP2pEnabled(true);
		// } else {
		// activity.setIsWifiP2pEnabled(false);
		// }
		// }
	}

}

package com.infisight.hudprojector.activity;

import com.infisight.hudprojector.R;
import com.infisight.hudprojector.util.Constants;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
/**
 * 新车记录仪相关的内容。
 * @author Administrator
 *
 */
public class CameraActivity extends Activity{
	String TAG = "CameraActivity";
	final IntentFilter intentFilter = new IntentFilter();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(mNewMsgReceiver, makeNewMsgIntentFilter());
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(mNewMsgReceiver);
	}
	/**
	 * 广播接收器,需要对控件进行编号，然后按照顺序选择 ，先选择上下，在选择左右 ，比较复杂的手势操作，涉及到数据派发的问题
	 * 判断当前显示内容，在决定向哪里发送数据
	 */
	 BroadcastReceiver mNewMsgReceiver = new BroadcastReceiver() {// 如果是地图页面的显示，就实现数据的分发。
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			final String action = intent.getAction();
			if (Constants.MSG_NEW.equals(action)) {
				String strCmd = intent.getStringExtra(Constants.MSG_CMD);
				String strMsg = intent.getStringExtra(Constants.MSG_PARAM);
				Log.d(TAG, "strCmd:" + strCmd);
				Log.d(TAG, "strMsg:" + strMsg);
				if (Constants.GESTURE_INFO.equals(strCmd)) {
					if (strMsg.equals(Constants.GESTURE_TOLEFT))// 向左
					{
						// 实现消息的分发,更新主菜单上的状态
					}else if(strMsg.equals(Constants.GESTURE_TORIGHT))// 向右
					{
						
					}
					else if(strMsg.equals(Constants.GESTURE_TOUP))// 向上
					{
						
					}
					else if(strMsg.equals(Constants.GESTURE_TODOWN))// 向下
					{
						
					}
					else if(strMsg.equals(Constants.GESTURE_SINGLETAP))// 单击
					{
						
					}
					else if(strMsg.equals(Constants.GESTURE_DOUBLECLICK))// 双击
					{
						
					}
					else if(strMsg.equals(Constants.GESTURE_DOUBLECLICK))// 双击
					{
						
					}
					else if(strMsg.equals(Constants.GETSTURE_BACK))// 双击
					{
						
					}
				}
			}
		}
	 };
	/**
	 * 广播过滤器
	 */
	private static IntentFilter makeNewMsgIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.MSG_NEW);
		return intentFilter;
	}
	
	
}

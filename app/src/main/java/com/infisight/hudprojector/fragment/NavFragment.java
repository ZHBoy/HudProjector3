package com.infisight.hudprojector.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

/**
导航显示模块，估计还需要一个广播接收器，来实现广播的接收
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class NavFragment extends Fragment implements OnClickListener{
	String TAG = "NavFragment";
	 // 导航数据

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
    
}

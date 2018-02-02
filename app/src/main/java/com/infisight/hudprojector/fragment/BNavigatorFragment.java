package com.infisight.hudprojector.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BNavigatorFragment extends Fragment{
	LinearLayout ll_nav;
	TextView tv_remainTime,tv_remainDistance,tv_maneuserDistance,tv_nextRoadName,tv_currentRoadName,tv_limitedSpeed,tv_assistantDistance;
	TextView tv_serviceAreaDistance,tv_serviceArea;
	ImageView iv_maneuverIcon,iv_currentSpeed,iv_assistantType;
	Bundle mBundle = null;
	public static BNavigatorFragment newInstance(Bundle args)//
	{
		BNavigatorFragment fragment = new BNavigatorFragment();
		fragment.setArguments(args);
		return fragment;
	}
	public BNavigatorFragment()
	{}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(getArguments() != null)
		{
			mBundle = getArguments();
		}
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
}

package com.infisight.hudprojector.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.data.SpeechVoiceData;
import com.infisight.hudprojector.kdxfspeech.SpeechVoiceRecognition;
import com.infisight.hudprojector.service.BackgroundRecorder;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;
import com.infisight.hudprojector.util.RecordUtils;

/**
 * @author hao 首页fragment
 * 
 */
public class HomePJFragment extends Fragment implements OnClickListener {
	public String TAG = "HomePJFragment";
	ImageView fh_im_home_navi, fh_im_home_obd, fg_im_home_music,
			fg_im_home_phone;
	ImageView fh_im_home_recorder, fh_im_home_app, fg_im_home_setting,
			fg_im_home_fm;
	SpeechVoiceRecognition svr = null;
	Gson gson = null;

	public static HomePJFragment newInstance() {
		HomePJFragment fragment = new HomePJFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public HomePJFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		init();
		initClickListener();
		// svr = SpeechVoiceRecognition.getInstance(getActivity());
		// if (CommonUtil.isConnect(getActivity())) {
		// MainActivity.svr.startSpeaking(Constants.F_C_WHAT_YOU_DO);
		// }

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_home, container, false);
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {

		fh_im_home_navi = (ImageView) getActivity().findViewById(
				R.id.fh_im_home_navi);// 导航

		fh_im_home_obd = (ImageView) getActivity().findViewById(
				R.id.fh_im_home_obd);// obd

		fg_im_home_music = (ImageView) getActivity().findViewById(
				R.id.fg_im_home_music);// 音乐

		fg_im_home_phone = (ImageView) getActivity().findViewById(
				R.id.fg_im_home_phone);// 电话
		fh_im_home_recorder = (ImageView) getActivity().findViewById(
				R.id.fh_im_home_recorder);// 电话
		fh_im_home_app = (ImageView) getActivity().findViewById(
				R.id.fh_im_home_app);// 电话
		fg_im_home_setting = (ImageView) getActivity().findViewById(
				R.id.fg_im_home_setting);// 设置
		fg_im_home_fm = (ImageView) getActivity().findViewById(
				R.id.fg_im_home_fm);// 豆瓣
	}

	/**
	 * 初始化相关的按钮事件
	 */
	private void initClickListener() {
		fh_im_home_navi.setOnClickListener(this);
		fh_im_home_obd.setOnClickListener(this);
		fg_im_home_music.setOnClickListener(this);
		fg_im_home_phone.setOnClickListener(this);
		fh_im_home_recorder.setOnClickListener(this);
		fh_im_home_app.setOnClickListener(this);
		fg_im_home_setting.setOnClickListener(this);
		fg_im_home_fm.setOnClickListener(this);
	}

	BroadcastReceiver homePjReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			gson = new Gson();
		}

	};

	/**
	 * 广播过滤器
	 */
	private static IntentFilter makeNewMsgIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		return intentFilter;
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onResume() {
		getActivity()
				.registerReceiver(homePjReceiver, makeNewMsgIntentFilter());
		super.onResume();

	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(homePjReceiver);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		SpeechVoiceData svd = new SpeechVoiceData();
		switch (v.getId()) {
		case R.id.fh_im_home_navi:
			svd.setCommand(Constants.MAIN_NAVI_ACTION);
			svd.setValue(Constants.CONTROL_MAIN_KEY);
			try {
				CommonUtil.processBroadcast(getActivity(), svd);
			} catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			break;
		case R.id.fh_im_home_obd:
			svd.setCommand(Constants.MAIN_OBD_ACTION);
			svd.setValue(Constants.CONTROL_MAIN_KEY);
			try {
				CommonUtil.processBroadcast(getActivity(), svd);
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			break;
		case R.id.fg_im_home_music:
			svd.setCommand(Constants.MAIN_MUSIC_ACTION);
			svd.setValue(Constants.CONTROL_MAIN_KEY);
			try {
				CommonUtil.processBroadcast(getActivity(), svd);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case R.id.fg_im_home_phone:
			svd.setCommand(Constants.MAIN_PHONE_ACTION);
			svd.setValue(Constants.CONTROL_MAIN_KEY);
			try {
				CommonUtil.processBroadcast(getActivity(), svd);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.fh_im_home_recorder:
//			Log.i(TAG, RecordUtils.isServiceRunning(getActivity(),
//					"com.infisight.hudprojector.service.BackgroundRecorder")+"");
//			if (RecordUtils.isServiceRunning(getActivity(),
//					"com.infisight.hudprojector.service.BackgroundRecorder") == true) {
//				Intent intentService = new Intent(getActivity(),
//						BackgroundRecorder.class);
//				getActivity().stopService(intentService);
//			}
//			svd.setCommand(Constants.MAIN_GORECORDER_ACTION);
//			svd.setValue(Constants.CONTROL_MAIN_KEY);
//			try {
//				CommonUtil.processBroadcast(getActivity(), svd);
//			} catch (Exception e3) {
//				// TODO Auto-generated catch block
//				e3.printStackTrace();
//			}
			break;
		case R.id.fh_im_home_app:
			break;
		case R.id.fg_im_home_setting:
			Bundle bundle = new Bundle();
			SettingFragment sf = new SettingFragment();
			sf.setArguments(bundle);
			getFragmentManager().beginTransaction()
					.replace(R.id.info_container, sf).commit();
			// svd.setCommand(Constants.MAIN_SETTING_ACTION);
			// svd.setValue(Constants.CONTROL_MAIN_KEY);
			// try {
			// CommonUtil.processBroadcast(getActivity(), svd);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			break;
		case R.id.fg_im_home_fm:
			svd.setCommand(Constants.MAIN_DOUBAN_ACTION);
			svd.setValue(Constants.CONTROL_MAIN_KEY);
			try {
				CommonUtil.processBroadcast(getActivity(), svd);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;

		}
		Intent intentAction = new Intent(Constants.MAIN_KEY_SHOW_ACTION);
		intentAction.putExtra("isShow", false);
		getActivity().sendBroadcast(intentAction);
	}
}

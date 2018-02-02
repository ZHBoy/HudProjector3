package com.infisight.hudprojector.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.data.ObdCmdsConfigClass;
import com.infisight.hudprojector.data.SpeechVoiceData;
import com.infisight.hudprojector.data.VoiceDataClass;
import com.infisight.hudprojector.kdxfspeech.SpeechVoiceRecognition;
import com.infisight.hudprojector.service.MusicService;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 显示HUD的Fragment
 * 
 * @author Administrator
 * 
 */
public class OBDFragment extends Fragment {
	protected static final String TAG = "OBDFragment";

	// SpeechVoiceRecognition svr = null;
	Gson gson;
	TextView tv_noobdinfo;
	LinearLayout ll_left, ll_right;
	int CONTROLID = 10001;
	HashMap<String, Integer> hmControlDes = new HashMap<String, Integer>();
	SharedPreferences sp;

	/**
	 * 创建唯一实例
	 * 
	 * @return
	 */
	public static OBDFragment newInstance(String hudInfo) {
		OBDFragment fragment = new OBDFragment();
		Bundle args = new Bundle();
		args.putString("hudInfo", hudInfo);
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * 构造函数
	 */
	public OBDFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 获取参数
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_obd, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		InitView();
		sp = getActivity().getSharedPreferences(Constants.FILE_BG_MUSIC_INFO, 0);
		// svr = SpeechVoiceRecognition.getInstance(getActivity());
	}

	private void InitView() {
		tv_noobdinfo = (TextView) getActivity().findViewById(R.id.tv_noobdinfo);// 提示没有OBD数据信息
		ll_left = (LinearLayout) getActivity().findViewById(R.id.ll_left);// 左列的OBD数据信息
		ll_right = (LinearLayout) getActivity().findViewById(R.id.ll_right);// 右列的OBD数据信息

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(mObdReceiver);
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		getActivity().registerReceiver(mObdReceiver, makeIntentFilter());
		super.onResume();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private static IntentFilter makeIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.OBD_HUD_DATA);
		intentFilter.addAction(Constants.COMMON_UTIL_ACTION);
		intentFilter.addAction(Constants.MAIN_OBD_ACTION);
		return intentFilter;
	}

	// 如果数据已经改变，就重绘制，否则，就不再重新绘制了
	private BroadcastReceiver mObdReceiver = new BroadcastReceiver() {

		@SuppressLint("ResourceAsColor")
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			final String action = intent.getAction();
			gson = new Gson();
			SpeechVoiceData svd;
			VoiceDataClass vdc;
			Log.d("obd", "bod:-----" + action);
			try {
				// 显示手机端的obd
				if (Constants.OBD_HUD_DATA.equals(action)) {
					Log.d("obd", "bod:" + action);
					String strMsg = intent.getStringExtra(Constants.MSG_PARAM);
					int position = 0;
					Gson gson = new Gson();
					List<ObdCmdsConfigClass> lstObd = new ArrayList<ObdCmdsConfigClass>();
					Log.d("obd", "strMsg" + strMsg);
					try {
						lstObd = gson.fromJson(strMsg,
								new TypeToken<List<ObdCmdsConfigClass>>() {
								}.getType());
						Log.d("obd", "lstObd" + lstObd);
					} catch (Exception e) {
					}
					if (lstObd.size() > 0) {
						Log.d("obd", "lstObd.size()" + lstObd.size());
						CONTROLID = 10001;
						tv_noobdinfo.setVisibility(View.GONE);
						ll_left.setVisibility(View.VISIBLE);
						if (lstObd.size() > 1) {
							ll_right.setVisibility(View.VISIBLE);
						}
					} else {
						tv_noobdinfo.setVisibility(View.VISIBLE);
						ll_right.setVisibility(View.GONE);
					}
					for (ObdCmdsConfigClass occc : lstObd) {
						if (position % 2 == 0) {
							updateOBDView(true, CONTROLID, occc.getData(),
									occc.getDesc());
						} else {
							updateOBDView(false, CONTROLID, occc.getData(),
									occc.getDesc());
						}
						position++;
						CONTROLID++;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/**
	 * 依据数据更新OBD的显示界面
	 */
	private void updateOBDView(boolean isLeft, int controlId, String data,
			String desc) {
		// LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
		// LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT); //先设置WeightSum
		// ll_left.setWeightSum(3);
		// RelativeLayout.LayoutParams rlp = new
		// RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		// RelativeLayout newrl = new RelativeLayout(getActivity());
		// // newrl.setLayoutParams(rlp);
		// TextView newtvtip = new TextView(getActivity());
		// RelativeLayout.LayoutParams rlptip = new
		// RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		// newtvtip.setLayoutParams(rlptip);
		// newtvtip.
		// TextView newtvcontent = new TextView(getActivity());
		int controlId2 = 0;
		if (hmControlDes.keySet().contains(desc))
			controlId2 = hmControlDes.get(desc);
		try {
			((TextView) getActivity().findViewById(controlId2)).setText(data);
		} catch (Exception e) {
			View newl = LayoutInflater.from(getActivity()).inflate(
					R.layout.item_obddata, null);
			TextView newtvtip = (TextView) newl.findViewById(R.id.tv_datatip);
			TextView newtvcontent = (TextView) newl
					.findViewById(R.id.tv_datacontent);
			newtvcontent.setId(controlId);
			newtvtip.setText(desc);
			newtvcontent.setText(data);
			hmControlDes.put(desc, controlId);
			if (isLeft) {
				ll_left.addView(newl);
			} else {
				ll_right.addView(newl);
			}
		}

	}

	/**
	 * 依据新的数据来更新OBD的数据,需要将ID和
	 */
	private void updateOBDData() {

	}

	// 当fragment被被替换时，这是最后一个被调用的方法
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
}

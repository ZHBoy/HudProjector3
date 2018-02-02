package com.infisight.hudprojector.fragment;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.infisight.hudprojector.R;
import com.infisight.hudprojector.data.TrafficInfo;
import com.infisight.hudprojector.db.DbManager;
import com.infisight.hudprojector.service.TrafficService;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;
import com.infisight.hudprojector.util.Utils;
import com.infisight.hudprojector.widget.Geomark;
import com.infisight.hudprojector.widget.RainAnimotion;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class TrafficFragment extends Fragment {
	private static final String SERVICE_SP = "service_sp";
	private static final String SERVICE_LAST_TRAFFIC = "service_last_traffic";
	private LinearLayout mCloumLayout;
	private ArrayList<TrafficInfo> infos = new ArrayList<TrafficInfo>();
	private boolean isGeomark = true;
	private Geomark mGeomark;
	private RainAnimotion mRain;
	private TextView mTipTextView;
	private String type = "移动流量";
	private String graph = "折线图";
	private DbManager mDbManager;
	private String networkType = "";
	private long ret = (long) 0.0;
	private long totalGPRS = 0;// 记录当月GPRS总流量
	private long total = 0;// 记录当月WIFI总流量
	// private Uri uri = Uri.parse("content://telephony/carriers");//GPRS接口关闭中用到
	private long maxTraffic = 500 * 1024 * 1024;// 默认流量警报为500M

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			int id = view.getId();
			switch (id) {
			case R.id.traffic_gprs:
				type = "移动流量";
				infos = mDbManager.queryTotal(DbManager.NETWORK_TYPE_MOBILE);
				mGeomark.setData(infos);
				mRain.setData(infos);
				mTipTextView.setText(type + graph);
				if (isGeomark) {
					mGeomark.setVisibility(View.GONE);
					mGeomark.setVisibility(View.VISIBLE);
				} else {
					mRain.setVisibility(View.GONE);
					mRain.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.traffic_graph_tip_image: {
				if (isGeomark) {
					isGeomark = false;
					graph = "柱状图";
					mTipTextView.setText(type + graph);
					mGeomark.setVisibility(View.GONE);
					mRain.setVisibility(View.VISIBLE);
				} else {
					isGeomark = true;
					graph = "折线图";
					mTipTextView.setText(type + graph);
					mGeomark.setVisibility(View.VISIBLE);
					mRain.setVisibility(View.GONE);
				}
				break;
			}
			case R.id.traffic_wifi:
				type = "wifi数据";
				infos = mDbManager.queryTotal(DbManager.NETWORK_TYPE_WIFI);
				mGeomark.setData(infos);
				mRain.setData(infos);
				mTipTextView.setText(type + graph);
				if (isGeomark) {
					mGeomark.setVisibility(View.GONE);
					mGeomark.setVisibility(View.VISIBLE);
				} else {
					mRain.setVisibility(View.GONE);
					mRain.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.set_maxtraffic:
//				setMaxTraffic();
			default:
				break;
			}
		}
	};
	
	public View onCreateView(LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_traffic, container, false);
	};
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Intent intent = new Intent(getActivity(), TrafficService.class);
		getActivity().startService(intent);
		
		
		mCloumLayout = (LinearLayout) getActivity().findViewById(R.id.traffic_cloum_content);
		mDbManager = new DbManager(getActivity());
		infos = mDbManager.queryTotal(DbManager.NETWORK_TYPE_WIFI);

		networkType = DbManager.NETWORK_TYPE_WIFI;

		String date=Utils.getCurrentTime("yyyy MM");
		total = mDbManager.queryTotalTraffic(networkType, date);
		((TextView) getActivity().findViewById(R.id.traffic_wifi_text))
				.setText(switchUnit(total));
		CommonUtil.sendMsg(Constants.MODEL_TRAFFIC_WIFI_COM, switchUnit(total), getActivity(), Constants.MODEL_TRAFFIC_COM);
		Log.i("MODEL_TRAFFIC", total+"");
		int width = (infos.size() + 1) * 50;
		total = 0;
		infos = mDbManager.queryTotal(DbManager.NETWORK_TYPE_MOBILE);
		networkType = DbManager.NETWORK_TYPE_MOBILE;
		totalGPRS = mDbManager.queryTotalTraffic(networkType, date);
		// total=(long) Utils.get(TrafficActivity.this,networkType + SERVICE_SP,
		// SERVICE_LAST_TRAFFIC, ret);//每天的流量
		((TextView) getActivity().findViewById(R.id.traffic_gprs_text))
				.setText(switchUnit(totalGPRS));
		CommonUtil.sendMsg(Constants.MODEL_TRAFFIC_GPRS_COM, switchUnit(totalGPRS), getActivity(), Constants.MODEL_TRAFFIC_COM);
		Log.i("MODEL_TRAFFIC", totalGPRS+"");
		if (width < (infos.size() + 1) * 50)
			width = (infos.size() + 1) * 50;

		mGeomark = new Geomark(getActivity());
		mGeomark.setData(infos);
		mRain = new RainAnimotion(getActivity());
		mRain.setData(infos);
		mRain.setVisibility(View.GONE);
		if (width < Utils.getScreenWidth(getActivity())) {
			mCloumLayout.addView(mGeomark,
					new LayoutParams(Utils.getScreenWidth(getActivity())+20,
							LayoutParams.WRAP_CONTENT));
			mCloumLayout.addView(mRain,
					new LayoutParams(Utils.getScreenWidth(getActivity()),
							LayoutParams.WRAP_CONTENT));
		} else {
			mCloumLayout.addView(mGeomark, new LayoutParams(width+20,
					LayoutParams.WRAP_CONTENT));
			mCloumLayout.addView(mRain, new LayoutParams(width,
					LayoutParams.WRAP_CONTENT));
		}

		getActivity().findViewById(R.id.traffic_gprs).setOnClickListener(mOnClickListener);
//		getActivity().findViewById(R.id.traffic_titleBar_back).setOnClickListener(
//				mOnClickListener);
		getActivity().findViewById(R.id.traffic_graph_tip_image).setOnClickListener(
				mOnClickListener);
		getActivity().findViewById(R.id.traffic_wifi).setOnClickListener(mOnClickListener);
		getActivity().findViewById(R.id.set_maxtraffic).setOnClickListener(mOnClickListener);
		mTipTextView = (TextView) getActivity().findViewById(R.id.traffic_graph_tip_text);
		
		try {
			maxTraffic=(Long) Utils.get(getActivity(), "maxTraffic", "maxTraffic", (long)500*1024*1024);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (totalGPRS > maxTraffic) {
			trafficWarmingDialog();
		}
		
		super.onActivityCreated(savedInstanceState);
	}

	public String switchUnit(long max) {
		String textString = "0B";
		if (max / 1024 > 0) {
			float value = 1f;
			value = 1024;
			String unit = "k";
			if (max / (1024 * 1024) > 0) {
				value = 1024 * 1024;
				unit = "M";
				if (max / (1024 * 1024 * 1024) > 0) {
					value = 1024 * 1024 * 1024;
					unit = "G";
				}
			}
			float result = max / value;
			BigDecimal b = new BigDecimal(result);// 新建一个BigDecimal
			float displayVar = b.setScale(1, BigDecimal.ROUND_HALF_UP)
					.floatValue();// 进行小数点一位保留处理现实在坐标系上的数值
			textString = displayVar + unit;
		} else {
			textString = max + "b";
		}
		return textString;
	}
	/**
	 * 流量超额提示对话框
	 * @param maxTraffic
	 */
	private void trafficWarmingDialog() {
		Log.i("trafficWarmingDialog", String.valueOf(totalGPRS));
		Log.i("trafficWarmingDialog",String.valueOf(maxTraffic));
		
			DialogInterface.OnClickListener dialogOnclickListener = new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					 case Dialog.BUTTON_POSITIVE:
					// // 关闭GPRS
					 break;
					case Dialog.BUTTON_NEGATIVE:
						// 继续使用GPRS
						break;
					}

				}
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(
					getActivity());
			builder.setTitle("流量警报");
			builder.setMessage("您的流量使用已超过限额");
			builder.setNegativeButton("确定", dialogOnclickListener);
			builder.create().show();
		
	}
	/**
	 * 设置流量上限
	 */
	private void setMaxTraffic(){
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View textEntryView = inflater.inflate(R.layout.dialog_settraffic, null);
		final EditText et=(EditText) textEntryView.findViewById(R.id.et_maxtraffic);
		AlertDialog.Builder builder=new AlertDialog.Builder(
				getActivity());
		builder.setTitle("流量上限设置");
		builder.setView(textEntryView);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				maxTraffic=(Long.valueOf(et.getText().toString()))*1024*1024;
//				Toast.makeText(TrafficActivity.this, String.valueOf(maxTraffic), 0).show();
				Utils.put(getActivity(), "maxTraffic", "maxTraffic", maxTraffic);
			}
		});
		builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.create().show();
	}
	/**
	 * 今日流量监测(暂未调用）
	 * @param date
	 */
	private void todayTrafficDetection(){
		String date=Utils.getCurrentTime("yyyy MM/dd");
		long todayTraffic = mDbManager.queryTotalTraffic(DbManager.NETWORK_TYPE_MOBILE, date);
		if(todayTraffic>(maxTraffic/20)){
			trafficWarmingDialog();
		}
	}
	
//	private void spinnerinit(){
//		mySpinner=(Spinner) findViewById(R.id.spinner1);
//		
//		spinnerList.add(object)
//	}
	

}

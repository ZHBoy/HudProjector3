package com.infisight.hudprojector.fragment;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.infisight.hudprojector.MainActivity;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.adapter.PhoneConnectAdapter;
import com.infisight.hudprojector.common.HudProjectApplication;
import com.infisight.hudprojector.data.DataMoreLevelClass;
import com.infisight.hudprojector.data.PMessage;
import com.infisight.hudprojector.data.PhoneInfoDataClass;
import com.infisight.hudprojector.data.PhoneInfoDataListClass;
import com.infisight.hudprojector.data.SpeechVoiceData;
import com.infisight.hudprojector.data.VoiceDataClass;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;
import com.infisight.hudprojector.widget.CircleImageView;

@SuppressLint("NewApi")
public class PhoneFragment extends Fragment {

	public static String TAG = "PhoneFragment";
	// SpeechVoiceRecognition svr = null;
	Gson gson = null;
	ImageView fp_im_hang_up;
	CircleImageView fp_im_person_icon;
	TextView fp_tv_phone_name, fp_tv_phone_number, fp_tv_phone_location,
			fp_tv_phone_time;
	LinearLayout ph_ll_backgroud;
	PhoneConnectAdapter groupAdapter;
	private PopupWindow popupWindow;
	private ListView lv_group;
	private View view;
	int onTelTime = 0;
	Timer timer = new Timer();
	SharedPreferences sp, sp_is_phone;
	String phone_state = null;
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			onTelTime += msg.what;
			fp_tv_phone_time.setText(CommonUtil.secToTime(onTelTime));
			super.handleMessage(msg);
		}
	};

	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 由于主线程安全，页面的更新需放到主线程中
			Message msg = new Message();
			msg.what = 1;
			handler.sendMessage(msg);
		}
	};

	public static PhoneFragment newInstance() {
		Log.i(TAG, "PhoneFragment newInstance");
		PhoneFragment fragment = new PhoneFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	public PhoneFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		sp = getActivity()
				.getSharedPreferences(Constants.FILE_BG_MUSIC_INFO, 0);
		sp_is_phone = getActivity().getSharedPreferences(
				Constants.FILE_SP_IS_PHONE, 0);
		phone_state = sp_is_phone.getString(Constants.KEY_RING_STATE, null);
		// svr = SpeechVoiceRecognition.getInstance(getActivity());
		init();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		return inflater.inflate(R.layout.fragment_phone, container, false);
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		gson = new Gson();
		ph_ll_backgroud = (LinearLayout) getActivity().findViewById(
				R.id.ph_ll_backgroud);
		fp_im_person_icon = (CircleImageView) getActivity().findViewById(
				R.id.fp_im_person_icon);// 联系人头像

		fp_im_person_icon.setImageResource(R.drawable.ic_launcher);
		fp_tv_phone_name = (TextView) getActivity().findViewById(
				R.id.fp_tv_phone_name);// 联系人姓名
		fp_tv_phone_number = (TextView) getActivity().findViewById(
				R.id.fp_tv_phone_number);// 联系人手机号
		fp_tv_phone_location = (TextView) getActivity().findViewById(
				R.id.fp_tv_phone_location);// 号码归属地
		fp_tv_phone_time = (TextView) getActivity().findViewById(
				R.id.fp_tv_phone_time);// 通话时长
		fp_im_hang_up = (ImageView) getActivity().findViewById(
				R.id.fp_im_hang_up);// 挂断按钮

		fp_im_person_icon.setVisibility(View.GONE);
		fp_im_hang_up.setVisibility(View.GONE);

		fp_im_hang_up.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CommonUtil.sendMsg(Constants.MODEL_PHONE_HANG_UP_COM,
						Constants.TELEPHONY_IDLE, getActivity(),
						Constants.MODEL_PHONE_COM);
				MainActivity.svr.startSpeaking(Constants.F_C_PHONE_HANG_UP);
				sp_is_phone.edit().clear().commit();
				CommonUtil.retHomeFragment(getActivity());
			}
		});
		// 如果正在通话，则显示上次的信息
		if (phone_state != null) {
			PhoneInfoDataClass tc = gson.fromJson(phone_state,
					PhoneInfoDataClass.class);
			fp_tv_phone_name.setText(tc.getPhoneName());
			fp_tv_phone_number.setText(tc.getPhoneNumber());
			fp_tv_phone_location.setText("未知");
			timer.schedule(task, 10, 1000);// 通话时长
			fp_im_person_icon.setImageResource(R.drawable.ic_launcher);
			if (tc.getPhoneName() != null) {
				MainActivity.svr.startSpeaking("您正在和，" + tc.getPhoneName()
						+ "，进行通话");
			} else if (tc.getPhoneNumber() != null) {
				MainActivity.svr.startSpeaking("您正在和，" + tc.getPhoneNumber()
						+ "进行通话，");
			}

		}
		// 如果不是，正常流程
		else {
			Bundle bundle = getArguments();
			if (bundle == null) {
				MainActivity.svr.startSpeaking(Constants.F_C_PHONE_NO_PERSON);
				ph_ll_backgroud.setBackgroundResource(R.drawable.cover_expand);
				return;
			}
			Log.i(TAG, bundle.toString());
			String phoneInfo = bundle.getString("phoneInfos");
			String call_in_phone = bundle.getString("call_in_phone");

			// 电话打出去 联系人名字
			if (phoneInfo != null) {
				CommonUtil.sendMsg(Constants.MODEL_PHONE_NAME_COM, phoneInfo,
						getActivity(), Constants.MODEL_PHONE_COM);
			}
			// 电话打进来 联系人信息
			else if (call_in_phone != null) {
				PhoneInfoDataClass tc = gson.fromJson(call_in_phone,
						PhoneInfoDataClass.class);
				fp_tv_phone_name.setText(tc.getPhoneName());
				fp_tv_phone_number.setText(tc.getPhoneNumber());
				fp_tv_phone_location.setText("未知");
				timer.schedule(task, 10, 1000);// 通话时长
				fp_im_person_icon.setImageResource(R.drawable.ic_launcher);

				if (tc.getPhoneName() != null && tc.getPhoneNumber() != null) {
					phone_state = gson.toJson(tc);
				}
			}
			// 其他情况为异常情况
			else {
				MainActivity.svr.startSpeaking(Constants.F_C_PHONE_NO_PERSON);
				ph_ll_backgroud.setBackgroundResource(R.drawable.cover_expand);
			}
		}
	}

	BroadcastReceiver phoneReceiver = new BroadcastReceiver() {

		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			gson = new Gson();
			final String action = intent.getAction();
			SpeechVoiceData svd;
			VoiceDataClass vdc;
			Log.i(TAG, "phoneReceiver:" + action);
			try {
				if (action != null
						&& (Constants.MAIN_PHONE_ACTION.equals(action)
								|| Constants.COMMON_UTIL_ACTION.equals(action) || Constants.MODEL_PHONE_ACTION
									.equals(action))) {
					Log.i(TAG, "action:" + action);
					// 语音消息的处理
					svd = gson.fromJson(intent.getStringExtra(action),
							SpeechVoiceData.class);
					if (svd != null) {
						if (svd.getValue() != null) {
							vdc = gson.fromJson(svd.getValue(),
									VoiceDataClass.class);
							// 打电话给某人（phoneFragment中）
							if (vdc.getType().equals(Constants.F_R_PHONE_TO[0])) {
								// if (popupWindow != null
								// && popupWindow.isShowing()) {
								// onDismiss();
								// popupWindow.dismiss();
								// }
								CommonUtil.sendMsg(
										Constants.MODEL_PHONE_NAME_COM,
										vdc.getValue(), getActivity(),
										Constants.MODEL_PHONE_COM);

								Log.i(TAG, "vdc.getValue():" + vdc.getValue());
							}
							// 语音挂断
							else if (vdc.getValue() != null
									&& vdc.getValue().equals(
											Constants.F_R_PHONE_HANG_UP[1])) {
								CommonUtil.sendMsg(
										Constants.MODEL_PHONE_HANG_UP_COM,
										vdc.getValue(), getActivity(),
										Constants.MODEL_PHONE_COM);
								CommonUtil.retHomeFragment(getActivity());

							}
							// 选择联系人
							else if (vdc.getType().equals(
									Constants.F_R_CHOOSE_ITEM[0])) {
								int index = CommonUtil.getIndex(vdc.getValue());
								PhoneInfoDataClass pidc = groupAdapter.lists
										.get(index);

								fp_tv_phone_name.setText(pidc.getPhoneName());
								fp_tv_phone_number.setText(pidc
										.getPhoneNumber());
								fp_tv_phone_location.setText(pidc
										.getPhoneLocation());
								timer.schedule(task, 10, 1000);// 通话时长
								if (pidc.getPhonePhoto() != null) {
									fp_im_person_icon.setImageBitmap(pidc
											.getPhonePhoto());
								} else {
									fp_im_person_icon
											.setImageResource(R.drawable.ic_launcher);
								}
								CommonUtil.sendMsg(
										Constants.MODEL_PHONE_ITEM_CHOOSE_COM,
										pidc.getPhoneNumber(), getActivity(),
										Constants.MODEL_PHONE_COM);
								onDismiss();
								popupWindow.dismiss();
								if (pidc != null && pidc.getPhoneName() != null) {
									MainActivity.svr.startSpeaking("正在给"
											+ pidc.getPhoneName() + "打电话");
								}

								if (pidc.getPhoneName() != null
										&& pidc.getPhoneNumber() != null) {
									phone_state = gson.toJson(pidc);
								}
								// 保存接听后的联系人信息
								if (pidc.getPhoneName() != null
										&& pidc.getPhoneNumber() != null) {
									sp_is_phone
											.edit()
											.putString(
													Constants.KEY_RING_STATE,
													gson.toJson(pidc)).commit();
								}
							}
							/*
							 * // 下翻页 else if (vdc.getType().equals(
							 * Constants.F_R_PHONE_PAGE_DOWN[0])) { int footNum
							 * = lv_group.getFooterViewsCount(); if (footNum ==
							 * groupAdapter.lists.size()) {
							 * MainActivity.svr.startSpeaking("已翻至底部"); } else {
							 * lv_group.smoothScrollToPosition(footNum + 1); } }
							 * // 上翻页 else if (vdc.getType().equals(
							 * Constants.F_R_PHONE_PAGE_DOWN[0])) { int footNum
							 * = lv_group.getHeaderViewsCount(); if (footNum ==
							 * 0) { MainActivity.svr.startSpeaking("已翻至顶部"); }
							 * else { lv_group.smoothScrollToPosition(footNum -
							 * 1); } }
							 */
						}

					} else {
						// 呼出电话信息回传
						String strExtra = intent
								.getStringExtra(Constants.MSG_PARAM);
						// 统一的一层封装
						DataMoreLevelClass dmlc = gson.fromJson(strExtra,
								DataMoreLevelClass.class);
						switch (dmlc.getCommand()) {

						// 说联系人
						case Constants.MODEL_PHONE_NAME_COM:
							PhoneInfoDataListClass lsts = gson.fromJson(
									dmlc.getContent(),
									PhoneInfoDataListClass.class);
							Log.i(TAG, "MODEL_PHONE_NAME_COM:"
									+ Constants.MODEL_PHONE_NAME_COM);
							if (lsts.getPhoneInfoDataClass() != null
									&& lsts.getPhoneInfoDataClass().size() != 0) {
								// 没有找到相关联系人
								if (lsts.getPhoneInfoDataClass().size() == 0) {
									MainActivity.svr.startSpeaking("没有找到相关联系人");
									ph_ll_backgroud
											.setBackgroundResource(R.drawable.cover_expand);
								}
								// 只有一个联系人
								else if (lsts.getPhoneInfoDataClass().size() == 1) {
									PhoneInfoDataClass pidc = lsts
											.getPhoneInfoDataClass().get(0);
									Log.i(TAG,
											pidc.getPhoneName()
													+ pidc.getPhoneNumber());
									fp_tv_phone_name.setText(pidc
											.getPhoneName());
									fp_tv_phone_number.setText(pidc
											.getPhoneNumber());
									fp_tv_phone_location.setText(pidc
											.getPhoneLocation());
									timer.schedule(task, 10, 1000);// 通话时长
									if (pidc.getPhonePhoto() != null) {
										fp_im_person_icon.setImageBitmap(pidc
												.getPhonePhoto());
									} else {
										fp_im_person_icon
												.setImageResource(R.drawable.ic_launcher);
									}
									if (pidc.getPhoneName() != null
											&& pidc.getPhoneNumber() != null) {
										phone_state = gson.toJson(pidc);
									}

									// 保存接听后的联系人信息
									if (pidc.getPhoneName() != null
											&& pidc.getPhoneNumber() != null) {
										sp_is_phone
												.edit()
												.putString(
														Constants.KEY_RING_STATE,
														gson.toJson(pidc))
												.commit();
									}
									ph_ll_backgroud
											.setBackgroundResource(R.color.black);
									fp_im_hang_up.setVisibility(View.VISIBLE);
									fp_im_person_icon
											.setVisibility(View.VISIBLE);
								}
								// 多个联系人（展示列表）
								else {
									showWindow(lsts.getPhoneInfoDataClass());
									ph_ll_backgroud
											.setBackgroundResource(R.color.black);
								}
							} else {
								MainActivity.svr.startSpeaking("没有找到相关联系人");
								ph_ll_backgroud
										.setBackgroundResource(R.drawable.cover_expand);
							}
							break;
						// 挂断电话
						case Constants.MODEL_PHONE_HANG_UP_COM:
							timer.cancel();
							onTelTime = 0;
							CommonUtil.retHomeFragment(getActivity());
							sp_is_phone.edit().clear().commit();
						default:
							break;
						}

					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/**
	 * 显示
	 * 
	 * @param parent
	 */
	private void showWindow(List<PhoneInfoDataClass> lists) {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			groupAdapter.lists.clear();
			popupWindow = null;
		}
		LayoutInflater layoutInflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		view = layoutInflater.inflate(R.layout.popupwindow_phone_connect, null);

		lv_group = (ListView) view.findViewById(R.id.pw_connect_info);

		groupAdapter = new PhoneConnectAdapter(lists, getActivity());
		lv_group.setAdapter(groupAdapter);
		// 创建一个PopuWidow对象
		popupWindow = new PopupWindow(view, 600, 500);

		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		WindowManager windowManager = (WindowManager) getActivity()
				.getSystemService(Context.WINDOW_SERVICE);
		// 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
		int xPos = windowManager.getDefaultDisplay().getWidth() / 2
				- popupWindow.getWidth() / 2;

		// 弹出popupwindow，背景变暗
		ColorDrawable cd = new ColorDrawable(0x000000);
		popupWindow.setBackgroundDrawable(cd);
		WindowManager.LayoutParams lp = getActivity().getWindow()
				.getAttributes();
		lp.alpha = 0.4f;
		getActivity().getWindow().setAttributes(lp);

		popupWindow.showAsDropDown(getActivity().findViewById(1), xPos, 0);
	}

	// popupwindow消失之后，背景恢复
	public void onDismiss() {
		WindowManager.LayoutParams lp = getActivity().getWindow()
				.getAttributes();
		lp.alpha = 1f;
		getActivity().getWindow().setAttributes(lp);
	}

	/**
	 * 广播过滤器
	 */
	private static IntentFilter phoneInfoIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.MODEL_PHONE_ACTION);
		intentFilter.addAction(Constants.MODEL_PHONE_NAME_ACTION);
		intentFilter.addAction(Constants.MAIN_PHONE_ACTION);
		intentFilter.addAction(Constants.COMMON_UTIL_ACTION);
		return intentFilter;
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onResume() {
		getActivity().registerReceiver(phoneReceiver, phoneInfoIntentFilter());
		super.onResume();

	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(phoneReceiver);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 发送广播，在界面上进行修改mainActivity
	 */
	public void processBroadcast(Context context, String name) {
		Log.d(TAG, "phoneFragment---------");
		Intent intentAction = new Intent(Constants.MSG_SEND);
		intentAction.putExtra("NMESSAGE", name);
		context.sendBroadcast(intentAction);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		if (popupWindow != null && popupWindow.isShowing()) {
			onDismiss();
			popupWindow.dismiss();
		}
		onTelTime = 0;
		if (timer != null) {
			timer.cancel();
		}
		super.onDestroyView();
	}
}

package com.infisight.hudprojector.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.DriveWayView;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.infisight.hudprojector.MainActivity;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.common.HudProjectApplication;
import com.infisight.hudprojector.data.PhoneInfoDataClass;
import com.infisight.hudprojector.data.SpeechVoiceData;
import com.infisight.hudprojector.data.StartNaviInfo;
import com.infisight.hudprojector.data.VoiceDataClass;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;
import com.infisight.hudprojector.util.WordDataSet;
import com.infisight.hudprojector.widget.CustomProgressDialog;
import com.infisight.hudprojector.widget.Titanic;
import com.infisight.hudprojector.widget.TitanicTextView;
import com.infisight.hudprojector.widget.Typefaces;
import com.infisight.hudprojector.widget.ViewAnimation;

/**
 * 显示HUD的Fragment 之所以在hud中还要进行AMapNaviListener（路径规划），因为onNaviInfoUpdate实时处理信息
 * 
 * @author hao
 * 
 */
@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class HudFragment extends Fragment implements AMapLocationListener,
		OnPoiSearchListener, OnClickListener, AMapNaviViewListener {
	protected static final String TAG = "HudFragment";
	Gson gson;
	AMap mAmap;
	// AMap mAMapAllPath;// 小地图显示总路线
	// MapView mapView;
	StartNaviInfo naviInfos = null;
	// private RouteOverLay mRouteOverLay = null;// 小地图线路
	// RelativeLayout map_view_rl;
	AMapNaviView mAMapNaviView;
	TextView tv_speed_test;
	TextView hm_tv_nearby_bank, hm_tv_search_parking_lot,
			hm_tv_search_gas_station, hm_tv_search_hotel,
			tv_simple_next_road_name, tv_simple_next_road_distance,
			tv_simple_next_road_distance_unit, tv_simple_limit_speed;

	TextView hud_nearby_one, hud_nearby_two, hud_nearby_three, hud_nearby_four;
	TitanicTextView tv_fg_hud_music, tv_fg_hud_music_simple;

	TextView fg_tv_next_roadname2, fg_hud_remainder_distance,
			fg_hud_car_left_path_remain_dis, fg_hud_im_limit_speed,
			fg_hud_distance_unit, fg_hud_car_left_path_remain_dis_util,
			fg_hud_remainder_time_h, fg_hud_remainder_time_m,
			fg_hud_time_unit_m, fg_hud_time_unit_h;// 下一个道路名称
	ImageView fg_im_next_direction2, fg_hud_im_camera, im_simple_camera;// 指示标
	ImageView fg_im_side_info_show, im_hudImage_simple,
			fg_im_simple_side_info_show, hm_im_close_pop, fg_hud_music_simple,
			fg_hud_music, myCustomEnlargedCross;

	LinearLayout ll_hud_simple_view, ll_hud_map_view, ll_step_path,
			ll_whole_path;

	// popupwindow显示详细信息
	LinearLayout ll_auto_add_view;
	LinearLayout layout;

	// 隐藏按钮
	LinearLayout ll_hud_dismiss_view, ll_hud_dismiss_view_simple;

	TextView fg_tv_hud_exit_navi, fg_tv_hud_save_addr, fg_tv_hud_nearby_addr,
			fg_tv_hud_see_plan, fg_tv_hud_exit_navi_simple,
			fg_tv_hud_save_addr_simple, fg_tv_hud_nearby_addr_simple,
			fg_tv_hud_see_plan_simple;
	// SpeechVoiceRecognition svr = null;
	View view = null;
	// ProgressBar fg_bar_distance2;
	private Handler handler = new Handler();

	int ll_hud_dismiss_view_height = 0;

	int progressDistance = 0;

	int totalDistance = 0;

	SharedPreferences sp_bg_music_info, continue_navi, sp_is_phone,
			sp_location_info;// 播放音乐，继续导航信息，电话信息，实时定位信息
	SeekBar seekBar;
	String newHudNaviInfo = null;// 记录新的导航位置
	boolean isEndNavi;
	// 起点终点列表
	private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
	private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
	AMapNavi mAMapNavi;

	MainActivity mainActivity = null;

	int plan = 0;// 用来装载路径规划的策略

	private PoiSearch.Query query;// Poi查询条件类

	String position = null;// 要搜索的地点

	boolean isNaviToSearchPoi = false;// true的话，按下确定键就导航过去

	// private LatLonPoint lp;// 终点位置

	private PoiSearch poiSearch;

	AlertDialog mAlertDialog = null;

	String defineIsOk = null;

	// private MarkerOptions mko;

	private PopupWindow popupWindow = null;// 附近的地点
	private PopupWindow popupWindowNearby = null;// 附近的地点详细

	int width;
	int height;
	boolean isHandlerWork;// 判断是不是自动切换到简版，false为切换
	float currentSpeed = -1;
	AnimationDrawable spinner = null;
	Animation operatingAnim;
	private DriveWayView myDriveWayView;

	Animation mShowAction, mHiddenAction;// 控件显示隐藏的动画
	// 控制指示标志和路口放大图的显示隐藏动画
	ViewAnimation animCustomEnlargedCrossView;
	ViewAnimation animFg_im_next_direction;
	List<Marker> locationMarkerLst;// 附近的地点marker列表
	Marker locationMarker;
	private ArrayList<MarkerOptions> moLst;
	List<PoiItem> poiItemsLsts = null;
	int indexNearbyLoc = 0;
	CustomProgressDialog progressDialog;// 加载条

	/**
	 * 创建唯一实例
	 * 
	 * @return
	 */
	public HudFragment newInstance(String hudInfo) {
		Log.i(TAG, "newInstance");
		HudFragment fragment = new HudFragment();
		Bundle args = new Bundle();
		args.putString("hudInfo", hudInfo);
		fragment.setArguments(args);
		Log.i(TAG, "fragment.setArguments(args)");
		return fragment;
	}

	/**
	 * 构造函数
	 */
	public HudFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		WindowManager wm = getActivity().getWindowManager();
		width = wm.getDefaultDisplay().getWidth();
		height = wm.getDefaultDisplay().getHeight();
		mainActivity = (MainActivity) getActivity();
		mainActivity.SetLayoutGone();
		gson = new Gson();
		view = inflater.inflate(R.layout.fragment_hud2, container, false);
		initHudView();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mAMapNavi = AMapNavi.getInstance(getActivity().getApplicationContext());
		Log.i(TAG, "onActivityCreated");
		sp_bg_music_info = getActivity().getSharedPreferences(
				Constants.FILE_BG_MUSIC_INFO, 0);
		continue_navi = getActivity().getSharedPreferences(
				Constants.FILE_HUD_CONTIUE_NAVI, 0);
		sp_is_phone = getActivity().getSharedPreferences(
				Constants.FILE_SP_IS_PHONE, 0);
		// sp_startNaviInfo =
		// getActivity().getSharedPreferences("StartNaviInfo",
		// 0);
		sp_location_info = getActivity().getSharedPreferences(
				Constants.LOCATION_INFO, 0);
		// svr = SpeechVoiceRecognition.getInstance(getActivity());
		if (sp_is_phone.getString(Constants.KEY_RING_STATE, null) != null) {
			if (ll_hud_map_view.getVisibility() == View.VISIBLE) {
				fg_im_side_info_show.setVisibility(View.VISIBLE);
			} else {
				fg_im_simple_side_info_show.setVisibility(View.VISIBLE);
			}
		}
		InitView(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onSaveInstanceState");
		super.onSaveInstanceState(outState);
		mAMapNaviView.onSaveInstanceState(outState);

	}

	private void initHudView() {
		Log.i(TAG, "initHudView");
		tv_speed_test = (TextView) view.findViewById(R.id.tv_speed_test);

		// map_view_rl = (RelativeLayout) view.findViewById(R.id.map_view_rl);
		// FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) map_view_rl
		// .getLayoutParams();
		// lp.height = height / 3;
		// lp.width = width / 3;
		// map_view_rl.setLayoutParams(lp);
		// mapView = (MapView) view.findViewById(R.id.view_map);
		// mapView.onCreate(savedInstanceState);// 此方法必须重写

		myDriveWayView = (DriveWayView) view.findViewById(R.id.myDriveWayView);
		myCustomEnlargedCross = (ImageView) view
				.findViewById(R.id.myEnlargedCross);

		fg_hud_music_simple = (ImageView) view
				.findViewById(R.id.fg_hud_music_simple);

		fg_hud_music = (ImageView) view.findViewById(R.id.fg_hud_music);

		tv_fg_hud_music = (TitanicTextView) view
				.findViewById(R.id.tv_fg_hud_music);

		tv_fg_hud_music_simple = (TitanicTextView) view
				.findViewById(R.id.tv_fg_hud_music_simple);

		operatingAnim = AnimationUtils.loadAnimation(getActivity(),
				R.anim.rotate);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);

		ll_hud_dismiss_view = (LinearLayout) view
				.findViewById(R.id.ll_hud_dismiss_view);

		ll_hud_dismiss_view_height = ll_hud_dismiss_view.getLayoutParams().height;
		ll_hud_dismiss_view_simple = (LinearLayout) view
				.findViewById(R.id.ll_hud_dismiss_view_simple);

		mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		mShowAction.setDuration(500);
		mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				1.0f);
		mHiddenAction.setDuration(500);

		fg_tv_hud_exit_navi = (TextView) view
				.findViewById(R.id.fg_tv_hud_exit_navi);

		fg_tv_hud_save_addr = (TextView) view
				.findViewById(R.id.fg_tv_hud_save_addr);

		fg_tv_hud_nearby_addr = (TextView) view
				.findViewById(R.id.fg_tv_hud_nearby_addr);

		fg_tv_hud_see_plan = (TextView) view
				.findViewById(R.id.fg_tv_hud_see_plan);

		fg_tv_hud_exit_navi_simple = (TextView) view
				.findViewById(R.id.fg_tv_hud_exit_navi_simple);

		fg_tv_hud_save_addr_simple = (TextView) view
				.findViewById(R.id.fg_tv_hud_save_addr_simple);

		fg_tv_hud_nearby_addr_simple = (TextView) view
				.findViewById(R.id.fg_tv_hud_nearby_addr_simple);

		fg_tv_hud_see_plan_simple = (TextView) view
				.findViewById(R.id.fg_tv_hud_see_plan_simple);

		fg_tv_hud_exit_navi.setOnClickListener(this);

		fg_tv_hud_save_addr.setOnClickListener(this);

		fg_tv_hud_nearby_addr.setOnClickListener(this);

		fg_tv_hud_see_plan.setOnClickListener(this);

		fg_tv_hud_exit_navi_simple.setOnClickListener(this);

		fg_tv_hud_save_addr_simple.setOnClickListener(this);

		fg_tv_hud_nearby_addr_simple.setOnClickListener(this);

		fg_tv_hud_see_plan_simple.setOnClickListener(this);

		seekBar = (SeekBar) view.findViewById(R.id.seekBar);

		ll_hud_map_view = (LinearLayout) view
				.findViewById(R.id.ll_hud_map_view);

		ll_hud_simple_view = (LinearLayout) view
				.findViewById(R.id.ll_hud_simple_view);

		tv_simple_next_road_name = (TextView) view
				.findViewById(R.id.tv_simple_next_road_name);

		tv_simple_next_road_distance = (TextView) view
				.findViewById(R.id.tv_simple_next_road_distance);

		tv_simple_next_road_distance_unit = (TextView) view
				.findViewById(R.id.tv_simple_next_road_distance_unit);

		fg_im_side_info_show = (ImageView) view
				.findViewById(R.id.fg_im_side_info_show);

		fg_im_simple_side_info_show = (ImageView) view
				.findViewById(R.id.fg_im_simple_side_info_show);

		fg_hud_remainder_distance = (TextView) view
				.findViewById(R.id.fg_hud_remainder_distance);

		fg_hud_car_left_path_remain_dis = (TextView) view
				.findViewById(R.id.fg_hud_car_left_path_remain_dis);

		fg_hud_time_unit_m = (TextView) view
				.findViewById(R.id.fg_hud_time_unit_m);

		fg_hud_time_unit_h = (TextView) view
				.findViewById(R.id.fg_hud_time_unit_h);

		fg_tv_next_roadname2 = (TextView) view
				.findViewById(R.id.fg_tv_next_roadname2);

		fg_hud_car_left_path_remain_dis_util = (TextView) view
				.findViewById(R.id.fg_hud_car_left_path_remain_dis_util);

		fg_hud_distance_unit = (TextView) view
				.findViewById(R.id.fg_hud_distance_unit);

		fg_hud_remainder_time_h = (TextView) view
				.findViewById(R.id.fg_hud_remainder_time_h);

		fg_hud_remainder_time_m = (TextView) view
				.findViewById(R.id.fg_hud_remainder_time_m);

		fg_im_next_direction2 = (ImageView) view
				.findViewById(R.id.fg_im_next_direction2);

		ll_step_path = (LinearLayout) view.findViewById(R.id.ll_step_path);

		ll_whole_path = (LinearLayout) view.findViewById(R.id.ll_whole_path);

		// hud_ll_direction_left = (LinearLayout) view
		// .findViewById(R.id.hud_ll_direction_left);
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) fg_im_next_direction2
				.getLayoutParams();
		params.setMargins(0, 0, (width / 4), 0);// 通过自定义坐标来放置你的控件
		fg_im_next_direction2.setLayoutParams(params);

		FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) myCustomEnlargedCross
				.getLayoutParams();
		params2.setMargins(0, 0, (width / 4), 0);// 通过自定义坐标来放置你的控件
		myCustomEnlargedCross.setLayoutParams(params2);

		FrameLayout.LayoutParams params3 = (FrameLayout.LayoutParams) ll_step_path
				.getLayoutParams();
		params3.setMargins(0, 0, (width / 4), 0);// 通过自定义坐标来放置你的控件
		ll_step_path.setLayoutParams(params3);

		FrameLayout.LayoutParams params4 = (FrameLayout.LayoutParams) ll_whole_path
				.getLayoutParams();
		params4.setMargins(0, 0, (width / 4), 0);// 通过自定义坐标来放置你的控件
		ll_whole_path.setLayoutParams(params4);

		fg_hud_im_limit_speed = (TextView) view
				.findViewById(R.id.fg_hud_im_limit_speed);

		tv_simple_limit_speed = (TextView) view
				.findViewById(R.id.tv_simple_limit_speed);

		im_hudImage_simple = (ImageView) view
				.findViewById(R.id.im_hudImage_simple);

		im_hudImage_simple.setBackgroundResource(R.anim.spinner_100);
		AnimationDrawable spinner = (AnimationDrawable) im_hudImage_simple
				.getBackground();
		spinner.setExitFadeDuration(200);
		spinner.start();

		fg_hud_im_camera = (ImageView) view.findViewById(R.id.fg_hud_im_camera);

		im_simple_camera = (ImageView) view.findViewById(R.id.im_simple_camera);

		animCustomEnlargedCrossView = new ViewAnimation(1500);
		animCustomEnlargedCrossView.flag = ViewAnimation.FRONT_ANIM;
		animCustomEnlargedCrossView.setFillAfter(true);
		animFg_im_next_direction = new ViewAnimation(1500);
		animFg_im_next_direction.flag = ViewAnimation.BACK_ANIM;
		animFg_im_next_direction.setFillAfter(true);

	}

	private void InitView(Bundle savedInstanceState) {
		Log.i(TAG, "InitView");
		mAMapNaviView = (AMapNaviView) view.findViewById(R.id.extendnavimap2);
		mAMapNaviView.onCreate(savedInstanceState);
		// AMapNaviViewOptions viewOptions = mAMapNaviView.getViewOptions();
		AMapNaviViewOptions viewOptions = new AMapNaviViewOptions();
		viewOptions.setLayoutVisible(false);
		viewOptions.setNaviNight(true);
		viewOptions.setSettingMenuEnabled(true);// 设置菜单按钮是否在导航界面显示。
		viewOptions.setReCalculateRouteForTrafficJam(true);// 道路堵塞是重新路径规划
		viewOptions.setReCalculateRouteForYaw(true);// 偏航是重新路径规划
		viewOptions.setTilt(90);// 倾斜角度
		viewOptions.setTrafficBarEnabled(false);// 光柱是否显示
		viewOptions.setLaneInfoShow(false);// 显示道路信息
		viewOptions.setAutoChangeZoom(true);// 设置自动改变缩放级别
		viewOptions.setTrafficLine(true);
		// viewOptions.setLeaderLineEnabled(Color.RED); // 牵引线
		// viewOptions.setMonitorCameraEnabled(true);
		// viewOptions.setMonitorCameraBitmap(BitmapFactory.decodeResource(
		// this.getResources(), R.drawable.icon_end));
		viewOptions.setAutoDrawRoute(true);// 是否自动画路
		mAMapNaviView.setViewOptions(viewOptions);
		mAmap = mAMapNaviView.getMap();
		Bundle bundle = getArguments();
		double a = 0.0;
		double b = 0.0;
		String lat = sp_location_info.getString(Constants.LATITUDE, "");
		String lon = sp_location_info.getString(Constants.LONGITUDE, "");
		if (lat != "" && lon != "") {
			a = Double.parseDouble(lat);
			b = Double.parseDouble(lon);
		}
		NaviLatLng start_navi = new NaviLatLng(a, b);
		String con_addr = continue_navi.getString(
				Constants.KEY_CONTIUE_NAVI_ADDRESS, null);

		if (bundle != null) {
			mStartPoints.add(start_navi);
			// 说明是关机后重启
			if (bundle.getString("con_navi_str") != null) {
				int con_plan = continue_navi.getInt(
						Constants.KEY_CONTIUE_NAVI_PLAN, 0);
				plan = con_plan;
				mEndPoints = gson.fromJson(con_addr,
						new TypeToken<List<NaviLatLng>>() {
						}.getType());
			}
			// 正常流程的导航
			else {
				naviInfos = (StartNaviInfo) bundle
						.getSerializable("navi_point_info");
				mEndPoints.add(naviInfos.getNavi_end());
				plan = naviInfos.getRoute_plan();
				Log.d(TAG, "mAMapNavi != null!222222222");
			}
		}
		// 导航中返回首页再回到导航
		else if (con_addr != null) {
			int con_plan = continue_navi.getInt(
					Constants.KEY_CONTIUE_NAVI_PLAN, 0);
			plan = con_plan;
			mEndPoints = gson.fromJson(con_addr,
					new TypeToken<List<NaviLatLng>>() {
					}.getType());
			mStartPoints.add(start_navi);
			Log.d(TAG, "mAMapNavi != null!111111");
		}
		mAMapNavi.setAMapNaviListener(mAMapNaviListener);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		getActivity().unregisterReceiver(gHudReceiver);
		if (mAMapNaviView != null) {
			mAMapNaviView.onPause();
		}
		super.onPause();
		Log.i(TAG, "onPause");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showProgressDialog();
		mAMapNaviView.onResume();
		getActivity().registerReceiver(gHudReceiver, makeNewMsgIntentFilter());
		Log.i(TAG, "onResume");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		dissmissProgressDialog();
		mainActivity.SetLayoutVisible();
		backgroundAlpha(1f);
		// isEndNavi==true的时候则销毁AMapNavi
		if (isEndNavi == true && mAMapNaviView != null) {
			isEndNavi = false;
			progressDistance = 0;
			totalDistance = 0;
			removeSp();
			if (mAMapNavi != null) {
				mAMapNavi.stopNavi();
				mAMapNavi.removeAMapNaviListener(mAMapNaviListener);
				mAMapNavi.destroy();
			}
			removeLastSaveNaviInfo();
		}
		isNaviToSearchPoi = false;
		dismissPopupWindow();
		Log.i(TAG, "onDestroy");
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.i(TAG, "onDestroyView");
		if (mAMapNaviView != null) {
			mAMapNaviView.onDestroy();
		}
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		Log.i(TAG, "onDetach");
	}

	/*
	 * 清除保留熄火后可以继续导航的路径信息
	 */
	public void removeLastSaveNaviInfo() {
		continue_navi.edit().clear().commit();
	}

	/**
	 * 设置添加屏幕的背景透明度
	 * 
	 * @param bgAlpha
	 */
	public void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = getActivity().getWindow()
				.getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		getActivity().getWindow().setAttributes(lp);
	}

	// 设置dialog的长宽
	public WindowManager.LayoutParams getDialogParams(Dialog mDialog) {
		WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
		params.width = 282;
		params.height = 224;
		return params;
	}

	/**
	 * 显示菜单
	 */
	private void showPopupWindow() {
		LinearLayout layout_menu = (LinearLayout) LayoutInflater.from(
				getActivity()).inflate(R.layout.hud_menu, null);
		hm_tv_nearby_bank = (TextView) layout_menu
				.findViewById(R.id.hm_tv_nearby_bank);

		hm_tv_search_gas_station = (TextView) layout_menu
				.findViewById(R.id.hm_tv_search_gas_station);

		hm_tv_search_parking_lot = (TextView) layout_menu
				.findViewById(R.id.hm_tv_search_parking_lot);

		hm_tv_search_hotel = (TextView) layout_menu
				.findViewById(R.id.hm_tv_search_hotel);

		hm_im_close_pop = (ImageView) layout_menu
				.findViewById(R.id.hm_im_close_pop);

		hm_tv_nearby_bank.setOnClickListener(this);
		hm_tv_search_gas_station.setOnClickListener(this);
		hm_tv_search_parking_lot.setOnClickListener(this);
		hm_tv_search_hotel.setOnClickListener(this);
		hm_im_close_pop.setOnClickListener(this);

		popupWindow = new PopupWindow(getActivity());
		popupWindow.setWidth(width / 4);
		popupWindow.setHeight(height / 2);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout_menu);

		int[] location = new int[2];
		ll_hud_dismiss_view.getLocationOnScreen(location);
		popupWindow.setAnimationStyle(R.style.popwin_anim_style);
		popupWindow.showAtLocation(ll_hud_dismiss_view, Gravity.NO_GRAVITY,
				location[0] + width / 2, location[1] - popupWindow.getHeight());//
		Log.i(TAG, "yzhou :" + (location[1] - popupWindow.getHeight()));
		if (ll_hud_simple_view.getVisibility() == View.VISIBLE) {
			fg_tv_hud_exit_navi_simple.setFocusable(false);
			fg_tv_hud_save_addr_simple.setFocusable(false);
			fg_tv_hud_nearby_addr_simple.setFocusable(false);
			fg_tv_hud_see_plan_simple.setFocusable(false);
		} else {
			fg_tv_hud_exit_navi.setFocusable(false);
			fg_tv_hud_save_addr.setFocusable(false);
			fg_tv_hud_nearby_addr.setFocusable(false);
			fg_tv_hud_see_plan.setFocusable(false);
		}
	}

	// 附近的地点的Runnable
	Runnable nearbyRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			dismissNearbyPopupWindow();
		}
	};

	/**
	 * 显示菜单
	 */
	private void showNearbyPopupWindow() {
		if (popupWindowNearby != null && popupWindowNearby.isShowing()) {
			popupWindowNearby.dismiss();
			popupWindowNearby = null;
			handler.removeCallbacks(nearbyRunnable);
		}
		layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(
				R.layout.hud_nearby_loc, null);
		ll_auto_add_view = (LinearLayout) layout
				.findViewById(R.id.ll_auto_add_view);
		ll_auto_add_view.setVisibility(View.VISIBLE);
		// 动态添加view的方式将底线详细加入进去
		if (poiItemsLsts != null && poiItemsLsts.size() > 0) {
			int poiNum = poiItemsLsts.size();
			if (poiNum > 5) {
				poiNum = 5;
			}
			for (indexNearbyLoc = 0; indexNearbyLoc < poiNum; indexNearbyLoc++) {
				String dis[] = CommonUtil.meterTurnKm(poiItemsLsts.get(
						indexNearbyLoc).getDistance());// 距离中心点的距离
				int index = indexNearbyLoc + 1;
				TextView tv = new TextView(getActivity());

				LayoutParams lp = new LayoutParams(
						android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
						android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
						Gravity.CENTER);
				tv.setLayoutParams(lp);

				tv.setTextSize(25);
				tv.setPadding(15, 15, 15, 15);
				tv.setFocusable(true);
				tv.setFocusableInTouchMode(true);
				tv.requestFocus();
				tv.requestFocusFromTouch();
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundResource(R.drawable.control_bg);
				tv.setSingleLine();
				tv.setEllipsize(TruncateAt.MARQUEE);
				tv.setMarqueeRepeatLimit(-1);
				tv.setText(index + "、"
						+ poiItemsLsts.get(indexNearbyLoc).getTitle() + "-"
						+ dis[0] + " " + dis[1]);
				ll_auto_add_view.addView(tv);
				ll_auto_add_view.setFocusableInTouchMode(true);
				tv.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						navi_toNearby_loc(poiItemsLsts.get(indexNearbyLoc - 1));
						indexNearbyLoc = 0;
					}
				});
			}
		}
		popupWindowNearby = new PopupWindow(getActivity());
		popupWindowNearby.setWidth(LayoutParams.WRAP_CONTENT);
		popupWindowNearby.setHeight(LayoutParams.WRAP_CONTENT);
		popupWindowNearby.setOutsideTouchable(true);
		popupWindowNearby.setFocusable(true);
		popupWindowNearby.setContentView(layout);
		popupWindowNearby.showAtLocation(view, Gravity.CENTER, 0, 0);
		handler.postDelayed(nearbyRunnable, 12000);

	}

	/**
	 * 导航选中的附近的点
	 * 
	 * @param poiItem
	 */
	public void navi_toNearby_loc(PoiItem poiItem) {
		try {
			if (poiItem == null) {
				dismissNearbyPopupWindow();
				return;
			}
			NaviLatLng mNaviLatLng = new NaviLatLng(poiItem.getLatLonPoint()
					.getLatitude(), poiItem.getLatLonPoint().getLongitude());
			// String str = "";
			// String name = poiItem.getTitle();
			// if (name != null) {
			// str += "目的地：" + name;
			// }
			// String dis[] = CommonUtil.meterTurnKm(poiItem.getDistance());//
			// 距离中心点的距离
			// if (dis != null) {
			// str += "，直线距离" + dis[0] + dis[1];
			// }
			// String area = poiItem.getBusinessArea();// 所在商圈
			// if (area != null) {
			// str += "位于" + area;
			// }
			// boolean isDiscountInfo = poiItem.isDiscountInfo();// 是否有优惠信息
			// if (isDiscountInfo) {
			// str += "正在搞优惠活动";
			// }
			// if (str != null && str.length() > 0) {
			// MainActivity.svr.startSpeaking(str);
			// } else {
			// return;
			// }
			if (mAMapNavi != null) {
				mAMapNavi.stopNavi();
				mAMapNavi.removeAMapNaviListener(mAMapNaviListener);
				mAMapNavi.destroy();
				mAMapNavi = null;
			}
			Intent mintent = new Intent(Constants.NEW_NAVI_PLAN_ACTION);
			mintent.putExtra(Constants.NAIV_TO_SEARCH_POI_LAT,
					gson.toJson(mNaviLatLng));
			getActivity().sendBroadcast(mintent);
			dismissNearbyPopupWindow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dismissNearbyPopupWindow();
		}

	}

	// 进度条Runnable
	Runnable progressRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (progressDialog != null && progressDialog.isShowing()) {
				dissmissProgressDialog();
				MainActivity.svr.startSpeaking(Constants.F_C_OUT_TIME_SEARCH);
			}
		}
	};

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(getActivity());
			// progressDialog.setMessage(getActivity().getResources().getString(
			// R.string.delay_search));
		}
		progressDialog.show();
		handler.postDelayed(progressRunnable, 12000);
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	/**
	 * 销毁PopWindow(附近的地点)
	 */
	public void dismissPopupWindow() {
		// TODO Auto-generated method stub
		// 要做的事情
		if (popupWindow != null && popupWindow.isShowing())
			popupWindow.dismiss();
		if (ll_hud_simple_view.getVisibility() == View.VISIBLE) {
			ll_hud_dismiss_view_simple.startAnimation(mHiddenAction);
			ll_hud_dismiss_view_simple.setVisibility(View.GONE);
			fg_tv_hud_exit_navi_simple.setFocusable(true);
			fg_tv_hud_save_addr_simple.setFocusable(true);
			fg_tv_hud_nearby_addr_simple.setFocusable(true);
			fg_tv_hud_see_plan_simple.setFocusable(true);
		} else {
			ll_hud_dismiss_view.startAnimation(mHiddenAction);
			ll_hud_dismiss_view.setVisibility(View.GONE);
			fg_tv_hud_exit_navi.setFocusable(true);
			fg_tv_hud_save_addr.setFocusable(true);
			fg_tv_hud_nearby_addr.setFocusable(true);
			fg_tv_hud_see_plan.setFocusable(true);
		}
	}

	/**
	 * 销毁PopWindow(附近的地点详细)
	 */
	public void dismissNearbyPopupWindow() {
		// 恢复缩放级别
		mAmap.moveCamera(CameraUpdateFactory.zoomTo(18));
		mAmap.moveCamera(CameraUpdateFactory.changeTilt(90));
		// 要做的事情
		if (popupWindowNearby != null && popupWindowNearby.isShowing())
			popupWindowNearby.dismiss();
		if (ll_hud_simple_view.getVisibility() == View.VISIBLE) {
			ll_hud_dismiss_view_simple.startAnimation(mHiddenAction);
			ll_hud_dismiss_view_simple.setVisibility(View.GONE);
			fg_tv_hud_exit_navi_simple.setFocusable(true);
			fg_tv_hud_save_addr_simple.setFocusable(true);
			fg_tv_hud_nearby_addr_simple.setFocusable(true);
			fg_tv_hud_see_plan_simple.setFocusable(true);
		} else {
			ll_hud_dismiss_view.startAnimation(mHiddenAction);
			ll_hud_dismiss_view.setVisibility(View.GONE);
			fg_tv_hud_exit_navi.setFocusable(true);
			fg_tv_hud_save_addr.setFocusable(true);
			fg_tv_hud_nearby_addr.setFocusable(true);
			fg_tv_hud_see_plan.setFocusable(true);
		}
	}

	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery(LatLonPoint mLatLonPoint, String positions) {
		// showProgressDialog();// 显示进度框
		position = positions;
		Log.i(TAG,
				"当前城市：" + sp_location_info.getString(Constants.CITYNAME, "上海市"));
		query = new PoiSearch.Query("", positions, sp_location_info.getString(
				Constants.CITYNAME, "上海市"));// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(5);// 设置每页最多返回多少条poiitem
		query.setPageNum(0);// 设置查第一页

		if (mLatLonPoint != null) {
			poiSearch = new PoiSearch(getActivity(), query);
			poiSearch.setOnPoiSearchListener(this);
			poiSearch.setBound(new SearchBound(mLatLonPoint, 2000, true));//
			// 设置搜索区域为以lp点为圆心，其周围2000米范围
			/*
			 * List<LatLonPoint> list = new ArrayList<LatLonPoint>();
			 * list.add(lp);
			 * list.add(AMapUtil.convertToLatLonPoint(Constants.BEIJING));
			 * poiSearch.setBound(new SearchBound(list));// 设置多边形poi搜索范围
			 */
			poiSearch.searchPOIAsyn();// 异步搜索
		}
	}

	BroadcastReceiver gHudReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			gson = new Gson();
			final String action = intent.getAction();
			Log.i(TAG, "gHudReceiver" + action);
			SpeechVoiceData svd;
			VoiceDataClass vdc = null;

			try {
				if (action.equals(Constants.HUD_MENU_SHOW_ACTION)) {
					Log.i(TAG, "ll_hud_simple_viewHUD_MENU_SHOW_ACTION");
					// 确定键
					if (intent.getIntExtra("keycode", -1) == KeyEvent.KEYCODE_ENTER) {

						// 简版
						if (ll_hud_simple_view.getVisibility() == View.VISIBLE) {
							// 显示按钮条
							if (ll_hud_dismiss_view_simple.getVisibility() == View.GONE) {
								fg_tv_hud_exit_navi_simple.requestFocus();
								ll_hud_dismiss_view_simple
										.startAnimation(mShowAction);
								ll_hud_dismiss_view_simple
										.setVisibility(View.VISIBLE);
								Log.i(TAG, "ll_hud_dismiss_view_simple");
							}
						}
						// 地图版
						else if (ll_hud_map_view.getVisibility() == View.VISIBLE) {
							// 显示按钮条
							if (ll_hud_dismiss_view.getVisibility() == View.GONE) {
								ll_hud_dismiss_view.startAnimation(mShowAction);
								ll_hud_dismiss_view.setVisibility(View.VISIBLE);
								fg_tv_hud_exit_navi.requestFocus();
							}
						}
					}
					// 返回键
					else {
						// 附近地点详细
						if (popupWindowNearby != null
								&& popupWindowNearby.isShowing()) {
							popupWindowNearby.dismiss();
							return;
						}
						// 简版
						if (ll_hud_simple_view.getVisibility() == View.VISIBLE) {
							// 隐藏按钮条
							if (ll_hud_dismiss_view_simple.getVisibility() == View.VISIBLE) {
								ll_hud_dismiss_view_simple
										.startAnimation(mHiddenAction);
								ll_hud_dismiss_view_simple
										.setVisibility(View.INVISIBLE);
								dismissPopupWindow();
								return;
							}

							isHandlerWork = false;
							shouHudTypeView();
						}
						// 地图版
						else if (ll_hud_map_view.getVisibility() == View.VISIBLE) {
							// 隐藏按钮条
							if (ll_hud_dismiss_view.getVisibility() == View.VISIBLE) {
								ll_hud_dismiss_view
										.startAnimation(mHiddenAction);
								ll_hud_dismiss_view.setVisibility(View.GONE);
								dismissPopupWindow();
								Log.i(TAG,
										ll_hud_map_view.getVisibility()
												+ ":ll_hud_simple_view.getVisibility() ");
							}
						}
					}

				}
				if (action != null
						&& action.equals(Constants.MUSIC_AUDIOFOCUS_ACTION)) {

					HudProjectApplication.music_name = intent
							.getStringExtra("music_name");
					if (HudProjectApplication.music_name != null) {
						if (ll_hud_simple_view.getVisibility() == View.VISIBLE) {
							tv_fg_hud_music_simple
									.setText(HudProjectApplication.music_name);
							tv_fg_hud_music_simple.setTypeface(Typefaces.get(
									getActivity(), "Satisfy-Regular.ttf"));
							new Titanic().start(tv_fg_hud_music_simple);
						} else {
							tv_fg_hud_music
									.setText(HudProjectApplication.music_name);
							tv_fg_hud_music.setTypeface(Typefaces.get(
									getActivity(), "Satisfy-Regular.ttf"));

							new Titanic().start(tv_fg_hud_music);
						}

					}

					boolean hud_music = intent.getBooleanExtra("hud_music",
							false);
					HudProjectApplication.is_show_hud_music = hud_music;
					if (HudProjectApplication.is_show_hud_music == true) {
						if (ll_hud_simple_view.getVisibility() == View.VISIBLE) {
							fg_hud_music_simple.setVisibility(View.VISIBLE);
							fg_hud_music_simple.startAnimation(operatingAnim);
						} else {
							fg_hud_music.setVisibility(View.VISIBLE);
							fg_hud_music.startAnimation(operatingAnim);
							tv_fg_hud_music.setVisibility(View.VISIBLE);
						}
					} else {

						if (ll_hud_simple_view.getVisibility() == View.VISIBLE) {
							fg_hud_music_simple.clearAnimation();
							fg_hud_music_simple.setVisibility(View.GONE);
							tv_fg_hud_music_simple.setVisibility(View.GONE);
						} else {
							fg_hud_music.clearAnimation();
							fg_hud_music.setVisibility(View.GONE);
							tv_fg_hud_music.setVisibility(View.GONE);
							Log.d(TAG, "fg_hud_music.setVisibility(View.GONE);");
						}
					}
				}

				// HUD界面指令
				if (action != null && Constants.MAIN_HUD_ACTION.equals(action)) {
					Log.i(TAG, action + "action");
					svd = gson.fromJson(
							intent.getStringExtra(Constants.MAIN_HUD_ACTION),
							SpeechVoiceData.class);
					vdc = gson.fromJson(svd.getValue(), VoiceDataClass.class);
					// 搜索地点
					if (vdc.getType().equals(Constants.F_R_POSITION_SHOW[0])) {
						String lat = sp_location_info.getString(
								Constants.LATITUDE, "");
						String lon = sp_location_info.getString(
								Constants.LONGITUDE, "");
						Log.i(TAG, vdc.getValue() + "vdc.getValue()");
						if (lat != "" && lon != "") {
							doSearchQuery(
									new LatLonPoint(Double.parseDouble(lat),
											Double.parseDouble(lon)),
									vdc.getValue());
							shouHudTypeView();
						}

					}

					// 继续导航
					else if (vdc.getType().equals(Constants.F_R_RESUME_NAVI[0])) {
						dissmissProgressDialog();
						if (mAlertDialog != null && mAlertDialog.isShowing()) {
							mAlertDialog.dismiss();
						}
						if (mAMapNavi != null) {
							mAMapNavi.resumeNavi();
							MainActivity.svr
									.startSpeaking(Constants.F_C_RESUME_NAVI);
						}

					}

					// 暂停导航
					else if (vdc.getType().equals(Constants.F_R_PAUSE_NAVI[0])) {
						dissmissProgressDialog();
						if (mAlertDialog != null && mAlertDialog.isShowing()) {
							mAlertDialog.dismiss();
						}
						if (mAMapNavi != null) {
							mAMapNavi.pauseNavi();
							MainActivity.svr
									.startSpeaking(Constants.F_C_PAUSE_NAVI);
						}

					}

					// 路线总览
					else if (vdc.getType().equals(
							Constants.F_R_NAVI_ROUTE_PLAN_All_SEE[0])) {
						// SpeechVoiceData svdData = CommonUtil
						// .packSpeechCommandInfo(vdc,
						// Constants.FRAG_TO_MAIN_ACTION, null);
						// CommonUtil.processBroadcast(getActivity(), svdData);
						mAMapNaviView.displayOverview();
						handler.postDelayed(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								mAMapNaviView.recoverLockMode();
								mAmap.moveCamera(CameraUpdateFactory
										.changeTilt(90));
							}
						}, 10000);
						shouHudTypeView();
					}
				}
				// 公共指令
				else if (action != null
						&& Constants.COMMON_UTIL_ACTION.equals(action)) {
					svd = gson
							.fromJson(
									intent.getStringExtra(Constants.COMMON_UTIL_ACTION),
									SpeechVoiceData.class);
					if (svd == null || svd.getValue() == null) {
						return;
					}
					vdc = gson.fromJson(svd.getValue(), VoiceDataClass.class);
					// 附近地点详情选择
					if (vdc.getType().equals(Constants.F_R_CHOOSE_ITEM[0])) {
						if (popupWindowNearby.isShowing()) {// 开始选择路径
							int selectIndex = CommonUtil.getIndex(vdc
									.getValue());
							if (poiItemsLsts != null
									&& poiItemsLsts.size() >= 0) {
								if (selectIndex >= poiItemsLsts.size()) {
									MainActivity.svr
											.startSpeaking(Constants.F_C_OUT_ITIMS);
								} else {
									navi_toNearby_loc(poiItemsLsts
											.get(selectIndex));

								}
							}
						}
					}
				}
				// 控制顶部条显示或隐藏
				else if (action != null
						&& Constants.HUD_TITLE_SHOW_ACTION.equals(action)) {
					String handle_info_command = intent
							.getStringExtra(Constants.handle_info_command);
					String handle_info_content = intent
							.getStringExtra(Constants.handle_info_content);
					// 接听操作
					if (handle_info_command != null
							&& handle_info_command
									.equals(Constants.handle_answer)) {
						if (handle_info_content != null) {
							gson.fromJson(handle_info_content,
									PhoneInfoDataClass.class);
							fg_im_side_info_show.setVisibility(View.VISIBLE);
							fg_im_simple_side_info_show
									.setVisibility(View.VISIBLE);
							Log.i(TAG,
									"fg_ll_side_info_show.setVisibility(View.VISIBLE);");
						}

					}
					// 挂断操作
					else if (handle_info_command != null
							&& handle_info_command
									.equals(Constants.handle_hang_up)) {
						fg_im_side_info_show.setVisibility(View.GONE);
						fg_im_simple_side_info_show.setVisibility(View.GONE);
						Log.i(TAG,
								"fg_ll_side_info_show.setVisibility(View.GONE);");

					}

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};

	/**
	 * 广播过滤器
	 */
	private static IntentFilter makeNewMsgIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.MAIN_HUD_ACTION);
		intentFilter.addAction(Constants.COMMON_UTIL_ACTION);
		intentFilter.addAction(Constants.HUD_TITLE_SHOW_ACTION);// 控制顶部条显示或隐藏：电话、音乐
		intentFilter.addAction(Constants.HUD_MENU_SHOW_ACTION);// hud菜单显示隐藏
		intentFilter.addAction(Constants.MUSIC_AUDIOFOCUS_ACTION);// hud音乐图标显示和隐藏
		// intentFilter.addAction("rui_state_info");
		return intentFilter;
	}

	// private LatLonPoint LatLonPointLocation() {
	// LatLonPoint mLatLonPoint = null;
	// String lat = sp_location_info.getString(Constants.LATITUDE, "");
	// String lon = sp_location_info.getString(Constants.LONGITUDE, "");
	// if (lat != "" && lon != "") {
	// mLatLonPoint = new LatLonPoint(Double.parseDouble(lat),
	// Double.parseDouble(lon));
	// }
	//
	// return mLatLonPoint;
	//
	// }

	/*
	 * 清楚sp
	 */
	public void removeSp() {
		getActivity().getSharedPreferences(Constants.FILE_STARTNAVIINFO, 0)
				.edit().clear().commit();
	}

	private AMapNaviListener mAMapNaviListener = new AMapNaviListener() {
		@Override
		public void onArriveDestination() {
			Log.i(TAG, "onArriveDestination");
			// doSearchQuery(lp, "停车场");
			isEndNavi = true;// 导航结束
			// CameraUpdate cuOut = CameraUpdateFactory.zoomTo(18);
			// mAmap.moveCamera(cuOut);
			removeLastSaveNaviInfo();
			ll_hud_map_view.setVisibility(View.VISIBLE);
			ll_hud_simple_view.setVisibility(View.GONE);
			// CommonUtil.retHomeFragment(getActivity());
		}

		@Override
		public void onArrivedWayPoint(int arg0) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onArrivedWayPoint");
		}

		@Override
		public void onCalculateRouteFailure(int arg0) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onCalculateRouteFailure" + arg0);
		}

		@Override
		public void onEndEmulatorNavi() {
			Log.i(TAG, "onEndEmulatorNavi");
			// doSearchQuery(lp, "停车场");
			isEndNavi = true;// 导航结束
			// CameraUpdate cuOut = CameraUpdateFactory.zoomTo(18);
			// mAmap.moveCamera(cuOut);
			removeLastSaveNaviInfo();
			ll_hud_map_view.setVisibility(View.VISIBLE);
			ll_hud_simple_view.setVisibility(View.GONE);
			// CommonUtil.retHomeFragment(getActivity());
		}

		@Override
		public void onGetNavigationText(int arg0, String arg1) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onGetNavigationText:" + arg1);
			// svr.startSpeaking(arg1);
			if (arg1.contains("事故多发地")) {
				arg1 = arg1.replaceAll("事故多发地", "事故多发第");
			}
			MainActivity.svr.startSpeaking(arg1);

		}

		@Override
		public void onGetNavigationText(String s) {

		}

		@Override
		public void onGpsOpenStatus(boolean arg0) {
			// TODO Auto-generated method stub
			Log.i(TAG, "onGpsOpenStatus:");
		}

		@Override
		public void onInitNaviFailure() {
			// TODO Auto-generated method stub
			Log.i(TAG, "导航创建失败");
		}

		@Override
		public void onInitNaviSuccess() {
			// TODO Auto-generated method stub
			Log.i(TAG, "导航创建成功时的回调函数");
			mAMapNavi.calculateDriveRoute(mStartPoints, mEndPoints, null, plan);
		}

		@Override
		public void onLocationChange(AMapNaviLocation arg0) {
			// TODO Auto-generated method stub
			// if (arg0.getSpeed() * 3.6 > 0) {
			// svr.startSpeaking("当前车速：" + arg0.getSpeed() * 3.6);
			// }
			tv_speed_test.setText(arg0.getSpeed() * 3.6 + "");
			Log.i(TAG, "onLocationChange");

		}

		@Override
		public void onNaviInfoUpdate(NaviInfo naveinfo) {
			if (mAMapNavi != null) {
				Log.i(TAG, "mAMapNavi不等于mnull");
			} else {
				Log.i(TAG, "mAMapNavi等于mnull..........");
			}
			if (naveinfo == null) {
				return;
			}
			float speed = sp_location_info.getFloat(Constants.SPEED, 0);
			if (speed != currentSpeed && speed != 0) {
				Log.v(TAG, "当前车速：" + speed);
				currentSpeed = speed;
				im_hudImage_simple.setBackgroundResource(CommonUtil
						.rt_hud_simple_image(getActivity(), speed));
				if (spinner != null && spinner.isRunning()) {
					spinner.stop();
					spinner = null;
				}
				spinner = (AnimationDrawable) im_hudImage_simple
						.getBackground();
				spinner.setExitFadeDuration(100);
				spinner.start();
			}

			// CameraUpdate cuOut = CameraUpdateFactory.zoomTo(18);
			// mAmap.moveCamera(cuOut);
			if (totalDistance == 0) {
				totalDistance = naveinfo.getPathRetainDistance();
				seekBar.setMax(totalDistance);
			}
			// 路名和指示标
			fg_tv_next_roadname2.setText(naveinfo.getNextRoadName());
			fg_tv_next_roadname2.setVisibility(View.VISIBLE);

			tv_simple_next_road_name.setText(naveinfo.getNextRoadName());

			if (isHandlerWork == false) {
				shouHudTypeView(naveinfo.getCurStepRetainDistance());
			}

			int IconType = naveinfo.getIconType();

			if (WordDataSet.hmHudInductionIcons.keySet().contains(IconType)) {
				// 路段距离大于1公里不显示诱导图
				if (naveinfo.getCurStepRetainDistance() > 1000) {
					fg_im_next_direction2.setVisibility(View.GONE);

				} else {
					fg_im_next_direction2.setVisibility(View.VISIBLE);
					fg_im_next_direction2
							.setBackgroundResource(WordDataSet.hmHudInductionIcons
									.get(IconType));
					fg_im_next_direction2.refreshDrawableState();
				}
			}
			Log.d(TAG,
					"总距离：" + totalDistance + "剩余距离："
							+ naveinfo.getPathRetainDistance());

			// 当前路段剩余距离
			String current_dis[] = CommonUtil.meterTurnKm(naveinfo
					.getCurStepRetainDistance());

			fg_hud_car_left_path_remain_dis.setText(current_dis[0]);
			fg_hud_car_left_path_remain_dis_util.setText(current_dis[1]);

			tv_simple_next_road_distance.setText(current_dis[0]);
			tv_simple_next_road_distance_unit.setText(current_dis[1]);

			// 进度条
			progressDistance = totalDistance - naveinfo.getPathRetainDistance();// 所行走的距离
			seekBar.setProgress(progressDistance);

			// 剩余总距离
			String dis[] = CommonUtil.meterTurnKm(naveinfo
					.getPathRetainDistance());
			fg_hud_remainder_distance.setText(dis[0]);
			fg_hud_distance_unit.setText(dis[1]);
			// 剩余总时间
			String time[] = CommonUtil.getTimeFormat(naveinfo
					.getPathRetainTime());
			if (time[0] != null) {
				fg_hud_remainder_time_h.setText(time[0]);
				fg_hud_time_unit_h.setText("小时");
			}
			if (time[1] != null) {
				fg_hud_remainder_time_m.setText(time[1]);
				fg_hud_time_unit_m.setText("分钟");
			}
			// Log.e(TAG,
			// "naveinfo.getLimitSpeed()" + naveinfo.getLimitSpeed()
			// + "naveinfo.getCameraDistance()"
			// + naveinfo.getCameraDistance()
			// + "naveinfo.getCameraType()"
			// + naveinfo.getCameraType());
			// 设置限速
			if (naveinfo.getLimitSpeed() != 0) {
				fg_hud_im_limit_speed.setVisibility(View.VISIBLE);
				tv_simple_limit_speed.setVisibility(View.VISIBLE);
				fg_hud_im_limit_speed.setText(naveinfo.getLimitSpeed() + "");
				tv_simple_limit_speed.setText(naveinfo.getLimitSpeed() + "");
			} else {
				fg_hud_im_limit_speed.setVisibility(View.GONE);
				tv_simple_limit_speed.setVisibility(View.GONE);
			}
			// 设置摄像头图像
			if (naveinfo.getCameraDistance() != -1) {
				if (naveinfo.getCameraType() == 1) {
					fg_hud_im_camera.setVisibility(View.VISIBLE);
					im_simple_camera.setVisibility(View.VISIBLE);
				} else {
					fg_hud_im_camera.setVisibility(View.GONE);
					im_simple_camera.setVisibility(View.GONE);
				}
			} else {
				fg_hud_im_camera.setVisibility(View.GONE);
				im_simple_camera.setVisibility(View.GONE);
			}

			// Log.i(TAG, "距离服务站服务站:" + naveinfo.getServiceAreaDistance());

			// String lat = getActivity().getSharedPreferences(
			// Constants.LOCATION_INFO, 0).getString(Constants.LATITUDE,
			// "");
			// String lon = getActivity().getSharedPreferences(
			// Constants.LOCATION_INFO, 0).getString(Constants.LONGITUDE,
			// "");
			// if (lat != "" && lon != "") {
			// mko = new MarkerOptions();
			// mko.icon(BitmapDescriptorFactory
			// .fromResource(R.drawable.center_icon));
			// mko.position(new LatLng(Double.parseDouble(lat), Double
			// .parseDouble(lon)));
			// mAmap.addMarker(mko);
			// }

			// mAMapAllPath.animateCamera(mCameraUpdate);
			// mAMapAllPath.addGroundOverlay(new GroundOverlayOptions()
			// .anchor(0f, 0f)
			// .transparency(0.3F)
			// .image(BitmapDescriptorFactory
			// .fromResource(R.drawable.groundoverlay))
			// .zIndex(0.5f).positionFromBounds(bounds));

			// sbf.append(" 下一导航点距离：");
			// sbf.append(naveinfo.getCurStepRetainDistance());
			// sbf.append(" 剩余旅程：");
			// sbf.append(naveinfo.getPathRetainDistance());
			// if (naveinfo.getCameraDistance() != -1) {
			// sbf.append(" 摄像头类型：");
			// if (naveinfo.getCameraType() == 0) {
			// sbf.append("测速");
			// }
			// if (naveinfo.getCameraType() == 1) {
			// sbf.append("监控");
			// }
			// sbf.append(" 摄像头距离：");
			// sbf.append(naveinfo.getCameraDistance());
			// }
			// sbf.append(" 当前位置前一个形状点索引：");
			// sbf.append(naveinfo.getCurPoint());
			// sbf.append(" 当前大路段索引：");
			// sbf.append(naveinfo.getCurStep());
			// sbf.append(" 当前路段剩余距离：");
			// sbf.append(naveinfo.getCurStepRetainDistance());
			// sbf.append(" 当前路段剩余时间：");
			// sbf.append(naveinfo.getCurStepRetainTime());
			// sbf.append(" 导航转向图标：");
			// sbf.append(naveinfo.getIconType());
			// sbf.append(" 电子眼限速：");
			// sbf.append(naveinfo.getLimitSpeed());
			// sbf.append(" 导航信息类型：");
			// sbf.append(naveinfo.getNaviType());
			// sbf.append(" 路线剩余距离：");
			// sbf.append(naveinfo.getPathRetainDistance());
			// sbf.append(" 路线剩余时间：");
			// sbf.append(naveinfo.getPathRetainTime());

		}

		@Override
		public void onNaviInfoUpdated(AMapNaviInfo arg0) {
			// TODO Auto-generated method stub
			dissmissProgressDialog();
		}

		@Override
		public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

		}

		@Override
		public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

		}

		@Override
		public void onReCalculateRouteForTrafficJam() {
			// TODO Auto-generated method stub
			Log.i(TAG, "onReCalculateRouteForTrafficJam");
		}

		@Override
		public void onReCalculateRouteForYaw() {
			// TODO Auto-generated method stub
			MainActivity.svr.startSpeaking("以为您重新规划路线");

		}

		@Override
		public void onStartNavi(int arg0) {
			// TODO Auto-generated method stub

			Log.i(TAG, "onStartNavi:" + arg0);
		}

		@Override
		public void onTrafficStatusUpdate() {
			// TODO Auto-generated method stub
			Log.i(TAG, "onTrafficStatusUpdate:");
		}

		@Override
		public void OnUpdateTrafficFacility(TrafficFacilityInfo arg0) {
			// TODO Auto-generated method stub
			// Log.e(TAG, "boardcastType:" + arg0.boardcastType +
			// "--arg0.coor_X:"
			// + arg0.coor_X + "--arg0.coor_Y:" + arg0.coor_Y + "--distance:"
			// + arg0.distance + "--limitSpeed:" + arg0.limitSpeed);
			Log.i(TAG, "OnUpdateTrafficFacility:");

		}

		@Override
		public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

		}

		@Override
		public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

		}

		@Override
		public void onPlayRing(int i) {

		}

		@Override
		public void hideLaneInfo() {
			// TODO Auto-generated method stub
			myDriveWayView.setVisibility(View.INVISIBLE);

		}

		@Override
		public void onCalculateRouteSuccess(int[] ints) {
			Log.i(TAG, "onCalculateRouteSuccess");
			// 导航开始之后保存信息。用于汽车熄火等hud断电时使用
			continue_navi
					.edit()
					.putString(Constants.KEY_CONTIUE_NAVI_ADDRESS,
							gson.toJson(mEndPoints))
					.putInt(Constants.KEY_CONTIUE_NAVI_PLAN, plan).commit();
			// 设置模拟速度
			mAMapNavi.setEmulatorNaviSpeed(100);
			// 开启模拟导航
			mAMapNavi.startNavi(AMapNavi.EmulatorNaviMode);
			// 开启实时导航
			// mAMapNavi.startNavi(AMapNavi.GPSNaviMode);

			mAmap.setMapTextZIndex(-1);
			// 将高的地图logo放到右侧
			mAmap.showMapText(false);
			UiSettings mUiSettings = mAmap.getUiSettings();
			if (mUiSettings != null) {
				mUiSettings
						.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
			}
			// 小地图
			// if (mAMapAllPath == null) {
			// mAMapAllPath = mapView.getMap();
			// mAMapAllPath.setMapType(3);
			// LatLngBounds.Builder builder = new LatLngBounds.Builder();
			// builder.include(new LatLng(naviPath.getStartPoint()
			// .getLatitude(), naviPath.getStartPoint().getLongitude()));
			// builder.include(new LatLng(
			// naviPath.getEndPoint().getLatitude(), naviPath
			// .getEndPoint().getLongitude()));
			// LatLngBounds mLatLngBounds = builder.build();
			// CameraUpdate mCameraUpdate = CameraUpdateFactory
			// .newLatLngBounds(mLatLngBounds, 5);
			// mAMapAllPath.moveCamera(mCameraUpdate);
			// }
			//
			// mRouteOverLay = new RouteOverLay(mAMapAllPath, naviPath,
			// getActivity());
			// mRouteOverLay.setStartPointBitmap(BitmapFactory.decodeResource(
			// getActivity().getResources(), R.drawable.all_plan_start));
			// mRouteOverLay.setEndPointBitmap(BitmapFactory.decodeResource(
			// getActivity().getResources(), R.drawable.all_plan_end));
			// // mRouteOverLay.setEmulateGPSLocationVisible();
			// mRouteOverLay.setTrafficLine(true);
			// mRouteOverLay.addToMap();
			// mRouteOverLay.zoomToSpan();

			// 绘制导航指示箭头功能，当去远的地方时，绘制时间长导致卡死。
			// List<NaviLatLng> naviLsts = naviPath.getCoordList();
			// List<NaviLatLng> naviLsts = naviPath.getWayPoint();
			// NavigateArrowOptions mNavigateArrowOptions = new
			// NavigateArrowOptions();
			// for (NaviLatLng naviLatLng : naviLsts) {
			// mNavigateArrowOptions.add(new LatLng(naviLatLng.getLatitude(),
			// naviLatLng.getLongitude()));
			// }
			// mNavigateArrowOptions.zIndex(1);
			// mNavigateArrowOptions.visible(true);
			// mNavigateArrowOptions.sideColor(R.color.white);
			// // mNavigateArrowOptions.topColor(R.color.red);
			// mNavigateArrowOptions.width(100);
			// mAmap.addNavigateArrow(mNavigateArrowOptions);

		}

		@Override
		public void notifyParallelRoad(int i) {

		}

		@Override
		public void showCross(AMapNaviCross aMapNaviCross) {
			myCustomEnlargedCross.setImageBitmap(aMapNaviCross.getBitmap());
			myCustomEnlargedCross.startAnimation(animFg_im_next_direction);
			fg_im_next_direction2.startAnimation(animCustomEnlargedCrossView);
			myCustomEnlargedCross.setVisibility(View.VISIBLE);
		}

		@Override
		public void hideCross() {

			myCustomEnlargedCross.startAnimation(animCustomEnlargedCrossView);
			fg_im_next_direction2.startAnimation(animFg_im_next_direction);
			myCustomEnlargedCross.setVisibility(View.GONE);
		}

		@Override
		public void showModeCross(AMapModelCross aMapModelCross) {

		}

		@Override
		public void hideModeCross() {

		}

		@Override
		public void showLaneInfo(AMapLaneInfo[] laneInfos,
				byte[] laneBackgroundInfo, byte[] laneRecommendedInfo) {

			// 你可以使用我们的DriveWayView来自定义位置
			myDriveWayView.loadDriveWayBitmap(laneBackgroundInfo,
					laneRecommendedInfo);
			myDriveWayView.invalidate();
			myDriveWayView.setVisibility(View.VISIBLE);

			// or
			// 只接收数据，自行绘制属于你的道路选择view
			// 下面是解释

			Log.d("解释：", "当前车道数量为" + laneInfos.length + "条");
			for (int i = 0; i < laneInfos.length; i++) {
				AMapLaneInfo info = laneInfos[i];
				Log.d("解释：", "该条车道的类型为" + info.getLaneTypeIdHexString());
				// 你将收到两位字符
				// 第一位表示背景
				// 第二位表示当前推荐的方向（如果不推荐则为F）
				// 请看drawable-hpdi，里面有一些图
				// 其中，从0 - E，各自代表

				// 直行0
				// 左转1
				// 左转，直行2
				// 右转3
				// 右转和直行4
				// 左转调头5
				// 左转和右转6
				// 直行，左转，右转 7
				// 右转调头8
				// 直行，左转调头9
				// 直行，右转调头A
				// 左转和左转调头B
				// 右转和右转掉头C
				// 。。。

				// 所以（以下三图均存在）
				// 如果00，说明该车道为直行且推荐直行
				// 如果0F，说明该车道为直行，但不推荐
				// 如果20，说明该车道为左转直行车道，推荐直行
				// 以此类推
			}
		}

		@Override
		public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

		}
	};

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onPoiSearched");
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					// 取得搜索到的poiitems有多少页
					List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					if (locationMarkerLst != null
							&& locationMarkerLst.size() > 0) {
						for (int j = 0; j < locationMarkerLst.size(); j++) {
							locationMarkerLst.get(j).hideInfoWindow();
							locationMarkerLst.get(j).destroy();
						}
						locationMarkerLst.clear();
						locationMarkerLst = null;
						moLst.clear();
						moLst = null;
						poiItemsLsts.clear();
						poiItemsLsts = null;
					}
					poiItemsLsts = new ArrayList<PoiItem>();
					moLst = new ArrayList<MarkerOptions>();
					// LatLngBounds.Builder builder = new
					// LatLngBounds.Builder();
					if (poiItems != null && poiItems.size() > 0) {
						for (int i = 0; i < poiItems.size(); i++) {
							if (poiItems.get(i) != null) {
								MarkerOptions mo = new MarkerOptions()
										.anchor(0.5f, 1)
										.icon(BitmapDescriptorFactory
												.fromResource(CommonUtil
														.retNearby_loc(
																getActivity(),
																i)))
										.position(
												new LatLng(
														poiItems.get(i)
																.getLatLonPoint()
																.getLatitude(),
														poiItems.get(i)
																.getLatLonPoint()
																.getLongitude()));

								moLst.add(mo);
								poiItemsLsts.add(poiItems.get(i));

								// builder.include(new LatLng(poiItems.get(i)
								// .getLatLonPoint().getLatitude(),
								// poiItems.get(i).getLatLonPoint()
								// .getLongitude()));
								// mAmap.addMarker(mo).showInfoWindow();

							}
						}
						locationMarkerLst = mAmap.addMarkers(moLst, true);
						mAmap.moveCamera(CameraUpdateFactory.zoomTo(15));
						// LatLngBounds bounds = builder.build();
						// mAmap.moveCamera(CameraUpdateFactory.newLatLngBounds(
						// bounds, 18));
						MainActivity.svr.startSpeaking("已经找到离您最近的"
								+ poiItems.size() + "个" + position);
						if (ll_auto_add_view != null) {
							ll_auto_add_view.removeAllViews();
							ll_auto_add_view.setVisibility(View.GONE);
						}
						showNearbyPopupWindow();
					} else {
						Log.d(TAG, "您附近没有" + position);
						MainActivity.svr.startSpeaking("您附近没有" + position);
						isNaviToSearchPoi = false;
						dismissNearbyPopupWindow();
						mAmap.moveCamera(CameraUpdateFactory.zoomTo(18));
						mAmap.moveCamera(CameraUpdateFactory.changeTilt(90));
						return;
					}
				}
			} else {
				return;
			}
		} else {
			return;
		}
	}

	@Override
	public void onPoiItemSearched(PoiItem poiItem, int i) {

	}

	// 简版、地图版本切换Runnable
	Runnable shouHudTypeRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				if (ll_hud_simple_view.getVisibility() == View.GONE) {
					ll_hud_map_view.setVisibility(View.GONE);
					ll_hud_simple_view.setVisibility(View.VISIBLE);
					ll_hud_simple_view.startAnimation(AnimationUtils
							.loadAnimation(getActivity(),
									android.R.anim.slide_in_left));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.w(TAG, "使用动画的时候，出错");
			}
			isHandlerWork = false;
		}
	};

	/**
	 * 十秒之后切换会简版hud
	 */
	public void shouHudTypeView() {

		if (isHandlerWork == false) {
			// 表明当前是简版
			if (ll_hud_map_view.getVisibility() == View.GONE) {
				ll_hud_map_view.setVisibility(View.VISIBLE);
				ll_hud_map_view.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), android.R.anim.slide_in_left));
				ll_hud_simple_view.setVisibility(View.GONE);
				// 十秒钟之后切换会简版
				handler.postDelayed(shouHudTypeRunnable, 10000);
				isHandlerWork = true;
			}
		}

	}

	/**
	 * @param dis_remain
	 *            1000米以内地图版，反之简版 地图和简版hud切换
	 */
	public void shouHudTypeView(int dis_remain) {

		if (dis_remain <= 15000) {

			if (ll_hud_map_view.getVisibility() == View.GONE) {
				ll_hud_dismiss_view_simple.setVisibility(View.GONE);
				ll_hud_simple_view.setVisibility(View.GONE);
				ll_hud_map_view.setVisibility(View.VISIBLE);
				ll_hud_map_view.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), android.R.anim.slide_in_left));

				if (HudProjectApplication.is_show_hud_music) {
					fg_hud_music.setVisibility(View.VISIBLE);
					fg_hud_music.startAnimation(operatingAnim);

					tv_fg_hud_music.setVisibility(View.VISIBLE);
					tv_fg_hud_music.setText(HudProjectApplication.music_name);
					tv_fg_hud_music.setTypeface(Typefaces.get(getActivity(),
							"Satisfy-Regular.ttf"));
					new Titanic().start(tv_fg_hud_music);

				} else {
					fg_hud_music.setVisibility(View.GONE);
					tv_fg_hud_music.setVisibility(View.GONE);
				}

			}

		} else {
			if (ll_hud_simple_view.getVisibility() == View.GONE) {
				ll_hud_dismiss_view.setVisibility(View.GONE);
				ll_hud_map_view.setVisibility(View.GONE);
				ll_hud_simple_view.setVisibility(View.VISIBLE);
				ll_hud_simple_view.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), android.R.anim.slide_in_left));
				if (HudProjectApplication.is_show_hud_music) {
					fg_hud_music_simple.setVisibility(View.VISIBLE);
					fg_hud_music_simple.startAnimation(operatingAnim);
					tv_fg_hud_music_simple.setVisibility(View.VISIBLE);
					tv_fg_hud_music_simple
							.setText(HudProjectApplication.music_name);
					tv_fg_hud_music_simple.setTypeface(Typefaces.get(
							getActivity(), "Satisfy-Regular.ttf"));
					new Titanic().start(tv_fg_hud_music_simple);
				} else {
					fg_hud_music_simple.setVisibility(View.GONE);
					tv_fg_hud_music_simple.setVisibility(View.GONE);
				}
			}
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String lat = sp_location_info.getString(Constants.LATITUDE, "");
		String lon = sp_location_info.getString(Constants.LONGITUDE, "");
		switch (v.getId()) {

		// case R.id.fg_im_side_info_show:
		// CommonUtil.sendMsg(Constants.MODEL_PHONE_HANG_UP_COM,
		// Constants.TELEPHONY_IDLE, getActivity(),
		// Constants.MODEL_PHONE_COM);
		// HudProjectApplication.phone_info = null;
		//
		// case R.id.fg_im_simple_side_info_show:
		// CommonUtil.sendMsg(Constants.MODEL_PHONE_HANG_UP_COM,
		// Constants.TELEPHONY_IDLE, getActivity(),
		// Constants.MODEL_PHONE_COM);
		// HudProjectApplication.phone_info = null;
		case R.id.hm_tv_nearby_bank:
			if (lat != "" && lon != "") {
				doSearchQuery(
						new LatLonPoint(Double.parseDouble(lat),
								Double.parseDouble(lon)), "银行");
			}
			dismissPopupWindow();
			shouHudTypeView();
			break;
		case R.id.hm_tv_search_gas_station:
			Log.d(TAG, "hm_tv_end_navi---点击操作");
			if (lat != "" && lon != "") {
				doSearchQuery(
						new LatLonPoint(Double.parseDouble(lat),
								Double.parseDouble(lon)), "加油站");
			}
			dismissPopupWindow();
			shouHudTypeView();
			break;
		case R.id.hm_tv_search_parking_lot:
			if (lat != "" && lon != "") {
				doSearchQuery(
						new LatLonPoint(Double.parseDouble(lat),
								Double.parseDouble(lon)), "停车场");
			}
			dismissPopupWindow();
			shouHudTypeView();
			break;
		case R.id.hm_tv_search_hotel:
			if (lat != "" && lon != "") {
				doSearchQuery(
						new LatLonPoint(Double.parseDouble(lat),
								Double.parseDouble(lon)), "酒店");
			}
			dismissPopupWindow();
			shouHudTypeView();
			break;
		case R.id.fg_tv_hud_exit_navi:// 退出导航
			isEndNavi = true;
			CommonUtil.retHomeFragment(getActivity());
			dismissPopupWindow();
			break;
		case R.id.fg_tv_hud_save_addr:// 收藏地点
			mainActivity.save_current_adrress();
			ll_hud_dismiss_view.startAnimation(mHiddenAction);
			ll_hud_dismiss_view.setVisibility(View.GONE);
			break;
		case R.id.fg_tv_hud_nearby_addr:// 附近
			showPopupWindow();
			break;

		case R.id.fg_tv_hud_see_plan:// 全览图
			// VoiceDataClass vdc = new VoiceDataClass(null,
			// Constants.F_R_NAVI_ROUTE_PLAN_All_SEE[0], null, null);
			// SpeechVoiceData svdData = CommonUtil.packSpeechCommandInfo(vdc,
			// Constants.FRAG_TO_MAIN_ACTION, null);
			// try {
			// CommonUtil.processBroadcast(getActivity(), svdData);
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			mAMapNaviView.displayOverview();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mAMapNaviView.recoverLockMode();
					mAmap.moveCamera(CameraUpdateFactory.changeTilt(90));
				}
			}, 10000);
			shouHudTypeView();
			break;

		case R.id.hm_im_close_pop:
			dismissPopupWindow();
			break;
		case R.id.fg_tv_hud_exit_navi_simple:// 退出导航
			isEndNavi = true;
			CommonUtil.retHomeFragment(getActivity());
			dismissPopupWindow();
			break;
		case R.id.fg_tv_hud_save_addr_simple:// 收藏地点
			mainActivity.save_current_adrress();
			ll_hud_dismiss_view_simple.startAnimation(mHiddenAction);
			ll_hud_dismiss_view_simple.setVisibility(View.GONE);
			break;
		case R.id.fg_tv_hud_nearby_addr_simple:// 附近
			showPopupWindow();
			break;

		case R.id.fg_tv_hud_see_plan_simple:// 全览图
			// VoiceDataClass vdc_simple = new VoiceDataClass(null,
			// Constants.F_R_NAVI_ROUTE_PLAN_All_SEE[0], null, null);
			// SpeechVoiceData svdData_simple =
			// CommonUtil.packSpeechCommandInfo(
			// vdc_simple, Constants.FRAG_TO_MAIN_ACTION, null);
			// try {
			// CommonUtil.processBroadcast(getActivity(), svdData_simple);
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			mAMapNaviView.displayOverview();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mAMapNaviView.recoverLockMode();
				}
			}, 5000);
			shouHudTypeView();
			break;

		default:
			break;
		}
	}

	public boolean controlButtonShow() {
		if (ll_hud_dismiss_view.getVisibility() == View.GONE
				&& ll_hud_dismiss_view_simple.getVisibility() == View.GONE) {
			return false;
		}
		return true;
	}

	@Override
	public void onLocationChanged(AMapLocation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLockMap(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviViewLoaded() {

	}

	@Override
	public boolean onNaviBackClick() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onNaviCancel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviMapMode(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviSetting() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviTurnClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNextRoadClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScanViewButtonClick() {
		// TODO Auto-generated method stub

	}

}

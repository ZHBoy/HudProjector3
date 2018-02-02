package com.infisight.hudprojector.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.google.gson.Gson;
import com.infisight.hudprojector.MainActivity;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.adapter.ReTermAdapter;
import com.infisight.hudprojector.adapter.RoutePlanItemChooseAdapter;
import com.infisight.hudprojector.common.HudProjectApplication;
import com.infisight.hudprojector.data.AssociateKeyClass;
import com.infisight.hudprojector.data.DataLocationInfo;
import com.infisight.hudprojector.data.SpeechVoiceData;
import com.infisight.hudprojector.data.StartNaviInfo;
import com.infisight.hudprojector.data.VoiceDataClass;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;
import com.infisight.hudprojector.util.Permission.PermissionUtils;
import com.infisight.hudprojector.util.ToastUtil;
import com.infisight.hudprojector.util.WordDataSet;
import com.infisight.hudprojector.widget.CustomProgressDialog;
import com.infisight.hudprojector.widget.TimerCountDownView;
import com.infisight.hudprojector.widget.TimerCountDownView.CountdownTimerListener;

/**
 * @author hao
 * 
 *         当可视范围改变时回调的接口OnCameraChangeListener
 */
@SuppressWarnings("deprecation")
@SuppressLint("ResourceAsColor")
public class GNaviFragment extends Fragment implements AMapNaviListener,
		OnClickListener, LocationSource, AMapLocationListener {
	private String TAG = "GNaviFragment";
	private MapView mapView;
	private AMap aMap;
	RelativeLayout rl_gnavi;
	FrameLayout fl_gnavi;
	ListView associate_key;
	// private String keyWord = "";// 要输入的poi搜索关键字
	// private ProgressDialog progDialog = null;// 搜索时进度条
	CustomProgressDialog progressDialog;
	List<AssociateKeyClass> lstAk47 = new ArrayList<AssociateKeyClass>();
	ReTermAdapter reTermAdapter;// 联想词
	RoutePlanItemChooseAdapter routePlanItemChooseAdapter;// 路径规划信息
	private PoiResult poiResult; // poi返回的结果
	private PoiSearch.Query query;// Poi查询条件类
	private PoiSearch poiSearch;// POI搜索
	Gson gson;
	MarkerOptions mko;
	// SpeechVoiceRecognition svr = null;
	StartNaviInfo sni;
	NaviLatLng start_navi;// 起点坐标，再定位回调中设置
	NaviLatLng end_navi;// 终点坐标
	String destination = null;
	boolean isOk = false;
	SharedPreferences sp;
	SharedPreferences sp_naviprefer;
	boolean isChooseRoutePlan = false;
	int height;
	String routePlanName = null;
	// String pathAddress = null;
	String dis = "";
	String time = "";
	// 路径规划线路选择
	LinearLayout gnavi_ll_route_plans, gnavi_ll_not_traffic_plan,
			gnavi_ll_not_high_speed_plan, gnavi_ll_not_fees_plan = null;
	TextView gnavi_tv_not_traffic_plan,
			gnavi_tv_not_traffic_plan_distance_and_time = null;// 避免拥堵
	TextView gnavi_tv_not_high_speed_plan,
			gnavi_tv_not_high_speed_plan_distance_and_time = null;// 不走高速
	TextView gnavi_tv_not_fees_plan,
			gnavi_tv_not_fees_plan_distance_and_time = null;// 避免收费

	// 常用地点
	LinearLayout gnavi_ll_common_loc;
	TextView gnavi_tv_home, gnavi_tv_company;

	// 路径规划
	private RouteOverLay mRouteOverLay = null;
	private AMapNavi mAMapNavi = null;
	// 起点终点列表
	private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
	private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();

	GeocodeSearch geocoderSearch = null;
	RegeocodeQuery mRegeocodeQuery = null;
	FrameLayout fl_gmap;
	// TextView tv_counttime;
	TimerCountDownView tv_counttime;
	TextView timer;
	int timeCount = 20;
	Handler handler = new Handler();
	private OnLocationChangedListener mListener;
	private AMapLocationClient mlocationClient;
	private AMapLocationClientOption mLocationOption;
	int isStartNaiv = 0;// 1 表示避免拥堵 2 表示不走高速 3 标示距离最短
	String end_point_name = null;// 终点名称

	// boolean isControlOper = false;
	public static GNaviFragment newInstance() {
		GNaviFragment fragment = new GNaviFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * 设置离线地图存储目录，在下载离线地图或初始化地图设置; 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
		 * 则需要在离线地图下载和使用地图页面都进行路径设置
		 */
		// Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
		// MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
		// mapView = (MapView)getActivity().findViewById(R.id.map);
		// mapView.onCreate(savedInstanceState);// 此方法必须重写
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		PermissionUtils.verifyLocationPermissions(getActivity());
		AMapLocationClient.setApiKey("62a8554483a2cce1877ab1c1d8d20a39");
		gson = new Gson();
		mapView = (MapView) getActivity().findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		// svr = SpeechVoiceRecognition.getInstance(getActivity());
		// svr.invalidRecKey = 0;
		sp = getActivity()
				.getSharedPreferences(Constants.FILE_BG_MUSIC_INFO, 0);
		initView();
		init();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.gnavi_start, container, false);
	}

	// 初始化xml控件
	private void initView() {

		fl_gmap = (FrameLayout) getActivity().findViewById(R.id.fl_gmap);
		tv_counttime = (TimerCountDownView) getActivity().findViewById(
				R.id.tv_counttime);
		timer = (TextView) getActivity().findViewById(R.id.timer);
		// 路线选择的容器
		gnavi_ll_route_plans = (LinearLayout) getActivity().findViewById(
				R.id.gnavi_ll_route_plans);
		// 避免拥堵的容器
		gnavi_ll_not_traffic_plan = (LinearLayout) getActivity().findViewById(
				R.id.gnavi_ll_not_traffic_plan);
		// 不走高速的容器
		gnavi_ll_not_high_speed_plan = (LinearLayout) getActivity()
				.findViewById(R.id.gnavi_ll_not_high_speed_plan);
		// 避免收费的容器
		gnavi_ll_not_fees_plan = (LinearLayout) getActivity().findViewById(
				R.id.gnavi_ll_not_fees_plan);
		// 避免拥堵
		gnavi_tv_not_traffic_plan = (TextView) getActivity().findViewById(
				R.id.gnavi_tv_not_traffic_plan);
		gnavi_tv_not_traffic_plan_distance_and_time = (TextView) getActivity()
				.findViewById(R.id.gnavi_tv_not_traffic_plan_distance_and_time);
		// 不走高速
		gnavi_tv_not_high_speed_plan = (TextView) getActivity().findViewById(
				R.id.gnavi_tv_not_high_speed_plan);
		gnavi_tv_not_high_speed_plan_distance_and_time = (TextView) getActivity()
				.findViewById(
						R.id.gnavi_tv_not_high_speed_plan_distance_and_time);
		// 避免收费
		gnavi_tv_not_fees_plan = (TextView) getActivity().findViewById(
				R.id.gnavi_tv_not_fees_plan);
		gnavi_tv_not_fees_plan_distance_and_time = (TextView) getActivity()
				.findViewById(R.id.gnavi_tv_not_fees_plan_distance_and_time);
		// 常用地点
		gnavi_ll_common_loc = (LinearLayout) getActivity().findViewById(
				R.id.gnavi_ll_common_loc);
		gnavi_tv_home = (TextView) getActivity().findViewById(
				R.id.gnavi_tv_home);
		gnavi_tv_company = (TextView) getActivity().findViewById(
				R.id.gnavi_tv_company);
		gnavi_tv_home.setOnClickListener(this);
		gnavi_tv_company.setOnClickListener(this);
		// 策略选择
		rl_gnavi = (RelativeLayout) getActivity().findViewById(R.id.rl_gnavi);// 左侧用户可操作提示
		fl_gnavi = (FrameLayout) getActivity().findViewById(R.id.fl_gnavi);// 地图和联想词容器
		associate_key = (ListView) getActivity().findViewById(
				R.id.associate_key);// 联想词
		gnavi_ll_not_traffic_plan.setOnClickListener(this);
		gnavi_ll_not_high_speed_plan.setOnClickListener(this);
		gnavi_ll_not_fees_plan.setOnClickListener(this);
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		mAMapNavi = AMapNavi.getInstance(getActivity().getApplicationContext());
		mAMapNavi.setAMapNaviListener(this);
		associate_key.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (isChooseRoutePlan) {
					sni = new StartNaviInfo(start_navi, end_navi, 0);
					MainActivity.svr
							.startSpeaking(Constants.F_C_WHAT_YOU_CHOOSE_ROUTE_PLAN
									+ reTermAdapter.lists.get(position)
									+ Constants.F_C_READY_NAVI);
					SpeechVoiceData svdData = CommonUtil.packSpeechCommandInfo(
							new VoiceDataClass(null,
									Constants.F_R_START_NAVI[0], null, null),
							Constants.FRAG_TO_MAIN_ACTION, gson.toJson(sni));

					try {
						CommonUtil.processBroadcast(getActivity(), svdData);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					AssociateKeyClass akc = reTermAdapter.lists.get(position);
					// isControlOper = true;
					// 成功之后才赋值
					doSearchQuery(akc.getKeyName());
					// svc.startSpeaking(vdc.getPromptKey());

				}
			}
		});
	}

	// 计算驾车路线
	private void calculateDriveRoute(final int planType) {
		showProgressDialog();
		mAMapNavi.calculateDriveRoute(mStartPoints, mEndPoints, null, planType);

	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onResume() {
		getActivity().registerReceiver(gNaviReceiver, makeNewMsgIntentFilter());
		super.onResume();
		mapView.onResume();
		if (aMap == null) {
			aMap = mapView.getMap();
		}
		aMap.setMapType(3);
		// if (mapView.getVisibility() == View.GONE) {
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
		MyLocationStyle mMyLocationStyle = new MyLocationStyle();
		mMyLocationStyle.radiusFillColor(0x00000000);
		mMyLocationStyle.strokeWidth(0f);
		mMyLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.icon_st));
		aMap.setMyLocationStyle(mMyLocationStyle);
		aMap.setMyLocationEnabled(true);// 显示定位按钮
		// }
		String str = getArguments().getString("destination");
		aMap.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChangeFinish(CameraPosition position) {
				// TODO Auto-generated method stub
				if (aMap.getMaxZoomLevel() == position.zoom) {
					MainActivity.svr.startSpeaking("地图已经放大到最大");
				}

				if (aMap.getMinZoomLevel() == position.zoom) {
					MainActivity.svr.startSpeaking("地图已经缩小到最小");

				}
			}

			@Override
			public void onCameraChange(CameraPosition arg0) {
				// TODO Auto-generated method stub

			}
		});
		mRouteOverLay = new RouteOverLay(aMap, null, getActivity());
		if (str.equals("打开导航") || str.equals(Constants.CONTROL_MAIN_KEY)) {
			if (CommonUtil.isConnect(getActivity())) {
				MainActivity.svr.startSpeaking(Constants.F_C_INPUT_RIGHT_ADDR);
				HudProjectApplication.gnavi_page_state = 0;
			}

		} else {
			packKeyInfo(str);
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
		getActivity().unregisterReceiver(gNaviReceiver);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		routePlanName = null;
		isOk = false;
		isChooseRoutePlan = false;
		// pathAddress = null;
		dis = null;
		time = "";
		geocoderSearch = null;
		mRegeocodeQuery = null;
		if (mRouteOverLay != null) {
			mRouteOverLay.removeFromMap();
			mRouteOverLay.destroy();
		}

		if (mAMapNavi != null) {
			mAMapNavi.removeAMapNaviListener(this);
			mAMapNavi.destroy();
		}
		deactivate();// 停止定位
		super.onDestroy();

	}

	@Override
	public void onDestroyView() {
		Log.i(TAG, "onDestroyView");
		// TODO Auto-generated method stub
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		// if (progDialog != null && progDialog.isShowing())
		// progDialog.dismiss();
		if (mapView != null) {
			mapView.onDestroy();
		}
		super.onDestroyView();
		isChooseRoutePlan = false;
		Log.i(TAG, "onDestroyView_down");

	}

	// 进度条的runnable
	Runnable progressRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (progressDialog != null && progressDialog.isShowing()) {
				dissmissProgressDialog();
				MainActivity.svr.startSpeaking(Constants.F_C_OUT_TIME_SEARCH);
				CommonUtil.retHomeFragment(getActivity());

			}
		}
	};

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		handler.removeCallbacks(progressRunnable);
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
			handler.removeCallbacks(progressRunnable);
		}
	}

	/**
	 * 开始进行poi搜索(找到具体的地点)
	 */
	protected boolean doSearchQuery(String address) {
		end_point_name = address;
		showProgressDialog();// 显示进度框
		query = new PoiSearch.Query(address, "", "上海");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(10);// 设置每页最多返回多少条poiitem
		query.setPageNum(0);// 设置查第一页

		poiSearch = new PoiSearch(getActivity(), query);
		poiSearch.setOnPoiSearchListener(new OnPoiSearchListener() {

			@Override
			public void onPoiSearched(PoiResult result, int rCode) {
				// TODO Auto-generated method stub
				try {
					if (rCode == 0) {
						if (result != null && result.getQuery() != null) {// 搜索poi的结果
							if (result.getQuery().equals(query)) {// 是否是同一条
								poiResult = result;
								// 取得搜索到的poiitems有多少页
								List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
								List<SuggestionCity> suggestionCities = poiResult
										.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
								if (poiItems != null && poiItems.size() > 0) {
									// 去掉上次的终点
									if (mEndPoints != null
											&& mEndPoints.size() > 0) {
										mEndPoints.clear();

									}
									double lat = poiItems.get(0)
											.getLatLonPoint().getLatitude();
									double lon = poiItems.get(0)
											.getLatLonPoint().getLongitude();
									end_navi = new NaviLatLng(lat, lon);
									Log.i(TAG, lat + "--" + lon);
									isOk = true;
									mStartPoints.add(start_navi);
									mEndPoints.add(end_navi);
								} else if (suggestionCities != null
										&& suggestionCities.size() > 0) {
									showSuggestCity(suggestionCities);
									isOk = true;
								} else {
									MainActivity.svr
											.startSpeaking(Constants.F_C_FIND_LOC_DEFEAT);
									ToastUtil.show(getActivity(), "没有搜索到相关数据！");
									dissmissProgressDialog();
									isOk = false;
								}
							}
							// 这是开始路径规划的最后一步，isOk = true时，说明已经找到了相关地点
							if (isOk == true) {
								associate_key.setVisibility(View.GONE);

								HudProjectApplication.gnavi_page_state = 2;
								// 读取手机端传过来的导航偏好
								String str;
								try {
									sp_naviprefer = getActivity()
											.getSharedPreferences(
													Constants.FILE_NAVIPREFER,
													Activity.MODE_PRIVATE);
									str = sp_naviprefer.getString(
											Constants.KEY_NAVIPREFER, "避免拥堵");
								} catch (Exception e) {
									str = "避免拥堵";
								}
								if (str.equals("避免拥堵")) {
									calculateDriveRoute(4);
									routePlanName = "避免拥堵";
								} else if (str.equals("不走高速")) {
									calculateDriveRoute(3);
									routePlanName = "不走高速";
								} else if (str.equals("避免收费")) {
									calculateDriveRoute(1);
									routePlanName = "避免收费";
								}
								isChooseRoutePlan = true;
							}
						} else {
							MainActivity.svr
									.startSpeaking(Constants.F_C_FIND_LOC_DEFEAT);
							ToastUtil.show(getActivity(), "对不起，没有搜索到相关数据！");
							dissmissProgressDialog();
							Log.i(TAG, "对不起，没有搜索到相关数据！");
							isOk = false;
						}

					} else if (rCode == 27) {
						MainActivity.svr
								.startSpeaking(Constants.F_C_FIND_LOC_DEFEAT);
						ToastUtil.show(getActivity(), "搜索失败,请检查网络连接！");
						Log.i(TAG, "搜索失败,请检查网络连接！");
						dissmissProgressDialog();
						isOk = false;
					} else if (rCode == 32) {
						ToastUtil.show(getActivity(), "key验证无效");
						Log.i(TAG, "key验证无效");
						isOk = false;
					} else {
						ToastUtil.show(getActivity(), "未知错误，请稍后重试!错误码为:"
								+ rCode);
						Log.i(TAG, "未知错误，请稍后重试!错误码为:" + rCode);
						dissmissProgressDialog();
						isOk = false;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void onPoiItemSearched(PoiItem poiItem, int i) {

			}

		});
		poiSearch.searchPOIAsyn();
		Log.i(TAG, "isOk:" + isOk);
		return isOk;
	}

	/*
	 * 装载联想词到adapter中
	 */
	public boolean packKeyInfo(String address) {
		showProgressDialog();// 显示进度框
		String newText = address;
		// mStartPoints = null;
		// mEndPoints = null;
		if (getActivity().getSharedPreferences(
				Constants.FILE_SAVE_LOCATION_INFO, 0).getString(newText, null) != null) {
			end_navi = gson.fromJson(
					getActivity().getSharedPreferences(
							Constants.FILE_SAVE_LOCATION_INFO, 0).getString(
							newText, null), DataLocationInfo.class)
					.getmNaviLatLng();
			if (mRouteOverLay != null) {
				mRouteOverLay.removeFromMap();
			}
			mStartPoints.add(start_navi);
			mEndPoints.add(end_navi);
			calculateDriveRoute(4);
			routePlanName = "避免拥堵";
			isChooseRoutePlan = true;
			return true;
		}
		Inputtips inputTips = new Inputtips(getActivity(),
				new InputtipsListener() {

					@Override
					public void onGetInputtips(List<Tip> tipList, int rCode) {
						if (rCode == 0) {// 正确返回
							if (tipList == null || tipList.size() == 0) {
								MainActivity.svr
										.startSpeaking(Constants.F_C_NOT_FOUND_TIP_ADDR);
								dissmissProgressDialog();// 隐藏对话框
								return;
							}
							lstAk47 = new ArrayList<AssociateKeyClass>();
							int index = tipList.size();
							// 说的是确切地点时
							if (index == 1) {
								doSearchQuery(tipList.get(0).getName());
								end_point_name = tipList.get(0).getName();
								dissmissProgressDialog();// 隐藏对话框
								// calculateDriveRoute(4);
								return;
							}
							if (tipList.size() > 3) {
								index = 3;
							}
							for (int i = 0; i < index; i++) {
								lstAk47.add(new AssociateKeyClass(tipList
										.get(i).getName(), tipList.get(i)
										.getDistrict()));
								Log.i(TAG, tipList.get(i).getName()
										+ tipList.get(i).getDistrict());
							}
							reTermAdapter = new ReTermAdapter(lstAk47,
									getActivity());
							if (index > 0) {
								associate_key.setVisibility(View.VISIBLE);
								mapView.setVisibility(View.GONE);
								fl_gmap.setVisibility(View.GONE);

							}
							MainActivity.svr
									.startSpeaking(Constants.F_C_FOUND_RIGHT_ADDR);
							associate_key.setAdapter(reTermAdapter);
							HudProjectApplication.gnavi_page_state = 1;
							dissmissProgressDialog();// 隐藏对话框
						} else {
							dissmissProgressDialog();// 隐藏对话框
							MainActivity.svr
									.startSpeaking(Constants.F_C_NOT_FOUND_TIP_ADDR);

							// 导航目的地输入有误
							return;
						}
					}
				});
		try {
			inputTips.requestInputtips(newText, "上海");// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号

		} catch (AMapException e) {
			e.printStackTrace();
			dissmissProgressDialog();
		}
		if (lstAk47.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * poi没有搜索到数据，返回一些推荐城市的信息
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "推荐城市\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
					+ cities.get(i).getCityCode() + "城市编码:"
					+ cities.get(i).getAdCode() + "\n";
		}
		ToastUtil.show(getActivity(), infomation);

	}

	BroadcastReceiver gNaviReceiver = new BroadcastReceiver() {

		@SuppressLint("ResourceAsColor")
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// 这里捕获异常主要是防止空指针和数组下标越界
			try {
				final String action = intent.getAction();
				Log.i(TAG, "gNaviReceiver" + action);
				SpeechVoiceData svd;
				VoiceDataClass vdc;

				// double a = 0.0;
				// double b = 0.0;
				// String lat = getActivity().getSharedPreferences(
				// Constants.LOCATION_INFO, 0).getString(
				// Constants.LATITUDE, "");
				// String lon = getActivity().getSharedPreferences(
				// Constants.LOCATION_INFO, 0).getString(
				// Constants.LONGITUDE, "");
				// a = Double.parseDouble(lat);
				// b = Double.parseDouble(lon);
				// if (a <= 0 && b <= 0) {
				// MainActivity.svr
				// .startSpeaking(Constants.F_C_DEFEAT_LOCATION);
				// return;
				// }

				// 本页导航及公共action指令操作
				if (Constants.MAIN_NAVI_ACTION.equals(action)
						|| Constants.COMMON_UTIL_ACTION.equals(action)) {
					svd = gson.fromJson(intent.getStringExtra(action),
							SpeechVoiceData.class);
					vdc = gson.fromJson(svd.getValue(), VoiceDataClass.class);
					if (vdc.getType().equals(Constants.F_R_NAV_GOTO[0])) {
						// 将颜色设置为白色
						gnavi_tv_not_high_speed_plan.setTextColor(getActivity()
								.getResources().getColor(R.color.white));
						gnavi_tv_not_fees_plan.setTextColor(getActivity()
								.getResources().getColor(R.color.white));
						gnavi_tv_not_traffic_plan.setTextColor(getActivity()
								.getResources().getColor(R.color.white));
						// 重新规划的时候路程和时间制空
						gnavi_tv_not_fees_plan_distance_and_time.setText("");
						gnavi_tv_not_traffic_plan_distance_and_time.setText("");
						gnavi_tv_not_high_speed_plan_distance_and_time
								.setText("");
						isChooseRoutePlan = false;
						packKeyInfo(vdc.getValue());
						handler.removeCallbacks(runnable);

					} else if (vdc.getType().equals(
							Constants.F_R_CHOOSE_ITEM[0])) {
						if (isChooseRoutePlan == false) {// 开始选择路径
							int selectIndex = CommonUtil.getIndex(vdc
									.getValue());
							if (selectIndex >= reTermAdapter.lists.size()) {
								MainActivity.svr
										.startSpeaking(Constants.F_C_OUT_ITIMS);
							} else {
								AssociateKeyClass akc = reTermAdapter.lists
										.get(CommonUtil.getIndex(vdc.getValue()));
								// 成功之后才赋值
								doSearchQuery(akc.getKeyName());

								Log.i(TAG,
										"akc.getKeyName()" + akc.getKeyName());
								// calculateDriveRoute(4);
							}
						}

					} else if (vdc.getType().equals(
							Constants.F_R_NAVI_ROUTE_PLAN[0])) {
						if (vdc.getValue().contains("避免拥堵")) {
							if (isStartNaiv == 1) {
								choosedplan(isStartNaiv);
								return;
							}
							isStartNaiv = 1;
							routePlanName = "避免拥堵";
							// gnavi_ll_not_traffic_plan
							// .setBackgroundColor(R.color.green);
						} else if (vdc.getValue().contains("不走高速")) {
							if (isStartNaiv == 2) {
								choosedplan(isStartNaiv);
								return;
							}
							isStartNaiv = 2;
							routePlanName = "不走高速";
							// gnavi_ll_not_high_speed_plan
							// .setBackgroundColor(R.color.green);
						} else if (vdc.getValue().contains("避免收费")) {
							if (isStartNaiv == 3) {
								choosedplan(isStartNaiv);
								return;
							}
							isStartNaiv = 3;
							routePlanName = "避免收费";
							// gnavi_ll_not_fees_plan
							// .setBackgroundColor(R.color.green);
						}
						if (routePlanName != null) {
							// 移除之前绘制的路线
							if (mRouteOverLay != null) {
								mRouteOverLay.removeFromMap();
							}
							int index = WordDataSet.routePlans
									.get(routePlanName);
							calculateDriveRoute(index);
						}
					} else if (vdc.getType()
							.equals(Constants.F_R_START_NAVI[0])) {

						if (start_navi != null && end_navi != null) {
							Log.i(TAG, "start_navi " + start_navi
									+ " end_navi " + end_navi);
							int index = 4;// 推荐默认路线：时间最短且避免拥堵
							if (routePlanName != null) {
								index = WordDataSet.routePlans
										.get(routePlanName);
							}
							handler.removeCallbacks(runnable);
							sni = new StartNaviInfo(start_navi, end_navi, index);

							SpeechVoiceData svdData = CommonUtil
									.packSpeechCommandInfo(vdc,
											Constants.FRAG_TO_MAIN_ACTION,
											gson.toJson(sni));

							CommonUtil.processBroadcast(getActivity(), svdData);
							Log.i(TAG, start_navi + "-----" + end_navi + "--"
									+ index);
						}
					}
					// 缩放地图
					else if (vdc.getType().equals(
							Constants.F_R_ENLARGE_OR_NARROW_MAP[0])) {
						// 如果地图显示的话
						if (mapView.getVisibility() == View.VISIBLE) {
							// 放大地图
							if (vdc.getValue().contains(
									Constants.F_R_ENLARGE_OR_NARROW_MAP[1])) {
								CameraUpdate cuIn = CameraUpdateFactory
										.zoomIn();
								aMap.moveCamera(cuIn);
							}
							// 缩小地图
							else if (vdc.getValue().contains(
									Constants.F_R_ENLARGE_OR_NARROW_MAP[2])) {
								CameraUpdate cuOut = CameraUpdateFactory
										.zoomOut();
								aMap.moveCamera(cuOut);
							}
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i(TAG, "GNaviFragment  出现异常了。。。。。");
			}
		}

	};

	/**
	 * 广播过滤器
	 */
	private static IntentFilter makeNewMsgIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.MAIN_NAVI_ACTION);
		intentFilter.addAction(Constants.COMMON_UTIL_ACTION);
		return intentFilter;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (hidden) {
			Log.i(TAG, "GNaviFragment is hidden");
		}
	}

	@Override
	public void onArriveDestination() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onArrivedWayPoint(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCalculateRouteFailure(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEndEmulatorNavi() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetNavigationText(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetNavigationText(String s) {

	}

	@Override
	public void onGpsOpenStatus(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNaviFailure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInitNaviSuccess() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLocationChange(AMapNaviLocation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNaviInfoUpdate(NaviInfo arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	@Deprecated
	public void onNaviInfoUpdated(AMapNaviInfo arg0) {
		// TODO Auto-generated method stub

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

	}

	@Override
	public void onReCalculateRouteForYaw() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartNavi(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTrafficStatusUpdate() {
		// TODO Auto-generated method stub

	}

	// @Override
	// public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
	// // TODO Auto-generated method stub
	// }
	//
	// @Override
	// public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {
	// // TODO Auto-generated method stub
	// try {
	// // pathAddress = arg0.getRegeocodeAddress().getStreetNumber()
	// // .getStreet();
	// // if (pathAddress != null && end_point_name != null) {
	// // if (!pathAddress.equals("")) {
	// // pathAddress = "主要途径点：" + pathAddress;
	// // }
	// // Log.i(TAG, "getCrossroads():" + pathAddress);
	// // MainActivity.svr.startSpeaking("终点：" + end_point_name + "。全程："
	// // + dis + "," + time + "," + pathAddress);
	// // }
	//
	// if (end_point_name != null) {
	// MainActivity.svr.startSpeaking("终点：" + end_point_name + "。全程："
	// + dis + "," + time);
	// }
	// } catch (NullPointerException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return;
	// }
	// }
	CountdownTimerListener litener = new CountdownTimerListener() {

		@Override
		public void onCountDown(String time) {
			timer.setText(time);
		}

		@Override
		public void onTimeArrive(boolean isArrive) {
			if (isArrive) {
				// Toast.makeText(mContext, "时间到", Toast.LENGTH_SHORT).show();
				if (start_navi != null && end_navi != null) {
					int index = 4;// 推荐默认路线：时间最短且避免拥堵
					if (routePlanName != null) {
						index = WordDataSet.routePlans.get(routePlanName);
					}
					try {
						sni = new StartNaviInfo(start_navi, end_navi, index);
						SpeechVoiceData svdData = CommonUtil
								.packSpeechCommandInfo(
										new VoiceDataClass(null,
												Constants.F_R_START_NAVI[0],
												null, null),
										Constants.FRAG_TO_MAIN_ACTION, gson
												.toJson(sni));
						Log.i(TAG, "runnable：" + svdData.toString());

						CommonUtil.processBroadcast(getActivity(), svdData);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					MainActivity.svr
							.startSpeaking(Constants.F_C_NAVI_GPS_ERROR);
					CommonUtil.retHomeFragment(getActivity());
				}
				handler.removeCallbacks(runnable);
			}
		}
	};

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// timeCount--;
			tv_counttime.setMaxTime(timeCount);
			tv_counttime.updateView();
			tv_counttime.addCountdownTimerListener(litener);
			// if (timeCount == 0) {
			//
			// }
		}

	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.gnavi_ll_not_traffic_plan:
			if (isStartNaiv == 1) {
				choosedplan(isStartNaiv);
				break;
			}
			Log.i(TAG, "gnavi_ll_not_traffic_plan66666666666");
			routePlanName = "避免拥堵";
			isStartNaiv = 1;
			if (routePlanName != null) {
				// 移除之前绘制的路线
				if (mRouteOverLay != null) {
					mRouteOverLay.removeFromMap();
				}
				int index = WordDataSet.routePlans.get(routePlanName);
				calculateDriveRoute(index);
			}
			break;
		case R.id.gnavi_ll_not_high_speed_plan:
			if (isStartNaiv == 2) {
				choosedplan(isStartNaiv);
				break;
			}
			isStartNaiv = 2;
			routePlanName = "不走高速";
			if (routePlanName != null) {
				// 移除之前绘制的路线

				int index = WordDataSet.routePlans.get(routePlanName);
				calculateDriveRoute(index);
			}
			break;
		case R.id.gnavi_ll_not_fees_plan:
			if (isStartNaiv == 3) {
				choosedplan(isStartNaiv);
				break;
			}
			isStartNaiv = 3;
			routePlanName = "避免收费";
			if (routePlanName != null) {
				// 移除之前绘制的路线
				if (mRouteOverLay != null) {
					mRouteOverLay.removeFromMap();
				}
				int index = WordDataSet.routePlans.get(routePlanName);
				calculateDriveRoute(index);
			}
			break;
		// 家
		case R.id.gnavi_tv_home:
			aMap.clear();
			end_point_name = "家";
			end_navi = new NaviLatLng(30.797577, 120.763278);
			// 120.763278,30.797577嘉兴
			mStartPoints.add(start_navi);
			mEndPoints.add(end_navi);
			calculateDriveRoute(4);
			routePlanName = "避免拥堵";
			Log.d(TAG, "回家");
			break;
		// 公司
		case R.id.gnavi_tv_company:
			// 120.588504,31.343937苏州
			end_navi = new NaviLatLng(31.343937, 120.588504);
			end_point_name = "公司";
			mStartPoints.add(start_navi);
			mEndPoints.add(end_navi);
			calculateDriveRoute(4);
			routePlanName = "避免拥堵";
			Log.d(TAG, "回公司");
			break;
		}
	}

	/**
	 * @param isStartNaiv
	 *            再次选中策略 直接进行导航
	 */
	public void choosedplan(int isStartNaiv) {
		try {
			SpeechVoiceData svdData = null;
			switch (isStartNaiv) {
			case 1:
				sni = new StartNaviInfo(start_navi, end_navi, 4);
				svdData = CommonUtil.packSpeechCommandInfo(new VoiceDataClass(
						null, Constants.F_R_START_NAVI[0], null, null),
						Constants.FRAG_TO_MAIN_ACTION, gson.toJson(sni));
				Log.i(TAG, "runnable：" + svdData.toString());
				CommonUtil.processBroadcast(getActivity(), svdData);
				break;
			case 2:
				sni = new StartNaviInfo(start_navi, end_navi, 3);

				svdData = CommonUtil.packSpeechCommandInfo(new VoiceDataClass(
						null, Constants.F_R_START_NAVI[0], null, null),
						Constants.FRAG_TO_MAIN_ACTION, gson.toJson(sni));
				Log.i(TAG, "runnable：" + svdData.toString());
				CommonUtil.processBroadcast(getActivity(), svdData);
				break;
			case 3:
				sni = new StartNaviInfo(start_navi, end_navi, 1);

				svdData = CommonUtil.packSpeechCommandInfo(new VoiceDataClass(
						null, Constants.F_R_START_NAVI[0], null, null),
						Constants.FRAG_TO_MAIN_ACTION, gson.toJson(sni));
				Log.i(TAG, "runnable：" + svdData.toString());
				CommonUtil.processBroadcast(getActivity(), svdData);
				break;
			default:
				return;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void OnUpdateTrafficFacility(TrafficFacilityInfo arg0) {
		// TODO Auto-generated method stub

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
	public void hideCross() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showModeCross(AMapModelCross aMapModelCross) {

	}

	@Override
	public void hideModeCross() {

	}

	@Override
	public void hideLaneInfo() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCalculateRouteSuccess(int[] ints) {
		AMapNaviPath naviPath = mAMapNavi.getNaviPath();
		if (naviPath == null) {
			return;
		}
		time = "";
		// 获取路径规划线路，显示到地图上
		mRouteOverLay.setAMapNaviPath(naviPath);
		// mRouteOverLay.setStartPointBitmap(BitmapFactory.decodeResource(
		// getActivity().getResources(), R.drawable.start));
		// mRouteOverLay.setEndPointBitmap(BitmapFactory.decodeResource(
		// getActivity().getResources(), R.drawable.end));
		mRouteOverLay.setTrafficLine(true);
		mRouteOverLay.zoomToSpan();
		mRouteOverLay.addToMap();
		dissmissProgressDialog();// 隐藏对话框

		gnavi_ll_common_loc.setVisibility(View.GONE);
		associate_key.setVisibility(View.GONE);
		mapView.setVisibility(View.VISIBLE);
		fl_gmap.setVisibility(View.VISIBLE);
		gnavi_ll_route_plans.setVisibility(View.VISIBLE);
		aMap.setMyLocationEnabled(false);// 隐藏定位按钮
		deactivate();// 停止定位

		// 途经点
		// LatLonPoint mLatLonPoint = new
		// LatLonPoint(naviPath.getCenterForPath()
		// .getLatitude(), naviPath.getCenterForPath().getLongitude());
		// geocoderSearch = new GeocodeSearch(getActivity());
		// //
		// latLonPoint参数表示一个Latlng,第二参数表示范围多少米，GeocodeSearch.AMAP表示是国测局坐标系还是GPS原生坐标系
		// mRegeocodeQuery = new RegeocodeQuery(mLatLonPoint, 200,
		// GeocodeSearch.AMAP);
		// geocoderSearch.getFromLocationAsyn(mRegeocodeQuery);
		// geocoderSearch.setOnGeocodeSearchListener(this);

		String str_dis[] = CommonUtil.meterTurnKm(naviPath.getAllLength());
		dis = str_dis[0] + str_dis[1];
		String str_time[] = CommonUtil.getTimeFormat(naviPath.getAllTime());
		if (str_time[0] != null) {
			time = str_time[0] + "小时";
		}
		if (str_time[1] != null) {
			time += str_time[1] + "分钟";
		}
		if (end_point_name != null) {
			MainActivity.svr.startSpeaking("终点：" + end_point_name + "。全程："
					+ dis + "," + time);
		}
		switch (naviPath.getStrategy()) {
			// 避免拥堵
			case 4:
				isStartNaiv = 1;
				// 将选中的字体改为绿色
				gnavi_tv_not_traffic_plan_distance_and_time.setText(dis + "  "
						+ time);
				gnavi_tv_not_traffic_plan.setTextColor(getActivity().getResources()
						.getColor(R.color.green));
				gnavi_tv_not_traffic_plan_distance_and_time
						.setTextColor(getActivity().getResources().getColor(
								R.color.green));
				// 为选择的设置为白色
				gnavi_tv_not_high_speed_plan.setTextColor(getActivity()
						.getResources().getColor(R.color.white));
				gnavi_tv_not_high_speed_plan_distance_and_time
						.setTextColor(getActivity().getResources().getColor(
								R.color.white));

				gnavi_tv_not_fees_plan.setTextColor(getActivity().getResources()
						.getColor(R.color.white));
				gnavi_tv_not_fees_plan_distance_and_time.setTextColor(getActivity()
						.getResources().getColor(R.color.white));
				if (tv_counttime.getVisibility() == View.GONE) {
					tv_counttime.setVisibility(View.VISIBLE);
					timer.setVisibility(View.VISIBLE);
				}
				handler.removeCallbacks(runnable);
				timeCount = 20;
				handler.postDelayed(runnable, 1000);

				break;
			// 不走高速
			case 3:
				isStartNaiv = 2;
				// 将选中的字体改为绿色
				gnavi_tv_not_high_speed_plan_distance_and_time.setText(dis + "  "
						+ time);

				gnavi_tv_not_high_speed_plan.setTextColor(getActivity()
						.getResources().getColor(R.color.green));
				gnavi_tv_not_high_speed_plan_distance_and_time
						.setTextColor(getActivity().getResources().getColor(
								R.color.green));

				// 为选中的选设置为白色
				gnavi_tv_not_traffic_plan.setTextColor(getActivity().getResources()
						.getColor(R.color.white));
				gnavi_tv_not_traffic_plan_distance_and_time
						.setTextColor(getActivity().getResources().getColor(
								R.color.white));

				gnavi_tv_not_fees_plan.setTextColor(getActivity().getResources()
						.getColor(R.color.white));
				gnavi_tv_not_fees_plan_distance_and_time.setTextColor(getActivity()
						.getResources().getColor(R.color.white));
				if (tv_counttime.getVisibility() == View.GONE) {
					tv_counttime.setVisibility(View.VISIBLE);
					timer.setVisibility(View.VISIBLE);
				}
				handler.removeCallbacks(runnable);
				timeCount = 20;
				handler.postDelayed(runnable, 1000);
				break;
			// 避免收费
			case 1:
				isStartNaiv = 3;
				// 将选中的字体改为绿色
				gnavi_tv_not_fees_plan_distance_and_time.setText(dis + "  " + time);
				gnavi_tv_not_fees_plan.setTextColor(getActivity().getResources()
						.getColor(R.color.green));
				gnavi_tv_not_fees_plan_distance_and_time.setTextColor(getActivity()
						.getResources().getColor(R.color.green));

				// 为选中的选设置为白色
				gnavi_tv_not_traffic_plan.setTextColor(getActivity().getResources()
						.getColor(R.color.white));
				gnavi_tv_not_traffic_plan_distance_and_time
						.setTextColor(getActivity().getResources().getColor(
								R.color.white));

				gnavi_tv_not_high_speed_plan.setTextColor(getActivity()
						.getResources().getColor(R.color.white));
				gnavi_tv_not_high_speed_plan_distance_and_time
						.setTextColor(getActivity().getResources().getColor(
								R.color.white));
				if (tv_counttime.getVisibility() == View.GONE) {
					tv_counttime.setVisibility(View.VISIBLE);
					timer.setVisibility(View.VISIBLE);
				}
				handler.removeCallbacks(runnable);
				timeCount = 20;
				handler.postDelayed(runnable, 1000);
				break;
			default:
				break;
		}
	}

	@Override
	public void notifyParallelRoad(int i) {

	}

	@Override
	public void showCross(AMapNaviCross arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showLaneInfo(AMapLaneInfo[] arg0, byte[] arg1, byte[] arg2) {
		// TODO Auto-generated method stub

	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mlocationClient == null) {
			mlocationClient = new AMapLocationClient(getActivity());
			mLocationOption = new AMapLocationClientOption();
			// 设置定位监听
			mlocationClient.setLocationListener(this);
			// 设置为高精度定位模式
			mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
			// 设置定位参数
			mlocationClient.setLocationOption(mLocationOption);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用onDestroy()方法
			// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
			mlocationClient.startLocation();
			Log.e(TAG, "开启定位了、、、、、、、、");
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
		Log.i(TAG, "停止定位");
	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		// TODO Auto-generated method stub
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null && amapLocation.getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				start_navi = new NaviLatLng(amapLocation.getLatitude(),
						amapLocation.getLongitude());
				CameraUpdate mCameraUpdate = CameraUpdateFactory.zoomTo(17);
				aMap.moveCamera(mCameraUpdate);
			} else {
				deactivate();// 停止定位
				String errText = "定位失败," + amapLocation.getErrorCode() + ": "
						+ amapLocation.getErrorInfo();
				Log.e(TAG, errText);
				MainActivity.svr.startSpeaking(Constants.F_C_DEFEAT_LOCATION);
				rl_gnavi.setVisibility(View.VISIBLE);
				mapView.setVisibility(View.GONE);
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						rl_gnavi.setVisibility(View.VISIBLE);
						dissmissProgressDialog();
						CommonUtil.retHomeFragment(getActivity());
					}
				}, 5000);
			}
		}
	}

	@Override
	public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

	}

}

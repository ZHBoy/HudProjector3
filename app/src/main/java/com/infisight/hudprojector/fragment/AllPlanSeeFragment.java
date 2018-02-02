package com.infisight.hudprojector.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.LatLngBounds.Builder;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.view.RouteOverLay;
import com.google.gson.Gson;
import com.infisight.hudprojector.R;
import com.infisight.hudprojector.common.HudProjectApplication;
import com.infisight.hudprojector.data.SpeechVoiceData;
import com.infisight.hudprojector.data.VoiceDataClass;
import com.infisight.hudprojector.util.CommonUtil;
import com.infisight.hudprojector.util.Constants;

/**
 * @author hao
 * 
 *         当可视范围改变时回调的接口OnCameraChangeListener
 */
@SuppressLint("ResourceAsColor")
public class AllPlanSeeFragment extends Fragment {
	private static final String TAG = "AllPlanSeeFragment";
	private MapView mapView;
	private AMap aMap;
	private Gson gson = null;
	private MarkerOptions mko;
	private RouteOverLay mRouteOverLay = null;
	SharedPreferences sp;

	public static AllPlanSeeFragment newInstance() {
		AllPlanSeeFragment fragment = new AllPlanSeeFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@SuppressWarnings("deprecation")
	public AllPlanSeeFragment() {

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
		// mapView.onCreate(savedInstanceState);// 此方法必须重写
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.i(TAG, "onActivityCreated");
		mapView = (MapView) getActivity().findViewById(R.id.hud_plan_all);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		Log.i(TAG, "onCreateView");
		sp = getActivity().getSharedPreferences(Constants.FILE_NAVI_CONTINUE, 0);
		return inflater.inflate(R.layout.hud_plan_all_see, container, false);

	}

	/**
	 * 初始化AMap对象
	 */
	public void init() {
		try {
			gson = new Gson();
			CameraUpdate mCameraUpdate = null;
			LatLng start_ll = null;
			LatLng path_ll = null;
			LatLng end_ll = null;
			float continue_zoom = sp.getFloat(Constants.KEY_CONTINUE_ZOOM, 0);
			String continue_navipath = sp.getString(Constants.KEY_CONTINUE_NAVIPATH, null);

			if (continue_zoom == 0 || continue_navipath == null) {
				return;
			}
			Log.i(TAG, "continue_zoom:" + continue_zoom);
			AMapNaviPath navipath = gson.fromJson(continue_navipath,
					AMapNaviPath.class);
			start_ll = new LatLng(navipath.getStartPoint().getLatitude(),
					navipath.getStartPoint().getLongitude());
			path_ll = new LatLng(navipath.getCenterForPath().getLatitude(),
					navipath.getCenterForPath().getLongitude());
			end_ll = new LatLng(navipath.getEndPoint().getLatitude(), navipath
					.getEndPoint().getLongitude());
			// Builder llBounds = null;
			// llBounds = LatLngBounds.builder().include(start_ll);
			// llBounds = LatLngBounds.builder().include(path_ll);
			// llBounds = LatLngBounds.builder().include(end_ll);
			// llBounds = HudProjectApplication.naviPath.getBoundsForPath();
			// mCameraUpdate = CameraUpdateFactory.newLatLngBounds(llBounds,
			// 15);// 定义了一个可视区域

			double a = 0.0;
			double b = 0.0;
			try {
				String lat = getActivity().getSharedPreferences(
						Constants.LOCATION_INFO, 0).getString(
						Constants.LATITUDE, "");
				String lon = getActivity().getSharedPreferences(
						Constants.LOCATION_INFO, 0).getString(
						Constants.LONGITUDE, "");
				if (lat != "" && lon != "") {
					a = Double.parseDouble(lat);
					b = Double.parseDouble(lon);
				}
				mCameraUpdate = CameraUpdateFactory.changeLatLng(path_ll);// 定义了一个可视区域

				CameraUpdate cuOut = CameraUpdateFactory.zoomTo(continue_zoom);
				aMap = mapView.getMap();
				aMap.setMapType(3);// 黑夜模式
				mko = new MarkerOptions();
				mko.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.center_icon));
				mko.position(new LatLng(a, b));
				aMap.addMarker(mko);
				aMap.moveCamera(mCameraUpdate);
				aMap.moveCamera(cuOut);
				mRouteOverLay = new RouteOverLay(aMap, null, getActivity());
				mRouteOverLay.setStartPointBitmap(CommonUtil
						.drawableToBitmap(getActivity().getResources()
								.getDrawable(R.drawable.no_color_icon)));
				// 获取路径规划线路，显示到地图上
				mRouteOverLay.setRouteInfo(navipath);
				mRouteOverLay.setTrafficLine(true);
				mRouteOverLay.addToMap();
				mRouteOverLay.zoomToSpan();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				SpeechVoiceData svd = new SpeechVoiceData();
				VoiceDataClass vdc = new VoiceDataClass(
						Constants.MAIN_NAVI_ACTION, Constants.F_R_OPEN_NAVI[0],
						null, null);
				svd.setCommand(Constants.MAIN_NAVI_ACTION);
				svd.setValue(gson.toJson(vdc));
				try {
					CommonUtil.processBroadcast(getActivity(), svd);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			new Handler().postDelayed(new Runnable() {

				public void run() {

					// execute the task
					SpeechVoiceData svd = new SpeechVoiceData();
					VoiceDataClass vdc = new VoiceDataClass(
							Constants.MAIN_NAVI_ACTION,
							Constants.F_R_OPEN_NAVI[0], null, null);
					svd.setCommand(Constants.MAIN_NAVI_ACTION);
					svd.setValue(gson.toJson(vdc));
					try {
						CommonUtil.processBroadcast(getActivity(), svd);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}, 10000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(TAG, "出异常了" + e.toString());
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();

	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
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
		super.onDestroy();
		mapView.onDestroy();
		if (mRouteOverLay != null) {
			mRouteOverLay.removeFromMap();
			mRouteOverLay.destroy();
		}
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

}

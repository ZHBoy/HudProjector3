package com.infisight.hudprojector.data;

import java.io.Serializable;

import com.amap.api.navi.model.NaviLatLng;

public class StartNaviInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private NaviLatLng navi_start;
	private NaviLatLng navi_end;
	private int route_plan;

	public StartNaviInfo() {
		super();
	}

	public StartNaviInfo(NaviLatLng navi_start, NaviLatLng navi_end,
			int route_plan) {
		super();
		this.navi_start = navi_start;
		this.navi_end = navi_end;
		this.route_plan = route_plan;
	}

	public NaviLatLng getNavi_start() {
		return navi_start;
	}

	public void setNavi_start(NaviLatLng navi_start) {
		this.navi_start = navi_start;
	}

	public NaviLatLng getNavi_end() {
		return navi_end;
	}

	public void setNavi_end(NaviLatLng navi_end) {
		this.navi_end = navi_end;
	}

	public int getRoute_plan() {
		return route_plan;
	}

	public void setRoute_plan(int route_plan) {
		this.route_plan = route_plan;
	}

}

package com.infisight.hudprojector.data;

import com.amap.api.maps.model.LatLng;


/**
 * 路径规划节点
 * @author Administrator
 *
 */
public class PlanNodeClass {
	private String cityName;
	private String placeName;
	private LatLng latLng;
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getPlaceName() {
		return placeName;
	}
	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}
	public LatLng getLatLng() {
		return latLng;
	}
	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}
	
}

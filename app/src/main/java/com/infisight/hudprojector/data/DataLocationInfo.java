package com.infisight.hudprojector.data;

import com.amap.api.navi.model.NaviLatLng;

public class DataLocationInfo {

	private NaviLatLng mNaviLatLng;
	private String addressString;
	private String cityString;

	public DataLocationInfo() {
		super();
	}

	public DataLocationInfo(NaviLatLng mNaviLatLng, String addressString,
			String cityString) {
		super();
		this.mNaviLatLng = mNaviLatLng;
		this.addressString = addressString;
		this.cityString = cityString;
	}

	public NaviLatLng getmNaviLatLng() {
		return mNaviLatLng;
	}

	public void setmNaviLatLng(NaviLatLng mNaviLatLng) {
		this.mNaviLatLng = mNaviLatLng;
	}

	public String getCityString() {
		return cityString;
	}

	public void setCityString(String cityString) {
		this.cityString = cityString;
	}

	public String getAddressString() {
		return addressString;
	}

	public void setAddressString(String addressString) {
		this.addressString = addressString;
	}

}

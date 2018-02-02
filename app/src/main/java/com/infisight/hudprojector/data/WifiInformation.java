package com.infisight.hudprojector.data;

public class WifiInformation {
	private String wifiName;
	private String wifiPassword;
	
	
	
	public WifiInformation() {
	}
	
	public WifiInformation(String wifiName, String wifiPassword) {
		this.wifiName = wifiName;
		this.wifiPassword = wifiPassword;
	}
	public String getWifiName() {
		return wifiName;
	}
	public void setWifiName(String wifiName) {
		this.wifiName = wifiName;
	}
	public String getWifiPassword() {
		return wifiPassword;
	}
	public void setWifiPassword(String wifiPassword) {
		this.wifiPassword = wifiPassword;
	}

	@Override
	public String toString() {
		return "WifiInfo [wifiName=" + wifiName + ", wifiPassword="
				+ wifiPassword + "]";
	}
	
	
}

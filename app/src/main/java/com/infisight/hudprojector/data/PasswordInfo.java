package com.infisight.hudprojector.data;

public class PasswordInfo {
	private String wifiName;
	private String wifiPassword;
	private String ftpName;
	private String ftpPassword;
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
	public String getFtpName() {
		return ftpName;
	}
	public void setFtpName(String ftpName) {
		this.ftpName = ftpName;
	}
	public String getFtpPassword() {
		return ftpPassword;
	}
	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}
	@Override
	public String toString() {
		return "PasswordInfo [wifiName=" + wifiName + ", wifiPassword="
				+ wifiPassword + ", ftpName=" + ftpName + ", ftpPassword="
				+ ftpPassword + "]";
	}
	public PasswordInfo(String wifiName, String wifiPassword, String ftpName,
			String ftpPassword) {
		super();
		this.wifiName = wifiName;
		this.wifiPassword = wifiPassword;
		this.ftpName = ftpName;
		this.ftpPassword = ftpPassword;
	}

}

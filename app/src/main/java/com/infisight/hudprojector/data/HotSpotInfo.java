package com.infisight.hudprojector.data;
/**
 * 储存热点用户名，密码
 * @author Administrator
 *
 */
public class HotSpotInfo {
	String hsName;
	String hsPwd;
	
	
	public HotSpotInfo() {
		super();
	}


	public HotSpotInfo(String hsName, String hsPwd) {
		this.hsName = hsName;
		this.hsPwd = hsPwd;
	}


	public String getHsName() {
		return hsName;
	}


	public void setHsName(String hsName) {
		this.hsName = hsName;
	}


	public String getHsPwd() {
		return hsPwd;
	}


	public void setHsPwd(String hsPwd) {
		this.hsPwd = hsPwd;
	}


	@Override
	public String toString() {
		return "HotSpotInfo [hsName=" + hsName + ", hsPwd=" + hsPwd + "]";
	}
	
	
	
}

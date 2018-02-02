package com.infisight.hudprojector.data;


/**
 * 从手机端传递的导航数据
 * @author Administrator
 *{"drivingPolicy":"ECAR_AVOID_JAM","pnEnd":{"cityName":"上海市","latLng":{"latitude":31.278301,"longitude":121.594258},"placeName":"五莲路"},
"pnStart":{"cityName":"上海市","latLng":{"latitude":31.229155,"longitude":121.538635},"placeName":"浦电路"},"startOrEndNav":"start"}
 */
public class NavDataClass {
	private PlanNodeClass pnStart;
	private PlanNodeClass pnEnd;
	private int driveMode;
	private String startOrEndNav;
	public PlanNodeClass getPnStart() {
		return pnStart;
	}
	public void setPnStart(PlanNodeClass pnStart) {
		this.pnStart = pnStart;
	}
	public PlanNodeClass getPnEnd() {
		return pnEnd;
	}
	public void setPnEnd(PlanNodeClass pnEnd) {
		this.pnEnd = pnEnd;
	}
	public String getStartOrEndNav() {
		return startOrEndNav;
	}
	public void setStartOrEndNav(String startOrEndNav) {
		this.startOrEndNav = startOrEndNav;
	}
	public int getDriveMode() {
		return driveMode;
	}
	public void setDriveMode(int driveMode) {
		this.driveMode = driveMode;
	}
	
}

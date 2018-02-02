package com.infisight.hudprojector.data;

public class HudInfoClass {
	private String total_Distance;// 总距离
	private String total_Time;// 总时间
	private String streetName;// 下一个路的名称
	private String direct_Icon;// 指示图标
	private String section_Distance;// 路段距离
	private String limitSpeed;// 限速

	public String getLimitSpeed() {
		return limitSpeed;
	}

	public void setLimitSpeed(String limitSpeed) {
		this.limitSpeed = limitSpeed;
	}

	public HudInfoClass() {
		super();
	}

	public HudInfoClass(String total_Distance, String total_Time,
			String streetName, String direct_Icon, String section_Distance,
			String limitSpeed) {
		super();
		this.total_Distance = total_Distance;
		this.total_Time = total_Time;
		this.streetName = streetName;
		this.direct_Icon = direct_Icon;
		this.section_Distance = section_Distance;
		this.limitSpeed = limitSpeed;
	}

	public String getTotal_Distance() {
		return total_Distance;
	}

	public void setTotal_Distance(String total_Distance) {
		this.total_Distance = total_Distance;
	}

	public String getTotal_Time() {
		return total_Time;
	}

	public void setTotal_Time(String total_Time) {
		this.total_Time = total_Time;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getDirect_Icon() {
		return direct_Icon;
	}

	public void setDirect_Icon(String direct_Icon) {
		this.direct_Icon = direct_Icon;
	}

	public String getSection_Distance() {
		return section_Distance;
	}

	public void setSection_Distance(String section_Distance) {
		this.section_Distance = section_Distance;
	}

}

package com.infisight.hudprojector.data;

public class AppControlActionClass {

	public String AppType;// hud反馈是否为主控设备(1是主控，0不是)
	public String DeviceId;// 当前设备id
	public String DeviceName;// 当前设备名称
	public String DeviceAuthority;// 权限(当前用户拥有的权限)
	public String SavedDeviceId;// 主控设备
	public String SaveDeviceName;// 主控设备名称

	public AppControlActionClass() {
		super();
	}

	public AppControlActionClass(String appType, String deviceId,
			String deviceName, String deviceAuthority, String savedDeviceId,
			String saveDeviceName) {
		super();
		AppType = appType;
		DeviceId = deviceId;
		DeviceName = deviceName;
		DeviceAuthority = deviceAuthority;
		SavedDeviceId = savedDeviceId;
		SaveDeviceName = saveDeviceName;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return super.equals(o);
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}

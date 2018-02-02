package com.infisight.hudprojector.data;

/**
 * Date : 15-7-10
 * Description : 这个类封装了需要sdk连接设备的必要信息
 */
public class BleConnectDevice {

    /**
     * 唯一的标识：因为需要满足同时连接多个设备，所以不同设备之间需要通过tag来区别
     */
	private String Address;
	private String Name;
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	

  
}

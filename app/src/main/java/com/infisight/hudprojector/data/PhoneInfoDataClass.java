package com.infisight.hudprojector.data;

import android.graphics.Bitmap;

public class PhoneInfoDataClass {

	private String phoneName;// 名称
	private String phoneNumber;// 电话号码
	private String phoneTime;// 通话时长
	private String phoneLocation;// 归属地
	private Bitmap phonePhoto;// 头像

	public PhoneInfoDataClass() {
		super();
	}

	public PhoneInfoDataClass(String phoneName, String phoneNumber,
			String phoneTime, String phoneLocation, Bitmap phonePhoto) {
		super();
		this.phoneName = phoneName;
		this.phoneNumber = phoneNumber;
		this.phoneTime = phoneTime;
		this.phoneLocation = phoneLocation;
		this.phonePhoto = phonePhoto;
	}

	public String getPhoneName() {
		return phoneName;
	}

	public void setPhoneName(String phoneName) {
		this.phoneName = phoneName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneTime() {
		return phoneTime;
	}

	public void setPhoneTime(String phoneTime) {
		this.phoneTime = phoneTime;
	}

	public String getPhoneLocation() {
		return phoneLocation;
	}

	public void setPhoneLocation(String phoneLocation) {
		this.phoneLocation = phoneLocation;
	}

	public Bitmap getPhonePhoto() {
		return phonePhoto;
	}

	public void setPhonePhoto(Bitmap phonePhoto) {
		this.phonePhoto = phonePhoto;
	}

}

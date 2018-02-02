package com.infisight.hudprojector.data;

/**
 * @author hao 装载联想词的实体类
 * 
 */
public class AssociateKeyClass {

	private String KeyIndex;
	private String KeyName;
	private String KeyAddr;
	private String KeyCity;
	private String KeyDistance;// 与起点的距离

	public AssociateKeyClass() {
		super();
	}

	public AssociateKeyClass(String keyName, String keyCity) {
		super();
		KeyName = keyName;
		KeyCity = keyCity;
	}

	public AssociateKeyClass(String keyIndex, String keyName, String keyAddr,
			String keyCity, String keyDistance) {
		super();
		KeyIndex = keyIndex;
		KeyName = keyName;
		KeyAddr = keyAddr;
		KeyCity = keyCity;
		KeyDistance = keyDistance;
	}

	public String getKeyIndex() {
		return KeyIndex;
	}

	public void setKeyIndex(String keyIndex) {
		KeyIndex = keyIndex;
	}

	public String getKeyName() {
		return KeyName;
	}

	public void setKeyName(String keyName) {
		KeyName = keyName;
	}

	public String getKeyAddr() {
		return KeyAddr;
	}

	public void setKeyAddr(String keyAddr) {
		KeyAddr = keyAddr;
	}

	public String getKeyCity() {
		return KeyCity;
	}

	public void setKeyCity(String keyCity) {
		KeyCity = keyCity;
	}

	public String getKeyDistance() {
		return KeyDistance;
	}

	public void setKeyDistance(String keyDistance) {
		KeyDistance = keyDistance;
	}

}

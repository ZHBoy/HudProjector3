package com.infisight.hudprojector.data;

/**
 * @author hao
 * 
 *         传递音乐list对象类
 * 
 */
public class MusicTypeClass {
	private String id;
	private String musicTypeList;

	public MusicTypeClass(String id, String musicTypeList) {
		super();
		this.id = id;
		this.musicTypeList = musicTypeList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMusicTypeList() {
		return musicTypeList;
	}

	public void setMusicTypeList(String musicTypeList) {
		this.musicTypeList = musicTypeList;
	}

}

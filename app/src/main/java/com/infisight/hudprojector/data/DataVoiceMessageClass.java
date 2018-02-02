package com.infisight.hudprojector.data;

public class DataVoiceMessageClass {

	//语音合成
	private String voiceName;//发音人
	private String voiceSpeed;//发音速度
	private String voicePitch;//音调
	private String voiceVolume;//音量
	private String voiceType;//音量
	//语音识别
	private String voiceAccent;//口音
	public DataVoiceMessageClass() {
		super();
	}
	
	public DataVoiceMessageClass(String voiceName, String voiceSpeed,
			String voicePitch, String voiceVolume, String voiceType,
			String voiceAccent) {
		super();
		this.voiceName = voiceName;
		this.voiceSpeed = voiceSpeed;
		this.voicePitch = voicePitch;
		this.voiceVolume = voiceVolume;
		this.voiceType = voiceType;
		this.voiceAccent = voiceAccent;
	}

	public String getVoiceName() {
		return voiceName;
	}
	public void setVoiceName(String voiceName) {
		this.voiceName = voiceName;
	}
	public String getVoiceSpeed() {
		return voiceSpeed;
	}
	public void setVoiceSpeed(String voiceSpeed) {
		this.voiceSpeed = voiceSpeed;
	}
	public String getVoicePitch() {
		return voicePitch;
	}
	public void setVoicePitch(String voicePitch) {
		this.voicePitch = voicePitch;
	}
	public String getVoiceVolume() {
		return voiceVolume;
	}
	public void setVoiceVolume(String voiceVolume) {
		this.voiceVolume = voiceVolume;
	}
	public String getVoiceType() {
		return voiceType;
	}
	public void setVoiceType(String voiceType) {
		this.voiceType = voiceType;
	}
	public String getVoiceAccent() {
		return voiceAccent;
	}
	public void setVoiceAccent(String voiceAccent) {
		this.voiceAccent = voiceAccent;
	}
	
}

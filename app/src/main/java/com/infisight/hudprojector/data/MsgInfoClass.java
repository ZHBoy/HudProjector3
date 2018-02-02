package com.infisight.hudprojector.data;

/**
 * 短信信息实体类 
 * @author hao
 *
 */
public class MsgInfoClass {
	
	private String Msg_TelNum;
	private String Msg_Content;
	private String Msg_Time;
	private String Msg_DisplayName;
	private String Msg_Photo;
	private DataVoiceMessageClass mDataVoice;
	
	public MsgInfoClass() {
		super();
	}
	public MsgInfoClass(String msg_TelNum, String msg_Content, String msg_Time,
			String msg_DisplayName, String msg_Photo,
			DataVoiceMessageClass mDataVoice) {
		super();
		Msg_TelNum = msg_TelNum;
		Msg_Content = msg_Content;
		Msg_Time = msg_Time;
		Msg_DisplayName = msg_DisplayName;
		Msg_Photo = msg_Photo;
		this.mDataVoice = mDataVoice;
	}
	public String getMsg_TelNum() {
		return Msg_TelNum;
	}
	public void setMsg_TelNum(String msg_TelNum) {
		Msg_TelNum = msg_TelNum;
	}
	public String getMsg_Content() {
		return Msg_Content;
	}
	public void setMsg_Content(String msg_Content) {
		Msg_Content = msg_Content;
	}
	public String getMsg_Time() {
		return Msg_Time;
	}
	public void setMsg_Time(String msg_Time) {
		Msg_Time = msg_Time;
	}
	public String getMsg_DisplayName() {
		return Msg_DisplayName;
	}
	public void setMsg_DisplayName(String msg_DisplayName) {
		Msg_DisplayName = msg_DisplayName;
	}
	public String getMsg_Photo() {
		return Msg_Photo;
	}
	public void setMsg_Photo(String msg_Photo) {
		Msg_Photo = msg_Photo;
	}
	public DataVoiceMessageClass getmDataVoice() {
		return mDataVoice;
	}
	public void setmDataVoice(DataVoiceMessageClass mDataVoice) {
		this.mDataVoice = mDataVoice;
	}
	
}

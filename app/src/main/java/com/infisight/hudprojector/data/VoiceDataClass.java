package com.infisight.hudprojector.data;

/**
 * @author hao 语音识别与合成的封装类，当语音识别不到时，提示音
 */
public class VoiceDataClass {

	private String command;// 标示
	private String type;// 指令
	private String value;// 指令内容
	private String promptKey;// 提示词

	public VoiceDataClass() {
		super();
	}

	public VoiceDataClass(String command, String type, String value,
			String promptKey) {
		super();
		this.command = command;
		this.type = type;
		this.value = value;
		this.promptKey = promptKey;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPromptKey() {
		return promptKey;
	}

	public void setPromptKey(String promptKey) {
		this.promptKey = promptKey;
	}

	@Override
	public String toString() {
		return "VoiceDataClass [command=" + command + ", type=" + type
				+ ", value=" + value + ", promptKey=" + promptKey + "]";
	}

	
}

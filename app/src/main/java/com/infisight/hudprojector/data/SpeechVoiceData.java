package com.infisight.hudprojector.data;

public class SpeechVoiceData {

	private String command;
	private String value;

	public SpeechVoiceData() {

	}

	public SpeechVoiceData(String command, String string) {
		super();
		this.command = command;
		this.value = string;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "SpeechVoiceData [command=" + command + ", value=" + value + "]";
	}
	

}

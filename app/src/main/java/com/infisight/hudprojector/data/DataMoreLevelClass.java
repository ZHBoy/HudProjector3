package com.infisight.hudprojector.data;

/**
 * @author hao 此类用于一个类型的操作，有多次数据交互的时候。 比如：打电话时，1，说联系人
 *         2.确认联系人，这两个操作同用一个action，这时候就需要区分了。
 * 
 */
public class DataMoreLevelClass {

	private int command;
	private String content;

	public DataMoreLevelClass() {
		super();
	}

	public DataMoreLevelClass(int command, String content) {
		super();
		this.command = command;
		this.content = content;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}

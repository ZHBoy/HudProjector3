package com.infisight.hudprojector.data;

import java.io.Serializable;

/**
 * 主消息结构体
 * 
 * @author Administrator
 * 
 */
public class PMessage implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String toname;
	public String fromname;
	public String message;// 其他结构体都转为String类型放到这个数据中
	public int command;// audio,text,cmd,img,stream,video

	public PMessage(String toname, String fromname, String message, int command) {
		this.toname = toname;
		this.fromname = fromname;
		this.message = message;
		this.command = command;
	}

	public PMessage() {
		
	}
}

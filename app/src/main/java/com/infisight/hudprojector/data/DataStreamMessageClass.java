package com.infisight.hudprojector.data;
/**
 * 流媒体消息信息
 * @author Administrator
 *
 */
public class DataStreamMessageClass {
	private String content;
	private String name;
	private String path;
	private int size;
	private String updatetime; //System.Date.getMilliseconds()格式
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	
}

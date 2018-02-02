package com.infisight.hudprojector.data;
/**
 * OBD HUD 命令配置
 * @author Administrator
 *
 */
public class ObdCmdsConfigClass {
	private String cmd;//向OBD发送的命令，例如 010C
	private String data;//OBD返回并解析组合后的数据，例如 2014 RPM
	private String desc;//命令的描述，例如 引擎转速
	private String resType;//数据单位，例如 RPM
	private String impType;//
	private int  showOrder;//显示顺序
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getResType() {
		return resType;
	}
	public void setResType(String resType) {
		this.resType = resType;
	}
	public String getImpType() {
		return impType;
	}
	public void setImpType(String impType) {
		this.impType = impType;
	}
	public int getShowOrder() {
		return showOrder;
	}
	public void setShowOrder(int showOrder) {
		this.showOrder = showOrder;
	}
	
}

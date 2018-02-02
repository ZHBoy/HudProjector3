package com.infisight.hudprojector.data;

/**
 * 菜单的信息
 * @author Administrator
 *
 */
public class MenuClass {
	private int menuid;
	private int ordernum ;
	private String menuname;
	private int menuicon;
	private String fragmentname;
	public int getMenuid() {
		return menuid;
	}
	public void setMenuid(int menuid) {
		this.menuid = menuid;
	}
	public String getMenuname() {
		return menuname;
	}
	public void setMenuname(String menuname) {
		this.menuname = menuname;
	}
	public String getFragmentname() {
		return fragmentname;
	}
	public void setFragmentname(String fragmentname) {
		this.fragmentname = fragmentname;
	}
	public int getOrdernum() {
		return ordernum;
	}
	public void setOrdernum(int ordernum) {
		this.ordernum = ordernum;
	}
	public int getMenuicon() {
		return menuicon;
	}
	public void setMenuicon(int menuicon) {
		this.menuicon = menuicon;
	}
	
}

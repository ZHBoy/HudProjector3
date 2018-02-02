package com.infisight.hudprojector.data;

public class StepsInfo {
	private int time;
	private double distance;
	private String steps;
	
	
	public StepsInfo(int time, double distance, String steps) {
		super();
		this.time = time;
		this.distance = distance;
		this.steps = steps;
	}
	
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	
	public double geDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public String getSteps() {
		return steps;
	}
	public void setSteps(String steps) {
		this.steps = steps;
	}
	
}

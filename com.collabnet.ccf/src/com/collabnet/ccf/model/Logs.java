package com.collabnet.ccf.model;

public class Logs {
	private Landscape landscape;

	public Logs(Landscape landscape) {
		this.landscape = landscape;
	}
	
	public String toString() {
		return "Logs";
	}
	
	public Landscape getLandscape() {
		return landscape;
	}

}

package com.collabnet.ccf.model;

public class Logs {
	private Landscape landscape;
	private int type;
	
	public static final int TYPE_FOLDER = 0;
	public static final int TYPE_1_2 = 1;
	public static final int TYPE_2_1 = 2;

	public Logs(Landscape landscape) {
		this.landscape = landscape;
	}
	
	public Logs(Landscape landscape, int type) {
		this(landscape);
		this.type = type;
	}
	
	public String toString() {
		switch (type) {
		case TYPE_FOLDER:
			return "Logs";
		case TYPE_1_2:
			return Landscape.getTypeDescription(landscape.getType1()) + " => " + Landscape.getTypeDescription(landscape.getType2());
		case TYPE_2_1:
			return Landscape.getTypeDescription(landscape.getType2()) + " => " + Landscape.getTypeDescription(landscape.getType1());			
		default:
			return "Logs";
		}
	}
	
	public Landscape getLandscape() {
		return landscape;
	}
	
	public int getType() {
		return type;
	}

}

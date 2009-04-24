package com.collabnet.ccf.model;

public class ProjectMappings {
	private Landscape landscape;

	public ProjectMappings(Landscape landscape) {
		this.landscape = landscape;
	}
	
	public String toString() {
		return "Project Mappings";
	}
	
	public Landscape getLandscape() {
		return landscape;
	}

}

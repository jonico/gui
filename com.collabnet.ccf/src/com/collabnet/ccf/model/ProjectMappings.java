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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProjectMappings) {
			ProjectMappings compareTo = (ProjectMappings)obj;
			return landscape.equals(compareTo.getLandscape());
		}
		return super.equals(obj);
	}

}

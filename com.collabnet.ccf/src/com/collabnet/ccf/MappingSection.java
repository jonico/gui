package com.collabnet.ccf;


public abstract class MappingSection implements IMappingSection {
	private IPageCompleteListener projectPage;
	private int systemNumber;

	public IPageCompleteListener getProjectPage() {
		return projectPage;
	}

	public void setProjectPage(IPageCompleteListener projectPage) {
		this.projectPage = projectPage;
	}

	public int getSystemNumber() {
		return systemNumber;
	}

	public void setSystemNumber(int systemNumber) {
		this.systemNumber = systemNumber;
	}

}

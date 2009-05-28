package com.collabnet.ccf.model;

public class InconsistentIdentityMapping extends IdentityMapping {
	private int type;

	public final static int UNKNOWN = -1;
	public final static int MULTIPLE_SOURCE_TO_ONE_TARGET = 0;
	public final static int MULTIPLE_TARGET_TO_ONE_SOURCE = 1;
	public final static int ONE_WAY = 2;

	public InconsistentIdentityMapping(int type) {
		super();
		this.type = type;
	}
	
	public int getType() {
		return type;
	}

}

package com.collabnet.ccf.model;

public class InconsistentIdentityMapping extends IdentityMapping {
	private IdentityMappingConsistencyCheck consistencyCheck;

	public final static int MULTIPLE_SOURCE_TO_ONE_TARGET = 0;
	public final static int MULTIPLE_TARGET_TO_ONE_SOURCE = 1;
	public final static int ONE_WAY = 2;

	public InconsistentIdentityMapping(IdentityMappingConsistencyCheck consistencyCheck) {
		super();
		this.consistencyCheck = consistencyCheck;
	}
	
	public int getType() {
		return consistencyCheck.getType();
	}
	
	public IdentityMappingConsistencyCheck getConsistencyCheck() {
		return consistencyCheck;
	}

}

package com.collabnet.ccf.model;

public class IdentityMappingConsistencyCheck {
	private SynchronizationStatus synchronizationStatus;
	private int type;

	public final static int MULTIPLE_SOURCE_TO_ONE_TARGET = 0;
	public final static int MULTIPLE_TARGET_TO_ONE_SOURCE = 1;
	public final static int ONE_WAY = 2;
	
	private final static String MULTIPLE_SOURCE_TO_ONE_TARGET_DESCRIPTION = "Multiple source to one target";
	private final static String MULTIPLE_TARGET_TO_ONE_SOURCE_DESCRIPTION = "Multiple target to one source";
	private final static String ONE_WAY_DESCRIPTION = "One way mapping";

	public IdentityMappingConsistencyCheck(SynchronizationStatus synchronizationStatus, int type) {
		this.synchronizationStatus = synchronizationStatus;
		this.type = type;
	}
	
	public String toString() {
		switch (type) {
		case MULTIPLE_SOURCE_TO_ONE_TARGET:
			return MULTIPLE_SOURCE_TO_ONE_TARGET_DESCRIPTION;
		case MULTIPLE_TARGET_TO_ONE_SOURCE:
			return MULTIPLE_TARGET_TO_ONE_SOURCE_DESCRIPTION;
		case ONE_WAY:
			return ONE_WAY_DESCRIPTION;			
		default:
			return null;
		}
	}
	
	public SynchronizationStatus getSynchronizationStatus() {
		return synchronizationStatus;
	}
	
	public Landscape getLandscape() {
		return synchronizationStatus.getLandscape();
	}

	public String getRepository() {
		return synchronizationStatus.getSourceRepositoryId();
	}
	
	public int getType() {
		return type;
	}

}

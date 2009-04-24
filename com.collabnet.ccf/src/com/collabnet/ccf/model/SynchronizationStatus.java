package com.collabnet.ccf.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class SynchronizationStatus implements IPropertySource {
	private String sourceSystemId;
	private String sourceRepositoryId;
	private String targetSystemId;
	private String targetRepositoryId;
	private String sourceSystemKind;
	private String sourceRepositoryKind;
	private String targetSystemKind;
	private String targetRepositoryKind;
	private Timestamp sourceLastModificationTime;
	private String sourceLastArtifactVersion;
	private String sourceLastArtifactId;
	private String conflictResolutionPriority;
	private String sourceSystemTimezone;
	private String targetSystemTimezone;
	private String sourceSystemEncoding;
	private String targetSystemEncoding;
	
	public static String P_ID_SOURCE_SYSTEM_ID = "srcSysId"; //$NON-NLS-1$
	public static String P_SOURCE_SYSTEM_ID = "Source system ID";
	public static String P_ID_SOURCE_REPOSITORY_ID = "srcRepoId"; //$NON-NLS-1$
	public static String P_SOURCE_REPOSITORY_ID = "Source repository ID";	
	public static String P_ID_TARGET_SYSTEM_ID = "tgtSysId"; //$NON-NLS-1$
	public static String P_TARGET_SYSTEM_ID = "Target system ID";
	public static String P_ID_TARGET_REPOSITORY_ID = "tgtRepoId"; //$NON-NLS-1$
	public static String P_TARGET_REPOSITORY_ID = "Target repository ID";	
	public static String P_ID_SOURCE_SYSTEM_KIND = "srcSysKind"; //$NON-NLS-1$
	public static String P_SOURCE_SYSTEM_KIND = "Source system kind";
	public static String P_ID_SOURCE_REPOSITORY_KIND = "srcRepoKind"; //$NON-NLS-1$
	public static String P_SOURCE_REPOSITORY_KIND = "Source repository kind";
	public static String P_ID_TARGET_SYSTEM_KIND = "tgtSysKind"; //$NON-NLS-1$
	public static String P_TARGET_SYSTEM_KIND = "Target system kind";
	public static String P_ID_TARGET_REPOSITORY_KIND = "tgtRepoKind"; //$NON-NLS-1$
	public static String P_TARGET_REPOSITORY_KIND = "Target repository kind";	
	public static String P_ID_SOURCE_LAST_MODIFICATION_TIME = "srcLastMod"; //$NON-NLS-1$
	public static String P_SOURCE_LAST_MODIFICATION_TIME = "Source last modification time";
	public static String P_ID_SOURCE_LAST_ARTIFACT_VERSION = "srcLastArtVer"; //$NON-NLS-1$
	public static String P_SOURCE_LAST_ARTIFACT_VERSION = "Source last artifact version";
	public static String P_ID_SOURCE_LAST_ARTIFACT_ID = "srcLastArtId"; //$NON-NLS-1$
	public static String P_SOURCE_LAST_ARTIFACT_ID = "Source last artifact ID";
	public static String P_ID_CONFLICT_RESOLUTION_PRIORITY = "confResPty"; //$NON-NLS-1$
	public static String P_CONFLICT_RESOLUTION_PRIORITY = "Conflict resolution priority";
	public static String P_ID_SOURCE_SYSTEM_TIMEZONE = "srcTimezone"; //$NON-NLS-1$
	public static String P_SOURCE_SYSTEM_TIMEZONE = "Source system timezone";
	public static String P_ID_TARGET_SYSTEM_TIMEZONE = "tgtTimezone"; //$NON-NLS-1$
	public static String P_TARGET_SYSTEM_TIMEZONE = "Target system timezone";
	public static String P_ID_SOURCE_SYSTEM_ENCODING = "srcEncoding"; //$NON-NLS-1$
	public static String P_SOURCE_SYSTEM_ENCODING = "Source system encoding";
	public static String P_ID_TARGET_SYSTEM_ENCODING = "tgtEncoding"; //$NON-NLS-1$
	public static String P_TARGET_SYSTEM_ENCODING = "Target system encoding";	
	
	public static List<PropertyDescriptor> descriptors;
	static
	{	
		descriptors = new ArrayList<PropertyDescriptor>();
		descriptors.add(new PropertyDescriptor(P_ID_SOURCE_SYSTEM_ID, P_SOURCE_SYSTEM_ID));
		descriptors.add(new PropertyDescriptor(P_ID_SOURCE_REPOSITORY_ID, P_SOURCE_REPOSITORY_ID));
		descriptors.add(new PropertyDescriptor(P_ID_TARGET_SYSTEM_ID, P_TARGET_SYSTEM_ID));
		descriptors.add(new PropertyDescriptor(P_ID_TARGET_REPOSITORY_ID, P_TARGET_REPOSITORY_ID));
		descriptors.add(new PropertyDescriptor(P_ID_SOURCE_SYSTEM_KIND, P_SOURCE_SYSTEM_KIND));
		descriptors.add(new PropertyDescriptor(P_ID_SOURCE_REPOSITORY_KIND, P_SOURCE_REPOSITORY_KIND));
		descriptors.add(new PropertyDescriptor(P_ID_TARGET_SYSTEM_KIND, P_TARGET_SYSTEM_KIND));
		descriptors.add(new PropertyDescriptor(P_ID_TARGET_REPOSITORY_KIND, P_TARGET_REPOSITORY_KIND));	
		descriptors.add(new PropertyDescriptor(P_ID_SOURCE_LAST_MODIFICATION_TIME, P_SOURCE_LAST_MODIFICATION_TIME));
		descriptors.add(new PropertyDescriptor(P_ID_SOURCE_LAST_ARTIFACT_VERSION, P_SOURCE_LAST_ARTIFACT_VERSION));
		descriptors.add(new PropertyDescriptor(P_ID_SOURCE_LAST_ARTIFACT_ID, P_SOURCE_LAST_ARTIFACT_ID));
		descriptors.add(new PropertyDescriptor(P_ID_CONFLICT_RESOLUTION_PRIORITY, P_CONFLICT_RESOLUTION_PRIORITY));
		descriptors.add(new PropertyDescriptor(P_ID_SOURCE_SYSTEM_TIMEZONE, P_SOURCE_SYSTEM_TIMEZONE));
		descriptors.add(new PropertyDescriptor(P_ID_TARGET_SYSTEM_TIMEZONE, P_TARGET_SYSTEM_TIMEZONE));
		descriptors.add(new PropertyDescriptor(P_ID_SOURCE_SYSTEM_ENCODING, P_SOURCE_SYSTEM_ENCODING));
		descriptors.add(new PropertyDescriptor(P_ID_TARGET_SYSTEM_ENCODING, P_TARGET_SYSTEM_ENCODING));
	}		
	
	public String getSourceSystemId() {
		return sourceSystemId;
	}
	public void setSourceSystemId(String sourceSystemId) {
		this.sourceSystemId = sourceSystemId;
	}
	public String getSourceRepositoryId() {
		return sourceRepositoryId;
	}
	public void setSourceRepositoryId(String sourceRepositoryId) {
		this.sourceRepositoryId = sourceRepositoryId;
	}
	public String getTargetSystemId() {
		return targetSystemId;
	}
	public void setTargetSystemId(String targetSystemId) {
		this.targetSystemId = targetSystemId;
	}
	public String getTargetRepositoryId() {
		return targetRepositoryId;
	}
	public void setTargetRepositoryId(String targetRepositoryId) {
		this.targetRepositoryId = targetRepositoryId;
	}
	public String getSourceSystemKind() {
		return sourceSystemKind;
	}
	public void setSourceSystemKind(String sourceSystemKind) {
		this.sourceSystemKind = sourceSystemKind;
	}
	public String getSourceRepositoryKind() {
		return sourceRepositoryKind;
	}
	public void setSourceRepositoryKind(String sourceRepositoryKind) {
		this.sourceRepositoryKind = sourceRepositoryKind;
	}
	public String getTargetSystemKind() {
		return targetSystemKind;
	}
	public void setTargetSystemKind(String targetSystemKind) {
		this.targetSystemKind = targetSystemKind;
	}
	public String getTargetRepositoryKind() {
		return targetRepositoryKind;
	}
	public void setTargetRepositoryKind(String targetRepositoryKind) {
		this.targetRepositoryKind = targetRepositoryKind;
	}
	public Timestamp getSourceLastModificationTime() {
		return sourceLastModificationTime;
	}
	public void setSourceLastModificationTime(Timestamp sourceLastModificationTime) {
		this.sourceLastModificationTime = sourceLastModificationTime;
	}
	public String getSourceLastArtifactVersion() {
		return sourceLastArtifactVersion;
	}
	public void setSourceLastArtifactVersion(String sourceLastArtifactVersion) {
		this.sourceLastArtifactVersion = sourceLastArtifactVersion;
	}
	public String getSourceLastArtifactId() {
		return sourceLastArtifactId;
	}
	public void setSourceLastArtifactId(String sourceLastArtifactId) {
		this.sourceLastArtifactId = sourceLastArtifactId;
	}
	public String getConflictResolutionPriority() {
		return conflictResolutionPriority;
	}
	public void setConflictResolutionPriority(String conflictResolutionPriority) {
		this.conflictResolutionPriority = conflictResolutionPriority;
	}
	public String getSourceSystemTimezone() {
		return sourceSystemTimezone;
	}
	public void setSourceSystemTimezone(String sourceSystemTimezone) {
		this.sourceSystemTimezone = sourceSystemTimezone;
	}
	public String getTargetSystemTimezone() {
		return targetSystemTimezone;
	}
	public void setTargetSystemTimezone(String targetSystemTimezone) {
		this.targetSystemTimezone = targetSystemTimezone;
	}
	public String getSourceSystemEncoding() {
		return sourceSystemEncoding;
	}
	public void setSourceSystemEncoding(String sourceSystemEncoding) {
		this.sourceSystemEncoding = sourceSystemEncoding;
	}
	public String getTargetSystemEncoding() {
		return targetSystemEncoding;
	}
	public void setTargetSystemEncoding(String targetSystemEncoding) {
		this.targetSystemEncoding = targetSystemEncoding;
	}
	
	public Object getEditableValue() {
		return sourceSystemId + "/" + sourceRepositoryId + "-" + targetSystemId + "/" + targetRepositoryId;
	}
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return (IPropertyDescriptor[])getDescriptors().toArray(new IPropertyDescriptor[getDescriptors().size()]);
	}
	
	private static List<PropertyDescriptor> getDescriptors() {
		return descriptors;
	}	
	
	public Object getPropertyValue(Object id) {
		if (P_ID_SOURCE_SYSTEM_ID.equals(id)) return sourceSystemId;
		if (P_ID_SOURCE_REPOSITORY_ID.equals(id)) return sourceRepositoryId;
		if (P_ID_TARGET_SYSTEM_ID.equals(id)) return targetSystemId;
		if (P_ID_TARGET_REPOSITORY_ID.equals(id)) return targetRepositoryId;		
		if (P_ID_SOURCE_SYSTEM_KIND.equals(id)) return sourceSystemKind;
		if (P_ID_SOURCE_REPOSITORY_KIND.equals(id)) return sourceRepositoryKind;
		if (P_ID_TARGET_SYSTEM_KIND.equals(id)) return targetSystemKind;
		if (P_ID_TARGET_REPOSITORY_KIND.equals(id)) return targetRepositoryKind;
		if (P_ID_SOURCE_LAST_MODIFICATION_TIME.equals(id)) {
			if (sourceLastModificationTime != null) return sourceLastModificationTime.toString();
		}
		if (P_ID_SOURCE_LAST_ARTIFACT_VERSION.equals(id)) return sourceLastArtifactVersion;
		if (P_ID_SOURCE_LAST_ARTIFACT_ID.equals(id)) return sourceLastArtifactId;
		if (P_ID_CONFLICT_RESOLUTION_PRIORITY.equals(id)) return conflictResolutionPriority;
		if (P_ID_SOURCE_SYSTEM_TIMEZONE.equals(id)) return sourceSystemTimezone;
		if (P_ID_SOURCE_SYSTEM_ENCODING.equals(id)) return sourceSystemEncoding;
		if (P_ID_TARGET_SYSTEM_TIMEZONE.equals(id)) return targetSystemTimezone;
		if (P_ID_TARGET_SYSTEM_ENCODING.equals(id)) return targetSystemEncoding;
		return null;
	}
	
	public boolean isPropertySet(Object id) {
		return false;
	}
	
	public void resetPropertyValue(Object id) {
	}
	
	public void setPropertyValue(Object id, Object value) {
	}
}

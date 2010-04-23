package com.collabnet.ccf.model;

import org.eclipse.swt.graphics.Image;

import com.collabnet.ccf.ICcfParticipant;

public class MappingGroup {
	private ProjectMappings projectMappingsParent;
	private ICcfParticipant ccfParticipant;
	private String id;
	private String text;
	private Image image;
	private MappingGroup[] childGroups;
	private SynchronizationStatus[] childMappings;
	
	public MappingGroup(ICcfParticipant ccfParticipant, ProjectMappings projectMappingsParent, String id, String text, Image image) {
		super();
		this.ccfParticipant = ccfParticipant;
		this.projectMappingsParent = projectMappingsParent;
		this.id = id;
		this.text = text;
		this.image = image;
	}

	public MappingGroup[] getChildGroups() {
		return childGroups;
	}

	public void setChildGroups(MappingGroup[] childGroups) {
		this.childGroups = childGroups;
	}

	public SynchronizationStatus[] getChildMappings() {
		return childMappings;
	}

	public void setChildMappings(SynchronizationStatus[] childMappings) {
		this.childMappings = childMappings;
	}

	public String getText() {
		return text;
	}

	public Image getImage() {
		return image;
	}

	public String getId() {
		return id;
	}

	public ICcfParticipant getCcfParticipant() {
		return ccfParticipant;
	}

	public ProjectMappings getProjectMappingsParent() {
		return projectMappingsParent;
	}

	public String toString() {
		if (text != null) {
			return text;
		}
		return super.toString();
	}
	
	public Object[] getChildren() {
		if (childGroups == null) {
			return childMappings;
		} else {
			return childGroups;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MappingGroup) {
			MappingGroup compareTo = (MappingGroup)obj;
			return compareTo.getId().equals(id);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

}

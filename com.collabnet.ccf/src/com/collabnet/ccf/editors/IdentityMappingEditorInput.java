package com.collabnet.ccf.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.collabnet.ccf.model.IdentityMapping;

public class IdentityMappingEditorInput implements IEditorInput {
	private IdentityMapping identityMapping;

	public IdentityMappingEditorInput(IdentityMapping identityMapping) {
		this.identityMapping = identityMapping;
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return identityMapping.getEditableValue().toString();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "";
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class obj) {
		return null;
	}

	public IdentityMapping getIdentityMapping() {
		return identityMapping;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IdentityMappingEditorInput) {
			IdentityMappingEditorInput compareTo = (IdentityMappingEditorInput)obj;
			return compareTo.getIdentityMapping().equals(identityMapping);
		}
		return super.equals(obj);
	}

}

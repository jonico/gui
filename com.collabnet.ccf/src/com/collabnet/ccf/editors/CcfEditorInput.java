package com.collabnet.ccf.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.collabnet.ccf.model.Landscape;

public class CcfEditorInput implements IEditorInput {
	private Landscape landscape;
		
	public CcfEditorInput(Landscape landscape) {
		super();
		this.landscape = landscape;
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return landscape.getDescription();
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
	
	public Landscape getLandscape() {
		return landscape;
	}
	
	public void setLandscape(Landscape landscape) {
		this.landscape = landscape;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CcfEditorInput)) return false;
		CcfEditorInput compareInput = (CcfEditorInput)obj;
		return landscape.getDescription().equals(compareInput.getLandscape().getDescription());
	}

}

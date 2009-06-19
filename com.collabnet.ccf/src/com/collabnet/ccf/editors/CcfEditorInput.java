package com.collabnet.ccf.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.collabnet.ccf.model.Landscape;

public class CcfEditorInput implements IEditorInput {
	private Landscape landscape;
	private int editorType;

	public final static int EDITOR_TYPE_LANDSCAPE = 0;
	public final static int EDITOR_TYPE_STATUS = 1;
		
	public CcfEditorInput(Landscape landscape, int editorType) {
		super();
		this.landscape = landscape;
		this.editorType = editorType;
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
	
	public int getEditorType() {
		return editorType;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CcfEditorInput)) return false;
		CcfEditorInput compareInput = (CcfEditorInput)obj;
		return editorType == compareInput.getEditorType() && landscape.getDescription().equals(compareInput.getLandscape().getDescription());
	}

}

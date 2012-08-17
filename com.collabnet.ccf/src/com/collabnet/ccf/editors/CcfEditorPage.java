package com.collabnet.ccf.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.collabnet.ccf.model.Landscape;

public abstract class CcfEditorPage extends FormPage {
	
	public final static String STATE_CONTRACTED = "C";
	public final static String STATE_EXPANDED = "E";
	public final static String ACTIVE_PAGE = "CcfEditor.activePageId";

	public CcfEditorPage(String id, String title) {
		super(id, title);
	}

	public CcfEditorPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	@Override
	public void setActive(boolean active) {
		if (active) {
			((CcfEditor)getEditor()).getDialogSettings().put(ACTIVE_PAGE, getId());
		}
		super.setActive(active);
	}

	public IDialogSettings getDialogSettings() {
		return ((CcfEditor)getEditor()).getDialogSettings();
	}
	
	public Landscape getLandscape() {
		return ((CcfEditorInput)getEditorInput()).getLandscape();
	}
	
	public void setLandscape(Landscape landscape) {
		((CcfEditorInput)getEditorInput()).setLandscape(landscape);
	}
	
	public abstract void doSave(IProgressMonitor monitor);
	
	public abstract void setSystemNumber(int systemNumber);

}

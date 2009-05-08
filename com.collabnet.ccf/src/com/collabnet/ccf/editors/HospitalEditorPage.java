package com.collabnet.ccf.editors;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.collabnet.ccf.model.Patient;

public class HospitalEditorPage extends FormPage {
	
	public final static String STATE_CONTRACTED = "C";
	public final static String STATE_EXPANDED = "E";
	public final static String ACTIVE_PAGE = "HospitalEditor.activePageId";

	public HospitalEditorPage(String id, String title) {
		super(id, title);
	}

	public HospitalEditorPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}
	
	@Override
	public void setActive(boolean active) {
		if (active) {
			((HospitalEditor)getEditor()).getDialogSettings().put(ACTIVE_PAGE, getId());
		}
		super.setActive(active);
	}
	
	public IDialogSettings getDialogSettings() {
		return ((CcfEditor)getEditor()).getDialogSettings();
	}
	
	public Patient getPatient() {
		return ((HospitalEditorInput)getEditorInput()).getPatient();
	}

}

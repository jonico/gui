package com.collabnet.ccf.editors;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.collabnet.ccf.model.Landscape;

public class JmxConsoleEditorPage extends FormPage {
	
	public final static String ACTIVE_PAGE = "JmxConsoleEditor.activePageId";
	
	public JmxConsoleEditorPage(String id, String title) {
		super(id, title);
	}

	public JmxConsoleEditorPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}
	
	@Override
	public void setActive(boolean active) {
		if (active) {
			((JmxConsoleEditor)getEditor()).getDialogSettings().put(ACTIVE_PAGE, getId());
		}
		super.setActive(active);
	}
	
	public Landscape getLandscape() {
		return ((CcfEditorInput)getEditorInput()).getLandscape();
	}
	
	public void setLandscape(Landscape landscape) {
		((CcfEditorInput)getEditorInput()).setLandscape(landscape);
	}

}

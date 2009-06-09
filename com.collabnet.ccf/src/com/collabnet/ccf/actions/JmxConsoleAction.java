package com.collabnet.ccf.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.editors.CcfEditorInput;
import com.collabnet.ccf.editors.JmxConsoleEditor;
import com.collabnet.ccf.model.Landscape;

public class JmxConsoleAction extends Action {
	private Landscape landscape;

	public JmxConsoleAction(Landscape landscape) {
		this.landscape = landscape;
		setText("Show status...");
		setImageDescriptor(Activator.getDefault().getImageDescriptor(Activator.IMAGE_MONITOR));
	}

	@Override
	public void run() {
		CcfEditorInput editorInput = new CcfEditorInput(landscape);
		IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			page.openEditor(editorInput, JmxConsoleEditor.ID);
		} catch (PartInitException e) {
			Activator.handleError(e);
		}
	}
}

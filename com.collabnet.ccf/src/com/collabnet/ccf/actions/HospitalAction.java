package com.collabnet.ccf.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionDelegate;

public class HospitalAction extends ActionDelegate {
	private IStructuredSelection fSelection;
	
	public void run(IAction action) {
		MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Show Hospital", "Not yet implemented.");
	}

	public void selectionChanged(IAction action, ISelection sel) {
		if (sel instanceof IStructuredSelection) {
			fSelection= (IStructuredSelection) sel;
		}
	}	
}

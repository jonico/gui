package com.collabnet.ccf.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionDelegate;

public class ExaminePayloadAction extends ActionDelegate {

	public void run(IAction action) {
		MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Examine Payload", "Not yet implemented.");
	}	
	
}

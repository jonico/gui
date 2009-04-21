package com.collabnet.ccf.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.wizards.CustomWizardDialog;
import com.collabnet.ccf.wizards.NewLandscapeWizard;

public class NewLandscapeAction extends Action {

	@Override
	public String getText() {
		return "Add CCF Landscape...";
	}

	@Override
	public String getToolTipText() {
		return "Add CCF landscape";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_LANDSCAPE);
	}

	@Override
	public void run() {
		NewLandscapeWizard wizard = new NewLandscapeWizard();
		WizardDialog dialog = new CustomWizardDialog(Display.getDefault().getActiveShell(), wizard);
		dialog.open();
	}

}

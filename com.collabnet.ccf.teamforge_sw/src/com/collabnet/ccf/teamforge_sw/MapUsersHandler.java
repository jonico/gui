package com.collabnet.ccf.teamforge_sw;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.teamforge_sw.wizards.MapUsersWizard;
import com.collabnet.ccf.wizards.CustomWizardDialog;

public class MapUsersHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			Object[] items = ((IStructuredSelection) selection).toArray();
			for (Object item : items) {
				if (item instanceof SynchronizationStatus) {
					SynchronizationStatus projectMapping = (SynchronizationStatus)item;
					MapUsersWizard wizard = new MapUsersWizard(projectMapping);
					WizardDialog dialog = new CustomWizardDialog(Display.getDefault().getActiveShell(), wizard);
					dialog.open();
				}
			}
		}
		return null;
	}

}

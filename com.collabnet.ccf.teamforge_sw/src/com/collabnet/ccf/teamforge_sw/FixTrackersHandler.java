package com.collabnet.ccf.teamforge_sw;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import com.collabnet.ccf.model.MappingGroup;
import com.collabnet.ccf.teamforge_sw.wizards.FixTrackersWizard;
import com.collabnet.ccf.wizards.CustomWizardDialog;

public class FixTrackersHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			Object[] items = ((IStructuredSelection) selection).toArray();
			for (Object item : items) {
				if (item instanceof MappingGroup) {
					MappingGroup mappingGroup = (MappingGroup)item;
					FixTrackersWizard wizard = new FixTrackersWizard(mappingGroup);
					WizardDialog dialog = new CustomWizardDialog(Display.getDefault().getActiveShell(), wizard);
					dialog.open();
				}
			}
		}
		return null;
	}

}

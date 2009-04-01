package com.collabnet.ccf.actions;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionDelegate;

import com.collabnet.ccf.model.Hospital;

public class ReplayHospitalAction extends ActionDelegate {
	private IStructuredSelection fSelection;

	public void run(IAction action) {
		MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Replay Hospital", "Not yet implemented.");
	}
	
	public void selectionChanged(IAction action, ISelection sel) {
		if (sel instanceof IStructuredSelection) {
			fSelection= (IStructuredSelection) sel;
			if (action != null) action.setEnabled(isEnabledForSelection());
		}
	}	
	
	@SuppressWarnings("unchecked")
	private boolean isEnabledForSelection() {
		if (fSelection == null) return false;
		Iterator iter = fSelection.iterator();
		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof Hospital) {
				Hospital hospital = (Hospital)object;
				if (!hospital.getOriginatingComponent().endsWith("EntityService") && !hospital.getOriginatingComponent().endsWith("Write"))
					return false;
			}
		}
		return true;
	}	
	
}

package com.collabnet.ccf.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionDelegate;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.editors.StringInput;
import com.collabnet.ccf.editors.StringStorage;
import com.collabnet.ccf.model.Patient;

public class HospitalStackTraceAction extends ActionDelegate {
	private IStructuredSelection fSelection;
	
	@SuppressWarnings("unchecked")
	public void run(IAction action) {
		Iterator iter = fSelection.iterator();
		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof Patient) {
				IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
				Patient patient = (Patient)object;
				IStorage storage = new StringStorage(patient.getStackTrace(), patient.getId() + " Stack Trace");
				IStorageEditorInput input = new StringInput(storage);
				try {
					page.openEditor(input, "org.eclipse.ui.DefaultTextEditor");
				} catch (PartInitException e) {
					Activator.handleError("Hospital Stack Trace", e);
					break;
				}
			}
		}
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
			if (object instanceof Patient) {
				Patient patient = (Patient)object;
				if (patient.getStackTrace() == null || patient.getStackTrace().length() == 0)
					return false;
			}
		}
		return true;
	}	

}

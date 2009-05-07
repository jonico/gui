package com.collabnet.ccf.actions;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionDelegate;

import com.collabnet.ccf.dialogs.NewProjectMappingDialog;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.views.CcfExplorerView;

public class AddSynchronizationStatusAction extends ActionDelegate {
	private IStructuredSelection fSelection;
	
	@SuppressWarnings("unchecked")
	public void run(IAction action) {
		Iterator iter = fSelection.iterator();
		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof ProjectMappings) {
				ProjectMappings projectMappings = (ProjectMappings)object;
				NewProjectMappingDialog dialog = new NewProjectMappingDialog(Display.getDefault().getActiveShell(), projectMappings);
				if (dialog.open() == NewProjectMappingDialog.OK && CcfExplorerView.getView() != null) {
					CcfExplorerView.getView().refresh(projectMappings);
				}
			}
		}
	}

	public void selectionChanged(IAction action, ISelection sel) {
		if (sel instanceof IStructuredSelection) {
			fSelection= (IStructuredSelection) sel;
		}
	}	
}

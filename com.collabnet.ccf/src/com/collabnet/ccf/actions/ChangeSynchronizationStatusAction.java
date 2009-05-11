package com.collabnet.ccf.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionDelegate;

import com.collabnet.ccf.dialogs.ChangeProjectMappingDialog;
import com.collabnet.ccf.editors.CcfProjectMappingsEditorPage;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.views.CcfExplorerView;

public class ChangeSynchronizationStatusAction extends ActionDelegate {
	private IStructuredSelection fSelection;

	@SuppressWarnings("unchecked")
	public void run(IAction action) {
		List<ProjectMappings> projectMappingsList = new ArrayList<ProjectMappings>();
		boolean mappingsChanged = false;
		Iterator iter = fSelection.iterator();
		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof SynchronizationStatus) {
				SynchronizationStatus status = (SynchronizationStatus)object;
				ChangeProjectMappingDialog dialog = new ChangeProjectMappingDialog(Display.getDefault().getActiveShell(), status);
				if (dialog.open() == ChangeProjectMappingDialog.CANCEL) return;
				if (!projectMappingsList.contains(status.getProjectMappings())) {
					projectMappingsList.add(status.getProjectMappings());
				}
				mappingsChanged = true;
			}
		}
		if (mappingsChanged) {
			for (ProjectMappings projectMappings: projectMappingsList) {
				if (CcfExplorerView.getView() != null) {
					CcfExplorerView.getView().refresh(projectMappings);
				}
				CcfProjectMappingsEditorPage.notifyChanged(projectMappings);
			}
		}
	}
	
	public void selectionChanged(IAction action, ISelection sel) {
		if (sel instanceof IStructuredSelection) {
			fSelection= (IStructuredSelection) sel;
		}
	}	
	
}

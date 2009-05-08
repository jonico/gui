package com.collabnet.ccf.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionDelegate;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.dialogs.ResetProjectMappingDialog;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.views.CcfExplorerView;

public class ResetSynchronizationStatusAction extends ActionDelegate {
	private IStructuredSelection fSelection;
	
	@SuppressWarnings("unchecked")
	public void run(IAction action) {
		
		final List<SynchronizationStatus> statuses = new ArrayList<SynchronizationStatus>();
		Iterator iter = fSelection.iterator();
		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof SynchronizationStatus) {
				statuses.add((SynchronizationStatus)object);
			}
		}
		
		final ResetProjectMappingDialog dialog = new ResetProjectMappingDialog(Display.getDefault().getActiveShell());
		if (dialog.open() == ResetProjectMappingDialog.CANCEL) return;
		
		final List<ProjectMappings> projectMappingsList = new ArrayList<ProjectMappings>();
		final CcfDataProvider dataProvider = new CcfDataProvider();
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				Iterator<SynchronizationStatus> iter = statuses.iterator();
				while (iter.hasNext()) {
					SynchronizationStatus status = iter.next();
					try {
						dataProvider.resetSynchronizationStatus(status, dialog.getResetDate());
						if (!projectMappingsList.contains(status.getProjectMappings())) {
							projectMappingsList.add(status.getProjectMappings());
						}
					} catch (Exception e) {
						Activator.handleError(e);
						break;
					}
				}
			}			
		});
		if (CcfExplorerView.getView() != null) {
			for (ProjectMappings projectMappings: projectMappingsList) {
				CcfExplorerView.getView().refresh(projectMappings);
			}
		}	
	}

	public void selectionChanged(IAction action, ISelection sel) {
		if (sel instanceof IStructuredSelection) {
			fSelection= (IStructuredSelection) sel;
		}
	}	
}

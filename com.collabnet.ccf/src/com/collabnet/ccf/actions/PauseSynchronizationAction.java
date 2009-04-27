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
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.views.CcfExplorerView;

public class PauseSynchronizationAction extends ActionDelegate {
	private IStructuredSelection fSelection;
	
	public void run(IAction action) {
		final List<ProjectMappings> projectMappingsList = new ArrayList<ProjectMappings>();
		final CcfDataProvider dataProvider = new CcfDataProvider();
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				Iterator iter = fSelection.iterator();
				while (iter.hasNext()) {
					Object object = iter.next();
					if (object instanceof SynchronizationStatus) {
						SynchronizationStatus status = (SynchronizationStatus)object;		
						try {
							dataProvider.pauseSynchronization(status);
							if (!projectMappingsList.contains(status.getProjectMappings())) {
								projectMappingsList.add(status.getProjectMappings());
							}
						} catch (Exception e) {
							Activator.handleError(e);
							break;
						}
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
			if (action != null) action.setEnabled(isEnabledForSelection());
		}
	}	
	
	@SuppressWarnings("unchecked")
	private boolean isEnabledForSelection() {
		if (fSelection == null) return false;
		Iterator iter = fSelection.iterator();
		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof SynchronizationStatus) {
				SynchronizationStatus status = (SynchronizationStatus)object;
				if (status.isPaused())
					return false;
			}
		}
		return true;
	}	
}

package com.collabnet.ccf.actions;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.ActionDelegate;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.views.IdentityMappingConsistencyCheckView;

public class IdentityMappingConsistencyAction extends ActionDelegate {
	private IStructuredSelection fSelection;
	
	@SuppressWarnings("unchecked")
	public void run(IAction action) {
		Iterator iter = fSelection.iterator();
		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof Landscape) {
				try {
					Landscape landscape = (Landscape)object;
					IdentityMappingConsistencyCheckView.setLandscape(landscape);
					IdentityMappingConsistencyCheckView identityMappingConsistencyCheckView = (IdentityMappingConsistencyCheckView)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IdentityMappingConsistencyCheckView.ID);	
					identityMappingConsistencyCheckView.refresh();
				} catch (Exception e) {
					Activator.handleError(e);
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

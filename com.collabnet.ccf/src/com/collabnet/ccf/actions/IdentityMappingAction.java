package com.collabnet.ccf.actions;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionDelegate;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.db.Filter;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.views.IdentityMappingView;

public class IdentityMappingAction extends ActionDelegate {
	private IStructuredSelection fSelection;
	
	@SuppressWarnings("unchecked")
	public void run(IAction action) {
		Iterator iter = fSelection.iterator();
		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof Landscape || object instanceof SynchronizationStatus) {
				Landscape landscape = null;
				SynchronizationStatus status = null;
				if (object instanceof SynchronizationStatus) {
					status = (SynchronizationStatus)object;
					landscape = status.getLandscape();
				}
				else if (object instanceof Landscape) {
					landscape = (Landscape)object;
				}
				try {
					IdentityMappingView.setLandscape(landscape);
					if (status == null) {
						// TODO: Landscape filtering
						IdentityMappingView.setFilters(null, true, landscape.getDescription());
					} else {
						String sourceSystemKind;
						if (status.isPaused()) sourceSystemKind = status.getSourceSystemKind().substring(0, 2);
						else sourceSystemKind = status.getSourceSystemKind();
						Filter sourceSystemFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_SYSTEM_KIND, sourceSystemKind, true, Filter.FILTER_TYPE_LIKE);
						Filter sourceRepositoryFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_ID, status.getSourceRepositoryId(), true);
						Filter targetSystemFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_TARGET_SYSTEM_KIND, status.getTargetSystemKind(), true, Filter.FILTER_TYPE_LIKE);
						Filter targetRepositoryFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_ID, status.getTargetRepositoryId(), true);
						Filter[] filters = { sourceSystemFilter, sourceRepositoryFilter, targetSystemFilter, targetRepositoryFilter };
						
						Filter[][] filterGroups = { filters };
						
						IdentityMappingView.setFilters(filterGroups, true, status.toString());
					}
					IdentityMappingView identityMappingView = (IdentityMappingView)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IdentityMappingView.ID);
					identityMappingView.refresh();
				} catch (PartInitException e) {
					Activator.handleError(e);
				}
				break;
			}
		}
	}

	public void selectionChanged(IAction action, ISelection sel) {
		if (sel instanceof IStructuredSelection) {
			fSelection= (IStructuredSelection) sel;
		}
	}	
}

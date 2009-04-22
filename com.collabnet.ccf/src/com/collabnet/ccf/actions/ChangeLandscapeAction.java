package com.collabnet.ccf.actions;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionDelegate;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.dialogs.ChangeLandscapeDialog;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.views.CcfExplorerView;

public class ChangeLandscapeAction extends ActionDelegate {
	private IStructuredSelection fSelection;
	private boolean landscapeChanged;
	
	@SuppressWarnings("unchecked")
	public void run(IAction action) {
		landscapeChanged = false;
		Iterator iter = fSelection.iterator();
		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof Landscape) {
				Landscape landscape = (Landscape)object;
				ChangeLandscapeDialog dialog = new ChangeLandscapeDialog(Display.getDefault().getActiveShell(), landscape);
				if (dialog.open() == ChangeLandscapeDialog.OK) landscapeChanged = true;
				break;
			}
		}
		if (landscapeChanged && CcfExplorerView.getView() != null) {
			CcfExplorerView.getView().refresh();
		}		
	}

	public void selectionChanged(IAction action, ISelection sel) {
		if (sel instanceof IStructuredSelection) {
			fSelection= (IStructuredSelection) sel;
		}
	}	
}

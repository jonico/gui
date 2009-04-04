package com.collabnet.ccf.actions;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionDelegate;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.db.Filter;
import com.collabnet.ccf.db.Update;
import com.collabnet.ccf.model.Patient;
import com.collabnet.ccf.views.HospitalView;

public class ReplayHospitalAction extends ActionDelegate {
	private IStructuredSelection fSelection;
	private boolean patientsUpdated;

	public void run(IAction action) {
		if (!MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Replay", "Replay the selected hospital items?")) return;
		patientsUpdated = false;
		final CcfDataProvider dataProvider = new CcfDataProvider();
		Update update = new Update(CcfDataProvider.HOSPITAL_ERROR_CODE, CcfDataProvider.HOSPITAL_REPLAY, true);
		final Update[] updates = { update };
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				Iterator iter = fSelection.iterator();
				while (iter.hasNext()) {
					Object object = iter.next();
					if (object instanceof Patient) {
						Patient patient = (Patient)object;
						Filter filter = new Filter(CcfDataProvider.HOSPITAL_ID, Integer.toString(patient.getId()), false);
						Filter[] filters = { filter };
						try {
							dataProvider.updatePatients(updates, filters);
							patientsUpdated = true;
						} catch (Exception e) {
							Activator.handleError(e);
							break;
						}
					}
				}
			}			
		});
		if (patientsUpdated && HospitalView.getView() != null) {
			HospitalView.getView().refresh();
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
				if (!patient.getOriginatingComponent().endsWith("EntityService") && !patient.getOriginatingComponent().endsWith("Write"))
					return false;
			}
		}
		return true;
	}	
	
}

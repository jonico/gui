package com.collabnet.ccf.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.collabnet.ccf.model.Patient;

public class CancelReplayHospitalAction extends ReplayHospitalAction {

	@Override
	public boolean confirm() {
		return MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Cancel Replay", "Cancel replay of the selected hospital items?");
	}

	@Override
	public boolean isEnabled(Patient patient) {
		String errorCode = patient.getErrorCode();
		return errorCode != null && patient.getErrorCode().equals("replay");
	}

	@Override
	public String getUpdateValue() {
		return "Replay canceled";
	}

}

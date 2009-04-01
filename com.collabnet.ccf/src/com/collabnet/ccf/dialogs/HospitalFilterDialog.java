package com.collabnet.ccf.dialogs;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;

import com.collabnet.ccf.db.Filter;

public class HospitalFilterDialog extends TrayDialog {
	private Filter[] filters;

	public HospitalFilterDialog(Shell shell, Filter[] filters) {
		super(shell);
		this.filters = filters;
	}

}

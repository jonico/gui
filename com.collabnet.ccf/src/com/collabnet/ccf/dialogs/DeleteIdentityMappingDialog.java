package com.collabnet.ccf.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class DeleteIdentityMappingDialog extends CcfDialog {
	private Button reverseButton;
	private boolean deleteReverse;

	public DeleteIdentityMappingDialog(Shell shell) {
		super(shell, "DeleteIdentityMappingDialog");
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Delete Identity Mapping");
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		reverseButton = new Button(composite, SWT.CHECK);
		reverseButton.setText("Also delete reverse identity mapping");
		reverseButton.setSelection(true);
		
		return composite;
	}
	
	@Override
	protected void okPressed() {
		deleteReverse = reverseButton.getSelection();
		super.okPressed();
	}

	public boolean isDeleteReverse() {
		return deleteReverse;
	}

}

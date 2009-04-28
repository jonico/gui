package com.collabnet.ccf.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.collabnet.ccf.model.SynchronizationStatus;

public class ResetSynchronizationStatusDialog extends CcfDialog {
	private SynchronizationStatus[] statuses;
	private Button clearMappingsButton;
	
	private boolean clearMappings;

	public ResetSynchronizationStatusDialog(Shell shell, SynchronizationStatus[] statuses) {
		super(shell, "ResetSynchronizationStatusDialog");
		this.statuses = statuses;
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Reset Synchronization Status");
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label confirmLabel = new Label(composite, SWT.NONE);
		
		if (statuses.length == 1) {
			confirmLabel.setText("Reset " + statuses[0].toString() + " synchronization status?");
		} else {
			confirmLabel.setText("Reset synchronization status for the " + statuses.length + " selected project mappings?");
		}
		
		new Label(composite, SWT.NONE);
		
		clearMappingsButton = new Button(composite, SWT.CHECK);
		clearMappingsButton.setText("Clear identity mappings");
		
		return composite;
	}
	
	@Override
	protected void okPressed() {
		clearMappings = clearMappingsButton.getSelection();
		super.okPressed();
	}

	public boolean isClearMappings() {
		return clearMappings;
	}

}

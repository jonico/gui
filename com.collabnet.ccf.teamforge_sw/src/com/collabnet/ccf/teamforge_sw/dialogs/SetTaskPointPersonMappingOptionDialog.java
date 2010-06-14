package com.collabnet.ccf.teamforge_sw.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.collabnet.ccf.dialogs.CcfDialog;

public class SetTaskPointPersonMappingOptionDialog extends CcfDialog {
	public boolean mapToAssignedToUser;
	
	private Button assignedToButton;
	private Button pointPersonButton;

	public SetTaskPointPersonMappingOptionDialog(Shell shell, boolean mapToAssignedToUser) {
		super(shell, "SetTaskPointPersonMappingOption");
		this.mapToAssignedToUser = mapToAssignedToUser;
	}

	public boolean isMapToAssignedToUser() {
		return mapToAssignedToUser;
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Point Person Mapping Option");
		Group composite = new Group(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));		
		composite.setText("Map ScrumWorks Pro task point person to:");
	
		assignedToButton = new Button(composite, SWT.RADIO);
		assignedToButton.setText("Assigned To user");
		
		pointPersonButton = new Button(composite, SWT.RADIO);
		pointPersonButton.setText("Point Person flex field");
		
		if (mapToAssignedToUser) {
			assignedToButton.setSelection(true);
		} else {
			pointPersonButton.setSelection(true);
		}
		
		return composite;
	}

	@Override
	protected void okPressed() {
		mapToAssignedToUser = assignedToButton.getSelection();
		super.okPressed();
	}

}

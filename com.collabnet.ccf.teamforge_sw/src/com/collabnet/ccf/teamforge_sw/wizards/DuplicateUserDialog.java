package com.collabnet.ccf.teamforge_sw.wizards;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.collabnet.ccf.dialogs.CcfDialog;

public class DuplicateUserDialog extends CcfDialog {
	private List<String> duplicateUsers;
	private org.eclipse.swt.widgets.List duplicateUserList;

	public DuplicateUserDialog(Shell shell, List<String> duplicateUsers) {
		super(shell, "ProjectMappingsDuplicateUserDialog");
		this.duplicateUsers = duplicateUsers;
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Create Project Mappings");

		Composite duplicateGroup = new Composite(parent, SWT.NONE);
		Label duplicateLabel = new Label(duplicateGroup, SWT.WRAP);
		duplicateLabel.setText("These users could not be created because similar user names (different case) already exist:");
		duplicateGroup.setLayout(new GridLayout());
		duplicateGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));
		duplicateUserList = new org.eclipse.swt.widgets.List(duplicateGroup, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
		data.heightHint = 300;
		duplicateUserList.setLayoutData(data);
		
		for (String user : duplicateUsers) {
			duplicateUserList.add(user);
		}

		return duplicateGroup;
	}

}

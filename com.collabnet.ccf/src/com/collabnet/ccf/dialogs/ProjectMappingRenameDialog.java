package com.collabnet.ccf.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ProjectMappingRenameDialog extends CcfDialog {
	private Button renameFilesButton;
	private Button updateIdentityMappingsButton;
	private boolean renameFiles;
	private boolean updateIdentityMappings;

	public ProjectMappingRenameDialog(Shell shell, boolean renameFiles, boolean updateIdentityMappings) {
		super(shell, "renameProjectMappingDialog");
		this.renameFiles = renameFiles;
		this.updateIdentityMappings = updateIdentityMappings;
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Project Mapping Changed");
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		if (renameFiles) {
			renameFilesButton = new Button(composite, SWT.CHECK);
			renameFilesButton.setText("Rename field mapping files");
			renameFilesButton.setSelection(true);
		}
		
		if (updateIdentityMappings) {
			updateIdentityMappingsButton = new Button(composite, SWT.CHECK);
			updateIdentityMappingsButton.setText("Update identity mappings");
			updateIdentityMappingsButton.setSelection(true);
		}
		
		return composite;
	}

	@Override
	protected void okPressed() {
		if (renameFilesButton != null) {
			renameFiles = renameFilesButton.getSelection();
		}
		if (updateIdentityMappingsButton != null) {
			updateIdentityMappings = updateIdentityMappingsButton.getSelection();
		}
		super.okPressed();
	}

	public boolean isRenameFiles() {
		return renameFiles;
	}

	public boolean isUpdateIdentityMappings() {
		return updateIdentityMappings;
	}

}

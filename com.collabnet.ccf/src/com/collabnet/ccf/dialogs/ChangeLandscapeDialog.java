package com.collabnet.ccf.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.Landscape;

public class ChangeLandscapeDialog extends CcfDialog {
	private Landscape landscape;
	private String description;
	private Text descriptionText;
	private Button okButton;

	public ChangeLandscapeDialog(Shell shell, Landscape landscape) {
		super(shell, "ChangeLandscapeDialog");
		this.landscape = landscape;
		description = landscape.getDescription();
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Change Landscape");
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group descriptionGroup = new Group(composite, SWT.NONE);
		descriptionGroup.setText("Description:");
		GridLayout descriptionLayout = new GridLayout();
		descriptionLayout.numColumns = 1;
		descriptionGroup.setLayout(descriptionLayout);
		descriptionGroup.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		descriptionText = new Text(descriptionGroup, SWT.BORDER);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		descriptionText.setLayoutData(gd);
		descriptionText.setText(landscape.getDescription());
		descriptionText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				okButton.setEnabled(descriptionText.getText().trim().length() > 0);
			}		
		});
		
		return composite;
	}
		
	@Override
	protected void okPressed() {
		if (!description.equals(descriptionText.getText().trim())) {
			landscape.setDescription(descriptionText.getText().trim().replaceAll("/", "%slash%"));
			if (Activator.getDefault().storeLandscape(landscape)) {
				// Delete old node.
				Activator.getDefault().deleteLandscape(landscape);
			}
		}
		super.okPressed();
	}

	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        Button button = super.createButton(parent, id, label, defaultButton);
		if (id == IDialogConstants.OK_ID) {
			okButton = button;
		}
        return button;
    }

}

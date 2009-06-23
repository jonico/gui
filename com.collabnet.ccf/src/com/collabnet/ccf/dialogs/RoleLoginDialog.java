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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.model.Role;

public class RoleLoginDialog extends CcfDialog {
	private Role role;
	private Text passwordText;
	
	private Button okButton;

	public RoleLoginDialog(Shell shell, Role role) {
		super(shell, "RoleLoginDialog");
		this.role = role;
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Role Login: " + role.getName());
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label passwordLabel = new Label(composite, SWT.NONE);
		passwordLabel.setText("Password:");
		
		passwordText = new Text(composite, SWT.PASSWORD | SWT.BORDER);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		passwordText.setLayoutData(gd);
		
		passwordText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				okButton.setEnabled(passwordText.getText().trim().equals(role.getPassword().trim()));
			}			
		});
		
		return composite;
	}
	
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        Button button = super.createButton(parent, id, label, defaultButton);
		if (id == IDialogConstants.OK_ID) {
			okButton = button;
			okButton.setEnabled(false);
		}
        return button;
    }

}

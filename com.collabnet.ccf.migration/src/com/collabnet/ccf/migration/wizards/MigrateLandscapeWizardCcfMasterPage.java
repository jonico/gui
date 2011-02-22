package com.collabnet.ccf.migration.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MigrateLandscapeWizardCcfMasterPage extends WizardPage {
	private Text ccfMasterUrlText;
	private Text ccfMasterUserText;
	private Text ccfMasterPasswordText;
	
	private String url;
	private String user;
	private String password;

	public MigrateLandscapeWizardCcfMasterPage() {
		super("ccfMasterPage", "CCF Master", null);
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		setMessage("Select target CCF Master for migration");		
		Composite outerContainer = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		outerContainer.setLayout(layout);
		outerContainer.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		Label ccfMasterUrlLabel = new Label(outerContainer, SWT.NONE);
		ccfMasterUrlLabel.setText("CCF Master URL:");
		
		ccfMasterUrlText = new Text(outerContainer, SWT.BORDER);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		gd.widthHint = 300;
		ccfMasterUrlText.setLayoutData(gd);
		
		Label ccfMasterUserLabel = new Label(outerContainer, SWT.NONE);
		ccfMasterUserLabel.setText("User name:");
		
		ccfMasterUserText = new Text(outerContainer, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		gd.widthHint = 300;
		ccfMasterUserText.setLayoutData(gd);
		
		Label ccfMasterPasswordLabel = new Label(outerContainer, SWT.NONE);
		ccfMasterPasswordLabel.setText("Password:");
		
		ccfMasterPasswordText = new Text(outerContainer, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		gd.widthHint = 300;
		ccfMasterPasswordText.setLayoutData(gd);
		ccfMasterPasswordText.setEchoChar('*');
		
		ModifyListener modifyListener = new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent e) {
				if (e.getSource() == ccfMasterUrlText) {
					url = ccfMasterUrlText.getText().trim();
				}
				else if (e.getSource() == ccfMasterUserText) {
					user = ccfMasterUserText.getText().trim();
				}
				else if (e.getSource() == ccfMasterPasswordText) {
					password = ccfMasterPasswordText.getText().trim();
				}
				setPageComplete(canFinish());
			}
		};
		
		ccfMasterUrlText.addModifyListener(modifyListener);
		ccfMasterUserText.addModifyListener(modifyListener);
		ccfMasterPasswordText.addModifyListener(modifyListener);

		setControl(outerContainer);
	}
	
	public String getCcfMasterUrl() {
		if (!url.endsWith("/")) {
			url = url + "/";
		}
		return url;
	}
	
	public String getCcfMasterUser() {
		return user;
	}
	
	public String getCcfMasterPassword() {
		return password;
	}
	
	private boolean canFinish() {
		String url = ccfMasterUrlText.getText().trim();
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			return false;
		}
		if (url.startsWith("http://") && url.length() < 8) {
			return false;
		}
		else if (url.startsWith("https://") && url.length() < 9) {
			return false;
		}
		if (ccfMasterUserText.getText().trim().length() == 0 || ccfMasterPasswordText.getText().trim().length() == 0) {
			return false;
		}
		return true;
	}

}

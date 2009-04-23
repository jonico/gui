package com.collabnet.ccf.dialogs;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.DriverManager;
import java.util.Properties;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.Activator;

public class CcfPropertiesDialog extends CcfDialog {
	private File propertiesFile;
	private Properties properties;
	
	private Text urlText;
	private Text driverText;
	private Text userText;
	private Text passwordText;
	private Text xsltDirectoryText;
	
	private Button okButton;

	public CcfPropertiesDialog(Shell shell, File propertiesFile, Properties properties) {
		super(shell, "CcfPropertiesDialog"); //$NON-NLS-1$
		this.propertiesFile = propertiesFile;
		this.properties = properties;
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Edit CCF Properties");
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group databaseGroup = new Group(composite, SWT.NULL);
		GridLayout clientLayout = new GridLayout();
		clientLayout.numColumns = 2;
		databaseGroup.setLayout(clientLayout);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		databaseGroup.setLayoutData(gd);	
		databaseGroup.setText("Database:");
		
		Label urlLabel = new Label(databaseGroup, SWT.NONE);
		urlLabel.setText("URL:");
		urlText = new Text(databaseGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		urlText.setLayoutData(gd);
		
		Label driverLabel = new Label(databaseGroup, SWT.NONE);
		driverLabel.setText("Driver:");
		driverText = new Text(databaseGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		driverText.setLayoutData(gd);
		
		Label userLabel = new Label(databaseGroup, SWT.NONE);
		userLabel.setText("User:");
		userText = new Text(databaseGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		userText.setLayoutData(gd);
		
		Label passwordLabel = new Label(databaseGroup, SWT.NONE);
		passwordLabel.setText("Password:");
		passwordText = new Text(databaseGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		passwordText.setLayoutData(gd);
		passwordText.setEchoChar('*');
		
		Button testButton = new Button(databaseGroup, SWT.PUSH);
		testButton.setText("Test Connection");
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = GridData.HORIZONTAL_ALIGN_CENTER;
		testButton.setLayoutData(gd);
		testButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				testConnection(false);
			}			
		});
		
		Group xsltGroup = new Group(composite, SWT.NULL);
		GridLayout xsltLayout = new GridLayout();
		xsltLayout.numColumns = 1;
		xsltGroup.setLayout(xsltLayout);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		xsltGroup.setLayoutData(gd);	
		xsltGroup.setText("XSLT directory:");
		
		xsltDirectoryText = new Text(xsltGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		xsltDirectoryText.setLayoutData(gd);
		
		initializeValues();
		
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				okButton.setEnabled(canFinish());
			}		
		};
		
		urlText.addModifyListener(modifyListener);
		driverText.addModifyListener(modifyListener);
		xsltDirectoryText.addModifyListener(modifyListener);
		
		return composite;
	}
	
	@Override
	protected void okPressed() {
		if (!testConnection(true)) return;
		try {
			properties.setProperty(Activator.PROPERTIES_CCF_URL, urlText.getText().trim());
			properties.setProperty(Activator.PROPERTIES_CCF_DRIVER, driverText.getText().trim());
			properties.setProperty(Activator.PROPERTIES_CCF_USER, userText.getText().trim());
			properties.setProperty(Activator.PROPERTIES_CCF_PASSWORD, passwordText.getText().trim());
			properties.setProperty(Activator.PROPERTIES_CCF_XSLT_DIRECTORY, xsltDirectoryText.getText().trim());
			FileOutputStream outputStream = new FileOutputStream(propertiesFile);
			properties.store(outputStream, null);
			outputStream.close();
		} catch (Exception e) {
			Activator.handleError(e);
			return;
		}
		super.okPressed();
	}
	
	private boolean testConnection(boolean saving) {
		try {
			Class.forName(driverText.getText().trim());
			DriverManager.getConnection(urlText.getText().trim(), userText.getText().trim(), passwordText.getText().trim());	
			if (!saving) MessageDialog.openInformation(getShell(), "Test Connection", "Connection successful!");
		} catch (Exception e) {
			if (saving) {
				return MessageDialog.openQuestion(getShell(), "Save CCF Properties","Could not connect to database:\n\n" + e.getLocalizedMessage() + "\n\nSave anyway?");
			}
			MessageDialog.openError(getShell(), "Test Connection", "Connection failed:\n\n" + e.getLocalizedMessage());
		}
		return true;
	}
	
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        Button button = super.createButton(parent, id, label, defaultButton);
		if (id == IDialogConstants.OK_ID) {
			okButton = button;
		}
        return button;
    }
	
	private void initializeValues() {
		String url = properties.getProperty(Activator.PROPERTIES_CCF_URL);
		if (url != null) urlText.setText(url);
		String driver = properties.getProperty(Activator.PROPERTIES_CCF_DRIVER);
		if (driver != null) driverText.setText(driver);
		String user = properties.getProperty(Activator.PROPERTIES_CCF_USER);
		if (user != null) userText.setText(user);
		String password = properties.getProperty(Activator.PROPERTIES_CCF_PASSWORD);
		if (password != null) passwordText.setText(password);
		String xsltDirectory = properties.getProperty(Activator.PROPERTIES_CCF_XSLT_DIRECTORY);
		if (xsltDirectory != null) xsltDirectoryText.setText(xsltDirectory);
	}
	
	private boolean canFinish() {
		return urlText.getText().trim().length() > 0 &&
		driverText.getText().trim().length() > 0 &&
		xsltDirectoryText.getText().trim().length() > 0;
	}

}

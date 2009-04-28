package com.collabnet.ccf.dialogs;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.TimeZone;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.Activator;

public class SystemPropertiesDialog extends CcfDialog {
	private File propertiesFile1;
	private File propertiesFile2;
	private Properties properties;
	private int type;
	
	private Text urlText;
	private Text idText;
	private Text userText;
	private Text displayNameText;
	private Text passwordText;
	private Text resyncUserText;
	private Text resyncDisplayNameText;
	private Text resyncPasswordText;
	private Text encodingText;
	private Text attachmentSizeText;
	private Combo timeZonesCombo;
	
	private Button okButton;
	
	public static final int QC = 0;
	public static final int TF = 1;
	public static final int PT = 2;

	public SystemPropertiesDialog(Shell shell, File propertiesFile1, File propertiesFile2, Properties properties, int type) {
		super(shell, "SystemPropertiesDialog"); //$NON-NLS-1$
		this.propertiesFile1 = propertiesFile1;
		this.propertiesFile2 = propertiesFile2;
		this.properties = properties;
		this.type = type;
	}
	
	protected Control createDialogArea(Composite parent) {
		String title;
		switch (type) {
		case QC:
			title = "Edit QC Properties";
			break;
		case TF:
			title = "Edit TeamForge Properties";
			break;
		case PT:
			title = "Edit CEE Properties";
			break;			
		default:
			title = "Edit Properties";
			break;
		}
		
		getShell().setText(title);
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group urlGroup = new Group(composite, SWT.NULL);
		GridLayout urlLayout = new GridLayout();
		urlLayout.numColumns = 2;
		urlGroup.setLayout(urlLayout);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		urlGroup.setLayoutData(gd);	
		urlGroup.setText("System:");
		
		Label urlLabel = new Label(urlGroup, SWT.NONE);
		urlLabel.setText("URL:");
		
		urlText = new Text(urlGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		urlText.setLayoutData(gd);
		
		Label idLabel = new Label(urlGroup, SWT.NONE);
		idLabel.setText("ID:");
		
		idText = new Text(urlGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		idText.setLayoutData(gd);
		
		Group credentialsGroup = new Group(composite, SWT.NULL);
		GridLayout credentialsLayout = new GridLayout();
		credentialsLayout.numColumns = 2;
		credentialsGroup.setLayout(credentialsLayout);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		credentialsGroup.setLayoutData(gd);	
		credentialsGroup.setText("Credentials:");
		
		Label userLabel = new Label(credentialsGroup, SWT.NONE);
		userLabel.setText("User:");
		userText = new Text(credentialsGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		userText.setLayoutData(gd);
		
		if (type == PT) {
			Label displayNameLabel = new Label(credentialsGroup, SWT.NONE);
			displayNameLabel.setText("Display name:");
			displayNameText = new Text(credentialsGroup, SWT.BORDER);
			gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
			displayNameText.setLayoutData(gd);			
		}
		
		Label passwordLabel = new Label(credentialsGroup, SWT.NONE);
		passwordLabel.setText("Password:");
		passwordText = new Text(credentialsGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		passwordText.setLayoutData(gd);
		passwordText.setEchoChar('*');
		
		Group resyncGroup = new Group(composite, SWT.NULL);
		GridLayout resyncLayout = new GridLayout();
		resyncLayout.numColumns = 2;
		resyncGroup.setLayout(resyncLayout);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		resyncGroup.setLayoutData(gd);	
		resyncGroup.setText("Resync user:");
		
		Label resyncUserLabel = new Label(resyncGroup, SWT.NONE);
		resyncUserLabel.setText("User:");
		resyncUserText = new Text(resyncGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		resyncUserText.setLayoutData(gd);
		
		if (type == PT) {
			Label displayNameLabel = new Label(resyncGroup, SWT.NONE);
			displayNameLabel.setText("Display name:");
			resyncDisplayNameText = new Text(resyncGroup, SWT.BORDER);
			gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
			resyncDisplayNameText.setLayoutData(gd);			
		}
		
		Label resyncPasswordLabel = new Label(resyncGroup, SWT.NONE);
		resyncPasswordLabel.setText("Password:");
		resyncPasswordText = new Text(resyncGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		resyncPasswordText.setLayoutData(gd);
		resyncPasswordText.setEchoChar('*');
		
		Group timezoneGroup = new Group(composite, SWT.NULL);
		GridLayout timezoneLayout = new GridLayout();
		timezoneLayout.numColumns = 1;
		timezoneGroup.setLayout(timezoneLayout);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		timezoneGroup.setLayoutData(gd);	
		timezoneGroup.setText("Time zone:");
		
		timeZonesCombo = new Combo(timezoneGroup, SWT.READ_ONLY);
		String[] timeZoneIds = TimeZone.getAvailableIDs();
		Arrays.sort(timeZoneIds);
		for (String zone : timeZoneIds) {
			timeZonesCombo.add(zone);
		}	
		
		Composite sizeGroup = new Composite(composite, SWT.NULL);
		GridLayout sizeLayout = new GridLayout();
		sizeLayout.numColumns = 4;
		sizeGroup.setLayout(sizeLayout);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		sizeGroup.setLayoutData(gd);
		
		Label encodingLabel = new Label(sizeGroup, SWT.NONE);
		encodingLabel.setText("Encoding:");
		encodingText = new Text(sizeGroup, SWT.BORDER);
		gd = new GridData();
		gd.widthHint = 100;
		encodingText.setLayoutData(gd);
		
		Label sizeLabel = new Label(sizeGroup, SWT.NONE);
		sizeLabel.setText("Maximum attachment size per artifact:");
		attachmentSizeText = new Text(sizeGroup, SWT.BORDER);
		gd = new GridData();
		gd.widthHint = 100;
		attachmentSizeText.setLayoutData(gd);
		
		initializeValues();
		
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				okButton.setEnabled(canFinish());
			}		
		};
		
		urlText.addModifyListener(modifyListener);
		idText.addModifyListener(modifyListener);
		attachmentSizeText.addModifyListener(modifyListener);
		
		return composite;
	}
	
	@Override
	protected void okPressed() {
		try {
			properties.setProperty(Activator.PROPERTIES_SYSTEM_ID, idText.getText().trim());
			properties.setProperty(Activator.PROPERTIES_SYSTEM_TIMEZONE, timeZonesCombo.getText());
			properties.setProperty(Activator.PROPERTIES_SYSTEM_ENCODING, encodingText.getText().trim());
			switch (type) {
			case QC:
				setQcProperties();
				break;
			case TF:
				setTeamForgeProperties();
				break;
			case PT:
				setCeeProperties();
				break;				
			default:
				break;
			}
			if (propertiesFile1 != null) {
				FileOutputStream outputStream = new FileOutputStream(propertiesFile1);
				properties.store(outputStream, null);
				outputStream.close();
			}
			if (propertiesFile2 != null) {
				FileOutputStream outputStream = new FileOutputStream(propertiesFile2);
				properties.store(outputStream, null);
				outputStream.close();
			}
		} catch (Exception e) {
			Activator.handleError(e);
			return;
		}
		super.okPressed();
	}
	
	private void initializeValues() {
		switch (type) {
		case QC:
			initializeQcValues();
			break;
		case TF:
			initializeTeamForgeValues();
			break;
		case PT:
			initializeCeeValues();
			break;				
		default:
			break;
		}	
		String encoding = properties.getProperty(Activator.PROPERTIES_SYSTEM_ENCODING); //$NON-NLS-1$
		if (encoding != null) {
			encodingText.setText(encoding);
		}
		String timezone = properties.getProperty(Activator.PROPERTIES_SYSTEM_TIMEZONE, TimeZone.getDefault().getID()); //$NON-NLS-1$
		if (timezone != null) {
			timeZonesCombo.select(timeZonesCombo.indexOf(timezone));
		}
	}
	
	private void initializeQcValues() {
		String id = properties.getProperty(Activator.PROPERTIES_SYSTEM_ID, "Quality Center"); //$NON-NLS-1$
		idText.setText(id);
		String url = properties.getProperty(Activator.PROPERTIES_QC_URL);
		if (url != null) urlText.setText(url);
		String user = properties.getProperty(Activator.PROPERTIES_QC_USER);
		if (user != null) userText.setText(user);
		String password = properties.getProperty(Activator.PROPERTIES_QC_PASSWORD);
		if (password != null) passwordText.setText(password);	
		String resyncUser = properties.getProperty(Activator.PROPERTIES_QC_RESYNC_USER);
		if (resyncUser != null) resyncUserText.setText(resyncUser);
		String resyncPassword = properties.getProperty(Activator.PROPERTIES_QC_RESYNC_PASSWORD);
		if (resyncPassword != null) resyncPasswordText.setText(resyncPassword);	
		String attachmentSize = properties.getProperty(Activator.PROPERTIES_QC_ATTACHMENT_SIZE);
		if (attachmentSize != null) attachmentSizeText.setText(attachmentSize);
	}
	
	private void initializeTeamForgeValues() {
		String id = properties.getProperty(Activator.PROPERTIES_SYSTEM_ID, "TeamForge"); //$NON-NLS-1$
		idText.setText(id);
		String url = properties.getProperty(Activator.PROPERTIES_SFEE_URL);
		if (url != null) urlText.setText(url);
		String user = properties.getProperty(Activator.PROPERTIES_SFEE_USER);
		if (user != null) userText.setText(user);
		String password = properties.getProperty(Activator.PROPERTIES_SFEE_PASSWORD);
		if (password != null) passwordText.setText(password);	
		String resyncUser = properties.getProperty(Activator.PROPERTIES_SFEE_RESYNC_USER);
		if (resyncUser != null) resyncUserText.setText(resyncUser);
		String resyncPassword = properties.getProperty(Activator.PROPERTIES_SFEE_RESYNC_PASSWORD);
		if (resyncPassword != null) resyncPasswordText.setText(resyncPassword);	
		String attachmentSize = properties.getProperty(Activator.PROPERTIES_SFEE_ATTACHMENT_SIZE);
		if (attachmentSize != null) attachmentSizeText.setText(attachmentSize);		
	}
	
	private void initializeCeeValues() {
		String id = properties.getProperty(Activator.PROPERTIES_SYSTEM_ID, "Project Tracker"); //$NON-NLS-1$
		idText.setText(id);
		String url = properties.getProperty(Activator.PROPERTIES_CEE_URL);
		if (url != null) urlText.setText(url);
		String user = properties.getProperty(Activator.PROPERTIES_CEE_USER);
		if (user != null) userText.setText(user);
		String displayName = properties.getProperty(Activator.PROPERTIES_CEE_DISPLAY_NAME);
		if (displayName != null) displayNameText.setText(displayName);
		String password = properties.getProperty(Activator.PROPERTIES_CEE_PASSWORD);
		if (password != null) passwordText.setText(password);	
		String resyncUser = properties.getProperty(Activator.PROPERTIES_CEE_RESYNC_USER);
		if (resyncUser != null) resyncUserText.setText(resyncUser);
		String resyncDisplayName = properties.getProperty(Activator.PROPERTIES_CEE_RESYNC_DISPLAY_NAME);
		if (resyncDisplayName != null) resyncDisplayNameText.setText(resyncDisplayName);		
		String resyncPassword = properties.getProperty(Activator.PROPERTIES_CEE_RESYNC_PASSWORD);
		if (resyncPassword != null) resyncPasswordText.setText(resyncPassword);	
		String attachmentSize = properties.getProperty(Activator.PROPERTIES_CEE_ATTACHMENT_SIZE);
		if (attachmentSize != null) attachmentSizeText.setText(attachmentSize);				
	}
	
	private void setQcProperties() throws Exception {
		properties.setProperty(Activator.PROPERTIES_QC_URL, urlText.getText().trim());
		properties.setProperty(Activator.PROPERTIES_QC_USER, userText.getText().trim());
		properties.setProperty(Activator.PROPERTIES_QC_PASSWORD, passwordText.getText().trim());		
		properties.setProperty(Activator.PROPERTIES_QC_RESYNC_USER, resyncUserText.getText().trim());
		properties.setProperty(Activator.PROPERTIES_QC_RESYNC_PASSWORD, resyncPasswordText.getText().trim());
		properties.setProperty(Activator.PROPERTIES_QC_ATTACHMENT_SIZE, attachmentSizeText.getText().trim());
	}
	
	private void setTeamForgeProperties() throws Exception {
		properties.setProperty(Activator.PROPERTIES_SFEE_URL, urlText.getText().trim());
		properties.setProperty(Activator.PROPERTIES_SFEE_USER, userText.getText().trim());
		properties.setProperty(Activator.PROPERTIES_SFEE_PASSWORD, passwordText.getText().trim());		
		properties.setProperty(Activator.PROPERTIES_SFEE_RESYNC_USER, resyncUserText.getText().trim());
		properties.setProperty(Activator.PROPERTIES_SFEE_RESYNC_PASSWORD, resyncPasswordText.getText().trim());
		properties.setProperty(Activator.PROPERTIES_SFEE_ATTACHMENT_SIZE, attachmentSizeText.getText().trim());
	}
	
	private void setCeeProperties() throws Exception {
		properties.setProperty(Activator.PROPERTIES_CEE_URL, urlText.getText().trim());
		properties.setProperty(Activator.PROPERTIES_CEE_USER, userText.getText().trim());
		properties.setProperty(Activator.PROPERTIES_CEE_DISPLAY_NAME, displayNameText.getText().trim());
		properties.setProperty(Activator.PROPERTIES_CEE_PASSWORD, passwordText.getText().trim());		
		properties.setProperty(Activator.PROPERTIES_CEE_RESYNC_USER, resyncUserText.getText().trim());
		properties.setProperty(Activator.PROPERTIES_CEE_RESYNC_DISPLAY_NAME, resyncDisplayNameText.getText().trim());
		properties.setProperty(Activator.PROPERTIES_CEE_RESYNC_PASSWORD, resyncPasswordText.getText().trim());
		properties.setProperty(Activator.PROPERTIES_CEE_ATTACHMENT_SIZE, attachmentSizeText.getText().trim());		
	}
	
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        Button button = super.createButton(parent, id, label, defaultButton);
		if (id == IDialogConstants.OK_ID) {
			okButton = button;
		}
        return button;
    }
	
	private boolean canFinish() {
		if (urlText.getText().trim().length() == 0 || idText.getText().trim().length() == 0) return false;
		if (attachmentSizeText.getText().trim().length() > 0) {
			int maxSize = 0;
			try {
				maxSize = Integer.parseInt(attachmentSizeText.getText().trim());
			} catch (Exception e) {}
			if (maxSize <= 0) return false;
		}
		return true;
	}

}

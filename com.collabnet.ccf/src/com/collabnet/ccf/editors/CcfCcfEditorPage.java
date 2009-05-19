package com.collabnet.ccf.editors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.DriverManager;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.dialogs.HospitalColumnSelectionDialog;
import com.collabnet.ccf.model.Landscape;

public class CcfCcfEditorPage extends CcfEditorPage {
	private ScrolledForm form;
	private FormToolkit toolkit;
	
	private Label errorImageLabel;
	private Label errorTextLabel;
	
	private Text descriptionText;
	
	private Text urlText;
	private Text driverText;
	private Text userText;
	private Text passwordText;
	
	private Text host1Text;
	private Text jmxPort1Text;
	private Text host2Text;
	private Text jmxPort2Text;
	
	private Text templateText;
	
	private String description;
	private String url;
	private String driver;
	private String user;
	private String password;
	
	private String ccfHost1;
	private String jmxPort1;
	private String ccfHost2;
	private String jmxPort2;
	
	private String template;
	
	private Button insertButton;
	
	private boolean ccfPropertiesUpdated;
	
	public final static String DATABASE_SECTION_STATE = "CcfCcfEditorPage.databaseSectionExpanded";
	public final static String JMX_SECTION_STATE = "CcfCcfEditorPage.jmxSectionExpanded";
	public final static String TEMPLATE_SECTION_STATE = "CcfCcfEditorPage.templateSectionExpanded";

	public CcfCcfEditorPage(String id, String title) {
		super(id, title);
	}

	public CcfCcfEditorPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		form = managedForm.getForm();
        toolkit = getEditor().getToolkit();
        TableWrapLayout formLayout = new TableWrapLayout();
        formLayout.numColumns = 4;
        form.getBody().setLayout(formLayout);
		createControls(form.getBody());
	}
	
	private void createControls(Composite composite) {
		description = getLandscape().getDescription();
		if (description == null) description = "";
		url = getLandscape().getDatabaseUrl();
		if (url == null) url = "";
		driver = getLandscape().getDatabaseDriver();
		if (driver == null) driver = "";
		user = getLandscape().getDatabaseUser();
		if (user == null) user = "";
		password = getLandscape().getDatabasePassword();
		if (password == null) password = "";
		template = getLandscape().getLogMessageTemplate1();
		if (template == null) template = "";
		jmxPort1 = getLandscape().getJmxPort1();
		if (jmxPort1 == null) jmxPort1 = "";
		jmxPort2 = getLandscape().getJmxPort2();
		if (jmxPort2 == null) jmxPort2 = "";
		ccfHost1 = getLandscape().getCcfHost1();
		ccfHost2 = getLandscape().getCcfHost2();
		
		Label headerImageLabel = new Label(composite, SWT.NONE);
		headerImageLabel.setImage(Activator.getImage(getLandscape()));
		headerImageLabel.setBackground(((FormEditor)getEditor()).getToolkit().getColors().getBackground());
		
		Label headerLabel = new Label(composite, SWT.NONE);
		headerLabel.setText("CCF Properties");
		headerLabel.setBackground(((FormEditor)getEditor()).getToolkit().getColors().getBackground());
		headerLabel.setFont(JFaceResources.getHeaderFont());
        TableWrapData td = new TableWrapData(TableWrapData.FILL);
        headerLabel.setLayoutData(td);
        
		errorImageLabel = new Label(composite, SWT.NONE);
		errorImageLabel.setImage(Activator.getImage(Activator.IMAGE_ERROR));
		errorImageLabel.setBackground(((FormEditor)getEditor()).getToolkit().getColors().getBackground());
		errorImageLabel.setVisible(false);
		
		errorTextLabel = new Label(composite, SWT.NONE);
		errorTextLabel.setText("One or more required field is empty or invalid.");
		errorTextLabel.setBackground(((FormEditor)getEditor()).getToolkit().getColors().getBackground());
		errorTextLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
		errorTextLabel.setVisible(false);
		
		td = new TableWrapData(TableWrapData.FILL);
		td.align = TableWrapData.BOTTOM;
		errorTextLabel.setLayoutData(td);
		
	    Composite propertiesGroup = toolkit.createComposite(composite);
	    GridLayout propertiesLayout = new GridLayout();
	    propertiesLayout.numColumns = 2;
	    propertiesGroup.setLayout(propertiesLayout);
        td = new TableWrapData(TableWrapData.FILL_GRAB);
        td.colspan = 4;
        propertiesGroup.setLayoutData(td);

        toolkit.createLabel(propertiesGroup, "Description:");
        descriptionText = toolkit.createText(propertiesGroup, getLandscape().getDescription());
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		descriptionText.setLayoutData(gd);
		
		Section databaseSection = toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        td = new TableWrapData(TableWrapData.FILL_GRAB);
        td.colspan = 4;
        databaseSection.setLayoutData(td);
        databaseSection.setText("Database");
        Composite databaseSectionClient = toolkit.createComposite(databaseSection); 
        GridLayout databaseLayout = new GridLayout();
        databaseLayout.numColumns = 2;
        databaseLayout.verticalSpacing = 10;
        databaseSectionClient.setLayout(databaseLayout);
        databaseSection.setClient(databaseSectionClient);
        databaseSection.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
                if (e.getState()) getDialogSettings().put(DATABASE_SECTION_STATE, STATE_EXPANDED);
                else getDialogSettings().put(DATABASE_SECTION_STATE, STATE_CONTRACTED);
            }
        });
        
        toolkit.createLabel(databaseSectionClient, "URL:");
        urlText = toolkit.createText(databaseSectionClient, getLandscape().getDatabaseUrl());
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		urlText.setLayoutData(gd);
		
        toolkit.createLabel(databaseSectionClient, "Driver:");
        driverText = toolkit.createText(databaseSectionClient, getLandscape().getDatabaseDriver());
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		driverText.setLayoutData(gd);
		
        toolkit.createLabel(databaseSectionClient, "User:");
        userText = toolkit.createText(databaseSectionClient, getLandscape().getDatabaseUser());
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		userText.setLayoutData(gd);
		
        toolkit.createLabel(databaseSectionClient, "Password:");
        passwordText = toolkit.createText(databaseSectionClient, getLandscape().getDatabasePassword());
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		passwordText.setLayoutData(gd);
		passwordText.setEchoChar('*');
		
		Button testButton = toolkit.createButton(databaseSectionClient, "Test Connection", SWT.PUSH);
		gd = new GridData();
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = GridData.HORIZONTAL_ALIGN_CENTER;
		testButton.setLayoutData(gd);
		testButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				testConnection(false);
			}			
		});
		
		Section hostSection = toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        td = new TableWrapData(TableWrapData.FILL_GRAB);
        td.colspan = 4;
        hostSection.setLayoutData(td);
        hostSection.setText("CCF Hosts");
        Composite hostSectionClient = toolkit.createComposite(hostSection); 
        GridLayout jmxLayout = new GridLayout();
        jmxLayout.numColumns = 2;
        jmxLayout.verticalSpacing = 10;
        hostSectionClient.setLayout(jmxLayout);
        hostSection.setClient(hostSectionClient);
        hostSection.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
                if (e.getState()) getDialogSettings().put(JMX_SECTION_STATE, STATE_EXPANDED);
                else getDialogSettings().put(JMX_SECTION_STATE, STATE_CONTRACTED);
            }
        });	
        
		toolkit.createLabel(hostSectionClient, getLandscape().getType2() + " => " + getLandscape().getType1() + " host name:");
		host1Text = toolkit.createText(hostSectionClient, ccfHost1);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		host1Text.setLayoutData(gd);
        
		toolkit.createLabel(hostSectionClient, getLandscape().getType2() + " => " + getLandscape().getType1() + " JMX port:");
		jmxPort1Text = toolkit.createText(hostSectionClient, jmxPort1);
		gd = new GridData();
		gd.widthHint = 100;
		jmxPort1Text.setLayoutData(gd);
		
		toolkit.createLabel(hostSectionClient, getLandscape().getType1() + " => " + getLandscape().getType2() + " host name:");
		host2Text = toolkit.createText(hostSectionClient, ccfHost2);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		host2Text.setLayoutData(gd);
		
		toolkit.createLabel(hostSectionClient, getLandscape().getType1() + " => " + getLandscape().getType2() + " JMX port:");
		jmxPort2Text = toolkit.createText(hostSectionClient, jmxPort2);
		gd = new GridData();
		gd.widthHint = 100;
		jmxPort2Text.setLayoutData(gd);
		
		Section templateSection = toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        td = new TableWrapData(TableWrapData.FILL_GRAB);
        td.colspan = 4;
        templateSection.setLayoutData(td);
        templateSection.setText("Log Message Template");
        Composite templateSectionClient = toolkit.createComposite(templateSection); 
        GridLayout templateLayout = new GridLayout();
        templateLayout.numColumns = 1;
        templateLayout.verticalSpacing = 10;
        templateSectionClient.setLayout(templateLayout);
        templateSection.setClient(templateSectionClient);
        templateSection.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
                if (e.getState()) getDialogSettings().put(TEMPLATE_SECTION_STATE, STATE_EXPANDED);
                else getDialogSettings().put(TEMPLATE_SECTION_STATE, STATE_CONTRACTED);
            }
        });
        
        templateText = toolkit.createText(templateSectionClient, getLandscape().getLogMessageTemplate1(), SWT.BORDER | SWT.MULTI);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.heightHint = 175;
		templateText.setLayoutData(gd);
		
		Composite buttonGroup = toolkit.createComposite(templateSectionClient);
		GridLayout buttonLayout = new GridLayout();
		buttonLayout.numColumns = 2;
		buttonGroup.setLayout(buttonLayout);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		buttonGroup.setLayoutData(gd);	
		
		Button resetButton = toolkit.createButton(buttonGroup, "Restore Default Template", SWT.PUSH);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		resetButton.setLayoutData(gd);
		resetButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				templateText.setText(Activator.DEFAULT_LOG_MESSAGE_TEMPLATE);
			}			
		});
		
		insertButton = toolkit.createButton(buttonGroup, "Insert Hospital Column...", SWT.PUSH);
		insertButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				insertHospitalColumn();
			}			
		});
		insertButton.setEnabled(false);
		
        toolkit.paintBordersFor(databaseSectionClient);
        toolkit.paintBordersFor(hostSectionClient);
        toolkit.paintBordersFor(templateSectionClient);
        
        String expansionState = getDialogSettings().get(DATABASE_SECTION_STATE);
        databaseSection.setExpanded(expansionState == null  || expansionState.equals(STATE_EXPANDED));
        
        expansionState = getDialogSettings().get(JMX_SECTION_STATE);
        hostSection.setExpanded(expansionState == null  || expansionState.equals(STATE_EXPANDED));
        
        expansionState = getDialogSettings().get(TEMPLATE_SECTION_STATE);
        templateSection.setExpanded(expansionState == null  || expansionState.equals(STATE_EXPANDED));
        
        ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				errorImageLabel.setVisible(!canLeaveThePage());
				errorTextLabel.setVisible(!canLeaveThePage());
				((CcfEditor)getEditor()).setDirty();
				if (me.getSource() != descriptionText) {
					ccfPropertiesUpdated = true;
				}
			}			
		};
		
		descriptionText.addModifyListener(modifyListener);
		urlText.addModifyListener(modifyListener);
		driverText.addModifyListener(modifyListener);
		userText.addModifyListener(modifyListener);
		passwordText.addModifyListener(modifyListener);
		jmxPort1Text.addModifyListener(modifyListener);
		host1Text.addModifyListener(modifyListener);
		jmxPort2Text.addModifyListener(modifyListener);
		host2Text.addModifyListener(modifyListener);
		templateText.addModifyListener(modifyListener);
		
		FocusListener focusListener = new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if (e.getSource() == templateText) insertButton.setEnabled(true);
				else ((Text)e.getSource()).selectAll();
			}
			public void focusLost(FocusEvent e) {
				if (e.getSource() == templateText) insertButton.setEnabled(false);
				else ((Text)e.getSource()).setText(((Text)e.getSource()).getText());
			}					
		};
		
		descriptionText.addFocusListener(focusListener);
		urlText.addFocusListener(focusListener);
		driverText.addFocusListener(focusListener);
		userText.addFocusListener(focusListener);
		passwordText.addFocusListener(focusListener);
		jmxPort1Text.addFocusListener(focusListener);
		host1Text.addFocusListener(focusListener);
		jmxPort2Text.addFocusListener(focusListener);
		host2Text.addFocusListener(focusListener);
		templateText.addFocusListener(focusListener);
	}
	
	@Override
	public boolean canLeaveThePage() {
		if (descriptionText.getText().trim().length() == 0 ||
		urlText.getText().trim().length() == 0 ||
		driverText.getText().trim().length() == 0) return false;
		if (jmxPort1Text.getText().trim().length() > 0) {
			int port = 0;
			try {
				port = Integer.parseInt(jmxPort1Text.getText().trim());
			} catch (Exception e) {}
			if (port <= 0) return false;			
		}
		if (jmxPort2Text.getText().trim().length() > 0) {
			int port = 0;
			try {
				port = Integer.parseInt(jmxPort2Text.getText().trim());
			} catch (Exception e) {}
			if (port <= 0) return false;			
		}		
		return true;
	}

	private void insertHospitalColumn() {
		HospitalColumnSelectionDialog dialog = new HospitalColumnSelectionDialog(Display.getDefault().getActiveShell());
		if (dialog.open() == HospitalColumnSelectionDialog.OK) {
			String column = dialog.getColumn();
			if (column != null) {
				templateText.insert(column);
			}
		}
		templateText.setFocus();		
	}

	public boolean isDirty() {
		if (urlText == null) return false;
		return !descriptionText.getText().trim().equals(description) ||
		!urlText.getText().trim().equals(url) ||
		!driverText.getText().trim().equals(driver) ||
		!userText.getText().trim().equals(user) ||
		!passwordText.getText().trim().equals(password) ||
		!jmxPort1Text.getText().trim().equals(jmxPort1)	 ||
		!host1Text.getText().trim().equals(ccfHost1) ||
		!jmxPort2Text.getText().trim().equals(jmxPort2) ||
		!host2Text.getText().trim().equals(ccfHost2) ||
		!templateText.getText().trim().equals(template);
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		if (descriptionText == null) return;
		if (ccfPropertiesUpdated()) {
			saveCcfProperties();			
		}
		if (!descriptionText.getText().trim().equals(getLandscape().getDescription())) {
			getLandscape().setDescription(descriptionText.getText().trim().replaceAll("/", "%slash%"));
			if (Activator.getDefault().storeLandscape(getLandscape())) {
				// Delete old node.
				Activator.getDefault().deleteLandscape(getLandscape());
			}
			Landscape landscape = Activator.getDefault().getLandscape(getLandscape().getDescription());
			if (landscape != null) setLandscape(landscape);
		}
		description = descriptionText.getText().trim();
		url = urlText.getText().trim();
		driver = driverText.getText().trim();
		user = userText.getText().trim();
		password = passwordText.getText().trim();
		jmxPort1 = jmxPort1Text.getText().trim();
		ccfHost1 = host1Text.getText().trim();
		jmxPort2 = jmxPort2Text.getText().trim();
		ccfHost2 = host2Text.getText().trim();
		template = templateText.getText().trim();
	}
	
	private void saveCcfProperties() {
		try {
			File folder = new File(getLandscape().getConfigurationFolder());
			File propertiesFile = new File(folder, "ccf.properties");
			FileInputStream inputStream = new FileInputStream(propertiesFile);
			Properties properties = new Properties();
			properties.load(inputStream);
			inputStream.close();
			
			File propertiesFile1 = null;
			File propertiesFile2 = null;
			
			if (getLandscape().getConfigurationFolder1() != null) {
				folder = new File(getLandscape().getConfigurationFolder1());
				propertiesFile1 = new File(folder, "ccf.properties");
			}
			
			if (getLandscape().getConfigurationFolder2() != null) {
				folder = new File(getLandscape().getConfigurationFolder2());
				propertiesFile2 = new File(folder, "ccf.properties");
			}
			
			properties.setProperty(Activator.PROPERTIES_CCF_URL, getUrl());
			properties.setProperty(Activator.PROPERTIES_CCF_DRIVER, getDriver());
			properties.setProperty(Activator.PROPERTIES_CCF_USER, getUser());
			properties.setProperty(Activator.PROPERTIES_CCF_PASSWORD, getPassword());
			properties.setProperty(Activator.PROPERTIES_CCF_LOG_MESSAGE_TEMPLATE, getLogMessageTemplate());
			if (propertiesFile1 != null) {
				properties.setProperty(Activator.PROPERTIES_CCF_JMX_PORT, getJmxPort1());
				properties.setProperty(Activator.PROPERTIES_CCF_HOST_NAME, getHostName1());
				FileOutputStream outputStream = new FileOutputStream(propertiesFile1);
				properties.store(outputStream, null);
				outputStream.close();
			}
			if (propertiesFile2 != null) {
				properties.setProperty(Activator.PROPERTIES_CCF_JMX_PORT, getJmxPort2());
				properties.setProperty(Activator.PROPERTIES_CCF_HOST_NAME, getHostName2());
				FileOutputStream outputStream = new FileOutputStream(propertiesFile2);
				properties.store(outputStream, null);
				outputStream.close();
			}						
		} catch (Exception e) {
			Activator.handleError(e);
		}
	}

	public String getDescription() {
		return descriptionText.getText().trim();
	}
	
	public String getUrl() {
		return urlText.getText().trim();
	}
	
	public String getDriver() {
		return driverText.getText().trim();
	}
	
	public String getUser() {
		return userText.getText().trim();
	}
	
	public String getPassword() {
		return passwordText.getText().trim();
	}
	
	public String getJmxPort1() {
		return jmxPort1Text.getText().trim();
	}
	
	public String getJmxPort2() {
		return jmxPort2Text.getText().trim();
	}
	
	public String getHostName1() {
		return host1Text.getText().trim();
	}
	
	public String getHostName2() {
		return host2Text.getText().trim();
	}
	
	public String getLogMessageTemplate() {
		return templateText.getText().trim();
	}
	
	public boolean ccfPropertiesUpdated() {
		return ccfPropertiesUpdated;
	}
	
	public boolean testConnection(boolean saving) {
		try {
			Class.forName(driverText.getText().trim());
			DriverManager.getConnection(urlText.getText().trim(), userText.getText().trim(), passwordText.getText().trim());	
			if (!saving) MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Test Connection", "Connection successful!");
		} catch (Exception e) {
			if (saving) {
				return MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Landscape","Could not connect to database:\n\n" + e.getLocalizedMessage() + "\n\nSave anyway?");
			}
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Test Connection", "Connection failed:\n\n" + e.getLocalizedMessage());
		}
		return true;
	}

}

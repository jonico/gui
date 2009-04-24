package com.collabnet.ccf.wizards;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class NewLandscapeWizardPropertiesFolderPage extends WizardPage {
	private int type;
	
	private Text configFileText1;
	private Text configFileText2;
	private File parent1;
	private File parent2;
	
	public final static int TYPE_TF = 0;
	public final static int TYPE_PT = 1;

	public NewLandscapeWizardPropertiesFolderPage(String pageName, String title, ImageDescriptor titleImage, int type) {
		super(pageName, title, titleImage);
		this.type = type;
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite outerContainer = new Composite(parent,SWT.NONE);
		outerContainer.setLayout(new GridLayout());
		outerContainer.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		Group fileGroup1 = new Group(outerContainer, SWT.NULL);
		GridLayout fileLayout1 = new GridLayout();
		fileLayout1.numColumns = 2;
		fileGroup1.setLayout(fileLayout1);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		fileGroup1.setLayoutData(data);

		switch (type) {
		case TYPE_TF:
			fileGroup1.setText("TeamForge => QC config.xml:");
			break;
		case TYPE_PT:
			fileGroup1.setText("CEE => QC config.xml:");
			break;
		default:
			break;
		}
		
		configFileText1 = new Text(fileGroup1, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		data.widthHint = 350;
		configFileText1.setLayoutData(data);
		configFileText1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				setPageComplete(canFinish());
			}			
		});

		Button browseButton1 = new Button(fileGroup1, SWT.NULL);
		browseButton1.setText("Browse...");
		
		browseButton1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog d = new FileDialog(getShell(), SWT.PRIMARY_MODAL | SWT.OPEN);				
				switch (type) {
				case TYPE_TF:
					d.setText("Select TeamForge => QC config.xml file");
					break;
				case TYPE_PT:
					d.setText("Select CEE => QC config.xml file");
					break;
				default:
					break;
				}
				d.setFileName("config.xml"); //$NON-NLS-1$
				String[] filterExtensions = { "*.xml" }; //$NON-NLS-1$
				d.setFilterExtensions(filterExtensions);
				String file = d.open();
				if(file!=null) {
					IPath path = new Path(file);
					configFileText1.setText(path.toOSString());
					setPageComplete(canFinish());
				}							
			}
		});
		
		Group fileGroup2 = new Group(outerContainer, SWT.NULL);
		GridLayout fileLayout2 = new GridLayout();
		fileLayout2.numColumns = 2;
		fileGroup2.setLayout(fileLayout2);
		data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		fileGroup2.setLayoutData(data);

		switch (type) {
		case TYPE_TF:
			fileGroup2.setText("QC => TeamForge config.xml:");
			break;
		case TYPE_PT:
			fileGroup2.setText("QC => CEE config.xml:");
			break;
		default:
			break;
		}
		
		configFileText2 = new Text(fileGroup2, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		data.widthHint = 350;
		configFileText2.setLayoutData(data);
		configFileText2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				setPageComplete(canFinish());
			}			
		});

		Button browseButton2 = new Button(fileGroup2, SWT.NULL);
		browseButton2.setText("Browse...");
		
		browseButton2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog d = new FileDialog(getShell(), SWT.PRIMARY_MODAL | SWT.OPEN);				
				switch (type) {
				case TYPE_TF:
					d.setText("Select QC => TeamForge config.xml file");
					break;
				case TYPE_PT:
					d.setText("Select QC => CEE config.xml file");
					break;
				default:
					break;
				}
				d.setFileName("config.xml"); //$NON-NLS-1$
				String[] filterExtensions = { "*.xml" }; //$NON-NLS-1$
				d.setFilterExtensions(filterExtensions);
				String file = d.open();
				if(file!=null) {
					IPath path = new Path(file);
					configFileText2.setText(path.toOSString());
					setPageComplete(canFinish());
				}							
			}
		});

		setMessage("Select the config.xml files");

		setControl(outerContainer);
	}
	
	public String getConfigurationFolder1() {
		if (parent1 != null) {
			return parent1.getAbsolutePath();
		}
		return null;
	}
	
	public String getConfigurationFolder2() {
		if (configFileText2.getText().trim().length() > 0 && parent2 != null) {
			return parent2.getAbsolutePath();
		}
		return null;
	}
	
	private boolean canFinish() {
		String errorMessage;
		String propFileName;
		switch (type) {
		case TYPE_TF:
			propFileName = "sfee.properties"; //$NON-NLS-1$
			break;
		case TYPE_PT:
			propFileName = "cee.properties"; //$NON-NLS-1$
			break;
		default:
			propFileName = "sfee.properties"; //$NON-NLS-1$
			break;
		}
		setErrorMessage(null);
		if (configFileText1.getText().trim().length() == 0 && configFileText2.getText().trim().length() == 0) {
			setErrorMessage("At least one config.xml file must be selected");
			return false;			
		}
		if (configFileText1.getText().trim().length() > 0) {
			if (!configFileText1.getText().endsWith("config.xml")) return false;
			File file = new File(configFileText1.getText());
			if (!file.exists()) {				
				switch (type) {
				case TYPE_TF:
					errorMessage = "TeamForge => QC config.xml does not exist";
					break;
				case TYPE_PT:
					errorMessage = "CEE => QC config.xml does not exist";
					break;
				default:
					errorMessage = "File does not exist";
					break;
				}				
				setErrorMessage(errorMessage);
				return false;
			}
			parent1 = file.getParentFile();
			File ccfFile = new File(parent1, "ccf.properties");
			if (!ccfFile.exists()) {
				setErrorMessage("File ccf.properties must exist in same folder as config.xml");
				return false;
			}
			File qcFile = new File(parent1, "qc.properties");
			if (!qcFile.exists()) {
				setErrorMessage("File qc.properties must exist in same folder as config.xml");
				return false;
			}
			File propFile = new File(parent1, propFileName);
			if (!propFile.exists()) {
				setErrorMessage("File " + propFileName + " must exist in same folder as config.xml");
				return false;
			}
		}
		
		if (configFileText2.getText().trim().length() > 0) {
			if (!configFileText2.getText().endsWith("config.xml")) return false;
			File file = new File(configFileText2.getText());
			if (!file.exists()) {				
				switch (type) {
				case TYPE_TF:
					errorMessage = "QC => TeamForge config.xml does not exist";
					break;
				case TYPE_PT:
					errorMessage = "QC => CEE config.xml does not exist";
					break;
				default:
					errorMessage = "File does not exist";
					break;
				}				
				setErrorMessage(errorMessage);
				return false;
			}
			parent2 = file.getParentFile();
			File ccfFile = new File(parent2, "ccf.properties");
			if (!ccfFile.exists()) {
				setErrorMessage("File ccf.properties must exist in same folder as config.xml");
				return false;
			}
			File qcFile = new File(parent2, "qc.properties");
			if (!qcFile.exists()) {
				setErrorMessage("File qc.properties must exist in same folder as config.xml");
				return false;
			}
			File propFile = new File(parent2, propFileName);
			if (!propFile.exists()) {
				setErrorMessage("File " + propFileName + " must exist in same folder as config.xml");
				return false;
			}
		}
		
		if (configFileText1.getText().trim().length() > 0 && configFileText2.getText().trim().length() > 0 && configFileText1.getText().trim().equals(configFileText2.getText().trim())) {
			setErrorMessage("Same config.xml file cannot be used for both directions");
			return false;					
		}
		
		return true;
	}

}

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
import org.eclipse.swt.widgets.Text;

public class NewLandscapeWizardPropertiesFolderPage extends WizardPage {
	private int type;
	
	private Text configFileText;
	private File parent;
	
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
		
		Composite fileGroup = new Composite(outerContainer, SWT.NULL);
		GridLayout fileLayout = new GridLayout();
		fileLayout.numColumns = 3;
		fileGroup.setLayout(fileLayout);
		GridData data = new GridData(GridData.FILL_BOTH);
		fileGroup.setLayoutData(data);
		
		configFileText = new Text(fileGroup, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		data.widthHint = 350;
		configFileText.setLayoutData(data);
		configFileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				setPageComplete(canFinish());
			}			
		});

		Button browseButton = new Button(fileGroup, SWT.NULL);
		browseButton.setText("Browse...");
		
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog d = new FileDialog(getShell(), SWT.PRIMARY_MODAL | SWT.OPEN);
				d.setText("Select config.xml file");
				d.setFileName("config.xml"); //$NON-NLS-1$
				String[] filterExtensions = { "*.xml" };
				d.setFilterExtensions(filterExtensions);
				String file = d.open();
				if(file!=null) {
					IPath path = new Path(file);
					configFileText.setText(path.toOSString());
					setPageComplete(canFinish());
				}							
			}
		});

		setMessage("Select the config.xml file");

		setControl(outerContainer);
	}
	
	public String getConfigurationFolder() {
		if (parent != null) {
			return parent.getAbsolutePath();
		}
		return null;
	}
	
	private boolean canFinish() {
		setErrorMessage(null);
		if (!configFileText.getText().endsWith("config.xml")) return false;
		File file = new File(configFileText.getText());
		if (!file.exists()) {
			setErrorMessage("File does not exist");
			return false;
		}
		parent = file.getParentFile();
		File ccfFile = new File(parent, "ccf.properties");
		if (!ccfFile.exists()) {
			setErrorMessage("File ccf.properties must exist in same folder as config.xml");
			return false;
		}
		File qcFile = new File(parent, "qc.properties");
		if (!qcFile.exists()) {
			setErrorMessage("File qc.properties must exist in same folder as config.xml");
			return false;
		}
		String propFileName;
		switch (type) {
		case TYPE_TF:
			propFileName = "sfee.properties";
			break;
		case TYPE_PT:
			propFileName = "cee.properties";
			break;
		default:
			propFileName = "sfee.properties";
			break;
		}
		File propFile = new File(parent, propFileName);
		if (!propFile.exists()) {
			setErrorMessage("File " + propFileName + " must exist in same folder as config.xml");
			return false;
		}
		return true;
	}

}

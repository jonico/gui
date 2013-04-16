package com.collabnet.ccf.rqp;

import java.util.Properties;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.IMappingSection;
import com.collabnet.ccf.MappingSection;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.rqp.dialogs.ProjectPackageSelectionDialog;
import com.collabnet.ccf.rqp.dialogs.RequirementTypeSelectionDialog;
import com.collabnet.ccf.rqp.schemageneration.RQPLayoutExtractor;

public class RequisiteProMappingSection extends MappingSection {
	
	private Text projectText;
	private Text packageText;
	private Button projectBrowseButton;
	private Label requirementTypeLabel;
	private Text requirementTypeText;
	private Button requirementTypeBrowseButton;
	private Button defectsButton;
	private Button requirementsButton;
	private Label requirementsErrorLabel;
	private Label requirementsErrorMessageLabel;
	private boolean advancedProjectMappingOptionsEnabled;
	
	private IDialogSettings settings = Activator.getDefault().getDialogSettings();
	private static final String PREVIOUS_TYPE = "RequisiteProMappingSection.type";
	public static final String PREVIOUS_DOMAIN = "RequisiteProMappingSection.previousDomain.";
	public static final String PREVIOUS_DOMAIN_COUNT = "RequisiteProMappingSection.previousDomainCount";
	private static final int TYPE_DEFECTS = 0;
	private static final int TYPE_REQUIREMENTS = 1;
	
	public void updateSourceFields(SynchronizationStatus projectMapping) {
		projectMapping.setSourceRepositoryId(getRepositoryId());
		projectMapping.setSourceRepositoryKind("DEFECT");
	}
	
	public void updateTargetFields(SynchronizationStatus projectMapping) {
		projectMapping.setTargetRepositoryId(getRepositoryId());
		projectMapping.setTargetRepositoryKind("DEFECT");
	}
	
	private String getRepositoryId() {
		StringBuffer repositoryId = new StringBuffer(projectText.getText().trim() + "-" + packageText.getText().trim());
		if (requirementsButton.getSelection()) {
			repositoryId.append("-" + requirementTypeText.getText().trim());
		}
		return repositoryId.toString();
	}
	
	public Composite getComposite(Composite parent, final Landscape landscape) {		
		Group rqpGroup = new Group(parent, SWT.NULL);
		GridLayout rqpLayout = new GridLayout();
		rqpLayout.numColumns = 3;
		rqpGroup.setLayout(rqpLayout);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		rqpGroup.setLayoutData(gd);	
		
		if (landscape.getType1().equals("RQP") && landscape.getType2().equals("RQP")) {
			if (landscape.getRole() == Landscape.ROLE_ADMINISTRATOR) {
				String url;
				if (getSystemNumber() == 1) {
					url = landscape.getProperties1().getProperty(RequisiteProCcfParticipant.PROPERTIES_RQP_URL);
				} else {
					url = landscape.getProperties2().getProperty(RequisiteProCcfParticipant.PROPERTIES_RQP_URL);
				}
				rqpGroup.setText("RequisitePro (" + url + "):");
			} else {
				rqpGroup.setText("RequisitePro " + getSystemNumber());
			}
		} else {
			rqpGroup.setText("RequisitePro:");
		}		
		
		Label projectLabel = new Label(rqpGroup, SWT.NONE);
		projectLabel.setText("Project:");

		projectText = new Text(rqpGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		projectText.setLayoutData(gd);	
		
		new Label(rqpGroup, SWT.NONE);

		Label packageLabel = new Label(rqpGroup, SWT.NONE);
		packageLabel.setText("Package:");
	
		packageText = new Text(rqpGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		packageText.setLayoutData(gd);	
		
		projectBrowseButton = new Button(rqpGroup, SWT.PUSH);
		projectBrowseButton.setText("Browse...");
		projectBrowseButton.setVisible("win32".equals(SWT.getPlatform()));
		projectBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				ProjectPackageSelectionDialog dialog = new ProjectPackageSelectionDialog(Display.getDefault().getActiveShell(), landscape, projectText.getText(), packageText.getText(), ProjectPackageSelectionDialog.BROWSER_TYPE_PROJECT);
				if (dialog.open() == ProjectPackageSelectionDialog.OK) {
					projectText.setText(dialog.getProject());
					packageText.setText(dialog.getPackage());
					if (getProjectPage() != null) {
						getProjectPage().setPageComplete();
					}
				}
			}			
		});

		defectsButton = new Button(rqpGroup, SWT.RADIO);
		defectsButton.setText("Defects");
		gd = new GridData();
		gd.horizontalSpan = 3;
		defectsButton.setLayoutData(gd);
		requirementsButton = new Button(rqpGroup, SWT.RADIO);
		requirementsButton.setText("Requirements");
		gd = new GridData();
		gd.horizontalSpan = 3;
		requirementsButton.setLayoutData(gd);
	
		int rqpType;
		try {
			rqpType = settings.getInt(PREVIOUS_TYPE);
		} catch (Exception e) { rqpType = TYPE_DEFECTS; }
		switch (rqpType) {
		case TYPE_REQUIREMENTS:
			requirementsButton.setSelection(true);
			break;
		default:
			defectsButton.setSelection(true);
			break;
		}		
	
		requirementTypeLabel = new Label(rqpGroup, SWT.NONE);
		requirementTypeLabel.setText("Requirement type:");
		requirementTypeLabel.setVisible(requirementsButton.getSelection());
		requirementTypeText = new Text(rqpGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		requirementTypeText.setLayoutData(gd);	
		requirementTypeText.setVisible(requirementsButton.getSelection());
		requirementTypeBrowseButton = new Button(rqpGroup, SWT.PUSH);
		requirementTypeBrowseButton.setText("Browse...");
		requirementTypeBrowseButton.setVisible("win32".equals(SWT.getPlatform()) && requirementsButton.getSelection());
		requirementTypeBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if (!validate(landscape)) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Select Requirement Type", "Invalid RequisitePro Domain/Project entered.");
					return;
				}
				RequirementTypeSelectionDialog dialog = new RequirementTypeSelectionDialog(Display.getDefault().getActiveShell(), landscape, projectText.getText().trim());
				if (dialog.open() == RequirementTypeSelectionDialog.OK) {
					requirementTypeText.setText(dialog.getType());
					if (getProjectPage() != null) {
						getProjectPage().setPageComplete();
					}
				}
			}			
		});
		
		Composite warningGroup = new Composite(rqpGroup, SWT.NULL);
		GridLayout warningLayout = new GridLayout();
		warningLayout.numColumns = 2;
		warningGroup.setLayout(warningLayout);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 3;
		warningGroup.setLayoutData(gd);	
		
		requirementsErrorLabel = new Label(warningGroup, SWT.NONE);
		requirementsErrorLabel.setImage(Activator.getImage(Activator.IMAGE_ERROR));
		requirementsErrorLabel.setVisible(false);
		requirementsErrorMessageLabel = new Label(warningGroup, SWT.NONE);
		requirementsErrorMessageLabel.setText("CCF cannot map requirement types containing \"-\" character");
		requirementsErrorMessageLabel.setVisible(false);
		
		if (landscape.getRole() == Landscape.ROLE_OPERATOR) {
			projectBrowseButton.setEnabled(false);
			requirementTypeBrowseButton.setEnabled(false);
		}
	
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				if (getProjectPage() != null) {
					getProjectPage().setPageComplete();
				}
			}			
		};
		
		packageText.addModifyListener(modifyListener);
		projectText.addModifyListener(modifyListener);
		requirementTypeText.addModifyListener(modifyListener);
		
		projectText.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (getProjectPage() != null) {
					getProjectPage().setPageComplete();
				}
			}			
		});
		
		SelectionListener typeListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (requirementsButton.getSelection()) {
					settings.put(PREVIOUS_TYPE, TYPE_REQUIREMENTS);
					requirementTypeText.setFocus();
				} else {
					settings.put(PREVIOUS_TYPE, TYPE_DEFECTS);
				}
				requirementTypeLabel.setVisible(requirementsButton.getSelection());
				requirementTypeText.setVisible(requirementsButton.getSelection());
				requirementTypeBrowseButton.setVisible("win32".equals(SWT.getPlatform()) && requirementsButton.getSelection() && advancedProjectMappingOptionsEnabled);
				if (getProjectPage() != null) {
					getProjectPage().setPageComplete();
				}
			}			
		};
		
		defectsButton.addSelectionListener(typeListener);
		requirementsButton.addSelectionListener(typeListener);	
		
		advancedProjectMappingOptionsEnabled = com.collabnet.ccf.rqp.Activator.getDefault().getPreferenceStore().getBoolean(com.collabnet.ccf.rqp.Activator.PREFERENCES_ADVANCED_PROJECT_MAPPING);
		if (!advancedProjectMappingOptionsEnabled) {
			projectBrowseButton.setVisible(false);
			requirementTypeBrowseButton.setVisible(false);
		}
		
		return rqpGroup;
	}

	public void initializeComposite(SynchronizationStatus projectMapping, int type) {
		projectText.setText(getProject(projectMapping, type));
		packageText.setText(getPackage(projectMapping, type));
		String requirementType = getRequirementType(projectMapping, type);
		if (requirementType != null) {
			requirementTypeLabel.setVisible(true);
			requirementTypeText.setVisible(true);
			requirementTypeBrowseButton.setVisible(true);
			requirementTypeText.setText(requirementType);
			requirementsButton.setSelection(true);
			defectsButton.setSelection(false);
		} else {
			requirementTypeLabel.setVisible(false);
			requirementTypeText.setVisible(false);
			requirementTypeBrowseButton.setVisible(false);
			requirementsButton.setSelection(false);
			defectsButton.setSelection(true);
		}
	}

	public boolean isPageComplete() {
		if (requirementTypeText.isVisible() && requirementTypeText.getText().contains("-")) {
			requirementsErrorLabel.setVisible(true);
			requirementsErrorMessageLabel.setVisible(true);
		}
		else {
			requirementsErrorLabel.setVisible(false);
			requirementsErrorMessageLabel.setVisible(false);
		}
		if (projectText == null) {
			return false;
		}
		if (projectText.getText().trim().length() == 0 ||
			packageText.getText().trim().length() == 0) {
			return false;
		}
		if (requirementTypeText.isVisible()) {
			if (requirementTypeText.getText().trim().length() == 0) {
				return false;
			}
			if (requirementTypeText.getText().contains("-")) {
				return false;
			}
		}
		return true;
	}

	public boolean validate(Landscape landscape) {
		// Only validate on 32-bit windows, because the COM driver doesn't work on 64-bit windows.
		if (landscape.getRole() == Landscape.ROLE_OPERATOR || !"win32".equals(SWT.getPlatform())) return true;
		
		// Only validate if "Enable advanced project mapping wizard options" preference is selected.
		if (!advancedProjectMappingOptionsEnabled) {
			return true;
		}
		
		RQPLayoutExtractor rqpLayoutExtractor = new RQPLayoutExtractor();
		Properties properties;
		if (landscape.getType2().equals("QT")) {
			properties = landscape.getProperties2();
		} else {
			properties = landscape.getProperties1();
		}
		String url = properties.getProperty(Activator.PROPERTIES_RQP_URL, "");
		String user = properties.getProperty(Activator.PROPERTIES_RQP_USER, "");
		String password = Activator.decodePassword(properties.getProperty(
				Activator.PROPERTIES_RQP_PASSWORD, ""));
		rqpLayoutExtractor.setServerUrl(url);
		rqpLayoutExtractor.setUserName(user);
		rqpLayoutExtractor.setPassword(password);
		
		boolean validDomainAndProject;
		try {
			rqpLayoutExtractor.validateRQPProjectAndPackage(projectText.getText().trim());
			validDomainAndProject = true;
		} catch (Exception e) {
			validDomainAndProject = false;
		}
		if (!validDomainAndProject) {
			if (!showValidationQuestionDialog("Could not validate the supplied RequisitePro Domain/Project.  Continue anyway?")) {
				return false;
			}
		}
		return true;
	}
	
	private String getProject(SynchronizationStatus projectMapping, int type) {
		String repositoryId;
		if (type == IMappingSection.TYPE_SOURCE) {
			repositoryId = projectMapping.getSourceRepositoryId();
		} else {
			repositoryId = projectMapping.getTargetRepositoryId();
		}
		int index = repositoryId.indexOf("-");
		if (index == -1) return "";
		else return repositoryId.substring(0, index);
	}
	
	private String getPackage(SynchronizationStatus projectMapping, int type) {
		String repositoryId;
		if (type == IMappingSection.TYPE_SOURCE) {
			repositoryId = projectMapping.getSourceRepositoryId();
		} else {
			repositoryId = projectMapping.getTargetRepositoryId();
		}
		int index = repositoryId.indexOf("-");
		if (index == -1) return "";
		else {
			String rqp_package = repositoryId = repositoryId.substring(index + 1);
			index = rqp_package.indexOf("-");
			if (index != -1) {
				rqp_package = rqp_package.substring(0, index);
			}
			return rqp_package;
		}
	}
	
	private String getRequirementType(SynchronizationStatus projectMapping, int type) {
		String repositoryId;
		if (type == IMappingSection.TYPE_SOURCE) {
			repositoryId = projectMapping.getSourceRepositoryId();
		} else {
			repositoryId = projectMapping.getTargetRepositoryId();
		}
		int index = repositoryId.indexOf("-");
		if (index != -1) {
			String project = repositoryId.substring(index + 1);
			index = project.indexOf("-");
			if (index != -1) {
				return project.substring(index + 1);
			}
		}
		return null;
	}	

}

package com.collabnet.ccf.tfs;

import java.util.Properties;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.IMappingSection;
import com.collabnet.ccf.MappingSection;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.tfs.dialogs.CollectionProjectSelectionDialog;
import com.collabnet.ccf.tfs.dialogs.RequirementTypeSelectionDialog;
import com.collabnet.ccf.tfs.schemageneration.TFSLayoutExtractor;

public class TFSMappingSection extends MappingSection {
	
	private Text collectionText;
	private Text projectText;
	private Button projectBrowseButton;
	private Label workItemsTypeLabel;
	private Text workItemsTypeText;
	private Button workItemsTypeBrowseButton;
	private Label workItemsErrorLabel;
	private Label workItemsErrorMessageLabel;
	private boolean advancedProjectMappingOptionsEnabled;
	
	public static final String PREVIOUS_COLLECTION = "TFSMappingSection.previousDomain.";
	public static final String PREVIOUS_COLLECTION_COUNT = "TFSMappingSection.previousDomainCount";
	
	public void updateSourceFields(SynchronizationStatus projectMapping) {
		projectMapping.setSourceRepositoryId(getRepositoryId());
		projectMapping.setSourceRepositoryKind("WORK ITEM");
	}
	
	public void updateTargetFields(SynchronizationStatus projectMapping) {
		projectMapping.setTargetRepositoryId(getRepositoryId());
		projectMapping.setTargetRepositoryKind("WORK ITEM");
	}
	
	private String getRepositoryId() {
		StringBuffer repositoryId = new StringBuffer(collectionText.getText().trim() + "-" + projectText.getText().trim());
		repositoryId.append("-" + workItemsTypeText.getText().trim());
		return repositoryId.toString();
	}
	
	public Composite getComposite(Composite parent, final Landscape landscape) {		
		Group tfsGroup = new Group(parent, SWT.NULL);
		GridLayout tfsLayout = new GridLayout();
		tfsLayout.numColumns = 3;
		tfsGroup.setLayout(tfsLayout);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		tfsGroup.setLayoutData(gd);	
		
		if (landscape.getType1().equals("TFS") && landscape.getType2().equals("TFS")) {
			if (landscape.getRole() == Landscape.ROLE_ADMINISTRATOR) {
				String url;
				if (getSystemNumber() == 1) {
					url = landscape.getProperties1().getProperty(TFSCcfParticipant.PROPERTIES_TFS_URL);
				} else {
					url = landscape.getProperties2().getProperty(TFSCcfParticipant.PROPERTIES_TFS_URL);
				}
				tfsGroup.setText("Tean Foundation Server (" + url + "):");
			} else {
				tfsGroup.setText("Tean Foundation Server " + getSystemNumber());
			}
		} else {
			tfsGroup.setText("Team Foundation Server:");
		}		
		
		Label domainLabel = new Label(tfsGroup, SWT.NONE);
		domainLabel.setText("Collection:");

		collectionText = new Text(tfsGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		collectionText.setLayoutData(gd);	
		
		new Label(tfsGroup, SWT.NONE);

		Label projectLabel = new Label(tfsGroup, SWT.NONE);
		projectLabel.setText("Project:");
	
		projectText = new Text(tfsGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		projectText.setLayoutData(gd);	
		
		projectBrowseButton = new Button(tfsGroup, SWT.PUSH);
		projectBrowseButton.setText("Browse...");
		projectBrowseButton.setVisible("win32".equals(SWT.getPlatform()));
		projectBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				CollectionProjectSelectionDialog dialog = new CollectionProjectSelectionDialog(Display.getDefault().getActiveShell(), landscape, collectionText.getText(), projectText.getText(), CollectionProjectSelectionDialog.BROWSER_TYPE_PROJECT);
				if (dialog.open() == CollectionProjectSelectionDialog.OK) {
					collectionText.setText(dialog.getDomain());
					projectText.setText(dialog.getProject());
					if (getProjectPage() != null) {
						getProjectPage().setPageComplete();
					}
				}
			}			
		});

		gd = new GridData();
		gd.horizontalSpan = 3;
	
		workItemsTypeLabel = new Label(tfsGroup, SWT.NONE);
		workItemsTypeLabel.setText("Work Item Type:");
		workItemsTypeText = new Text(tfsGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		workItemsTypeText.setLayoutData(gd);	
		workItemsTypeBrowseButton = new Button(tfsGroup, SWT.PUSH);
		workItemsTypeBrowseButton.setText("Browse...");
		workItemsTypeBrowseButton.setVisible("win32".equals(SWT.getPlatform()));
		workItemsTypeBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if (!validate(landscape)) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Select WorkItem Type", "Invalid Quality Center Domain/Project entered.");
					return;
				}
				RequirementTypeSelectionDialog dialog = new RequirementTypeSelectionDialog(Display.getDefault().getActiveShell(), landscape, collectionText.getText().trim(), projectText.getText().trim());
				if (dialog.open() == RequirementTypeSelectionDialog.OK) {
					workItemsTypeText.setText(dialog.getType());
					if (getProjectPage() != null) {
						getProjectPage().setPageComplete();
					}
				}
			}			
		});
		
		Composite warningGroup = new Composite(tfsGroup, SWT.NULL);
		GridLayout warningLayout = new GridLayout();
		warningLayout.numColumns = 2;
		warningGroup.setLayout(warningLayout);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 3;
		warningGroup.setLayoutData(gd);	
		
		workItemsErrorLabel = new Label(warningGroup, SWT.NONE);
		workItemsErrorLabel.setImage(Activator.getImage(Activator.IMAGE_ERROR));
		workItemsErrorLabel.setVisible(false);
		workItemsErrorMessageLabel = new Label(warningGroup, SWT.NONE);
		workItemsErrorMessageLabel.setText("CCF cannot map WorkItem types containing \"-\" character");
		workItemsErrorMessageLabel.setVisible(false);
		
		if (landscape.getRole() == Landscape.ROLE_OPERATOR) {
			projectBrowseButton.setEnabled(false);
			workItemsTypeBrowseButton.setEnabled(false);
		}
	
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				if (getProjectPage() != null) {
					getProjectPage().setPageComplete();
				}
			}			
		};
		
		projectText.addModifyListener(modifyListener);
		collectionText.addModifyListener(modifyListener);
		workItemsTypeText.addModifyListener(modifyListener);
		
		collectionText.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (getProjectPage() != null) {
					getProjectPage().setPageComplete();
				}
			}			
		});
		
		advancedProjectMappingOptionsEnabled = com.collabnet.ccf.tfs.Activator.getDefault().getPreferenceStore().getBoolean(com.collabnet.ccf.tfs.Activator.PREFERENCES_ADVANCED_PROJECT_MAPPING);
		if (!advancedProjectMappingOptionsEnabled) {
			projectBrowseButton.setVisible(false);
			workItemsTypeBrowseButton.setVisible(false);
		}
		
		return tfsGroup;
	}

	public void initializeComposite(SynchronizationStatus projectMapping, int type) {
		collectionText.setText(getDomain(projectMapping, type));
		projectText.setText(getProject(projectMapping, type));
		String requirementType = getRequirementType(projectMapping, type);
			workItemsTypeLabel.setVisible(true);
			workItemsTypeText.setVisible(true);
			workItemsTypeBrowseButton.setVisible(true);
			workItemsTypeText.setText(requirementType);
	}

	public boolean isPageComplete() {
		if (workItemsTypeText.isVisible() && workItemsTypeText.getText().contains("-")) {
			workItemsErrorLabel.setVisible(true);
			workItemsErrorMessageLabel.setVisible(true);
		}
		else {
			workItemsErrorLabel.setVisible(false);
			workItemsErrorMessageLabel.setVisible(false);
		}
		if (collectionText == null) {
			return false;
		}
		if (collectionText.getText().trim().length() == 0 ||
			projectText.getText().trim().length() == 0) {
			return false;
		}
		if (workItemsTypeText.isVisible()) {
			if (workItemsTypeText.getText().trim().length() == 0) {
				return false;
			}
			if (workItemsTypeText.getText().contains("-")) {
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
		
		TFSLayoutExtractor tfsLayoutExtractor = new TFSLayoutExtractor();
		Properties properties;
		if (landscape.getType2().equals("QT")) {
			properties = landscape.getProperties2();
		} else {
			properties = landscape.getProperties1();
		}
		String url = properties.getProperty(Activator.PROPERTIES_TFS_URL, "");
		String user = properties.getProperty(Activator.PROPERTIES_TFS_USER, "");
		String password = Activator.decodePassword(properties.getProperty(Activator.PROPERTIES_TFS_PASSWORD, ""));
		tfsLayoutExtractor.setServerUrl(url);
		tfsLayoutExtractor.setUserName(user);
		tfsLayoutExtractor.setPassword(password);
		
		boolean validDomainAndProject;
		try {
			tfsLayoutExtractor.validateTFSDomainAndProject(collectionText.getText().trim(), projectText.getText().trim());
			validDomainAndProject = true;
		} catch (Exception e) {
			validDomainAndProject = false;
		}
		if (!validDomainAndProject) {
			if (!showValidationQuestionDialog("Could not validate the supplied Quality Center Domain/Project.  Continue anyway?")) {
				return false;
			}
		}
		return true;
	}
	
	private String getDomain(SynchronizationStatus projectMapping, int type) {
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
	
	private String getProject(SynchronizationStatus projectMapping, int type) {
		String repositoryId;
		if (type == IMappingSection.TYPE_SOURCE) {
			repositoryId = projectMapping.getSourceRepositoryId();
		} else {
			repositoryId = projectMapping.getTargetRepositoryId();
		}
		int index = repositoryId.indexOf("-");
		if (index == -1) return "";
		else {
			String project = repositoryId = repositoryId.substring(index + 1);
			index = project.indexOf("-");
			if (index != -1) {
				project = project.substring(0, index);
			}
			return project;
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

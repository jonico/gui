package com.collabnet.ccf.dialogs;

import java.util.Properties;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.db.Filter;
import com.collabnet.ccf.db.Update;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.schemageneration.QCLayoutExtractor;

public class ChangeProjectMappingDialog extends CcfDialog {
	private SynchronizationStatus status;
	
	private Text trackerText;
	private Text ptProjectText;
	private Text ptIssueTypeText;
	private Text qcProjectText;
	private Text qcDomainText;
	private Text qcRequirementTypeText;
	
	private String oldXslFileName;
	private String newXslFileName;
	private String newGraphicalXslFileName;
	private String newSourceRepositorySchemaFileName;
	private String newTargetRepositorySchemaFileName;
	private String newGenericArtifactToSourceRepositorySchemaFileName;
	private String newGenericArtifactToTargetREpositorySchemaFileName;
	private String newSourceRepositorySchemaToGenericArtifactFileName;
	private String newTargetRepositorySchemaToGenericArtifactFileName;
	
	private Combo conflictResolutionCombo;
	
	private Button okButton;
	
	private boolean changeError;

	public ChangeProjectMappingDialog(Shell shell, SynchronizationStatus status) {
		super(shell, "ChangeProjectMappingDialog");
		this.status = status;
		oldXslFileName = status.getXslFileName();
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Change Project Mapping");
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Group qcGroup = new Group(composite, SWT.NULL);
		GridLayout qcLayout = new GridLayout();
		qcLayout.numColumns = 3;
		qcGroup.setLayout(qcLayout);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		qcGroup.setLayoutData(gd);	
		qcGroup.setText("Quality Center:");
		
		Label domainLabel = new Label(qcGroup, SWT.NONE);
		domainLabel.setText("Domain:");
		
		qcDomainText = new Text(qcGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		qcDomainText.setLayoutData(gd);	
		qcDomainText.setText(getQcDomain());
		
		new Label(qcGroup, SWT.NONE);
		
		Label projectLabel = new Label(qcGroup, SWT.NONE);
		projectLabel.setText("Project:");
		
		qcProjectText = new Text(qcGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		qcProjectText.setLayoutData(gd);
		qcProjectText.setText(getQcProject());
		
		new Label(qcGroup, SWT.NONE);
		
		String requirementType = getQcRequirementType();
		if (requirementType != null) {
			Label requirementTypeLabel = new Label(qcGroup, SWT.NONE);
			requirementTypeLabel.setText("Requirement type:");
			qcRequirementTypeText = new Text(qcGroup, SWT.BORDER);
			gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
			qcRequirementTypeText.setLayoutData(gd);
			qcRequirementTypeText.setText(requirementType);
			
			Button requirementTypeBrowseButton = new Button(qcGroup, SWT.PUSH);
			requirementTypeBrowseButton.setText("Browse...");
// TODO:  Implement requirement type selection.
			requirementTypeBrowseButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					MessageDialog.openInformation(getShell(), "Select Requirement Type", "Not yet implmented.");
				}			
			});
		}
		
		Group otherGroup = new Group(composite, SWT.NULL);
		GridLayout otherLayout = new GridLayout();
		otherLayout.numColumns = 3;
		otherGroup.setLayout(otherLayout);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		otherGroup.setLayoutData(gd);	
		if (status.getLandscape().getType1().equals(Landscape.TYPE_PT) || status.getLandscape().getType2().equals(Landscape.TYPE_PT)) {
			otherGroup.setText(Landscape.TYPE_DESCRIPTION_PT + ":");
			Label ptProjectLabel = new Label(otherGroup, SWT.NONE);
			ptProjectLabel.setText("Project:");			
			ptProjectText = new Text(otherGroup, SWT.BORDER);
			gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
			ptProjectText.setLayoutData(gd);
			ptProjectText.setText(getPtProject());
			new Label(otherGroup, SWT.NONE);
			
			Label ptIssueTypeLabel = new Label(otherGroup, SWT.NONE);
			ptIssueTypeLabel.setText("Artifact type:");			
			ptIssueTypeText = new Text(otherGroup, SWT.BORDER);
			gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
			ptIssueTypeText.setLayoutData(gd);
			ptIssueTypeText.setText(getPtIssueType());
			new Label(otherGroup, SWT.NONE);
		} else {
			otherGroup.setText(Landscape.TYPE_DESCRIPTION_TF + ":");
			Label trackerLabel = new Label(otherGroup, SWT.NONE);
			trackerLabel.setText("Tracker ID:");			
			trackerText = new Text(otherGroup, SWT.BORDER);
			gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
			trackerText.setLayoutData(gd);
			trackerText.setText(getTrackerId());
			
			Button teamForgeBrowseButton = new Button(otherGroup, SWT.PUSH);
			teamForgeBrowseButton.setText("Browse...");
			
			// TODO:  Implement project/tracker selection.
			teamForgeBrowseButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					MessageDialog.openInformation(getShell(), "Select Project/Tracker", "Not yet implmented.");
				}			
			});
		}

		Label conflictResolutionPriorityLabel = new Label(composite, SWT.NONE);
		conflictResolutionPriorityLabel.setText("Conflict resolution priority:");
		
		conflictResolutionCombo = new Combo(composite, SWT.READ_ONLY);
		conflictResolutionCombo.add(SynchronizationStatus.CONFLICT_RESOLUTION_DESCRIPTION_ALWAYS_IGNORE);
		conflictResolutionCombo.add(SynchronizationStatus.CONFLICT_RESOLUTION_DESCRIPTION_ALWAYS_OVERRIDE);
		conflictResolutionCombo.add(SynchronizationStatus.CONFLICT_RESOLUTION_DESCRIPTION_QUARANTINE_ARTIFACT);

		conflictResolutionCombo.setText(SynchronizationStatus.getConflictResolutionDescription(status.getConflictResolutionPriority()));
		
		conflictResolutionCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				okButton.setEnabled(canFinish());
			}			
		});
		
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				okButton.setEnabled(canFinish());
			}			
		};

		if (trackerText != null) trackerText.addModifyListener(modifyListener);
		if (ptProjectText != null) {
			ptProjectText.addModifyListener(modifyListener);
			ptIssueTypeText.addModifyListener(modifyListener);
		}
		qcProjectText.addModifyListener(modifyListener);
		qcDomainText.addModifyListener(modifyListener);
		if (qcRequirementTypeText != null) {
			qcRequirementTypeText.addModifyListener(modifyListener);
		}
		
		return composite;
	}
	
	@Override
	protected void okPressed() {
// TODO:  Validate
//		if (!validate()) return;
		changeError = false;
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					Landscape landscape = status.getLandscape();
					CcfDataProvider dataProvider = new CcfDataProvider();
					Filter sourceSystemFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_ID, status.getSourceSystemId(), true);
					Filter sourceRepositoryFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_REPOSITORY_ID, status.getSourceRepositoryId(), true);
					Filter targetSystemFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_TARGET_SYSTEM_ID, status.getTargetSystemId(), true);
					Filter targetRepositoryFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_TARGET_REPOSITORY_ID, status.getTargetRepositoryId(), true);
					Filter[] filters = { sourceSystemFilter, sourceRepositoryFilter, targetSystemFilter, targetRepositoryFilter };
					
					String sourceRepository;
					String targetRepository;
					
					if (status.getSourceSystemKind().startsWith(Landscape.TYPE_QC)) {
						if (status.getTargetSystemKind().startsWith(Landscape.TYPE_PT)) {
							targetRepository = ptProjectText.getText().trim() + ":" + ptIssueTypeText.getText().trim();
						} else {
							targetRepository = trackerText.getText().trim();
						}
						StringBuffer repository = new StringBuffer(qcDomainText.getText().trim() + "-" + qcProjectText.getText().trim());
						if (qcRequirementTypeText != null) {
							repository.append("-" + qcRequirementTypeText.getText().trim());
						}
						sourceRepository = repository.toString();
					} else {
						StringBuffer repository = new StringBuffer(qcDomainText.getText().trim() + "-" + qcProjectText.getText().trim());
						if (qcRequirementTypeText != null) {
							repository.append("-" + qcRequirementTypeText.getText().trim());
						}
						targetRepository = repository.toString();
						if (status.getSourceSystemKind().startsWith(Landscape.TYPE_PT)) {
							sourceRepository = ptProjectText.getText().trim() + ":" + ptIssueTypeText.getText().trim();
						} else {
							sourceRepository = trackerText.getText().trim();
						}
					}

					Update sourceRepositoryUpdate = new Update(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_REPOSITORY_ID, sourceRepository);
					Update targetRepositoryUpdate = new Update(CcfDataProvider.SYNCHRONIZATION_STATUS_TARGET_REPOSITORY_ID, targetRepository);
					Update conflictResolutionPriorityUpdate = new Update(CcfDataProvider.SYNCHRONIZATION_STATUS_CONFLICT_RESOLUTION_PRIORITY, SynchronizationStatus.CONFLICT_RESOLUTIONS[conflictResolutionCombo.getSelectionIndex()]);
					Update[] updates = { sourceRepositoryUpdate, targetRepositoryUpdate, conflictResolutionPriorityUpdate };						
					dataProvider.updateSynchronizationStatuses(landscape, updates, filters);

					status.setSourceRepositoryId(sourceRepository);
					status.setTargetRepositoryId(targetRepository);	
					newXslFileName = status.getXslFileName();
					newGraphicalXslFileName = status.getGraphicalXslFileName();
					newSourceRepositorySchemaFileName = status.getSourceRepositorySchemaFileName();
					newTargetRepositorySchemaFileName = status.getTargetRepositorySchemaFileName();
					newGenericArtifactToSourceRepositorySchemaFileName = status.getGenericArtifactToSourceRepositorySchemaFileName();
					newGenericArtifactToTargetREpositorySchemaFileName = status.getGenericArtifactToTargetRepositorySchemaFileName();
					newSourceRepositorySchemaToGenericArtifactFileName = status.getSourceRepositorySchemaToGenericArtifactFileName();
					newTargetRepositorySchemaToGenericArtifactFileName = status.getTargetRepositorySchemaToGenericArtifactFileName();
					
					if (status.usesGraphicalMapping() && !newXslFileName.equals(oldXslFileName)) {
						status.switchToGraphicalMapping();
						dataProvider.setFieldMappingMode(status);
					}
				
				} catch (Exception e) {
					Activator.handleError(e);
					changeError = true;
					MessageDialog.openError(getShell(), "Change Project Mapping", e.getMessage());
				}
			}			
		});
		if (changeError) return;
		super.okPressed();
	}
	
	public boolean isXslFileNameChanged() {
		return !newXslFileName.equals(oldXslFileName);
	}
	
	public String getOldXslFileName() {
		return oldXslFileName;
	}
	
	public String getNewXslFileName() {
		return newXslFileName;
	}
	
	public String getNewGraphicalXslFileName() {
		return newGraphicalXslFileName;
	}

	public String getNewSourceRepositorySchemaFileName() {
		return newSourceRepositorySchemaFileName;
	}

	public String getNewTargetRepositorySchemaFileName() {
		return newTargetRepositorySchemaFileName;
	}

	public String getNewGenericArtifactToSourceRepositorySchemaFileName() {
		return newGenericArtifactToSourceRepositorySchemaFileName;
	}

	public String getNewGenericArtifactToTargetRepositorySchemaFileName() {
		return newGenericArtifactToTargetREpositorySchemaFileName;
	}

	public String getNewSourceRepositorySchemaToGenericArtifactFileName() {
		return newSourceRepositorySchemaToGenericArtifactFileName;
	}

	public String getNewTargetRepositorySchemaToGenericArtifactFileName() {
		return newTargetRepositorySchemaToGenericArtifactFileName;
	}

	public void setNewXslFileName(String newXslFileName) {
		this.newXslFileName = newXslFileName;
	}
	
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        Button button = super.createButton(parent, id, label, defaultButton);
		if (id == IDialogConstants.OK_ID) {
			okButton = button;
			okButton.setEnabled(false);
		}
        return button;
    }

	private boolean canFinish() {
		boolean canFinish = (trackerText == null || trackerText.getText().trim().length() > 0) &&
		(ptProjectText == null || ptProjectText.getText().trim().length() > 0) &&
		(ptIssueTypeText == null || ptIssueTypeText.getText().trim().length() > 0) &&
		qcProjectText.getText().trim().length() > 0 &&
		qcDomainText.getText().trim().length() > 0;
		if (!canFinish) return false;
		if (qcRequirementTypeText != null && qcRequirementTypeText.getText().trim().length() == 0) return false;
		return true;
	}
	
	private String getTrackerId() {
		String trackerId;
		if (status.getSourceSystemKind().startsWith(Landscape.TYPE_QC)) {
			trackerId = status.getTargetRepositoryId();
		} else {
			trackerId = status.getSourceRepositoryId();
		}
		return trackerId;
	}
	
	private String getQcDomain() {
		String repositoryId;
		if (status.getSourceSystemKind().startsWith(Landscape.TYPE_QC)) {
			repositoryId = status.getSourceRepositoryId();
		} else {
			repositoryId = status.getTargetRepositoryId();
		}
		int index = repositoryId.indexOf("-");
		if (index == -1) return "";
		else return repositoryId.substring(0, index);
	}
	
	private String getQcProject() {
		String repositoryId;
		if (status.getSourceSystemKind().startsWith(Landscape.TYPE_QC)) {
			repositoryId = status.getSourceRepositoryId();
		} else {
			repositoryId = status.getTargetRepositoryId();
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
	
	private String getQcRequirementType() {
		String repositoryId;
		if (status.getSourceSystemKind().startsWith(Landscape.TYPE_QC)) {
			repositoryId = status.getSourceRepositoryId();
		} else {
			repositoryId = status.getTargetRepositoryId();
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
	
	private String getPtProject() {
		String repositoryId;
		if (status.getSourceSystemKind().startsWith(Landscape.TYPE_PT)) {
			repositoryId = status.getSourceRepositoryId();
		} else {
			repositoryId = status.getTargetRepositoryId();
		}
		int index = repositoryId.indexOf(":");
		if (index == -1) return "";
		else return repositoryId.substring(0, index);
	}
	
	private String getPtIssueType() {
		String repositoryId;
		if (status.getSourceSystemKind().startsWith(Landscape.TYPE_PT)) {
			repositoryId = status.getSourceRepositoryId();
		} else {
			repositoryId = status.getTargetRepositoryId();
		}
		int index = repositoryId.indexOf(":");
		if (index == -1) return "";
		else return repositoryId.substring(index + 1);
	}
	
	private boolean validate() {
		QCLayoutExtractor qcLayoutExtractor = new QCLayoutExtractor();
		Properties properties = status.getLandscape().getProperties1();
		String url = properties.getProperty(Activator.PROPERTIES_QC_URL, "");
		String user = properties.getProperty(Activator.PROPERTIES_QC_USER, "");
		String password = properties.getProperty(
				Activator.PROPERTIES_QC_PASSWORD, "");
		qcLayoutExtractor.setServerUrl(url);
		qcLayoutExtractor.setUserName(user);
		qcLayoutExtractor.setPassword(password);
		try {
			qcLayoutExtractor.validateQCDomainAndProject(qcDomainText.getText().trim(), qcProjectText.getText().trim());
		} catch (Exception e) {
			MessageDialog.openError(getShell(), "Change Project Mapping", "Invalid Quality Center Domain/Project entered.");
			return false;
		}
		return true;
	}

}

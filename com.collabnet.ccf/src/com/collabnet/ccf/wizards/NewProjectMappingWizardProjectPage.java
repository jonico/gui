package com.collabnet.ccf.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.dialogs.ProjectTrackerSelectionDialog;
import com.collabnet.ccf.dialogs.RequirementTypeSelectionDialog;
import com.collabnet.ccf.dialogs.TeamForgeSelectionDialog;
import com.collabnet.ccf.model.ProjectMappings;

public class NewProjectMappingWizardProjectPage extends WizardPage {
	private ProjectMappings projectMappings;
	
	protected Combo qcDomainCombo;
	protected Text qcProjectText;
	private Label qcRequirementTypeLabel;
	protected Text qcRequirementTypeText;
	private Label teamForgeLabel;
	protected Text teamForgeText;
	protected Text ptProjectText;
	protected Text ptIssueTypeText;
	
	private Button requirementTypeBrowseButton;
	private Button teamForgeBrowseButton;
	private Button ptProjectBrowseButton;
	private Button ptArtifactTypeBrowseButton;
	
	private boolean requirementsSelected = false;
	private boolean planningFoldersSelected = false;
	
	private NewProjectMappingWizard wizard;
	
	private IDialogSettings settings = Activator.getDefault().getDialogSettings();
	public static final String PREVIOUS_QC_DOMAIN = "NewProjectMappingDialog.previousDomain.";
	public static final String PREVIOUS_QC_DOMAIN_COUNT = "NewProjectMappingDialog.previousDomainCount";
	
	public NewProjectMappingWizardProjectPage(ProjectMappings projectMappings) {
		super("projectPage", "Mapping Details", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_PROJECT_MAPPING_WIZBAN));
		this.projectMappings = projectMappings;
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		wizard = (NewProjectMappingWizard)getWizard();
		
		Composite outerContainer = new Composite(parent,SWT.NONE);
		outerContainer.setLayout(new GridLayout());
		outerContainer.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		Group qcGroup = new Group(outerContainer, SWT.NULL);
		GridLayout qcLayout = new GridLayout();
		qcLayout.numColumns = 3;
		qcGroup.setLayout(qcLayout);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		qcGroup.setLayoutData(gd);	
		qcGroup.setText("Quality Center:");
		
		Label domainLabel = new Label(qcGroup, SWT.NONE);
		domainLabel.setText("Domain:");

		qcDomainCombo = new Combo(qcGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		qcDomainCombo.setLayoutData(gd);	
		
		String[] previousDomains = getPreviousDomains();
		for (String domain : previousDomains) {
			qcDomainCombo.add(domain);
		}
		if (previousDomains.length > 0) qcDomainCombo.setText(previousDomains[0]);
		
		new Label(qcGroup, SWT.NONE);
		
		Label projectLabel = new Label(qcGroup, SWT.NONE);
		projectLabel.setText("Project:");
		
		qcProjectText = new Text(qcGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		qcProjectText.setLayoutData(gd);	
		
		new Label(qcGroup, SWT.NONE);
		
		qcRequirementTypeLabel = new Label(qcGroup, SWT.NONE);
		qcRequirementTypeLabel.setText("Requirement type:");
		qcRequirementTypeLabel.setVisible(requirementsSelected);
		qcRequirementTypeText = new Text(qcGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		qcRequirementTypeText.setLayoutData(gd);	
		qcRequirementTypeText.setVisible(requirementsSelected);
		requirementTypeBrowseButton = new Button(qcGroup, SWT.PUSH);
		requirementTypeBrowseButton.setText("Browse...");
		requirementTypeBrowseButton.setEnabled(false);
		requirementTypeBrowseButton.setVisible("win32".equals(SWT.getPlatform()) && requirementsSelected);
		requirementTypeBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				NewProjectMappingWizard wizard = (NewProjectMappingWizard)getWizard();
				if (!wizard.validate()) {
					MessageDialog.openError(getShell(), "Select Requirement Type", "Invalid Quality Center Domain/Project entered.");
					return;
				}
				RequirementTypeSelectionDialog dialog = new RequirementTypeSelectionDialog(getShell(), wizard.getProjectMappings().getLandscape(), qcDomainCombo.getText().trim(), qcProjectText.getText().trim());
				if (dialog.open() == RequirementTypeSelectionDialog.OK) {
					qcRequirementTypeText.setText(dialog.getType());
					setPageComplete(canFinish());
				}
			}			
		});
		
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				setPageComplete(canFinish());
				if (ptArtifactTypeBrowseButton != null) {
					ptArtifactTypeBrowseButton.setEnabled(ptProjectText.getText().trim().length() > 0);
				}
			}			
		};
		
		qcProjectText.addModifyListener(modifyListener);
		qcDomainCombo.addModifyListener(modifyListener);
		qcRequirementTypeText.addModifyListener(modifyListener);
		
		qcDomainCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				setPageComplete(canFinish());
			}			
		});
		
		if (wizard.getType() == NewProjectMappingWizard.TYPE_TF) {
			Group tfGroup = new Group(outerContainer, SWT.NULL);
			GridLayout tfLayout = new GridLayout();
			tfLayout.numColumns = 3;
			tfGroup.setLayout(tfLayout);
			gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
			tfGroup.setLayoutData(gd);	
			tfGroup.setText("TeamForge:");
			
			teamForgeLabel = new Label(tfGroup, SWT.NONE);
			if (planningFoldersSelected) {
				teamForgeLabel.setText("Project ID: ");
			} else {
				teamForgeLabel.setText("Tracker ID:");
			}	
			teamForgeText = new Text(tfGroup, SWT.BORDER);
			gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
			teamForgeText.setLayoutData(gd);
			teamForgeBrowseButton = new Button(tfGroup, SWT.PUSH);
			teamForgeBrowseButton.setText("Browse...");
			teamForgeBrowseButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent se) {
					int type;
					if (planningFoldersSelected) type = TeamForgeSelectionDialog.BROWSER_TYPE_PROJECT;
					else type = TeamForgeSelectionDialog.BROWSER_TYPE_TRACKER;
					TeamForgeSelectionDialog dialog = new TeamForgeSelectionDialog(getShell(), projectMappings.getLandscape(), type);
					if (dialog.open() == TeamForgeSelectionDialog.OK) {
						teamForgeText.setText(dialog.getSelectedId());
					}
				}			
			});
			teamForgeText.addModifyListener(modifyListener);
		} else {
			Group ptGroup = new Group(outerContainer, SWT.NULL);
			GridLayout ptLayout = new GridLayout();
			ptLayout.numColumns = 3;
			ptGroup.setLayout(ptLayout);
			gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
			ptGroup.setLayoutData(gd);	
			ptGroup.setText("Project Tracker:");			
			
			Label ptProjectLabel = new Label(ptGroup, SWT.NONE);
			ptProjectLabel.setText("Project:");			
			ptProjectText = new Text(ptGroup, SWT.BORDER);
			gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
			ptProjectText.setLayoutData(gd);
			
			ptProjectBrowseButton = new Button(ptGroup, SWT.PUSH);
			ptProjectBrowseButton.setText("Browse...");
			ptProjectBrowseButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent se) {
					ProjectTrackerSelectionDialog dialog = new ProjectTrackerSelectionDialog(getShell(), projectMappings.getLandscape(), ProjectTrackerSelectionDialog.BROWSER_TYPE_PROJECT);
					if (dialog.open() == ProjectTrackerSelectionDialog.OK) {
						ptProjectText.setText(dialog.getProjectName());
						setPageComplete(canFinish());
					}
				}			
			});
			
			Label ptIssueTypeLabel = new Label(ptGroup, SWT.NONE);
			ptIssueTypeLabel.setText("Artifact type:");			
			ptIssueTypeText = new Text(ptGroup, SWT.BORDER);
			gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
			ptIssueTypeText.setLayoutData(gd);
			
			ptArtifactTypeBrowseButton = new Button(ptGroup, SWT.PUSH);
			ptArtifactTypeBrowseButton.setText("Browse...");
			ptArtifactTypeBrowseButton.setEnabled(false);
			ptArtifactTypeBrowseButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent se) {
					MessageDialog.openInformation(getShell(), "Select Artifact Type", "Not yet implemented.");
				}			
			});
			
			ptProjectText.addModifyListener(modifyListener);
			ptIssueTypeText.addModifyListener(modifyListener);
		}
					
		setMessage("Enter project mapping details");

		setControl(outerContainer);
	}
	
	public void setRequirementsSelected(boolean requirementsSelected) {
		this.requirementsSelected = requirementsSelected;
		if (qcRequirementTypeText != null) {
			qcRequirementTypeLabel.setVisible(requirementsSelected);
			qcRequirementTypeText.setVisible(requirementsSelected);
			requirementTypeBrowseButton.setVisible("win32".equals(SWT.getPlatform()) && requirementsSelected);
			setPageComplete(canFinish());
		}
	}
	
	public void setPlanningFoldersSelected(boolean planningFoldersSelected) {
		this.planningFoldersSelected = planningFoldersSelected;
		if (teamForgeLabel != null) {
			if (planningFoldersSelected) {
				teamForgeLabel.setText("Project ID: ");
			} else {
				teamForgeLabel.setText("Tracker ID:");
			}
		}
	}
	
	private boolean canFinish() {
		requirementTypeBrowseButton.setEnabled(qcDomainCombo.getText().trim().length() > 0 && qcProjectText.getText().trim().length() > 0 && requirementTypeBrowseButton.isVisible());
		if (qcDomainCombo.getText().trim().length() == 0 ||
			qcProjectText.getText().trim().length() == 0 ||
			(wizard.getType() == NewProjectMappingWizard.TYPE_PT && ptProjectText.getText().trim().length() == 0) ||
			(wizard.getType() == NewProjectMappingWizard.TYPE_PT && ptIssueTypeText.getText().trim().length() == 0) ||
			(wizard.getType() == NewProjectMappingWizard.TYPE_TF && teamForgeText.getText().trim().length() == 0)) {
			return false;
		}
		if (qcRequirementTypeText.isVisible() && qcRequirementTypeText.getText().trim().length() == 0) {
			return false;
		}
		if (wizard.getType() == NewProjectMappingWizard.TYPE_TF) {
			if (planningFoldersSelected) {
				if (!teamForgeText.getText().trim().startsWith("proj")) return false;
				if (teamForgeText.getText().trim().length() < 5) return false;
			} else {
				if (!teamForgeText.getText().trim().startsWith("tracker")) return false;
				if (teamForgeText.getText().trim().length() < 8) return false;
			}
		}
		return true;
	}
	
	private String[] getPreviousDomains() {
		List<String> domainList = new ArrayList<String>();
		int count = 0;
		try {
			count = settings.getInt(PREVIOUS_QC_DOMAIN_COUNT);
		} catch (Exception e) {}
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				String domain = settings.get(PREVIOUS_QC_DOMAIN + i);
				if (domain != null) domainList.add(domain);
			}
		}
		String[] domains = new String[domainList.size()];
		domainList.toArray(domains);
		return domains;
	}

}

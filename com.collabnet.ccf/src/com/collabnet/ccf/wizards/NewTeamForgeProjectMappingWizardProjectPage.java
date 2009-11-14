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
import com.collabnet.ccf.dialogs.TeamForgeSelectionDialog;
import com.collabnet.ccf.model.ProjectMappings;

public class NewTeamForgeProjectMappingWizardProjectPage extends WizardPage {
	private ProjectMappings projectMappings;
	
	protected Combo qcDomainCombo;
	protected Text qcProjectText;
	private Label qcRequirementTypeLabel;
	protected Text qcRequirementTypeText;
	private Label teamForgeLabel;
	protected Text teamForgeText;
	
	private Button requirementTypeBrowseButton;
	private Button teamForgeBrowseButton;
	
	private boolean requirementsSelected = false;
	private boolean planningFoldersSelected = false;
	
	private IDialogSettings settings = Activator.getDefault().getDialogSettings();
	public static final String PREVIOUS_QC_DOMAIN = "NewProjectMappingDialog.previousDomain.";
	public static final String PREVIOUS_QC_DOMAIN_COUNT = "NewProjectMappingDialog.previousDomainCount";
	
	public NewTeamForgeProjectMappingWizardProjectPage(ProjectMappings projectMappings) {
		super("projectPage", "Mapping Details", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_PROJECT_MAPPING_WIZBAN));
		this.projectMappings = projectMappings;
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
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
		requirementTypeBrowseButton.setVisible(requirementsSelected);
		// TODO:  Implement requirement type selection.
		requirementTypeBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				MessageDialog.openInformation(getShell(), "Select Requirement Type", "Not yet implmented.");
			}			
		});
		
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
		
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				setPageComplete(canFinish());
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
		
		teamForgeText.addModifyListener(modifyListener);
				
		setMessage("Enter project mapping details");

		setControl(outerContainer);
	}
	
	public void setRequirementsSelected(boolean requirementsSelected) {
		this.requirementsSelected = requirementsSelected;
		if (qcRequirementTypeText != null) {
			qcRequirementTypeLabel.setVisible(requirementsSelected);
			qcRequirementTypeText.setVisible(requirementsSelected);
			requirementTypeBrowseButton.setVisible(requirementsSelected);
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
		if (qcDomainCombo.getText().trim().length() == 0 ||
			qcProjectText.getText().trim().length() == 0 ||
			teamForgeText.getText().trim().length() == 0) {
			return false;
		}
		if (qcRequirementTypeText.isVisible() && qcRequirementTypeText.getText().trim().length() == 0) {
			return false;
		}
		if (planningFoldersSelected) {
			if (!teamForgeText.getText().trim().startsWith("proj")) return false;
			if (teamForgeText.getText().trim().length() < 5) return false;
		} else {
			if (!teamForgeText.getText().trim().startsWith("tracker")) return false;
			if (teamForgeText.getText().trim().length() < 8) return false;
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

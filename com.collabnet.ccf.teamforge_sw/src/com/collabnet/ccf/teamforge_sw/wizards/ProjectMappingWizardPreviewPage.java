package com.collabnet.ccf.teamforge_sw.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.SynchronizationStatus;

public class ProjectMappingWizardPreviewPage extends WizardPage {
	private String product;
	private String projectId;
	private String taskTracker;
	private String pbiTracker;
	private String trackerTaskMapping;
	private String trackerPbiMapping;
	private String planningFolderProductMapping;
	private String taskTrackerMapping;
	private String pbiTrackerMapping;
	private String productPlanningFolderMapping;
	
	private Text trackerTaskText;
	private Text trackerPbiText;
	private Text planningFolderProductText;
	private Text taskTrackerText;
	private Text pbiTrackerText;
	private Text productPlanningFolderText;
	
	private Combo trackerTaskCombo;
	private Combo trackerPbiCombo;
	private Combo planningFolderProductCombo;
	private Combo taskTrackerCombo;
	private Combo pbiTrackerCombo;
	private Combo productPlanningFolderCombo;
	
	private int selectionIndex;

	public ProjectMappingWizardPreviewPage() {
		super("previewPage", "Mappings Preview", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_PROJECT_MAPPING_WIZBAN));
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		Composite outerContainer = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		outerContainer.setLayout(layout);
		outerContainer.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		Label mappingLabel = new Label(outerContainer, SWT.NONE);
		mappingLabel.setText("Project Mapping:");
		
		Label conflictLabel = new Label(outerContainer, SWT.NONE);
		conflictLabel.setText("Conflict Resolution:");
		
		trackerTaskText = new Text(outerContainer, SWT.READ_ONLY | SWT.BORDER);
		trackerTaskText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		trackerTaskCombo = new Combo(outerContainer, SWT.READ_ONLY);
		populateConflictResolutionCombo(trackerTaskCombo);
		
		trackerPbiText = new Text(outerContainer, SWT.READ_ONLY | SWT.BORDER);
		trackerPbiText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		trackerPbiCombo = new Combo(outerContainer, SWT.READ_ONLY);
		populateConflictResolutionCombo(trackerPbiCombo);
		
		planningFolderProductText = new Text(outerContainer, SWT.READ_ONLY | SWT.BORDER);
		planningFolderProductText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		planningFolderProductCombo = new Combo(outerContainer, SWT.READ_ONLY);
		populateConflictResolutionCombo(planningFolderProductCombo);
		
		taskTrackerText = new Text(outerContainer, SWT.READ_ONLY | SWT.BORDER);
		taskTrackerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		taskTrackerCombo = new Combo(outerContainer, SWT.READ_ONLY);
		populateConflictResolutionCombo(taskTrackerCombo);
		
		pbiTrackerText = new Text(outerContainer, SWT.READ_ONLY | SWT.BORDER);
		pbiTrackerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		pbiTrackerCombo = new Combo(outerContainer, SWT.READ_ONLY);
		populateConflictResolutionCombo(pbiTrackerCombo);
		
		productPlanningFolderText = new Text(outerContainer, SWT.READ_ONLY | SWT.BORDER);
		productPlanningFolderText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		productPlanningFolderCombo = new Combo(outerContainer, SWT.READ_ONLY);
		populateConflictResolutionCombo(productPlanningFolderCombo);
		
		setMessage("The following mappings will be created.");

		setControl(outerContainer);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {		
			if (product == null || !product.equals(((ProjectMappingWizard)getWizard()).getSelectedProduct().getName()) ||
				projectId == null || !projectId.equals(((ProjectMappingWizard)getWizard()).getSelectedProject().getId()) ||
				taskTracker == null || !taskTracker.equals(((ProjectMappingWizard)getWizard()).getSelectedTaskTracker().getId()) ||
				pbiTracker == null || !pbiTracker.equals(((ProjectMappingWizard)getWizard()).getSelectedPbiTracker().getId())) {
				product = ((ProjectMappingWizard)getWizard()).getSelectedProduct().getName();
				projectId = ((ProjectMappingWizard)getWizard()).getSelectedProject().getId();
				taskTracker = ((ProjectMappingWizard)getWizard()).getSelectedTaskTracker().getId();
				pbiTracker = ((ProjectMappingWizard)getWizard()).getSelectedPbiTracker().getId();
				refreshMappings();
				setPageComplete(true);
			}
		}
	}
	
	public String getTrackerTaskMapping() {
		return trackerTaskMapping;
	}

	public String getTrackerPbiMapping() {
		return trackerPbiMapping;
	}

	public String getPlanningFolderProductMapping() {
		return planningFolderProductMapping;
	}

	public String getTaskTrackerMapping() {
		return taskTrackerMapping;
	}

	public String getPbiTrackerMapping() {
		return pbiTrackerMapping;
	}

	public String getProductPlanningFolderMapping() {
		return productPlanningFolderMapping;
	}

	public String getTrackerTaskConflictResolutionPriority() {
		return SynchronizationStatus.CONFLICT_RESOLUTIONS[getSelectionIndex(trackerTaskCombo)];
	}
	
	public String getTrackerPbiConflictResolutionPriority() {
		return SynchronizationStatus.CONFLICT_RESOLUTIONS[getSelectionIndex(trackerPbiCombo)];
	}
	
	public String getPlanningFolderProductConflictResolutionPriority() {
		return SynchronizationStatus.CONFLICT_RESOLUTIONS[getSelectionIndex(planningFolderProductCombo)];
	}
	
	public String getTaskTrackerConflictResolutionPriority() {
		return SynchronizationStatus.CONFLICT_RESOLUTIONS[getSelectionIndex(taskTrackerCombo)];
	}
	
	public String getPbiTrackerConflictResolutionPriority() {
		return SynchronizationStatus.CONFLICT_RESOLUTIONS[getSelectionIndex(pbiTrackerCombo)];
	}
	
	public String getProductPlanningFolderConflictResolutionPriority() {
		return SynchronizationStatus.CONFLICT_RESOLUTIONS[getSelectionIndex(productPlanningFolderCombo)];
	}
	
	private int getSelectionIndex(final Combo combo) {
		Display.getDefault().syncExec(new Runnable() {		
			public void run() {
				selectionIndex = combo.getSelectionIndex();
			}
		});
		return selectionIndex;
	}
	
	private void populateConflictResolutionCombo(Combo combo) {
		combo.add(SynchronizationStatus.CONFLICT_RESOLUTION_DESCRIPTION_ALWAYS_IGNORE);
		combo.add(SynchronizationStatus.CONFLICT_RESOLUTION_DESCRIPTION_ALWAYS_OVERRIDE);
		combo.add(SynchronizationStatus.CONFLICT_RESOLUTION_DESCRIPTION_QUARANTINE_ARTIFACT);
		combo.setText(SynchronizationStatus.CONFLICT_RESOLUTION_DESCRIPTION_ALWAYS_OVERRIDE);
	}
	
	private void refreshMappings() {
		ProjectMappingWizard wizard = (ProjectMappingWizard)getWizard();
		trackerTaskMapping = wizard.getSelectedTaskTracker().getId() + " => " + wizard.getSelectedProduct().getName() + "-Task";
		trackerPbiMapping = wizard.getSelectedPbiTracker().getId() + " => " + wizard.getSelectedProduct().getName() + "-PBI";
		planningFolderProductMapping = wizard.getSelectedProject().getId() + "-planningFolders => " + wizard.getSelectedProduct().getName() + "-Product";
		taskTrackerMapping = wizard.getSelectedProduct().getName() + "-Task => " + wizard.getSelectedTaskTracker().getId();
		pbiTrackerMapping = wizard.getSelectedProduct().getName() + "-PBI => " + wizard.getSelectedPbiTracker().getId();
		productPlanningFolderMapping = wizard.getSelectedProduct().getName() + "-Product => " + wizard.getSelectedProject().getId() + "-planningFolders";
		trackerTaskText.setText(trackerTaskMapping);
		trackerPbiText.setText(trackerPbiMapping);
		planningFolderProductText.setText(planningFolderProductMapping);
		taskTrackerText.setText(taskTrackerMapping);
		pbiTrackerText.setText(pbiTrackerMapping);
		productPlanningFolderText.setText(productPlanningFolderMapping);
	}

}

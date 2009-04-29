package com.collabnet.ccf.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;

public class AddProjectMappingDialog extends CcfDialog {
	private ProjectMappings projectMappings;
	
	private Button system1ToSystem2Button;
	private Button system2ToSystem1Button;
	private Text sourceRepositoryIdText;
	private Text targetRepositoryIdText;
	private Combo conflictResolutionCombo;
	
	private Button okButton;
	
	private boolean addError;
	
	private IDialogSettings settings = Activator.getDefault().getDialogSettings();
	private static final String PREVIOUS_CONFLICT_RESOLUTION_PRIORITY = "AddProjectMappingDialog.conflictResolutionPriority";
	
	public AddProjectMappingDialog(Shell shell, ProjectMappings projectMappings) {
		super(shell, "AddProjectMappingDialog");
		this.projectMappings = projectMappings;
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Add Project Mapping");
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group directionGroup = new Group(composite, SWT.NULL);
		GridLayout directionLayout = new GridLayout();
		directionLayout.numColumns = 1;
		directionGroup.setLayout(directionLayout);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		directionGroup.setLayoutData(gd);	
		directionGroup.setText("Direction:");

		system2ToSystem1Button = new Button(directionGroup, SWT.RADIO);
		system2ToSystem1Button.setText(projectMappings.getLandscape().getId2() + " => " + projectMappings.getLandscape().getId1());

		system1ToSystem2Button = new Button(directionGroup, SWT.RADIO);
		system1ToSystem2Button.setText(projectMappings.getLandscape().getId1() + " => " + projectMappings.getLandscape().getId2());
		
		system2ToSystem1Button.setSelection(true);
		
		Label sourceRepositoryIdLabel = new Label(composite, SWT.NONE);
		sourceRepositoryIdLabel.setText("Source repository ID:");
		
		sourceRepositoryIdText = new Text(composite, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		sourceRepositoryIdText.setLayoutData(gd);
		
		Label targetRepositoryIdLabel = new Label(composite, SWT.NONE);
		targetRepositoryIdLabel.setText("Target repository ID:");
		
		targetRepositoryIdText = new Text(composite, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		targetRepositoryIdText.setLayoutData(gd);
		
		Label conflictResolutionPriorityLabel = new Label(composite, SWT.NONE);
		conflictResolutionPriorityLabel.setText("Conflict resolution priority:");
		
		conflictResolutionCombo = new Combo(composite, SWT.READ_ONLY);
		conflictResolutionCombo.add(SynchronizationStatus.CONFLICT_RESOLUTION_ALWAYS_IGNORE);
		conflictResolutionCombo.add(SynchronizationStatus.CONFLICT_RESOLUTION_ALWAYS_OVERRIDE);
		conflictResolutionCombo.add(SynchronizationStatus.CONFLICT_RESOLUTION_QUARANTINE_ARTIFACT);
		
		String previousResolutionPriority = settings.get(PREVIOUS_CONFLICT_RESOLUTION_PRIORITY);
		if (previousResolutionPriority == null) conflictResolutionCombo.setText(SynchronizationStatus.CONFLICT_RESOLUTION_ALWAYS_IGNORE);
		else conflictResolutionCombo.setText(previousResolutionPriority);
		
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				okButton.setEnabled(canFinish());
			}			
		};
		
		sourceRepositoryIdText.addModifyListener(modifyListener);
		targetRepositoryIdText.addModifyListener(modifyListener);
		
		return composite;
	}
	
	@Override
	protected void okPressed() {
		addError = false;
		settings.put(PREVIOUS_CONFLICT_RESOLUTION_PRIORITY, conflictResolutionCombo.getText());
		final SynchronizationStatus status = new SynchronizationStatus();
		status.setSourceRepositoryId(sourceRepositoryIdText.getText().trim());
		status.setTargetRepositoryId(targetRepositoryIdText.getText().trim());
		status.setSourceRepositoryKind("DEFECT");
		status.setTargetRepositoryKind("TRACKER");
		status.setConflictResolutionPriority(conflictResolutionCombo.getText());
		if (system1ToSystem2Button.getSelection()) {
			status.setSourceSystemId(projectMappings.getLandscape().getId1());			
			status.setTargetSystemId(projectMappings.getLandscape().getId2());			
			status.setSourceSystemKind(projectMappings.getLandscape().getType1());			
			status.setTargetSystemKind(projectMappings.getLandscape().getType2());			
			status.setSourceSystemTimezone(projectMappings.getLandscape().getTimezone1());
			status.setTargetSystemTimezone(projectMappings.getLandscape().getTimezone2());
			if (projectMappings.getLandscape().getEncoding1() != null && projectMappings.getLandscape().getEncoding1().trim().length() > 0) {
				status.setSourceSystemEncoding(projectMappings.getLandscape().getEncoding1());
			}
			if (projectMappings.getLandscape().getEncoding2() != null && projectMappings.getLandscape().getEncoding2().trim().length() > 0) {
				status.setTargetSystemEncoding(projectMappings.getLandscape().getEncoding2());
			}
		}
		if (system2ToSystem1Button.getSelection()) {
			status.setSourceSystemId(projectMappings.getLandscape().getId2());
			status.setTargetSystemId(projectMappings.getLandscape().getId1());
			status.setSourceSystemKind(projectMappings.getLandscape().getType2());
			status.setTargetSystemKind(projectMappings.getLandscape().getType1());
			status.setSourceSystemTimezone(projectMappings.getLandscape().getTimezone2());
			status.setTargetSystemTimezone(projectMappings.getLandscape().getTimezone1());
			if (projectMappings.getLandscape().getEncoding2() != null && projectMappings.getLandscape().getEncoding2().trim().length() > 0) {
				status.setSourceSystemEncoding(projectMappings.getLandscape().getEncoding2());
			}
			if (projectMappings.getLandscape().getEncoding1() != null && projectMappings.getLandscape().getEncoding1().trim().length() > 0) {
				status.setTargetSystemEncoding(projectMappings.getLandscape().getEncoding1());
			}
		}
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					CcfDataProvider dataProvider = new CcfDataProvider();
					dataProvider.addSynchronizationStatus(projectMappings, status);
				} catch (Exception e) {
					Activator.handleError(e);
					addError = true;
				}
			}			
		});
		if (addError) return;
		super.okPressed();
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
		return sourceRepositoryIdText.getText().trim().length() > 0 &&
		targetRepositoryIdText.getText().trim().length() > 0;
	}

}

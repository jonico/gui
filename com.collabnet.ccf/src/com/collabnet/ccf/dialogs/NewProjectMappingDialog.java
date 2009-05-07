package com.collabnet.ccf.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
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
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;

public class NewProjectMappingDialog extends CcfDialog {
	private ProjectMappings projectMappings;
	
	private Button system1ToSystem2Button;
	private Button system2ToSystem1Button;
	private Button bothButton;
	
	private Text trackerText;
	private Text qcProjectText;
	private Text qcDomainText;

	private Label system1ToSystem2ConflictResolutionLabel;
	private Combo system1ToSystem2ConflictResolutionCombo;
	private Label system2ToSystem1ConflictResolutionLabel;
	private Combo system2ToSystem1ConflictResolutionCombo;
	
	private Button okButton;
	
	private boolean addError;
	
	private IDialogSettings settings = Activator.getDefault().getDialogSettings();
	private static final String PREVIOUS_DIRECTION = "NewProjectMappingDialog.direction";
	private static final String PREVIOUS_SYSTEM1_SYSTEM2_CONFLICT_RESOLUTION_PRIORITY = "NewProjectMappingDialog.conflictResolutionPriority12";
	private static final String PREVIOUS_SYSTEM2_SYSTEM1_CONFLICT_RESOLUTION_PRIORITY = "NewProjectMappingDialog.conflictResolutionPriority21";
	
	public NewProjectMappingDialog(Shell shell, ProjectMappings projectMappings) {
		super(shell, "NewProjectMappingDialog");
		this.projectMappings = projectMappings;
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("New Project Mapping");
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
		system2ToSystem1Button.setText(Landscape.getTypeDescription(projectMappings.getLandscape().getType2()) + " => " + Landscape.getTypeDescription(projectMappings.getLandscape().getType1()));

		system1ToSystem2Button = new Button(directionGroup, SWT.RADIO);
		system1ToSystem2Button.setText(Landscape.getTypeDescription(projectMappings.getLandscape().getType1()) + " => " + Landscape.getTypeDescription(projectMappings.getLandscape().getType2()));
		
		bothButton = new Button(directionGroup, SWT.RADIO);
		bothButton.setText("Create mappings for both directions");
		
		int direction = 0;
		try {
			direction = settings.getInt(PREVIOUS_DIRECTION);
		} catch (Exception e) {}
		switch (direction) {
		case 0:
			system2ToSystem1Button.setSelection(true);
			break;
		case 1:
			system1ToSystem2Button.setSelection(true);
			break;
		case 2:
			bothButton.setSelection(true);
			break;			
		default:
			system2ToSystem1Button.setSelection(true);
			break;
		}
		
		SelectionListener selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				if (system2ToSystem1Button.getSelection()) {
					settings.put(PREVIOUS_DIRECTION, 0);
				} else if (system1ToSystem2Button.getSelection()) {
					settings.put(PREVIOUS_DIRECTION, 1);
				} else if (bothButton.getSelection()) {
					settings.put(PREVIOUS_DIRECTION, 2);
				}
				setComboEnablement();
			}
	
		};
		
		system2ToSystem1Button.addSelectionListener(selectionListener);
		system1ToSystem2Button.addSelectionListener(selectionListener);
		bothButton.addSelectionListener(selectionListener);
		
		Group qcGroup = new Group(composite, SWT.NULL);
		GridLayout qcLayout = new GridLayout();
		qcLayout.numColumns = 2;
		qcGroup.setLayout(qcLayout);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		qcGroup.setLayoutData(gd);	
		qcGroup.setText("Quality Center:");
		
		Label domainLabel = new Label(qcGroup, SWT.NONE);
		domainLabel.setText("Domain:");
		
		qcDomainText = new Text(qcGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		qcDomainText.setLayoutData(gd);	
		
		Label projectLabel = new Label(qcGroup, SWT.NONE);
		projectLabel.setText("Project:");
		
		qcProjectText = new Text(qcGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		qcProjectText.setLayoutData(gd);
		
		Group otherGroup = new Group(composite, SWT.NULL);
		GridLayout otherLayout = new GridLayout();
		otherLayout.numColumns = 2;
		otherGroup.setLayout(otherLayout);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		otherGroup.setLayoutData(gd);	
		if (projectMappings.getLandscape().getType1().equals(Landscape.TYPE_PT) || projectMappings.getLandscape().getType2().equals(Landscape.TYPE_PT)) {
			otherGroup.setText(Landscape.TYPE_DESCRIPTION_PT + ":");
		} else {
			otherGroup.setText(Landscape.TYPE_DESCRIPTION_TF + ":");
		}
		
		Label trackerLabel = new Label(otherGroup, SWT.NONE);
		trackerLabel.setText("Tracker ID:");
		
		trackerText = new Text(otherGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		trackerText.setLayoutData(gd);
		
		Group conflictGroup = new Group(composite, SWT.NULL);
		GridLayout conflictLayout = new GridLayout();
		conflictLayout.numColumns = 2;
		conflictGroup.setLayout(conflictLayout);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		conflictGroup.setLayoutData(gd);	
		conflictGroup.setText("Conflict resolution priority:");
		
		system2ToSystem1ConflictResolutionLabel = new Label(conflictGroup, SWT.NONE);
		system2ToSystem1ConflictResolutionLabel.setText(system2ToSystem1Button.getText());
		system2ToSystem1ConflictResolutionCombo = new Combo(conflictGroup, SWT.READ_ONLY);
		system2ToSystem1ConflictResolutionCombo.add(SynchronizationStatus.CONFLICT_RESOLUTION_ALWAYS_IGNORE);
		system2ToSystem1ConflictResolutionCombo.add(SynchronizationStatus.CONFLICT_RESOLUTION_ALWAYS_OVERRIDE);
		system2ToSystem1ConflictResolutionCombo.add(SynchronizationStatus.CONFLICT_RESOLUTION_QUARANTINE_ARTIFACT);
		
		String previousResolutionPriority = settings.get(PREVIOUS_SYSTEM2_SYSTEM1_CONFLICT_RESOLUTION_PRIORITY);
		if (previousResolutionPriority == null) system2ToSystem1ConflictResolutionCombo.setText(SynchronizationStatus.CONFLICT_RESOLUTION_ALWAYS_IGNORE);
		else system2ToSystem1ConflictResolutionCombo.setText(previousResolutionPriority);
		
		system1ToSystem2ConflictResolutionLabel = new Label(conflictGroup, SWT.NONE);
		system1ToSystem2ConflictResolutionLabel.setText(system1ToSystem2Button.getText());
		system1ToSystem2ConflictResolutionCombo = new Combo(conflictGroup, SWT.READ_ONLY);
		system1ToSystem2ConflictResolutionCombo.add(SynchronizationStatus.CONFLICT_RESOLUTION_ALWAYS_IGNORE);
		system1ToSystem2ConflictResolutionCombo.add(SynchronizationStatus.CONFLICT_RESOLUTION_ALWAYS_OVERRIDE);
		system1ToSystem2ConflictResolutionCombo.add(SynchronizationStatus.CONFLICT_RESOLUTION_QUARANTINE_ARTIFACT);

		previousResolutionPriority = settings.get(PREVIOUS_SYSTEM1_SYSTEM2_CONFLICT_RESOLUTION_PRIORITY);
		if (previousResolutionPriority == null) system1ToSystem2ConflictResolutionCombo.setText(SynchronizationStatus.CONFLICT_RESOLUTION_ALWAYS_IGNORE);
		else system1ToSystem2ConflictResolutionCombo.setText(previousResolutionPriority);

		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				okButton.setEnabled(canFinish());
			}			
		};
		
		trackerText.addModifyListener(modifyListener);
		qcProjectText.addModifyListener(modifyListener);
		qcDomainText.addModifyListener(modifyListener);
		
		setComboEnablement();
		
		return composite;
	}
	
	@Override
	protected void okPressed() {
		addError = false;
		final SynchronizationStatus status = new SynchronizationStatus();
		
		if (system1ToSystem2Button.getSelection() || bothButton.getSelection()) {
			settings.put(PREVIOUS_SYSTEM1_SYSTEM2_CONFLICT_RESOLUTION_PRIORITY, system1ToSystem2ConflictResolutionCombo.getText());
			status.setConflictResolutionPriority(system1ToSystem2ConflictResolutionCombo.getText());
			status.setSourceRepositoryId(qcDomainText.getText().trim() + "-" + qcProjectText.getText().trim());
			status.setTargetRepositoryId(trackerText.getText().trim());
			
			status.setSourceSystemId(projectMappings.getLandscape().getId1());			
			status.setTargetSystemId(projectMappings.getLandscape().getId2());			
			status.setSourceSystemKind(projectMappings.getLandscape().getType1());			
			status.setTargetSystemKind(projectMappings.getLandscape().getType2());			
			status.setSourceRepositoryKind("DEFECT");
			status.setTargetRepositoryKind("TRACKER");			
			status.setSourceSystemTimezone(projectMappings.getLandscape().getTimezone1());
			status.setTargetSystemTimezone(projectMappings.getLandscape().getTimezone2());
			if (projectMappings.getLandscape().getEncoding1() != null && projectMappings.getLandscape().getEncoding1().trim().length() > 0) {
				status.setSourceSystemEncoding(projectMappings.getLandscape().getEncoding1());
			}
			if (projectMappings.getLandscape().getEncoding2() != null && projectMappings.getLandscape().getEncoding2().trim().length() > 0) {
				status.setTargetSystemEncoding(projectMappings.getLandscape().getEncoding2());
			}
			createMapping(status);
			if (addError) return;
		}
		if (system2ToSystem1Button.getSelection() || bothButton.getSelection()) {
			settings.put(PREVIOUS_SYSTEM2_SYSTEM1_CONFLICT_RESOLUTION_PRIORITY, system2ToSystem1ConflictResolutionCombo.getText());
			status.setConflictResolutionPriority(system2ToSystem1ConflictResolutionCombo.getText());
			status.setTargetRepositoryId(qcDomainText.getText().trim() + "-" + qcProjectText.getText().trim());
			status.setSourceRepositoryId(trackerText.getText().trim());	
			
			status.setSourceSystemId(projectMappings.getLandscape().getId2());
			status.setTargetSystemId(projectMappings.getLandscape().getId1());
			status.setSourceSystemKind(projectMappings.getLandscape().getType2());
			status.setTargetSystemKind(projectMappings.getLandscape().getType1());
			status.setSourceRepositoryKind("TRACKER");
			status.setTargetRepositoryKind("DEFECT");
			status.setSourceSystemTimezone(projectMappings.getLandscape().getTimezone2());
			status.setTargetSystemTimezone(projectMappings.getLandscape().getTimezone1());
			if (projectMappings.getLandscape().getEncoding2() != null && projectMappings.getLandscape().getEncoding2().trim().length() > 0) {
				status.setSourceSystemEncoding(projectMappings.getLandscape().getEncoding2());
			}
			if (projectMappings.getLandscape().getEncoding1() != null && projectMappings.getLandscape().getEncoding1().trim().length() > 0) {
				status.setTargetSystemEncoding(projectMappings.getLandscape().getEncoding1());
			}
			createMapping(status);
		}
		if (addError) return;
		super.okPressed();
	}

	private void createMapping(final SynchronizationStatus status) {
		status.setSourceSystemKind(status.getSourceSystemKind() + "_paused");
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					CcfDataProvider dataProvider = new CcfDataProvider();
					dataProvider.addSynchronizationStatus(projectMappings, status);
				} catch (Exception e) {
					Activator.handleError(e);
					addError = true;
					MessageDialog.openError(getShell(), "New Project Mapping", e.getMessage());
				}
			}			
		});
	}
	
	private void setComboEnablement() {
		system2ToSystem1ConflictResolutionLabel.setEnabled(system2ToSystem1Button.getSelection() || bothButton.getSelection());
		system2ToSystem1ConflictResolutionCombo.setEnabled(system2ToSystem1Button.getSelection() || bothButton.getSelection());
		system1ToSystem2ConflictResolutionLabel.setEnabled(system1ToSystem2Button.getSelection() || bothButton.getSelection());
		system1ToSystem2ConflictResolutionCombo.setEnabled(system1ToSystem2Button.getSelection() || bothButton.getSelection());
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
		return trackerText.getText().trim().length() > 0 &&
		qcProjectText.getText().trim().length() > 0 &&
		qcDomainText.getText().trim().length() > 0;
	}

}

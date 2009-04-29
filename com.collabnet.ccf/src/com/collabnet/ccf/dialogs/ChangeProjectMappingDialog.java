package com.collabnet.ccf.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.db.Filter;
import com.collabnet.ccf.db.Update;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;

public class ChangeProjectMappingDialog extends CcfDialog {
	private SynchronizationStatus status;
	
	private Text sourceRepositoryIdText;
	private Text targetRepositoryIdText;
	private Combo conflictResolutionCombo;
	
	private Button okButton;
	
	private boolean changeError;

	public ChangeProjectMappingDialog(Shell shell, SynchronizationStatus status) {
		super(shell, "ChangeProjectMappingDialog");
		this.status = status;
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Change Project Mapping");
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label sourceRepositoryIdLabel = new Label(composite, SWT.NONE);
		sourceRepositoryIdLabel.setText("Source repository ID:");
		
		sourceRepositoryIdText = new Text(composite, SWT.BORDER);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		sourceRepositoryIdText.setLayoutData(gd);
		
		sourceRepositoryIdText.setText(status.getSourceRepositoryId());
		
		Label targetRepositoryIdLabel = new Label(composite, SWT.NONE);
		targetRepositoryIdLabel.setText("Target repository ID:");
		
		targetRepositoryIdText = new Text(composite, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		targetRepositoryIdText.setLayoutData(gd);
		
		targetRepositoryIdText.setText(status.getTargetRepositoryId());
		
		Label conflictResolutionPriorityLabel = new Label(composite, SWT.NONE);
		conflictResolutionPriorityLabel.setText("Conflict resolution priority:");
		
		conflictResolutionCombo = new Combo(composite, SWT.READ_ONLY);
		conflictResolutionCombo.add(SynchronizationStatus.CONFLICT_RESOLUTION_ALWAYS_IGNORE);
		conflictResolutionCombo.add(SynchronizationStatus.CONFLICT_RESOLUTION_ALWAYS_OVERRIDE);
		conflictResolutionCombo.add(SynchronizationStatus.CONFLICT_RESOLUTION_QUARANTINE_ARTIFACT);

		conflictResolutionCombo.setText(status.getConflictResolutionPriority());
		
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
		changeError = false;
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					Landscape landscape = status.getProjectMappings().getLandscape();
					CcfDataProvider dataProvider = new CcfDataProvider();
					Filter sourceSystemFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_ID, status.getSourceSystemId(), true);
					Filter sourceRepositoryFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_REPOSITORY_ID, status.getSourceRepositoryId(), true);
					Filter targetSystemFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_TARGET_SYSTEM_ID, status.getTargetSystemId(), true);
					Filter targetRepositoryFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_TARGET_REPOSITORY_ID, status.getTargetRepositoryId(), true);
					Filter[] filters = { sourceSystemFilter, sourceRepositoryFilter, targetSystemFilter, targetRepositoryFilter };
					Update sourceRepositoryUpdate = new Update(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_REPOSITORY_ID, sourceRepositoryIdText.getText().trim());
					Update targetRepositoryUpdate = new Update(CcfDataProvider.SYNCHRONIZATION_STATUS_TARGET_REPOSITORY_ID, targetRepositoryIdText.getText().trim());
					Update conflictResolutionPriorityUpdate = new Update(CcfDataProvider.SYNCHRONIZATION_STATUS_CONFLICT_RESOLUTION_PRIORITY, conflictResolutionCombo.getText().trim());
					Update[] updates = { sourceRepositoryUpdate, targetRepositoryUpdate, conflictResolutionPriorityUpdate };						
					dataProvider.updateSynchronizationStatuses(landscape, updates, filters);
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

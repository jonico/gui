package com.collabnet.ccf.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.ICcfParticipant;
import com.collabnet.ccf.IMappingSection;
import com.collabnet.ccf.IPageCompleteListener;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.db.Filter;
import com.collabnet.ccf.db.Update;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;

public class ChangeProjectMappingDialog extends CcfDialog implements IPageCompleteListener {
	private SynchronizationStatus status;
	private ICcfParticipant ccfParticipant1;
	private ICcfParticipant ccfParticipant2;
	private IMappingSection mappingSection1;
	private IMappingSection mappingSection2;

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
		getCcfParticipants();
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Change Project Mapping");
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		if (ccfParticipant1 != null) {
			mappingSection1 = ccfParticipant1.getMappingSection(1);
			if (mappingSection1 != null) {
				mappingSection1.getComposite(composite, status.getLandscape());
				mappingSection1.initializeComposite(status, IMappingSection.TYPE_SOURCE);
				mappingSection1.setProjectPage(this);
			}
		}
		
		if (ccfParticipant2 != null) {
			mappingSection2 = ccfParticipant2.getMappingSection(2);
			if (mappingSection2 != null) {
				mappingSection2.getComposite(composite, status.getLandscape());
				mappingSection2.initializeComposite(status, IMappingSection.TYPE_TARGET);
				mappingSection2.setProjectPage(this);
			}
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

		return composite;
	}
	
	@Override
	protected void okPressed() {
		if (!validate()) {
			return;
		}
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

					mappingSection1.updateSourceFields(status);
					mappingSection2.updateTargetFields(status);
					
					String sourceRepository = status.getSourceRepositoryId();
					String targetRepository = status.getTargetRepositoryId();

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
					ExceptionDetailsErrorDialog.openError(getShell(), "Change Project Mapping", e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e));
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
		if (mappingSection1 != null && !mappingSection1.isPageComplete()) {
			return false;
		}
		if (mappingSection2 != null && !mappingSection2.isPageComplete()) {
			return false;
		}
		return true;
	}
	
	private boolean validate() {
		if (mappingSection1 != null && !mappingSection1.validate(status.getLandscape())) {
			return false;
		}
		if (mappingSection2 != null && !mappingSection2.validate(status.getLandscape())) {
			return false;
		}		
		return true;
	}
	
	private void getCcfParticipants() {
		try {
			ccfParticipant1 = Activator.getCcfParticipantForType(status.getSourceSystemKind());
			ccfParticipant2 = Activator.getCcfParticipantForType(status.getTargetSystemKind());
		} catch (Exception e) {
			Activator.handleError(e);
		}
	}
	
	public void setPageComplete() {
		okButton.setEnabled(canFinish());
	}

}

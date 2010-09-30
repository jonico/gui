package com.collabnet.ccf.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionDelegate;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.db.Filter;
import com.collabnet.ccf.db.Update;
import com.collabnet.ccf.dialogs.ChangeProjectMappingDialog;
import com.collabnet.ccf.dialogs.ProjectMappingRenameDialog;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;

public class ChangeSynchronizationStatusAction extends ActionDelegate {
	private IStructuredSelection fSelection;

	@SuppressWarnings("unchecked")
	public void run(IAction action) {
		List<ProjectMappings> projectMappingsList = new ArrayList<ProjectMappings>();
		boolean mappingsChanged = false;
		Iterator iter = fSelection.iterator();
		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof SynchronizationStatus) {
				final SynchronizationStatus status = (SynchronizationStatus)object;

				File xslFile = status.getXslFile();
				File graphicalXslFile = status.getGraphicalXslFile();
				File sourceRepositorySchemaFile = status.getSourceRepositorySchemaFile();
				File targetRepositorySchemaFile = status.getTargetRepositorySchemaFile();
				File genericArtifactToSourceRepositorySchemaFile = status.getGenericArtifactToSourceRepositorySchemaFile();
				File genericArtifactToTargetRepositorySchemaFile = status.getGenericArtifactToTargetRepositorySchemaFile();
				File sourceRepositorySchemaToGenericArtifactFile = status.getSourceRepositorySchemaToGenericArtifactFile();
				File targetRepositorySchemaToGenericArtifactFile = status.getTargetRepositorySchemaToGenericArtifactFile();
				File mfdFile = status.getMappingFile(status.getMFDFileName());
				
				final String oldSourceRepositoryId = status.getSourceRepositoryId();
				final String oldTargetRepositoryId = status.getTargetRepositoryId();
				ChangeProjectMappingDialog dialog = new ChangeProjectMappingDialog(Display.getDefault().getActiveShell(), status);
				if (dialog.open() == ChangeProjectMappingDialog.CANCEL) return;
				if (!projectMappingsList.contains(status.getProjectMappings())) {
					projectMappingsList.add(status.getProjectMappings());
				}

				if (dialog.isXslFileNameChanged()) {
					File newXslFile = new File(xslFile.getParentFile(), dialog.getNewXslFileName());
					File newGraphicalXslFile = new File(graphicalXslFile.getParentFile(), dialog.getNewGraphicalXslFileName());
					File newSourceRepositorySchemaFile = new File(sourceRepositorySchemaFile.getParentFile(), dialog.getNewSourceRepositorySchemaFileName());
					File newTargetRepositorySchemaFile = new File(targetRepositorySchemaFile.getParentFile(), dialog.getNewTargetRepositorySchemaFileName());
					File newGenericArtifactToSourceRepositorySchemaFile = new File(genericArtifactToSourceRepositorySchemaFile.getParentFile(), dialog.getNewGenericArtifactToSourceRepositorySchemaFileName());
					File newGenericArtifactToTargetRepositorySchemaFile = new File(genericArtifactToTargetRepositorySchemaFile.getParentFile(), dialog.getNewGenericArtifactToTargetRepositorySchemaFileName());
					File newSourceRepositorySchemaToGenericArtifactFile = new File(sourceRepositorySchemaToGenericArtifactFile.getParentFile(), dialog.getNewSourceRepositorySchemaToGenericArtifactFileName());
					File newTargetRepositorySchemaToGenericArtifactFile = new File(targetRepositorySchemaToGenericArtifactFile.getParentFile(), dialog.getNewTargetRepositorySchemaToGenericArtifactFileName());
					File newMfdFile = new File(mfdFile.getParentFile(), dialog.getNewMfdFileName());
	
					if ((xslFile != null && xslFile.exists() && !newXslFile.exists()) ||
						(graphicalXslFile != null && graphicalXslFile.exists() && !newGraphicalXslFile.exists()) ||
						(mfdFile != null && mfdFile.exists() && !newMfdFile.exists()) ||
						(sourceRepositorySchemaFile != null && sourceRepositorySchemaFile.exists() && !newSourceRepositorySchemaFile.exists()) ||
						(targetRepositorySchemaFile != null && targetRepositorySchemaFile.exists() && !newTargetRepositorySchemaFile.exists()) ||
						(genericArtifactToSourceRepositorySchemaFile != null && genericArtifactToSourceRepositorySchemaFile.exists() && !newGenericArtifactToSourceRepositorySchemaFile.exists()) ||					
						(genericArtifactToTargetRepositorySchemaFile != null && genericArtifactToTargetRepositorySchemaFile.exists() && !newGenericArtifactToTargetRepositorySchemaFile.exists()) ||					
						(sourceRepositorySchemaToGenericArtifactFile != null && sourceRepositorySchemaToGenericArtifactFile.exists() && !newSourceRepositorySchemaToGenericArtifactFile.exists()) ||			
						(targetRepositorySchemaToGenericArtifactFile != null && targetRepositorySchemaToGenericArtifactFile.exists() && !newTargetRepositorySchemaToGenericArtifactFile.exists())) {		
						ProjectMappingRenameDialog renameDialog = new ProjectMappingRenameDialog(Display.getDefault().getActiveShell(), true, true);
						renameDialog.open();
						if (renameDialog.isRenameFiles()) {
							if (xslFile != null && xslFile.exists() && !newXslFile.exists()) {
								xslFile.renameTo(newXslFile);
							}
							if (graphicalXslFile != null && graphicalXslFile.exists() && !newGraphicalXslFile.exists()) {
								graphicalXslFile.renameTo(newGraphicalXslFile);
							}
							if (mfdFile != null && mfdFile.exists() && !newMfdFile.exists()) {
								mfdFile.renameTo(newMfdFile);
							}
							if (sourceRepositorySchemaFile != null && sourceRepositorySchemaFile.exists() && !newSourceRepositorySchemaFile.exists()) {
								sourceRepositorySchemaFile.renameTo(newSourceRepositorySchemaFile);
							}
							if (targetRepositorySchemaFile != null && targetRepositorySchemaFile.exists() && !newTargetRepositorySchemaFile.exists()) {
								targetRepositorySchemaFile.renameTo(newTargetRepositorySchemaFile);
							}
							if (genericArtifactToSourceRepositorySchemaFile != null && genericArtifactToSourceRepositorySchemaFile.exists() && !newGenericArtifactToSourceRepositorySchemaFile.exists()) {
								genericArtifactToSourceRepositorySchemaFile.renameTo(newGenericArtifactToSourceRepositorySchemaFile);
							}	
							if (genericArtifactToTargetRepositorySchemaFile != null && genericArtifactToTargetRepositorySchemaFile.exists() && !newGenericArtifactToTargetRepositorySchemaFile.exists()) {
								genericArtifactToTargetRepositorySchemaFile.renameTo(newGenericArtifactToTargetRepositorySchemaFile);
							}	
							if (sourceRepositorySchemaToGenericArtifactFile != null && sourceRepositorySchemaToGenericArtifactFile.exists() && !newSourceRepositorySchemaToGenericArtifactFile.exists()) {
								sourceRepositorySchemaToGenericArtifactFile.renameTo(newSourceRepositorySchemaToGenericArtifactFile);
							}	
							if (targetRepositorySchemaToGenericArtifactFile != null && targetRepositorySchemaToGenericArtifactFile.exists() && !newTargetRepositorySchemaToGenericArtifactFile.exists()) {
								targetRepositorySchemaToGenericArtifactFile.renameTo(newTargetRepositorySchemaToGenericArtifactFile);
							}								
						}	
						if (renameDialog.isUpdateDatabase()) {
							BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
								public void run() {
									CcfDataProvider dataProvider = new CcfDataProvider();
									try {										
										updateIdentityMappings(status,
												oldSourceRepositoryId,
												oldTargetRepositoryId,
												dataProvider);
									} catch (Exception e) {
										Activator.handleDatabaseError(e, false, true, "Update Identity Mappings");
									}
									try {										
										updateHospital(status,
												oldSourceRepositoryId,
												oldTargetRepositoryId,
												dataProvider);
									} catch (Exception e) {
										Activator.handleDatabaseError(e, false, true, "Update Hospital");
									}
								}								
							});
						}
					}
				}
				mappingsChanged = true;
			}
		}
		if (mappingsChanged) {
			for (ProjectMappings projectMappings: projectMappingsList) {
				Activator.notifyChanged(projectMappings);
			}
		}
	}
	
	public void selectionChanged(IAction action, ISelection sel) {
		if (sel instanceof IStructuredSelection) {
			fSelection= (IStructuredSelection) sel;
		}
		if (action != null) {
			boolean paused = false;
			if (fSelection != null && fSelection.getFirstElement() instanceof SynchronizationStatus) {
				paused = ((SynchronizationStatus)fSelection.getFirstElement()).isPaused();
			}
			action.setEnabled(Activator.getDefault().getActiveRole().isChangeProjectMapping() && paused);
		}
	}

	private void updateIdentityMappings(final SynchronizationStatus status,
			String oldSourceRepositoryId, final String oldTargetRepositoryId, CcfDataProvider dataProvider)
			throws Exception {
		if (!oldSourceRepositoryId.equals(status.getSourceRepositoryId())) {
			Filter sourceSystemFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_SYSTEM_ID, status.getSourceSystemId(), true);
			Filter sourceRepositoryFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_ID, oldSourceRepositoryId, true);
			Filter targetSystemFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_TARGET_SYSTEM_ID, status.getTargetSystemId(), true);
			Filter targetRepositoryFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_ID, oldTargetRepositoryId, true);
			Filter[] filters = { sourceSystemFilter, sourceRepositoryFilter, targetSystemFilter, targetRepositoryFilter };										
			Update sourceRepositoryUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_ID, status.getSourceRepositoryId());
			Update sourceRepositoryKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_KIND, status.getSourceRepositoryKind());
			Update[] updates = { sourceRepositoryUpdate, sourceRepositoryKindUpdate };
			dataProvider.updateIdentityMappings(status.getLandscape(), updates, filters);
			// Update reverse
			sourceSystemFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_SYSTEM_ID, status.getTargetSystemId(), true);
			sourceRepositoryFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_ID, oldTargetRepositoryId, true);
			targetSystemFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_TARGET_SYSTEM_ID, status.getSourceSystemId(), true);
			targetRepositoryFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_ID, oldSourceRepositoryId, true);
			Filter[] reverseFilters = { sourceSystemFilter, sourceRepositoryFilter, targetSystemFilter, targetRepositoryFilter };										
			Update targetRepositoryUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_ID, status.getSourceRepositoryId());
			Update targetRepositoryKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_KIND, status.getSourceRepositoryKind());
			Update[] reverseUpdates = { targetRepositoryUpdate, targetRepositoryKindUpdate };
			dataProvider.updateIdentityMappings(status.getLandscape(), reverseUpdates, reverseFilters);
			oldSourceRepositoryId = status.getSourceRepositoryId();
		}
		if (!oldTargetRepositoryId.equals(status.getTargetRepositoryId())) {
			Filter sourceSystemFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_SYSTEM_ID, status.getSourceSystemId(), true);
			Filter sourceRepositoryFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_ID, oldSourceRepositoryId, true);
			Filter targetSystemFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_TARGET_SYSTEM_ID, status.getTargetSystemId(), true);
			Filter targetRepositoryFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_ID, oldTargetRepositoryId, true);
			Filter[] filters = { sourceSystemFilter, sourceRepositoryFilter, targetSystemFilter, targetRepositoryFilter };										
			Update targetRepositoryUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_ID, status.getTargetRepositoryId());
			Update targetRepositoryKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_KIND, status.getTargetRepositoryKind());
			Update[] updates = { targetRepositoryUpdate, targetRepositoryKindUpdate };
			dataProvider.updateIdentityMappings(status.getLandscape(), updates, filters);
			// Update reverse
			sourceSystemFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_SYSTEM_ID, status.getTargetSystemId(), true);
			sourceRepositoryFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_ID, oldTargetRepositoryId, true);
			targetSystemFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_TARGET_SYSTEM_ID, status.getSourceSystemId(), true);
			targetRepositoryFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_ID, oldSourceRepositoryId, true);
			Filter[] reverseFilters = { sourceSystemFilter, sourceRepositoryFilter, targetSystemFilter, targetRepositoryFilter };										
			Update sourceRepositoryUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_ID, status.getTargetRepositoryId());
			Update sourceRepositoryKindUpdate = new Update(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_KIND, status.getTargetRepositoryKind());
			Update[] reverseUpdates = { sourceRepositoryUpdate, sourceRepositoryKindUpdate };
			dataProvider.updateIdentityMappings(status.getLandscape(), reverseUpdates, reverseFilters);
		}
	}
	
	private void updateHospital(final SynchronizationStatus status,
			String oldSourceRepositoryId, final String oldTargetRepositoryId, CcfDataProvider dataProvider)
			throws Exception {
		if (!oldSourceRepositoryId.equals(status.getSourceRepositoryId())) {
			Filter sourceSystemFilter = new Filter(CcfDataProvider.HOSPITAL_SOURCE_SYSTEM_ID, status.getSourceSystemId(), true);
			Filter sourceRepositoryFilter = new Filter(CcfDataProvider.HOSPITAL_SOURCE_REPOSITORY_ID, oldSourceRepositoryId, true);
			Filter targetSystemFilter = new Filter(CcfDataProvider.HOSPITAL_TARGET_SYSTEM_ID, status.getTargetSystemId(), true);
			Filter targetRepositoryFilter = new Filter(CcfDataProvider.HOSPITAL_TARGET_REPOSITORY_ID, oldTargetRepositoryId, true);
			Filter[] filters = { sourceSystemFilter, sourceRepositoryFilter, targetSystemFilter, targetRepositoryFilter };										
			Update sourceRepositoryUpdate = new Update(CcfDataProvider.HOSPITAL_SOURCE_REPOSITORY_ID, status.getSourceRepositoryId());
			Update sourceRepositoryKindUpdate = new Update(CcfDataProvider.HOSPITAL_SOURCE_REPOSITORY_KIND, status.getSourceRepositoryKind());
			Update[] updates = { sourceRepositoryUpdate, sourceRepositoryKindUpdate };
			dataProvider.updatePatients(status.getLandscape(), updates, filters);
			// Update reverse
			sourceSystemFilter = new Filter(CcfDataProvider.HOSPITAL_SOURCE_SYSTEM_ID, status.getTargetSystemId(), true);
			sourceRepositoryFilter = new Filter(CcfDataProvider.HOSPITAL_SOURCE_REPOSITORY_ID, oldTargetRepositoryId, true);
			targetSystemFilter = new Filter(CcfDataProvider.HOSPITAL_TARGET_SYSTEM_ID, status.getSourceSystemId(), true);
			targetRepositoryFilter = new Filter(CcfDataProvider.HOSPITAL_TARGET_REPOSITORY_ID, oldSourceRepositoryId, true);
			Filter[] reverseFilters = { sourceSystemFilter, sourceRepositoryFilter, targetSystemFilter, targetRepositoryFilter };										
			Update targetRepositoryUpdate = new Update(CcfDataProvider.HOSPITAL_TARGET_REPOSITORY_ID, status.getSourceRepositoryId());
			Update targetRepositoryKindUpdate = new Update(CcfDataProvider.HOSPITAL_TARGET_REPOSITORY_KIND, status.getSourceRepositoryKind());
			Update[] reverseUpdates = { targetRepositoryUpdate, targetRepositoryKindUpdate };
			dataProvider.updatePatients(status.getLandscape(), reverseUpdates, reverseFilters);
			oldSourceRepositoryId = status.getSourceRepositoryId();
		}
		if (!oldTargetRepositoryId.equals(status.getTargetRepositoryId())) {
			Filter sourceSystemFilter = new Filter(CcfDataProvider.HOSPITAL_SOURCE_SYSTEM_ID, status.getSourceSystemId(), true);
			Filter sourceRepositoryFilter = new Filter(CcfDataProvider.HOSPITAL_SOURCE_REPOSITORY_ID, oldSourceRepositoryId, true);
			Filter targetSystemFilter = new Filter(CcfDataProvider.HOSPITAL_TARGET_SYSTEM_ID, status.getTargetSystemId(), true);
			Filter targetRepositoryFilter = new Filter(CcfDataProvider.HOSPITAL_TARGET_REPOSITORY_ID, oldTargetRepositoryId, true);
			Filter[] filters = { sourceSystemFilter, sourceRepositoryFilter, targetSystemFilter, targetRepositoryFilter };										
			Update targetRepositoryUpdate = new Update(CcfDataProvider.HOSPITAL_TARGET_REPOSITORY_ID, status.getTargetRepositoryId());
			Update targetRepositoryKindUpdate = new Update(CcfDataProvider.HOSPITAL_TARGET_REPOSITORY_KIND, status.getTargetRepositoryKind());
			Update[] updates = { targetRepositoryUpdate, targetRepositoryKindUpdate };
			dataProvider.updatePatients(status.getLandscape(), updates, filters);
			// Update reverse
			sourceSystemFilter = new Filter(CcfDataProvider.HOSPITAL_SOURCE_SYSTEM_ID, status.getTargetSystemId(), true);
			sourceRepositoryFilter = new Filter(CcfDataProvider.HOSPITAL_SOURCE_REPOSITORY_ID, oldTargetRepositoryId, true);
			targetSystemFilter = new Filter(CcfDataProvider.HOSPITAL_TARGET_SYSTEM_ID, status.getSourceSystemId(), true);
			targetRepositoryFilter = new Filter(CcfDataProvider.HOSPITAL_TARGET_REPOSITORY_ID, oldSourceRepositoryId, true);
			Filter[] reverseFilters = { sourceSystemFilter, sourceRepositoryFilter, targetSystemFilter, targetRepositoryFilter };										
			Update sourceRepositoryUpdate = new Update(CcfDataProvider.HOSPITAL_SOURCE_REPOSITORY_ID, status.getTargetRepositoryId());
			Update sourceRepositoryKindUpdate = new Update(CcfDataProvider.HOSPITAL_SOURCE_REPOSITORY_KIND, status.getTargetRepositoryKind());
			Update[] reverseUpdates = { sourceRepositoryUpdate, sourceRepositoryKindUpdate };
			dataProvider.updatePatients(status.getLandscape(), reverseUpdates, reverseFilters);
		}
	}	
	
}

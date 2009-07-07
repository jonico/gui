package com.collabnet.ccf.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.ActionDelegate;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.dialogs.ChangeProjectMappingDialog;
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
				SynchronizationStatus status = (SynchronizationStatus)object;
				File xslFile = status.getXslFile();
				File graphicalXslFile = status.getGraphicalXslFile();
				File sourceRepositorySchemaFile = status.getSourceRepositorySchemaFile();
				File targetRepositorySchemaFile = status.getTargetRepositorySchemaFile();
				File genericArtifactToSourceRepositorySchemaFile = status.getGenericArtifactToSourceRepositorySchemaFile();
				File genericArtifactToTargetRepositorySchemaFile = status.getGenericArtifactToTargetRepositorySchemaFile();
				File sourceRepositorySchemaToGenericArtifactFile = status.getSourceRepositorySchemaToGenericArtifactFile();
				File targetRepositorySchemaToGenericArtifactFile = status.getTargetRepositorySchemaToGenericArtifactFile();
				
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
					if ((xslFile != null && xslFile.exists() && !newXslFile.exists()) ||
						(graphicalXslFile != null && graphicalXslFile.exists() && !newGraphicalXslFile.exists()) ||
						(sourceRepositorySchemaFile != null && sourceRepositorySchemaFile.exists() && !newSourceRepositorySchemaFile.exists()) ||
						(targetRepositorySchemaFile != null && targetRepositorySchemaFile.exists() && !newTargetRepositorySchemaFile.exists()) ||
						(genericArtifactToSourceRepositorySchemaFile != null && genericArtifactToSourceRepositorySchemaFile.exists() && !newGenericArtifactToSourceRepositorySchemaFile.exists()) ||					
						(genericArtifactToTargetRepositorySchemaFile != null && genericArtifactToTargetRepositorySchemaFile.exists() && !newGenericArtifactToTargetRepositorySchemaFile.exists()) ||					
						(sourceRepositorySchemaToGenericArtifactFile != null && sourceRepositorySchemaToGenericArtifactFile.exists() && !newSourceRepositorySchemaToGenericArtifactFile.exists()) ||			
						(targetRepositorySchemaToGenericArtifactFile != null && targetRepositorySchemaToGenericArtifactFile.exists() && !newTargetRepositorySchemaToGenericArtifactFile.exists())) {		
						if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Change Project Mapping", "Do you wish to rename field mapping files?")) {
							if (xslFile != null && xslFile.exists() && !newXslFile.exists()) {
								xslFile.renameTo(newXslFile);
							}
							if (graphicalXslFile != null && graphicalXslFile.exists() && !newGraphicalXslFile.exists()) {
								graphicalXslFile.renameTo(newGraphicalXslFile);
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
			action.setEnabled(Activator.getDefault().getActiveRole().isChangeProjectMapping());
		}
	}	
	
}

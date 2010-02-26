package com.collabnet.ccf.wizards;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.IMappingSection;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;

public class NewProjectMappingWizard extends Wizard {
	private ProjectMappings projectMappings;
	private int direction = -1;
	
	private NewProjectMappingWizardMainPage mainPage;
	private NewProjectMappingWizardProjectPage projectPage;
	
	private boolean addError;

	public static final int TYPE_TF = 0;
	public static final int TYPE_PT = 1;

	public NewProjectMappingWizard(ProjectMappings projectMappings) {
		super();
		this.projectMappings = projectMappings;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}

	@Override
	public void addPages() {
		super.addPages();
		setWindowTitle("New Project Mapping");
		mainPage = new NewProjectMappingWizardMainPage(projectMappings);
		mainPage.setDirection(direction);
		addPage(mainPage);
		projectPage = new NewProjectMappingWizardProjectPage(projectMappings);
		addPage(projectPage);
	}

	@Override
	public boolean performFinish() {
		if (!validate()) {
			return false;
		}
		addError = false;
		final SynchronizationStatus status = new SynchronizationStatus();	
		createProjectMapping(status);
		if (addError) return false;		
		return true;
	}

	private void createProjectMapping(final SynchronizationStatus status) {
		if (mainPage.system1ToSystem2Button.getSelection() || mainPage.bothButton.getSelection()) {
			status.setConflictResolutionPriority(SynchronizationStatus.CONFLICT_RESOLUTIONS[mainPage.system1ToSystem2ConflictResolutionCombo.getSelectionIndex()]);
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
			projectPage.getMappingSection1().updateSourceFields(status);
			projectPage.getMappingSection2().updateTargetFields(status);
			
			createMapping(status);
			createFieldMappingFile(status);
			if (addError) return;
		}
		if (mainPage.system2ToSystem1Button.getSelection() || mainPage.bothButton.getSelection()) {
			status.setConflictResolutionPriority(SynchronizationStatus.CONFLICT_RESOLUTIONS[mainPage.system2ToSystem1ConflictResolutionCombo.getSelectionIndex()]);
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
			projectPage.getMappingSection2().updateSourceFields(status);
			projectPage.getMappingSection1().updateTargetFields(status);
			
			createMapping(status);
			createFieldMappingFile(status);
		}
	}
	
	public ProjectMappings getProjectMappings() {
		return projectMappings;
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
	
	private void createFieldMappingFile(final SynchronizationStatus status) {
		status.setLandscape(projectMappings.getLandscape());
		status.clearXslInfo();
		File xslFile = status.getXslFile();
		if (!xslFile.exists()) {
			try {
				xslFile.createNewFile();
				File sampleFile = status.getSampleXslFile();
				if (sampleFile != null && sampleFile.exists()) {
					CcfDataProvider.copyFile(sampleFile, xslFile);
				}
			} catch (IOException e) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "New Project Mapping", "Unable to create field mapping file " + xslFile.getName() + ":\n\n" + e.getMessage());
				Activator.handleError(e);
				return;
			}
		}
	}
	
	public boolean validate() {
		IMappingSection mappingSection1 = projectPage.getMappingSection1();
		if (mappingSection1 != null && !mappingSection1.validate(projectMappings.getLandscape())) {
			return false;
		}
		
		IMappingSection mappingSection2 = projectPage.getMappingSection2();
		if (mappingSection2 != null && !mappingSection2.validate(projectMappings.getLandscape())) {
			return false;
		}		
		return true;
	}

}

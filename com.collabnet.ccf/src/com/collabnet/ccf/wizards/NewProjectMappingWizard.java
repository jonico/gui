package com.collabnet.ccf.wizards;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.schemageneration.QCLayoutExtractor;

public class NewProjectMappingWizard extends Wizard {
	private ProjectMappings projectMappings;
	private int type = TYPE_TF;
	private int direction = -1;
	
	private NewProjectMappingWizardMainPage mainPage;
	private NewProjectMappingWizardProjectPage projectPage;
	
	private boolean addError;
	
	private IDialogSettings settings = Activator.getDefault().getDialogSettings();
	
	public static final int TYPE_TF = 0;
	public static final int TYPE_PT = 1;

	public NewProjectMappingWizard(ProjectMappings projectMappings, int type) {
		super();
		this.projectMappings = projectMappings;
		this.type = type;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getType() {
		return type;
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
			if (!MessageDialog.openQuestion(getShell(), "New Project Mapping", "Invalid Quality Center Domain/Project entered.  Add project mapping anyway?")) {
				return false;
			}
		}
		addError = false;
		final SynchronizationStatus status = new SynchronizationStatus();
		
		createProjectMapping(status);
		
		if (addError) return false;
		saveDomainSelection();		
		return true;
	}

	private void createProjectMapping(final SynchronizationStatus status) {
		if (mainPage.system1ToSystem2Button.getSelection() || mainPage.bothButton.getSelection()) {
			status.setConflictResolutionPriority(SynchronizationStatus.CONFLICT_RESOLUTIONS[mainPage.system1ToSystem2ConflictResolutionCombo.getSelectionIndex()]);
			StringBuffer sourceRepositoryId = new StringBuffer(projectPage.qcDomainCombo.getText().trim() + "-" + projectPage.qcProjectText.getText().trim());
			if (mainPage.requirementsButton.getSelection()) {
				sourceRepositoryId.append("-" + projectPage.qcRequirementTypeText.getText().trim());
			}
			status.setSourceRepositoryId(sourceRepositoryId.toString());
			if (projectPage.teamForgeText == null) {
				status.setTargetRepositoryId(projectPage.ptProjectText.getText().trim() + ":" + projectPage.ptIssueTypeText.getText().trim());				
			} else {
				if (mainPage.planningFoldersButton.getSelection()) {
					status.setTargetRepositoryId(projectPage.teamForgeText.getText().trim() + "-" + ProjectMappings.MAPPING_TYPE_PLANNING_FOLDERS);
				} else {
					status.setTargetRepositoryId(projectPage.teamForgeText.getText().trim());
				}
			}
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
			createFieldMappingFile(status);
			if (addError) return;
		}
		if (mainPage.system2ToSystem1Button.getSelection() || mainPage.bothButton.getSelection()) {
			status.setConflictResolutionPriority(SynchronizationStatus.CONFLICT_RESOLUTIONS[mainPage.system2ToSystem1ConflictResolutionCombo.getSelectionIndex()]);
			StringBuffer targetRepositoryId = new StringBuffer(projectPage.qcDomainCombo.getText().trim() + "-" + projectPage.qcProjectText.getText().trim());
			if (mainPage.requirementsButton.getSelection()) {
				targetRepositoryId.append("-" + projectPage.qcRequirementTypeText.getText().trim());
			}
			status.setTargetRepositoryId(targetRepositoryId.toString());
			if (projectPage.teamForgeText == null) {
				status.setSourceRepositoryId(projectPage.ptProjectText.getText().trim() + ":" + projectPage.ptIssueTypeText.getText().trim());
			} else {
				if (mainPage.planningFoldersButton.getSelection()) {
					status.setSourceRepositoryId(projectPage.teamForgeText.getText().trim() + "-" + ProjectMappings.MAPPING_TYPE_PLANNING_FOLDERS);
				} else {
					status.setSourceRepositoryId(projectPage.teamForgeText.getText().trim());
				}
			}
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
			createFieldMappingFile(status);
		}
	}
	
	public ProjectMappings getProjectMappings() {
		return projectMappings;
	}

	public void setRequirementsSelected(boolean requirementsSelected) {
		projectPage.setRequirementsSelected(requirementsSelected);
	}
	
	public void setPlanningFoldersSelected(boolean planningFoldersSelected) {
		projectPage.setPlanningFoldersSelected(planningFoldersSelected);
	}
	
	private void saveDomainSelection() {
		String[] domains = projectPage.qcDomainCombo.getItems();
		List<String> domainList = new ArrayList<String>();
		domainList.add(projectPage.qcDomainCombo.getText().trim());
		int count = 0;
		for (int i = 0; i < domains.length; i++) {
			if (!domains[i].equals(projectPage.qcDomainCombo.getText().trim())) {
				domainList.add(domains[i]);
				count++;
				if (count == 9) break;
			}
		}
		settings.put(NewProjectMappingWizardProjectPage.PREVIOUS_QC_DOMAIN_COUNT, domainList.size());
		count = 0;
		for (String domain : domainList) {
			settings.put(NewProjectMappingWizardProjectPage.PREVIOUS_QC_DOMAIN + count++, domain);
		}
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
		// Only validate on windows.
		if (!"win32".equals(SWT.getPlatform())) return true;
		
		QCLayoutExtractor qcLayoutExtractor = new QCLayoutExtractor();
		Properties properties = projectMappings.getLandscape().getProperties1();
		String url = properties.getProperty(Activator.PROPERTIES_QC_URL, "");
		String user = properties.getProperty(Activator.PROPERTIES_QC_USER, "");
		String password = properties.getProperty(
				Activator.PROPERTIES_QC_PASSWORD, "");
		qcLayoutExtractor.setServerUrl(url);
		qcLayoutExtractor.setUserName(user);
		qcLayoutExtractor.setPassword(password);
		
		boolean validDomainAndProject;
		try {
			qcLayoutExtractor.validateQCDomainAndProject(projectPage.qcDomainCombo.getText().trim(), projectPage.qcProjectText.getText().trim());
			validDomainAndProject = true;
		} catch (Exception e) {
			validDomainAndProject = false;
		}
		return validDomainAndProject;
	}

}

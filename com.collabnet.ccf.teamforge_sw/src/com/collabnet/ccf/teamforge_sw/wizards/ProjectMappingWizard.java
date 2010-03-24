package com.collabnet.ccf.teamforge_sw.wizards;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.teamforge.schemageneration.TFSoapClient;
import com.collabnet.teamforge.api.main.ProjectRow;
import com.collabnet.teamforge.api.tracker.TrackerRow;
import com.danube.scrumworks.api.client.types.ProductWSO;

public class ProjectMappingWizard extends Wizard {
	private ProjectMappings projectMappings;
	
	private ProjectMappingWizardSwpProductPage productPage;
	private ProjectMappingWizardTeamForgeProjectPage projectPage;
	private ProjectMappingWizardTeamForgeTrackerPage trackerPage;
	private ProjectMappingWizardPreviewPage previewPage;
	
	private TFSoapClient soapClient;

	public ProjectMappingWizard(ProjectMappings projectMappings) {
		super();
		this.projectMappings = projectMappings;
	}
	
	@Override
	public void addPages() {
		super.addPages();
		setWindowTitle("Map TeamForge and ScrumWorks Projects");
		productPage = new ProjectMappingWizardSwpProductPage();
		addPage(productPage);
		projectPage = new ProjectMappingWizardTeamForgeProjectPage();
		addPage(projectPage);
		trackerPage = new ProjectMappingWizardTeamForgeTrackerPage();
		addPage(trackerPage);
		previewPage = new ProjectMappingWizardPreviewPage();
		addPage(previewPage);
	}

	@Override
	public boolean needsProgressMonitor() {
		return true;
	}

	@Override
	public boolean performFinish() {
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				CcfDataProvider dataProvider = new CcfDataProvider();
				String taskName = "Creating project mappings";
				monitor.setTaskName(taskName);
				monitor.beginTask(taskName, 6);
				
				monitor.subTask(previewPage.getTrackerTaskMapping());
				SynchronizationStatus projectMapping = new SynchronizationStatus();
				projectMapping.setGroup(projectMappings.getLandscape().getGroup());
				projectMapping.setSourceRepositoryKind("TRACKER");
				projectMapping.setTargetRepositoryKind("TRACKER");
				if (projectMappings.getLandscape().getEncoding2() != null && projectMappings.getLandscape().getEncoding2().trim().length() > 0) {
					projectMapping.setTargetSystemEncoding(projectMappings.getLandscape().getEncoding2());
				}				
				if (projectMappings.getLandscape().getType1().equals("TF")) {
					projectMapping.setSourceSystemId(projectMappings.getLandscape().getId1());	
					projectMapping.setTargetSystemId(projectMappings.getLandscape().getId2());			
					projectMapping.setSourceSystemKind(projectMappings.getLandscape().getType1());			
					projectMapping.setTargetSystemKind(projectMappings.getLandscape().getType2());
					projectMapping.setSourceSystemTimezone(projectMappings.getLandscape().getTimezone1());
					projectMapping.setTargetSystemTimezone(projectMappings.getLandscape().getTimezone2());
				} else {
					projectMapping.setSourceSystemId(projectMappings.getLandscape().getId2());	
					projectMapping.setTargetSystemId(projectMappings.getLandscape().getId1());			
					projectMapping.setSourceSystemKind(projectMappings.getLandscape().getType2());			
					projectMapping.setTargetSystemKind(projectMappings.getLandscape().getType1());
					projectMapping.setSourceSystemTimezone(projectMappings.getLandscape().getTimezone2());
					projectMapping.setTargetSystemTimezone(projectMappings.getLandscape().getTimezone1());					
				}
				projectMapping.setSourceRepositoryId(getSelectedTaskTracker().getId());
				projectMapping.setTargetRepositoryId(getSelectedProduct().getName() + "-Task");
				projectMapping.setConflictResolutionPriority(previewPage.getTrackerTaskConflictResolutionPriority());
				createMapping(projectMapping, dataProvider);
				monitor.worked(1);
				
				monitor.subTask(previewPage.getTrackerPbiMapping());
				projectMapping.setSourceRepositoryId(getSelectedPbiTracker().getId());
				projectMapping.setTargetRepositoryId(getSelectedProduct().getName() + "-PBI");
				projectMapping.setConflictResolutionPriority(previewPage.getTrackerPbiConflictResolutionPriority());
				createMapping(projectMapping, dataProvider);
				monitor.worked(1);
				
				monitor.subTask(previewPage.getPlanningFolderProductMapping());
				projectMapping.setSourceRepositoryId(getSelectedProject().getId() + "-planningFolders");
				projectMapping.setTargetRepositoryId(getSelectedProduct().getName() + "-Product");
				projectMapping.setConflictResolutionPriority(previewPage.getPlanningFolderProductConflictResolutionPriority());
				createMapping(projectMapping, dataProvider);
				monitor.worked(1);
				
				monitor.subTask(previewPage.getTaskTrackerMapping());
				if (projectMappings.getLandscape().getType1().equals("SW")) {
					projectMapping.setSourceSystemId(projectMappings.getLandscape().getId1());	
					projectMapping.setTargetSystemId(projectMappings.getLandscape().getId2());			
					projectMapping.setSourceSystemKind(projectMappings.getLandscape().getType1());			
					projectMapping.setTargetSystemKind(projectMappings.getLandscape().getType2());
					projectMapping.setSourceSystemTimezone(projectMappings.getLandscape().getTimezone1());
					projectMapping.setTargetSystemTimezone(projectMappings.getLandscape().getTimezone2());
				} else {
					projectMapping.setSourceSystemId(projectMappings.getLandscape().getId2());	
					projectMapping.setTargetSystemId(projectMappings.getLandscape().getId1());			
					projectMapping.setSourceSystemKind(projectMappings.getLandscape().getType2());			
					projectMapping.setTargetSystemKind(projectMappings.getLandscape().getType1());
					projectMapping.setSourceSystemTimezone(projectMappings.getLandscape().getTimezone2());
					projectMapping.setTargetSystemTimezone(projectMappings.getLandscape().getTimezone1());					
				}
				projectMapping.setSourceRepositoryId(getSelectedProduct().getName() + "-Task");
				projectMapping.setTargetRepositoryId(getSelectedTaskTracker().getId());
				projectMapping.setConflictResolutionPriority(previewPage.getTaskTrackerConflictResolutionPriority());
				createMapping(projectMapping, dataProvider);
				monitor.worked(1);
				
				monitor.subTask(previewPage.getPbiTrackerMapping());
				projectMapping.setSourceRepositoryId(getSelectedProduct().getName() + "-PBI");
				projectMapping.setTargetRepositoryId(getSelectedPbiTracker().getId());
				projectMapping.setConflictResolutionPriority(previewPage.getPbiTrackerConflictResolutionPriority());
				createMapping(projectMapping, dataProvider);
				monitor.worked(1);
				
				monitor.subTask(previewPage.getProductPlanningFolderMapping());
				projectMapping.setSourceRepositoryId(getSelectedProduct().getName() + "-Product");
				projectMapping.setTargetRepositoryId(getSelectedProject().getId() + "-planningFolders");
				projectMapping.setConflictResolutionPriority(previewPage.getProductPlanningFolderConflictResolutionPriority());
				createMapping(projectMapping, dataProvider);
				monitor.worked(1);
				
				monitor.done();
			}
		};
		try {
			getContainer().run(true, false, runnable);
		} catch (Exception e) {
			Activator.handleError(e);
		}
		return true;
	}
	
	public ProductWSO getSelectedProduct() {
		return productPage.getSelectedProduct();
	}

	public ProjectRow getSelectedProject() {
		return projectPage.getSelectedProject();
	}
	
	public TrackerRow getSelectedPbiTracker() {
		return trackerPage.getSelectedPbiTracker();
	}
	
	public TrackerRow getSelectedTaskTracker() {
		return trackerPage.getSelectedTaskTracker();
	}
	
	public ProjectMappings getProjectMappings() {
		return projectMappings;
	}
	
	public TFSoapClient getSoapClient() {
		if (soapClient == null) {
			Landscape landscape = projectMappings.getLandscape();
			Properties properties = null;
			if (landscape.getType1().equals("TF")) {
				properties = landscape.getProperties1();
			} else {
				properties = landscape.getProperties2();
			}
			if (properties != null) {
				String serverUrl = properties.getProperty(Activator.PROPERTIES_SFEE_URL);
				String userId = properties.getProperty(Activator.PROPERTIES_SFEE_USER);
				String password = properties.getProperty(Activator.PROPERTIES_SFEE_PASSWORD);
				soapClient = TFSoapClient.getSoapClient(serverUrl, userId, password);
			}
		}
		return soapClient;
	}

	private void createMapping(SynchronizationStatus status, CcfDataProvider dataProvider) {
		status.setSourceSystemKind(status.getSourceSystemKind() + "_paused");
		try {
			dataProvider.addSynchronizationStatus(projectMappings, status);
		} catch (Exception e) {
			Activator.handleError(e);
			return;
		}
		if (projectMappings.getLandscape().getRole() == Landscape.ROLE_ADMINISTRATOR) {
			createFieldMappingFile(status);
		}
	}
	
	private void createFieldMappingFile(SynchronizationStatus status) {
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
				Activator.handleError(e);
			}
		}
	}

}

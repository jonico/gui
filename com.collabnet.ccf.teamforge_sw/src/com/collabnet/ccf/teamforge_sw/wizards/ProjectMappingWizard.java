package com.collabnet.ccf.teamforge_sw.wizards;

import java.util.Properties;

import org.eclipse.jface.wizard.Wizard;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.ProjectMappings;
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

}

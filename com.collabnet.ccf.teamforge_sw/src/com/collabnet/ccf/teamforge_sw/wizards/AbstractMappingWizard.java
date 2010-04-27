package com.collabnet.ccf.teamforge_sw.wizards;

import java.net.MalformedURLException;
import java.util.Properties;

import org.eclipse.jface.wizard.Wizard;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.teamforge.schemageneration.TFSoapClient;
import com.danube.scrumworks.api2.client.ScrumWorksAPIService;

public abstract class AbstractMappingWizard extends Wizard {
	private TFSoapClient soapClient;
	private SynchronizationStatus projectMapping;
	private ScrumWorksAPIService scrumWorksEndpoint;

	public AbstractMappingWizard(SynchronizationStatus projectMapping) {
		super();
		this.projectMapping = projectMapping;
	}
	
	@Override
	public boolean needsProgressMonitor() {
		return true;
	}

	public SynchronizationStatus getProjectMapping() {
		return projectMapping;
	}

	public TFSoapClient getSoapClient() {
		if (soapClient == null) {
			Landscape landscape = projectMapping.getProjectMappings().getLandscape();
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
	
//	public ScrumWorksEndpoint getScrumWorksEndpoint() throws ServiceException {
//		if (scrumWorksEndpoint == null) {
//			Landscape landscape = projectMapping.getProjectMappings().getLandscape();
//			scrumWorksEndpoint = com.collabnet.ccf.sw.Activator.getScrumWorksEndpoint(landscape);
//		}
//		return scrumWorksEndpoint;
//	}
	
	public ScrumWorksAPIService getScrumWorksEndpoint() throws MalformedURLException {
		if (scrumWorksEndpoint == null) {
			Landscape landscape = projectMapping.getProjectMappings().getLandscape();
			scrumWorksEndpoint = com.collabnet.ccf.sw.Activator.getScrumWorksEndpoint(landscape);
		}
		return scrumWorksEndpoint;
	}
	
	public String getProduct() {
		String repositoryId = null;
		if (projectMapping.getSourceRepositoryId().endsWith("-PBI") ||
		    projectMapping.getSourceRepositoryId().endsWith("-Task") ||
	        projectMapping.getSourceRepositoryId().endsWith("-Product") ||
	        projectMapping.getSourceRepositoryId().endsWith("-Release")) {
			repositoryId = projectMapping.getSourceRepositoryId();
		}
		else if (projectMapping.getTargetRepositoryId().endsWith("-PBI") ||
			    projectMapping.getTargetRepositoryId().endsWith("-Task") ||
		        projectMapping.getTargetRepositoryId().endsWith("-Product") ||
		        projectMapping.getTargetRepositoryId().endsWith("-Release")) {
				repositoryId = projectMapping.getTargetRepositoryId();
		}
		if (repositoryId != null) {
			String product = repositoryId.substring(0, repositoryId.lastIndexOf("-"));
			return product;
		}
		return null;
	}
	
	public String getTracker() {
		String tracker = null;
		if (projectMapping.getSourceRepositoryId().startsWith("tracker")) {
			tracker = projectMapping.getSourceRepositoryId();
		}
		else if (projectMapping.getTargetRepositoryId().startsWith("tracker")) {
			tracker = projectMapping.getTargetRepositoryId();
		}
		return tracker;		
	}
	
	public String getProject() {
		String project = null;
		if (projectMapping.getSourceRepositoryId().endsWith("-planningFolders")) {
			project = projectMapping.getSourceRepositoryId().substring(0, projectMapping.getSourceRepositoryId().indexOf("-"));
		}
		else if (projectMapping.getTargetRepositoryId().endsWith("-planningFolders")) {
			project = projectMapping.getTargetRepositoryId().substring(0, projectMapping.getTargetRepositoryId().indexOf("-"));
		}
		return project;
	}

}

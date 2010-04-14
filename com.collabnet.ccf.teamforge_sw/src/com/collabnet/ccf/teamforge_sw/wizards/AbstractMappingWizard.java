package com.collabnet.ccf.teamforge_sw.wizards;

import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.eclipse.jface.wizard.Wizard;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.sw.ScrumWorksCcfParticipant;
import com.collabnet.ccf.teamforge.schemageneration.TFSoapClient;
import com.danube.scrumworks.api.client.ScrumWorksEndpoint;
import com.danube.scrumworks.api.client.ScrumWorksEndpointBindingStub;
import com.danube.scrumworks.api.client.ScrumWorksServiceLocator;

public abstract class AbstractMappingWizard extends Wizard {
	private TFSoapClient soapClient;
	private SynchronizationStatus projectMapping;
	private ScrumWorksEndpoint scrumWorksEndpoint;

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
	
	public ScrumWorksEndpoint getScrumWorksEndpoint() throws ServiceException {
		if (scrumWorksEndpoint == null) {
			Landscape landscape = projectMapping.getProjectMappings().getLandscape();
			Properties properties = null;
			if (landscape.getType1().equals(ScrumWorksCcfParticipant.TYPE)) {
				properties = landscape.getProperties1();
			} else {
				properties = landscape.getProperties2();
			}	
			String url = properties.get(Activator.PROPERTIES_SW_URL).toString();
			String user = properties.get(Activator.PROPERTIES_SW_USER).toString();
			String password = properties.get(Activator.PROPERTIES_SW_PASSWORD).toString();
			if (!url.endsWith("scrumworks-api/scrumworks")) {
				if (!url.endsWith("/")) {
					url = url + "/";
				}
				url = url + "scrumworks-api/scrumworks";
			}
			ScrumWorksServiceLocator locator = new ScrumWorksServiceLocator();
			locator.setScrumWorksEndpointPortEndpointAddress(url);
			scrumWorksEndpoint = locator.getScrumWorksEndpointPort();
			((ScrumWorksEndpointBindingStub) scrumWorksEndpoint).setUsername(user);
			((ScrumWorksEndpointBindingStub) scrumWorksEndpoint).setPassword(password);
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
			String product = repositoryId.substring(0, repositoryId.indexOf("-"));
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

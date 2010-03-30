package com.collabnet.ccf.teamforge_sw.wizards;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.rpc.ServiceException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.ccf.teamforge.schemageneration.TFSoapClient;
import com.collabnet.teamforge.api.tracker.TrackerFieldDO;
import com.collabnet.teamforge.api.tracker.TrackerFieldValueDO;
import com.danube.scrumworks.api.client.ScrumWorksEndpoint;
import com.danube.scrumworks.api.client.ScrumWorksEndpointBindingStub;
import com.danube.scrumworks.api.client.ScrumWorksServiceLocator;
import com.danube.scrumworks.api.client.types.ThemeWSO;

public class SynchronizeThemesWizard extends Wizard {
	private TFSoapClient soapClient;
	private SynchronizationStatus projectMapping;
	private ScrumWorksEndpoint scrumWorksEndpoint;
	private SynchronizeThemesWizardPage wizardPage;
	private Exception error;
	
	public SynchronizeThemesWizard(SynchronizationStatus projectMapping) {
		super();
		this.projectMapping = projectMapping;
	}
	
	@Override
	public void addPages() {
		super.addPages();
		setWindowTitle("Synchronize Themes");
		wizardPage = new SynchronizeThemesWizardPage();
		addPage(wizardPage);
	}
	
	@Override
	public boolean needsProgressMonitor() {
		return true;
	}

	@Override
	public boolean performFinish() {
		if (wizardPage.getAddedValues().size() == 0 && wizardPage.getDeletedValues().size() == 0) {
			return true;
		}
		error = null;
		final List<TrackerFieldValueDO> couldNotBeDeletedList = new ArrayList<TrackerFieldValueDO>();
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					ThemeWSO[] productThemes = wizardPage.getProductThemes();
					TrackerFieldDO themesField = wizardPage.getThemesField();		
					List<TrackerFieldValueDO> updatedValuesList = new ArrayList<TrackerFieldValueDO>();
					for (ThemeWSO productTheme : productThemes) {
						TrackerFieldValueDO fieldValue = new TrackerFieldValueDO(getSoapClient().supports50());
						fieldValue.setIsDefault(false);
						fieldValue.setValue(productTheme.getName());	
						fieldValue.setId(wizardPage.getOldValuesMap().get(productTheme.getName()));
						updatedValuesList.add(fieldValue);			
					}
					String taskName = "Synchronizing themes";
					int totalWork = 1 + wizardPage.getDeletedValues().size();
					monitor.setTaskName(taskName);
					monitor.beginTask(taskName, totalWork);
					for (TrackerFieldValueDO deletedValue : wizardPage.getDeletedValues()) {
						monitor.subTask("Checking deleted theme ''" + deletedValue.getValue() + "''");
						if (getSoapClient().isFieldValueUsed(getTracker(), themesField.getName(), deletedValue)) {
							int insertIndex = getInsertIndex(updatedValuesList, deletedValue);
							updatedValuesList.add(insertIndex, deletedValue);
							couldNotBeDeletedList.add(deletedValue);
						}			
						monitor.worked(1);
					}			
					TrackerFieldValueDO[] fieldValues = new TrackerFieldValueDO[updatedValuesList.size()];
					updatedValuesList.toArray(fieldValues);
					themesField.setFieldValues(fieldValues);
					monitor.subTask("Updating tracker themes");
					getSoapClient().setField(getTracker(), themesField);
					monitor.worked(1);
				} catch (RemoteException e) {
					error = e;
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, runnable);
		} catch (Exception e) {
			Activator.handleError(e);
			MessageDialog.openError(getShell(), "Synchronize Themes", e.getMessage());
			return false;
		}
		if (error != null) {
			Activator.handleError(error);
			MessageDialog.openError(getShell(), "Synchronize Themes", error.getMessage());
			return false;
		}
		wizardPage.refresh(true);
		if (couldNotBeDeletedList.size() > 0) {
			MessageDialog.openWarning(getShell(), "Synchronize Themes", "One or more theme could not be removed from tracker because it is used by one or more artifact.");
			return false;
		}
		return true;
	}
	
	private int getInsertIndex(List<TrackerFieldValueDO> updatedValuesList, TrackerFieldValueDO insertedValue) {
		int index = 0;
		for (TrackerFieldValueDO fieldValue : updatedValuesList) {
			if (fieldValue.getValue().compareTo(insertedValue.getValue()) > 0) {
				break;
			}
			index++;
		}
		return index;
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
			if (landscape.getType1().equals("SW")) {
				properties = landscape.getProperties1();
			} else {
				properties = landscape.getProperties2();
			}	
			String url = properties.get(Activator.PROPERTIES_SW_URL).toString();
			String user = properties.get(Activator.PROPERTIES_SW_USER).toString();
			String password = properties.get(Activator.PROPERTIES_SW_PASSWORD).toString();
			if (!url.endsWith("/")) {
				url = url + "/";
			}
			url = url + "scrumworks-api/scrumworks";
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
		if (projectMapping.getSourceRepositoryId().endsWith("-PBI")) {
			repositoryId = projectMapping.getSourceRepositoryId();
		}
		else if (projectMapping.getTargetRepositoryId().endsWith("-PBI")) {
			repositoryId = projectMapping.getTargetRepositoryId();
		}
		if (repositoryId != null) {
			String product = repositoryId.substring(0, repositoryId.indexOf("-PBI"));
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

}

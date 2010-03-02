 	package com.collabnet.ccf.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.ICcfParticipant;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.model.Database;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.views.CcfExplorerView;

public class NewLandscapeWizard extends Wizard {
	private NewLandscapeWizardMainPage mainPage;
	private NewLandscapeWizardDatabasePage databasePage;
	private NewLandscapeWizardPropertiesFolderPage propertiesPage;
	private ICcfParticipant[] ccfParticipants;
	private Landscape newLandscape;

	public NewLandscapeWizard() {
		super();
	}
	
	public void addPages() {
		super.addPages();
		try {
			ccfParticipants = Activator.getCcfParticipants();
		} catch (Exception e) {
			Activator.handleError(e);
		}
		setWindowTitle("New CCF Landscape");

		mainPage = new NewLandscapeWizardMainPage("main", "Describe landscape", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_LANDSCAPE_WIZBAN), ccfParticipants);
		addPage(mainPage);
		
		databasePage = new NewLandscapeWizardDatabasePage("database", "Database", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_LANDSCAPE_WIZBAN));
		addPage(databasePage);
		
		propertiesPage = new NewLandscapeWizardPropertiesFolderPage("properties", "Select configuration files", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_LANDSCAPE_WIZBAN));
		mainPage.setPropertiesPage(propertiesPage);
		addPage(propertiesPage);

	}
	
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof NewLandscapeWizardMainPage) {
			if (mainPage.getRole() == Landscape.ROLE_OPERATOR) {
				return databasePage;
			} else {
				return propertiesPage;
			}
		}		
		return null;
	}
	
	public boolean canFinish() {
		if (!mainPage.isPageComplete()) return false;
		if (mainPage.getRole() == Landscape.ROLE_OPERATOR) {
			return databasePage.isPageComplete();
		}
		return propertiesPage.isPageComplete();
	}

	@Override
	public boolean performFinish() {
		Database database = null;
		if (mainPage.getRole() == Landscape.ROLE_OPERATOR) {
			database = new Database();
			database.setUrl(databasePage.getUrl());
			database.setDriver(databasePage.getDriver());
			database.setUser(databasePage.getUser());
			database.setPassword(databasePage.getPassword());
			if (mainPage.getGroup() != null) {
				addGroupIfNecessary(mainPage.getGroup(), database);
			}
		}
		boolean landscapeAdded = Activator.getDefault().storeLandscape(mainPage.getDescription().replaceAll("/", "%slash%"), mainPage.getRole(), mainPage.getGroup(), database, mainPage.getSelectedParticipant1(), mainPage.getSelectedParticipant2(), propertiesPage.getConfigurationFolder1(), propertiesPage.getConfigurationFolder2());
		if (landscapeAdded) {
			newLandscape = Activator.getDefault().getLandscape(mainPage.getDescription());
			if (CcfExplorerView.getView() != null) {
				CcfExplorerView.getView().refresh();
			}
		}
		return landscapeAdded;
	}
	
	private void addGroupIfNecessary(final String groupName, final Database database) {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					CcfDataProvider dataProvider = new CcfDataProvider();
					if (!dataProvider.groupExists(groupName, database)) {
						dataProvider.addGroup(groupName, database);
					}
				} catch (Exception e) {
					Activator.handleError(e);
				}
			}			
		});
	}
	
	public Landscape getNewLandscape() {
		return newLandscape;
	}
	
	public String getDatabaseUrl() {
		if (databasePage == null) {
			return Activator.DATABASE_DEFAULT_URL;
		}
		return databasePage.getUrl();
	}
	
	public String getDatabaseDriver() {
		if (databasePage == null) {
			return Activator.DATABASE_DEFAULT_DRIVER;
		}
		return databasePage.getDriver();
	}
	
	public String getDatabaseUser() {
		if (databasePage == null) {
			return Activator.DATABASE_DEFAULT_USER;
		}
		return databasePage.getUser();
	}
	
	public String getDatabasePassword() {
		if (databasePage == null) {
			return Activator.DATABASE_DEFAULT_PASSWORD;
		}
		return databasePage.getPassword();
	}

}

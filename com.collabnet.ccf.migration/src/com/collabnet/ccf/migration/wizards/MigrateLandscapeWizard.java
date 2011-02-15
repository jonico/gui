package com.collabnet.ccf.migration.wizards;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.jface.wizard.Wizard;
import org.xml.sax.SAXException;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.api.CcfMasterClient;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.teamforge.api.Connection;

public class MigrateLandscapeWizard extends Wizard {
	private Landscape landscape;
	private MigrateLandscapeWizardCcfMasterPage ccfMasterPage;
	private Connection connection;

	public MigrateLandscapeWizard(Landscape landscape) {
		super();
		this.landscape = landscape;
		
		connection = getConnection();
	}
	
	@Override
	public void addPages() {
		super.addPages();
		setWindowTitle("Migrate Landscape to CCF 2.x");
		ccfMasterPage = new MigrateLandscapeWizardCcfMasterPage();
		addPage(ccfMasterPage);
	}	

	@Override
	public boolean performFinish() {
		try {
			com.collabnet.ccf.api.model.Landscape[] ccfMasterLandscapes = getCcfMasterClient().getLandscapes();
			for (com.collabnet.ccf.api.model.Landscape landscape : ccfMasterLandscapes) {
				System.out.println(landscape.getDescription());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public boolean needsProgressMonitor() {
		return true;
	}

	public Landscape getLandscape() {
		return landscape;
	}
	
	public Connection getConnection() {
		if (connection == null) {
			Properties properties = null;
			if (landscape.getType1().equals("TF")) {
				properties = landscape.getProperties1();
			} else {
				properties = landscape.getProperties2();
			}
			if (properties != null) {
				String serverUrl = properties.getProperty(Activator.PROPERTIES_SFEE_URL);
				String userId = properties.getProperty(Activator.PROPERTIES_SFEE_USER);
				String password = Activator.decodePassword(properties.getProperty(Activator.PROPERTIES_SFEE_PASSWORD));
				connection = Connection.builder(serverUrl)
				.userNamePassword(userId, password)	
				.build();
			}
		}
		return connection;
	}	

	public CcfMasterClient getCcfMasterClient() {
		return CcfMasterClient.getClient(ccfMasterPage.getCcfMasterUrl(), ccfMasterPage.getCcfMasterUser(), ccfMasterPage.getCcfMasterPassword());
	}
}

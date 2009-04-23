package com.collabnet.ccf;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.graphics.Image;

import com.collabnet.ccf.actions.EditCcfPropertiesAction;
import com.collabnet.ccf.actions.EditQcPropertiesAction;
import com.collabnet.ccf.actions.EditTeamForgePropertiesAction;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.wizards.NewLandscapeWizardPropertiesFolderPage;

public class TeamForgeQCLandscapeContributor implements ILandscapeContributor {
	private String id;
	private String name;
	private String description;
	private Image image;
	private int sequence;
	
	private NewLandscapeWizardPropertiesFolderPage propertiesFolderPage;
	private WizardPage[] wizardPages;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;	
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setImage(Image image) {
		this.image = image;
	}

	public Image getImage() {
		return image;
	}

	public IWizardPage getNextPage(IWizardPage currentPage) {
		return null;
	}

	public int getSequence() {
		return sequence;
	}
	
	public String getType1() {
		return "QC";
	}
	
	public String getType2() {
		return "TF";
	}
	
	public String getConfigurationFolder() {
		return propertiesFolderPage.getConfigurationFolder();
	}

	public IWizardPage[] getWizardPages(boolean initializePages) {
		if (wizardPages == null || initializePages) {
			propertiesFolderPage = new NewLandscapeWizardPropertiesFolderPage("propertiesTeamForge", "Select config.xml", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_LANDSCAPE_WIZBAN), NewLandscapeWizardPropertiesFolderPage.TYPE_TF);
			WizardPage[] pages = { propertiesFolderPage };
			wizardPages = pages;
		}
		return wizardPages;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	public Action[] getEditPropertiesActions(Landscape landscape) {
		Action ccfAction = new EditCcfPropertiesAction(landscape);
		Action qcAction = new EditQcPropertiesAction(landscape);
		Action sfeeAction = new EditTeamForgePropertiesAction(landscape);
		Action[] actions = { ccfAction, qcAction, sfeeAction };
		return actions;
	}
	
	public int compareTo(Object compareToObject) {
		if (!(compareToObject instanceof ILandscapeContributor)) return 0;
		ILandscapeContributor compareToLandscapeContributor = (ILandscapeContributor)compareToObject;
		if (getSequence() > compareToLandscapeContributor.getSequence()) return 1;
		else if (compareToLandscapeContributor.getSequence() > getSequence()) return -1;
		return getName().compareTo(compareToLandscapeContributor.getName());
	}
}

package com.collabnet.ccf;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.graphics.Image;

import com.collabnet.ccf.wizards.NewLandscapeWizardPropertiesFolderPage;

public class ProjectTrackerQCLandscapeContributor implements ILandscapeContributor {
	private String name;
	private String description;
	private Image image;
	private int sequence;
	
	private NewLandscapeWizardPropertiesFolderPage propertiesFolderPage;
	private WizardPage[] wizardPages;
	
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

	public boolean addLandscape() {
		return true;
	}

	public IWizardPage getNextPage(IWizardPage currentPage) {
		return null;
	}

	public int getSequence() {
		return sequence;
	}

	public IWizardPage[] getWizardPages(boolean initializePages) {
		if (wizardPages == null || initializePages) {
			propertiesFolderPage = new NewLandscapeWizardPropertiesFolderPage("propertiesTracker", "Specify the relative location of the properties files", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_LANDSCAPE_WIZBAN));
			WizardPage[] pages = { propertiesFolderPage };
			wizardPages = pages;
		}
		return wizardPages;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	public int compareTo(Object compareToObject) {
		if (!(compareToObject instanceof ILandscapeContributor)) return 0;
		ILandscapeContributor compareToLandscapeContributor = (ILandscapeContributor)compareToObject;
		if (getSequence() > compareToLandscapeContributor.getSequence()) return 1;
		else if (compareToLandscapeContributor.getSequence() > getSequence()) return -1;
		return getName().compareTo(compareToLandscapeContributor.getName());
	}
}

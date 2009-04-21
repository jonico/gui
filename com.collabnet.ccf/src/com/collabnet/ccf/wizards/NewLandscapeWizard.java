package com.collabnet.ccf.wizards;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.ILandscapeContributor;

public class NewLandscapeWizard extends Wizard {
	private NewLandscapeWizardMainPage mainPage;
	private ILandscapeContributor[] landscapeContributors;
	
	private IWizardPage[][] pages;

	public NewLandscapeWizard() {
		super();
	}
	
	public void addPages() {
		super.addPages();
		try {
			landscapeContributors = Activator.getLandscapeContributors();
		} catch (Exception e) {
			Activator.handleError(e);
		}
		setWindowTitle("New CCF Landscape");
		
		mainPage = new NewLandscapeWizardMainPage("main", "Select the type of landscape", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_LANDSCAPE_WIZBAN), landscapeContributors);
		addPage(mainPage);
		
		pages = new WizardPage[landscapeContributors.length][];
		for (int i = 0; i < landscapeContributors.length; i++) {
			IWizardPage[] landscapePages = landscapeContributors[i].getWizardPages(true);
			pages[i] = landscapePages;		
		}
		for (int i = 0; i < pages.length; i++) {
			IWizardPage[] landscapePages = pages[i];
			if (landscapePages != null) {			
				for (int j = 0; j < landscapePages.length; j++) {
					addPage(landscapePages[j]);
				}
			}
		}
	}
	
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof NewLandscapeWizardMainPage) {
			IWizardPage[] landscapePages = mainPage.getSelectedLandscapeContributor().getWizardPages(false);
			if (landscapePages != null && landscapePages.length > 0) return landscapePages[0];
			else return null;
		}		
		else return mainPage.getSelectedLandscapeContributor().getNextPage(page);
	}
	
	public boolean canFinish() {
		if (!mainPage.isPageComplete()) return false;
		IWizardPage[] landscapePages;
		landscapePages = mainPage.getSelectedLandscapeContributor().getWizardPages(false);
		if (landscapePages == null) return true;
		for (int i = 0; i < landscapePages.length; i++) {
			if (!landscapePages[i].isPageComplete()) return false;
		}
		return true;
	}

	@Override
	public boolean performFinish() {
		MessageDialog.openInformation(getShell(), "New Landscape", "Not yet implemented.");
		return false;
	}

}

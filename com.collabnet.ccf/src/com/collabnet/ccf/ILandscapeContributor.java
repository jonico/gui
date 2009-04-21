package com.collabnet.ccf;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("unchecked")
public interface ILandscapeContributor extends Comparable {
	
	public String getName();
	
	public String getDescription();
	
	public void setName(String name);
	
	public void setDescription(String description);
	
	public void setSequence(int sequence);
	
	public int getSequence();
	
	public IWizardPage[] getWizardPages(boolean initializePages);
	
	public IWizardPage getNextPage(IWizardPage currentPage);
	
	public Image getImage();
	
	public void setImage(Image image);
	
	public boolean addLandscape();

}

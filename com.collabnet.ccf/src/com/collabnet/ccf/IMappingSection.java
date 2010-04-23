package com.collabnet.ccf;

import org.eclipse.swt.widgets.Composite;

import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.MappingGroup;
import com.collabnet.ccf.model.SynchronizationStatus;

public interface IMappingSection {
	public final static int TYPE_SOURCE = 0;
	public final static int TYPE_TARGET = 1;
	
	public void setSystemNumber(int systemNumber);
	
	public int getSystemNumber();
	
	public Composite getComposite(Composite parent, Landscape landscape);
	
	public void initializeComposite(SynchronizationStatus projectMapping, int type);
	
	public void initializeComposite(MappingGroup mappingGroup);
	
	public boolean isPageComplete();
	
	public void setProjectPage(IPageCompleteListener projectPage);
	
	public IPageCompleteListener getProjectPage();
	
	public boolean validate(Landscape landscape);
	
	public void updateSourceFields(SynchronizationStatus projectMapping);
	
	public void updateTargetFields(SynchronizationStatus projectMapping);

}

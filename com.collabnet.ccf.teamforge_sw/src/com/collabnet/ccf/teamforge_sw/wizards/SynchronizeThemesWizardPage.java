package com.collabnet.ccf.teamforge_sw.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.collabnet.ccf.Activator;
import com.collabnet.teamforge.api.tracker.TrackerFieldDO;
import com.collabnet.teamforge.api.tracker.TrackerFieldValueDO;
import com.danube.scrumworks.api2.client.Product;
import com.danube.scrumworks.api2.client.Theme;

public class SynchronizeThemesWizardPage extends WizardPage {
	private List<Theme> productThemes;
	private TrackerFieldDO themesField;
	private TrackerFieldValueDO[] trackerThemes;
	private Exception getProductThemesError;
	private Exception getTrackerThemesError;	
	private Exception unknownError;
	private List<TrackerFieldValueDO> deletedValues;
	private List<Theme> addedValues;
	private Map<String, String> oldValuesMap;
	private Group addGroup;
	private Group deleteGroup;
	private org.eclipse.swt.widgets.List addedValuesList;
	private org.eclipse.swt.widgets.List deletedValuesList;
	
	public SynchronizeThemesWizardPage() {
		super("mainPage", "Synchronize Themes", Activator.getDefault().getImageDescriptor(Activator.IMAGE_NEW_PROJECT_MAPPING_WIZBAN));
	}

	public void createControl(Composite parent) {
		Composite outerContainer = new Composite(parent,SWT.NONE);
		outerContainer.setLayout(new GridLayout());
		outerContainer.setLayoutData(
		new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
				
		getThemes();
		
		if (addedValues.size() > 0) {
			addGroup = new Group(outerContainer,SWT.NONE);
			addGroup.setLayout(new GridLayout());
			addGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));
			addGroup.setText("Add Themes to tracker:");
			addedValuesList = new org.eclipse.swt.widgets.List(addGroup, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			addedValuesList.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));
		}
		
		if (deletedValues.size() > 0) {
			deleteGroup = new Group(outerContainer,SWT.NONE);
			deleteGroup.setLayout(new GridLayout());
			deleteGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));
			deleteGroup.setText("Remove themes from tracker:");
			deletedValuesList = new org.eclipse.swt.widgets.List(deleteGroup, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			deletedValuesList.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));
		}
		
		if (addedValues.size() > 0 || deletedValues.size() > 0) {
			refresh(false);
		}

		setControl(outerContainer);
	}
	
	public void refresh(boolean getThemes) {
		SynchronizeThemesWizard wizard = (SynchronizeThemesWizard)getWizard();
		if (getThemes) {
			getThemes();
		}
		if (addGroup != null) {
			addedValuesList.removeAll();
			for (Theme theme : addedValues) {
				try {
					addedValuesList.add(wizard.getValue(theme));
				} catch (Exception e) {}
			}
		}
		if (deleteGroup != null) {
			deletedValuesList.removeAll();
			for (TrackerFieldValueDO fieldValue : deletedValues) {
				deletedValuesList.add(fieldValue.getValue());
			}
		}
	}
	
	private void getThemes() {
		getProductThemesError = null;
		getTrackerThemesError = null;
		unknownError = null;
		deletedValues = new ArrayList<TrackerFieldValueDO>();	
		addedValues = new ArrayList<Theme>();
		oldValuesMap = new HashMap<String, String>();
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				SynchronizeThemesWizard wizard = (SynchronizeThemesWizard)getWizard();
				String taskName = "Retrieving themes";
				monitor.setTaskName(taskName);
				monitor.beginTask(taskName, 3);
				monitor.subTask("SWP product themes");
				try {
					Product product = null;
					product =  wizard.getScrumWorksEndpoint().getProductById(getProductId());
					if (product == null) {
						product =  wizard.getScrumWorksEndpoint().getProductByName(getProduct());
					}
					monitor.worked(1);
					productThemes = wizard.getScrumWorksEndpoint().getThemesForProduct(product.getId());
					for (Theme productTheme : productThemes) {
						wizard.getValue(productTheme);
					}
					monitor.worked(1);
				} catch (Exception e) {
					Activator.handleError(e);
					getProductThemesError = e;
					return;
				}	
				monitor.subTask("TeamForge tracker themes");
				try {
					TrackerFieldDO[] fields = ((SynchronizeThemesWizard)getWizard()).getSoapClient().getFields(getTracker());
					monitor.worked(1);
					themesField = null;
					for (TrackerFieldDO field : fields) {
						if (field.getName().equals("Themes")) {
							themesField = field;
						}
					}
					if (themesField == null) {
						setErrorMessage("Themes field not defined for tracker " + getTracker() + ".");
						return;
					}
					trackerThemes = themesField.getFieldValues();
					
					List<String> newValuesList = new ArrayList<String>();	
					if (productThemes != null) {
						for (Theme productTheme : productThemes) {
							newValuesList.add(wizard.getValue(productTheme));
						}
					}
					for (TrackerFieldValueDO oldValue : themesField.getFieldValues()) {
						oldValuesMap.put(oldValue.getValue(), oldValue.getId());
						if (!newValuesList.contains(oldValue.getValue())) {
							deletedValues.add(oldValue);
						}
					}
					if (productThemes != null) {
						for (Theme productTheme : productThemes) {
							if (oldValuesMap.get(wizard.getValue(productTheme)) == null) {
								addedValues.add(productTheme);
							}
						}
					}
					
				} catch (Exception e) {
					Activator.handleError(e);
					getTrackerThemesError = e;
					return;
				}								
				monitor.done();
			}		
		};
		
		try {
			getContainer().run(true, false, runnable);
		} catch (Exception e) {
			Activator.handleError(e);
			unknownError = e;
		}
		
		if (getProductThemesError != null) {
			setErrorMessage("An unexpected error occurred while getting SWP product themes.  See error log for details.");
		}
		else if (getTrackerThemesError != null) {
			setErrorMessage("An unexpected error occurred while getting TeamForge tracker themes.  See error log for details.");
		}
		else if (unknownError != null) {
			setErrorMessage("An unexpected error occurred while getting themes.  See error log for details.");
		}		
		else if (addedValues.size() == 0 && deletedValues.size() == 0) {
			setMessage("No differences found between TeamForge tracker themes and SWP product themes.");
		} else {
			setMessage("Synchronize TeamForge tracker themes with SWP product themes.");
		}
		setPageComplete(productThemes != null && trackerThemes != null);
	}
	
	private String getProduct() {
		return ((AbstractMappingWizard)getWizard()).getProduct();
	}
	
	private Long getProductId() {
		return ((AbstractMappingWizard)getWizard()).getProductId();
	}
	
	private String getTracker() {
		return ((AbstractMappingWizard)getWizard()).getTracker();
	}
	
	public List<Theme> getProductThemes() {
		return productThemes;
	}
	
	public TrackerFieldDO getThemesField() {
		return themesField;
	}

	public List<TrackerFieldValueDO> getDeletedValues() {
		return deletedValues;
	}

	public List<Theme> getAddedValues() {
		return addedValues;
	}

	public Map<String, String> getOldValuesMap() {
		return oldValuesMap;
	}

}

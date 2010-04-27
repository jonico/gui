package com.collabnet.ccf.teamforge_sw.wizards;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.SynchronizationStatus;
import com.collabnet.teamforge.api.tracker.TrackerFieldDO;
import com.collabnet.teamforge.api.tracker.TrackerFieldValueDO;
import com.danube.scrumworks.api2.client.Theme;

public class SynchronizeThemesWizard extends AbstractMappingWizard {
	private SynchronizeThemesWizardPage wizardPage;
	private Exception error;
	
	public SynchronizeThemesWizard(SynchronizationStatus projectMapping) {
		super(projectMapping);
	}
	
	@Override
	public void addPages() {
		super.addPages();
		setWindowTitle("Synchronize Themes");
		wizardPage = new SynchronizeThemesWizardPage();
		addPage(wizardPage);
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
					List<Theme> productThemes = wizardPage.getProductThemes();
					TrackerFieldDO themesField = wizardPage.getThemesField();		
					List<TrackerFieldValueDO> updatedValuesList = new ArrayList<TrackerFieldValueDO>();
					for (Theme productTheme : productThemes) {
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

}

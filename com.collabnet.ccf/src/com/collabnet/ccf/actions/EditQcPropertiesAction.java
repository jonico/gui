package com.collabnet.ccf.actions;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.dialogs.SystemPropertiesDialog;
import com.collabnet.ccf.model.Landscape;

public class EditQcPropertiesAction extends Action {
	private Landscape landscape;

	public EditQcPropertiesAction(Landscape landscape) {
		super();
		this.landscape = landscape;
		setText("Edit QC properties...");
	}
	
	@Override
	public void run() {
		File folder = new File(landscape.getConfigurationFolder());
		File propertiesFile = new File(folder, "qc.properties");
		if (!propertiesFile.exists()) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Edit QC Properties", "File qc.properties not found.");
			return;
		}
		try {
			FileInputStream inputStream = new FileInputStream(propertiesFile);
			Properties properties = new Properties();
			properties.load(inputStream);
			inputStream.close();
			
			File propertiesFile1 = null;
			File propertiesFile2 = null;
			
			if (landscape.getConfigurationFolder1() != null) {
				folder = new File(landscape.getConfigurationFolder1());
				propertiesFile1 = new File(folder, "qc.properties");
			}
			
			if (landscape.getConfigurationFolder2() != null) {
				folder = new File(landscape.getConfigurationFolder2());
				propertiesFile2 = new File(folder, "qc.properties");
			}			
			
			SystemPropertiesDialog dialog = new SystemPropertiesDialog(Display.getDefault().getActiveShell(), propertiesFile1, propertiesFile2, properties, SystemPropertiesDialog.QC);
			dialog.open();
		} catch (Exception e) {
			Activator.handleError(e);
		}
	}	

}

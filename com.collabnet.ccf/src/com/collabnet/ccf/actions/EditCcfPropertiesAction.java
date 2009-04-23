package com.collabnet.ccf.actions;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.dialogs.CcfPropertiesDialog;
import com.collabnet.ccf.model.Landscape;

public class EditCcfPropertiesAction extends Action {
	private Landscape landscape;

	public EditCcfPropertiesAction(Landscape landscape) {
		super();
		this.landscape = landscape;
		setText("Edit CCF properties...");
	}

	@Override
	public void run() {
		File folder = new File(landscape.getConfigurationFolder());
		File propertiesFile = new File(folder, "ccf.properties");
		if (!propertiesFile.exists()) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Edit CCF Properties", "File ccf.properties not found.");
			return;
		}
		try {
			FileInputStream inputStream = new FileInputStream(propertiesFile);
			Properties properties = new Properties();
			properties.load(inputStream);
			inputStream.close();
			CcfPropertiesDialog dialog = new CcfPropertiesDialog(Display.getDefault().getActiveShell(), propertiesFile, properties);
			dialog.open();
		} catch (Exception e) {
			Activator.handleError(e);
		}
	}	

}

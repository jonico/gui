package com.collabnet.ccf.tfs.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.collabnet.ccf.tfs.Activator;

public class TFSPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {
	private Button advancedProjectMappingButton;
	
	IPreferenceStore store = Activator.getDefault().getPreferenceStore();

	public TFSPreferencesPage() {
		super();
	}

	public TFSPreferencesPage(String title) {
		super(title);
	}

	public TFSPreferencesPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	public void init(IWorkbench workbench) {}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gd);
		
		advancedProjectMappingButton = new Button(composite, SWT.CHECK);
		advancedProjectMappingButton.setText("Enable advanced project mapping wizard options (requires fast connection to HP Quality Center)");
		
		initializeValues();
		
		return composite;
	}

	@Override
	public boolean performOk() {
		store.setValue(Activator.PREFERENCES_ADVANCED_PROJECT_MAPPING, advancedProjectMappingButton.getSelection());
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		advancedProjectMappingButton.setSelection(Activator.DEFAULT_ADVANCED_PROJECT_MAPPING);
		super.performDefaults();
	}
	
	private void initializeValues() {
		advancedProjectMappingButton.setSelection(store.getBoolean(Activator.PREFERENCES_ADVANCED_PROJECT_MAPPING));
	}

}

package com.collabnet.ccf.teamforge_sw.preferences;

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

import com.collabnet.ccf.teamforge_sw.Activator;

public class TeamForgeSwpPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {
	private Button mapMultipleButton;
	
	IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	
	public TeamForgeSwpPreferencesPage() {
		super();
	}

	public TeamForgeSwpPreferencesPage(String title) {
		super(title);
	}

	public TeamForgeSwpPreferencesPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gd);
		
		mapMultipleButton = new Button(composite, SWT.CHECK);
		mapMultipleButton.setText("Map ScrumWorks Pro product to multiple TeamForge projects");
		
		initializeValues();
		
		return composite;
	}

	@Override
	public boolean performOk() {
		store.setValue(Activator.PREFERENCES_MAP_MULTIPLE, mapMultipleButton.getSelection());
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		mapMultipleButton.setSelection(Activator.DEFAULT_MAP_MULTIPLE);
		super.performDefaults();
	}

	public void init(IWorkbench workbench) {
	}
	
	private void initializeValues() {
		mapMultipleButton.setSelection(store.getBoolean(Activator.PREFERENCES_MAP_MULTIPLE));
	}

}

package com.collabnet.ccf.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.collabnet.ccf.Activator;

public class HospitalPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	private IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	
	public static final String ID = "com.collabnet.ccf.preferences.hospital";
	
	public HospitalPreferencePage() {
		super();
	}

	public HospitalPreferencePage(String title) {
		super(title);
	}

	public HospitalPreferencePage(String title, ImageDescriptor image) {
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

		return composite;
	}

	public void init(IWorkbench workbench) {
	}

}

package com.collabnet.ccf.preferences;

import org.eclipse.jface.resource.ImageDescriptor;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.views.HospitalView;

public class HospitalPreferencePage extends ColumnChooserPreferencePage {
	
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
	public String[] getAllColumns() {
		return CcfDataProvider.HOSPITAL_COLUMNS.split("\\,");
	}

	@Override
	public void initializeCurrentValues() {
		initializeValues(store.getString(Activator.PREFERENCES_HOSPITAL_COLUMNS).split("\\,"));
	}

	@Override
	public void initializeDefaultValues() {
		initializeValues(CcfDataProvider.DEFAULT_HOSPITAL_COLUMNS.split("\\,"));
	}

	@Override
	public void refresh() {
		if (needsRefresh && HospitalView.getView() != null) {
			HospitalView.getView().refreshTableLayout();
		}
	}

	@Override
	public void setValue(String selectedColumns) {
		store.setValue(Activator.PREFERENCES_HOSPITAL_COLUMNS, selectedColumns);
	}

}

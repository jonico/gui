package com.collabnet.ccf.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.collabnet.ccf.Activator;

public class CcfPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
		node.put(Activator.PREFERENCES_DATABASE_DESCRIPTION, Activator.DATABASE_DEFAULT_DESCRIPTION);
		node.put(Activator.PREFERENCES_DATABASE_DRIVER, Activator.DATABASE_DEFAULT_DRIVER);
		node.put(Activator.PREFERENCES_DATABASE_URL, Activator.DATABASE_DEFAULT_URL);
		node.put(Activator.PREFERENCES_DATABASE_USER, Activator.DATABASE_DEFAULT_USER);
		node.put(Activator.PREFERENCES_DATABASE_PASSWORD, Activator.DATABASE_DEFAULT_PASSWORD);
		node.putBoolean(Activator.PREFERENCES_AUTOCONNECT, Activator.DEFAULT_AUTOCONNECT);
		node.put(Activator.PREFERENCES_HOSPITAL_COLUMNS, Activator.DEFAULT_HOSPITAL_COLUMNS);
		node.put(Activator.PREFERENCES_IDENTITY_MAPPING_COLUMNS, Activator.DEFAULT_IDENTITY_MAPPING_COLUMNS);
		node.putInt(Activator.PREFERENCES_RESET_DELAY, Activator.DEFAULT_RESET_DELAY);
		node.putBoolean(Activator.PREFERENCES_SHOW_HOSPITAL_COUNT, Activator.DEFAULT_SHOW_HOSPITAL_COUNT);
	}

}

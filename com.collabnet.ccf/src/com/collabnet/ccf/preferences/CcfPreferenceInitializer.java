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
	}

}

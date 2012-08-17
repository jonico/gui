package com.collabnet.ccf.tfs.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.collabnet.ccf.tfs.Activator;

public class TFSPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
		node.putBoolean(Activator.PREFERENCES_ADVANCED_PROJECT_MAPPING, Activator.DEFAULT_ADVANCED_PROJECT_MAPPING);
	}

}

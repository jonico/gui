package com.collabnet.ccf.rqp.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.collabnet.ccf.rqp.Activator;

public class RequisiteProPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		@SuppressWarnings("deprecation")
		IEclipsePreferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
		node.putBoolean(Activator.PREFERENCES_ADVANCED_PROJECT_MAPPING, Activator.DEFAULT_ADVANCED_PROJECT_MAPPING);
	}

}

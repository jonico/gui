package com.collabnet.ccf.teamforge_sw.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.collabnet.ccf.teamforge_sw.Activator;

public class TeamForgeSwpPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
		node.putBoolean(Activator.PREFERENCES_MAP_MULTIPLE, Activator.DEFAULT_MAP_MULTIPLE);
	}

}

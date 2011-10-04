/*******************************************************************************
 * Copyright (c) 2011 CollabNet.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     CollabNet - initial API and implementation
 ******************************************************************************/
package com.collabnet.ccf.migration;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.collabnet.ccf.migration"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	public final String ICON_PATH = "icons/"; //$NON-NLS-1$
	public static final String IMG_PROJECT = "project.gif"; //$NON-NLS-1$
	public static final String IMG_REPOSITORY_MAPPING = "repositoryMapping.gif"; //$NON-NLS-1$
	public static final String IMG_REPOSITORY_MAPPING_DIRECTION_RUNNING = "repositoryMappingDirection_running.gif"; //$NON-NLS-1$
	public static final String IMG_REPOSITORY_MAPPING_DIRECTION_PAUSED = "repositoryMappingDirection_paused.gif"; //$NON-NLS-1$
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(IMG_PROJECT, imageDescriptorFromPlugin(PLUGIN_ID, ICON_PATH + IMG_PROJECT));
		reg.put(IMG_REPOSITORY_MAPPING, imageDescriptorFromPlugin(PLUGIN_ID, ICON_PATH + IMG_REPOSITORY_MAPPING));
		reg.put(IMG_REPOSITORY_MAPPING_DIRECTION_RUNNING, imageDescriptorFromPlugin(PLUGIN_ID, ICON_PATH + IMG_REPOSITORY_MAPPING_DIRECTION_RUNNING));
		reg.put(IMG_REPOSITORY_MAPPING_DIRECTION_PAUSED, imageDescriptorFromPlugin(PLUGIN_ID, ICON_PATH + IMG_REPOSITORY_MAPPING_DIRECTION_PAUSED));
	}
}

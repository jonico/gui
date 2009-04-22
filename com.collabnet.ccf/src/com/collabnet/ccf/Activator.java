package com.collabnet.ccf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.model.Landscape;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	
	private Hashtable<String, ImageDescriptor> imageDescriptors;

	// The plug-in ID
	public static final String PLUGIN_ID = "com.collabnet.ccf";
	
	// Landscape contributor extension point ID
	public static final String LANDSCAPE_CONTRIBUTORS = "com.collabnet.ccf.landscapeContributors"; //$NON-NLS-1$	
	
	private static ILandscapeContributor[] landscapeContributors;
	
	// Images
	public static final String IMAGE_NEW_LANDSCAPE = "new_landscape.gif"; //$NON-NLS-1$
	public static final String IMAGE_NEW_LANDSCAPE_WIZBAN = "new_landscape_wizban.gif"; //$NON-NLS-1$
	public static final String IMAGE_LANDSCAPE = "landscape.gif"; //$NON-NLS-1$
	public static final String IMAGE_LANDSCAPE_QC_PT = "landscape_QC_PT.gif"; //$NON-NLS-1$
	public static final String IMAGE_LANDSCAPE_QC_TF = "landscape_QC_TF.gif"; //$NON-NLS-1$
	public static final String IMAGE_REFRESH = "refresh.gif"; //$NON-NLS-1$
	public static final String IMAGE_FILTERS = "filters.gif"; //$NON-NLS-1$
	public static final String IMAGE_DATABASE_CONNECTION = "dbConnection.gif"; //$NON-NLS-1$
	public static final String IMAGE_HOSPITAL_ENTRY = "hospitalEntry.gif"; //$NON-NLS-1$
	public static final String IMAGE_HOSPITAL_ENTRY_FIXED = "hospitalEntryFixed.gif"; //$NON-NLS-1$
	public static final String IMAGE_SYSTEM_CEE = "system_cee.gif"; //$NON-NLS-1$
	public static final String IMAGE_SYSTEM_SFEE = "system_sfee.gif"; //$NON-NLS-1$
	public static final String IMAGE_SYSTEM_QC = "system_qc.gif"; //$NON-NLS-1$
	public static final String IMAGE_SYSTEM_OTHER = "system_other.gif"; //$NON-NLS-1$
	public static final String IMAGE_SYNC_STATUS_ENTRY = "sync_status_entry.gif"; //$NON-NLS-1$
	
	// Preferences
	public static final String PREFERENCES_DATABASE_DESCRIPTION = "pref_db_description";
	public static final String PREFERENCES_DATABASE_DRIVER = "pref_db_driver";
	public static final String PREFERENCES_DATABASE_URL = "pref_db_url";
	public static final String PREFERENCES_DATABASE_USER = "pref_db_user";
	public static final String PREFERENCES_DATABASE_PASSWORD = "pref_db_password";
	public static final String PREFERENCES_AUTOCONNECT = "pref_autoconnect";
	public static final String PREFERENCES_HOSPITAL_COLUMNS = "hospital_columns";
	
	// Default database
	public static final String DATABASE_DEFAULT_DESCRIPTION = "Default";
	public static final String DATABASE_DEFAULT_DRIVER = "org.hsqldb.jdbcDriver";
	public static final String DATABASE_DEFAULT_URL = "jdbc:hsqldb:hsql://localhost/xdb";
	public static final String DATABASE_DEFAULT_USER = "sa";
	public static final String DATABASE_DEFAULT_PASSWORD = "";
	public static final boolean DEFAULT_AUTOCONNECT = false;
	public static final String DEFAULT_HOSPITAL_COLUMNS = CcfDataProvider.DEFAULT_HOSPITAL_COLUMNS;
	
	// The shared instance
	private static Activator plugin;
	
	private static List<Image> landscapeContributorImages = new ArrayList<Image>();
	
	public static final String PREF_CCF_LANDSCAPES_NODE = "ccfLandscapes"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		landscapeContributors = getLandscapeContributors();
	
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		Iterator<Image> iter = landscapeContributorImages.iterator();
		while(iter.hasNext()) {
			Image image = (Image)iter.next();
			image.dispose();
		}
		super.stop(context);
	}

	// Initialize the landscape contributors by searching the registry for users of the
	// landscape contributors extension point.	
	public static ILandscapeContributor[] getLandscapeContributors() throws Exception {
		if (landscapeContributors == null) {
			ArrayList<ILandscapeContributor> landscapeContributorList = new ArrayList<ILandscapeContributor>();
			IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
			IConfigurationElement[] configurationElements = extensionRegistry.getConfigurationElementsFor(LANDSCAPE_CONTRIBUTORS);
			for (int i = 0; i < configurationElements.length; i++) {
				IConfigurationElement configurationElement = configurationElements[i];
				ILandscapeContributor landscapeContributor = (ILandscapeContributor)configurationElement.createExecutableExtension("class"); //$NON-NLS-1$
				landscapeContributor.setName(configurationElement.getAttribute("name")); //$NON-NLS-1$
				landscapeContributor.setDescription(configurationElement.getAttribute("description")); //$NON-NLS-1$
				String imageKey = configurationElement.getAttribute("image"); //$NON-NLS-1$
				if (imageKey != null) {
					ImageDescriptor imageDescriptor = imageDescriptorFromPlugin(PLUGIN_ID, "icons/" + imageKey); //$NON-NLS-1$
					Image image = imageDescriptor.createImage();
					landscapeContributorImages.add(image);
					landscapeContributor.setImage(image);
				}
				String seq = configurationElement.getAttribute("sequence"); //$NON-NLS-1$
				if (seq != null) landscapeContributor.setSequence(Integer.parseInt(seq));				
				landscapeContributorList.add(landscapeContributor);
			}
			landscapeContributors = new ILandscapeContributor[landscapeContributorList.size()];
			landscapeContributorList.toArray(landscapeContributors);	
			Arrays.sort(landscapeContributors);
		}
		return landscapeContributors;
	}
	
	public boolean storeLandscape(String description, ILandscapeContributor landscapeContributor) {
		Preferences prefs = getInstancePreferences().node(PREF_CCF_LANDSCAPES_NODE).node(description); //$NON-NLS-1$
		prefs.put("type1", landscapeContributor.getType1());
		prefs.put("type2", landscapeContributor.getType2());
		prefs.put("configFolder", landscapeContributor.getConfigurationFolder());
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			handleError(e);
			return false;
		}
		return true;
	}
	
	public boolean deleteLandscape(Landscape landscape) {
		Preferences node = landscape.getNode();
		if (node != null) {
			try {
				node.removeNode();
			} catch (BackingStoreException e) {
				handleError(e);
				return false;
			}
			return true;
		}
		return true;
	}
	
	public Landscape[] getLandscapes() {
		List<Landscape> landscapes = new ArrayList<Landscape>();
		try {
			String[] childrenNames = getInstancePreferences().node(PREF_CCF_LANDSCAPES_NODE).childrenNames();
			for (int i = 0; i < childrenNames.length; i++) {
				Preferences node = getInstancePreferences().node(PREF_CCF_LANDSCAPES_NODE).node(childrenNames[i]); //$NON-NLS-1$
				Landscape landscape = new Landscape();
				landscape.setDescription(childrenNames[i]);
				landscape.setType1(node.get("type1", ""));
				landscape.setType2(node.get("type2", ""));
				landscape.setConfigurationFolder(node.get("configFolder", ""));
				landscape.setNode(node);
				landscapes.add(landscape);
			}
			Landscape[] landscapeArray = new Landscape[landscapes.size()];
			landscapes.toArray(landscapeArray);
			return landscapeArray;
		} catch (Exception e) {
			handleError(e);
		}
		return new Landscape[0];
	}
	
	public org.osgi.service.prefs.Preferences getInstancePreferences() {
		return new InstanceScope().getNode(getBundle().getSymbolicName());
	}		

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	public static void handleError(Exception exception) {
		handleError(null, exception);
	}
	
	public static void handleError(String message, Exception exception) {
		if (message == null) getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, exception.getMessage(), exception));
		else getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, exception));
	}

    /**
     * Returns the image descriptor for the given image ID.
     * Returns null if there is no such image.
     */
    public ImageDescriptor getImageDescriptor(String id) {
    	if (imageDescriptors == null);
    		this.initializeImages();
		return (ImageDescriptor) imageDescriptors.get(id);
    }


	/**
	 * Creates an image and places it in the image registry.
	 */
	private void createImageDescriptor(String id) {
		imageDescriptors.put(id, imageDescriptorFromPlugin(PLUGIN_ID, "icons/" + id)); //$NON-NLS-1$
	}
	
	public static Image getImage(String key) {
		return getDefault().getImageRegistry().get(key);
	}
	
	public static Image getImage(Landscape landscape) {
		Image image = getImage("landscape_" + landscape.getType1() + "_" + landscape.getType2() + ".gif");
		if (image == null) image = getImage("landscape.gif");
		return image;
	}
	
	private void initializeImages() {
		imageDescriptors = new Hashtable<String, ImageDescriptor>(40);
		createImageDescriptor(IMAGE_REFRESH);
		createImageDescriptor(IMAGE_FILTERS);
		createImageDescriptor(IMAGE_DATABASE_CONNECTION);
		createImageDescriptor(IMAGE_HOSPITAL_ENTRY);
		createImageDescriptor(IMAGE_HOSPITAL_ENTRY_FIXED);
		createImageDescriptor(IMAGE_SYSTEM_CEE);
		createImageDescriptor(IMAGE_SYSTEM_SFEE);
		createImageDescriptor(IMAGE_SYSTEM_QC);
		createImageDescriptor(IMAGE_SYSTEM_OTHER);
		createImageDescriptor(IMAGE_SYNC_STATUS_ENTRY);
		createImageDescriptor(IMAGE_NEW_LANDSCAPE);
		createImageDescriptor(IMAGE_NEW_LANDSCAPE_WIZBAN);
		createImageDescriptor(IMAGE_LANDSCAPE);
		createImageDescriptor(IMAGE_LANDSCAPE_QC_PT);
		createImageDescriptor(IMAGE_LANDSCAPE_QC_TF);
	}
	
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(IMAGE_REFRESH, getImageDescriptor(IMAGE_REFRESH));
		reg.put(IMAGE_FILTERS, getImageDescriptor(IMAGE_FILTERS));
		reg.put(IMAGE_DATABASE_CONNECTION, getImageDescriptor(IMAGE_DATABASE_CONNECTION));
		reg.put(IMAGE_HOSPITAL_ENTRY, getImageDescriptor(IMAGE_HOSPITAL_ENTRY));
		reg.put(IMAGE_HOSPITAL_ENTRY_FIXED, getImageDescriptor(IMAGE_HOSPITAL_ENTRY_FIXED));
		reg.put(IMAGE_SYSTEM_CEE, getImageDescriptor(IMAGE_SYSTEM_CEE));
		reg.put(IMAGE_SYSTEM_SFEE, getImageDescriptor(IMAGE_SYSTEM_SFEE));
		reg.put(IMAGE_SYSTEM_QC, getImageDescriptor(IMAGE_SYSTEM_QC));
		reg.put(IMAGE_SYSTEM_OTHER, getImageDescriptor(IMAGE_SYSTEM_OTHER));
		reg.put(IMAGE_SYNC_STATUS_ENTRY, getImageDescriptor(IMAGE_SYNC_STATUS_ENTRY));
		reg.put(IMAGE_NEW_LANDSCAPE, getImageDescriptor(IMAGE_NEW_LANDSCAPE));
		reg.put(IMAGE_NEW_LANDSCAPE_WIZBAN, getImageDescriptor(IMAGE_NEW_LANDSCAPE_WIZBAN));
		reg.put(IMAGE_LANDSCAPE, getImageDescriptor(IMAGE_LANDSCAPE));
		reg.put(IMAGE_LANDSCAPE_QC_PT, getImageDescriptor(IMAGE_LANDSCAPE_QC_PT));
		reg.put(IMAGE_LANDSCAPE_QC_TF, getImageDescriptor(IMAGE_LANDSCAPE_QC_TF));
	}
}

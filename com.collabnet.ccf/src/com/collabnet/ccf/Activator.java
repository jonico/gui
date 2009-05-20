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
import com.collabnet.ccf.model.ProjectMappings;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	
	private Hashtable<String, ImageDescriptor> imageDescriptors;

	// The plug-in ID
	public static final String PLUGIN_ID = "com.collabnet.ccf"; //$NON-NLS-1$
	
	// Landscape contributor extension point ID
	public static final String LANDSCAPE_CONTRIBUTORS = "com.collabnet.ccf.landscapeContributors"; //$NON-NLS-1$	
	
	private static ILandscapeContributor[] landscapeContributors;
	private static List<IProjectMappingsChangeListener> changeListeners = new ArrayList<IProjectMappingsChangeListener>();
	
	// Images
	public static final String IMAGE_ERROR = "error.gif"; //$NON-NLS-1$
	public static final String IMAGE_NEW_LANDSCAPE = "new_landscape.gif"; //$NON-NLS-1$
	public static final String IMAGE_NEW_LANDSCAPE_WIZBAN = "new_landscape_wizban.png"; //$NON-NLS-1$
	public static final String IMAGE_LANDSCAPE = "landscape.gif"; //$NON-NLS-1$
	public static final String IMAGE_PROJECT_MAPPINGS = "project_mappings.gif"; //$NON-NLS-1$
	public static final String IMAGE_LOGS = "logs.gif"; //$NON-NLS-1$
	public static final String IMAGE_LOG = "log.gif"; //$NON-NLS-1$
	public static final String IMAGE_LANDSCAPE_QC_PT = "landscape_QC_PT.gif"; //$NON-NLS-1$
	public static final String IMAGE_LANDSCAPE_QC_TF = "landscape_QC_TF.gif"; //$NON-NLS-1$
	public static final String IMAGE_REFRESH = "refresh.gif"; //$NON-NLS-1$
	public static final String IMAGE_FILTERS = "filters.gif"; //$NON-NLS-1$
	public static final String IMAGE_FORWARD = "forward_nav.gif"; //$NON-NLS-1$
	public static final String IMAGE_BACKWARD = "nav_backward.gif"; //$NON-NLS-1$
	public static final String IMAGE_DATABASE_CONNECTION = "dbConnection.gif"; //$NON-NLS-1$
	public static final String IMAGE_HOSPITAL_ENTRY = "hospitalEntry.gif"; //$NON-NLS-1$
	public static final String IMAGE_HOSPITAL_ENTRY_FIXED = "hospitalEntryFixed.gif"; //$NON-NLS-1$
	public static final String IMAGE_HOSPITAL_ENTRY_REPLAY = "hospitalEntryReplay.gif"; //$NON-NLS-1$
	public static final String IMAGE_SYNC_STATUS_ENTRY = "sync_status_entry.gif"; //$NON-NLS-1$
	public static final String IMAGE_SYNC_STATUS_ENTRY_PAUSED = "sync_status_entry_paused.gif"; //$NON-NLS-1$
	public static final String IMAGE_IDENTITY_MAPPING = "identityMappingView.gif"; //$NON-NLS-1$
	
	// Preferences
	public static final String PREFERENCES_DATABASE_DESCRIPTION = "pref_db_description"; //$NON-NLS-1$
	public static final String PREFERENCES_DATABASE_DRIVER = "pref_db_driver"; //$NON-NLS-1$
	public static final String PREFERENCES_DATABASE_URL = "pref_db_url"; //$NON-NLS-1$
	public static final String PREFERENCES_DATABASE_USER = "pref_db_user"; //$NON-NLS-1$
	public static final String PREFERENCES_DATABASE_PASSWORD = "pref_db_password"; //$NON-NLS-1$
	public static final String PREFERENCES_AUTOCONNECT = "pref_autoconnect"; //$NON-NLS-1$
	public static final String PREFERENCES_HOSPITAL_COLUMNS = "hospital_columns"; //$NON-NLS-1$
	public static final String PREFERENCES_IDENTITY_MAPPING_COLUMNS = "identity_mapping_columns"; //$NON-NLS-1$
	public static final String PREFERENCES_RESET_DELAY = "pref_reset_delay"; //$NON-NLS-1$
	
	// CCF Properties
	public static final String PROPERTIES_CCF_URL = "ccf.db.url"; //$NON-NLS-1$
	public static final String PROPERTIES_CCF_DRIVER = "ccf.db.driver"; //$NON-NLS-1$
	public static final String PROPERTIES_CCF_USER = "ccf.db.username"; //$NON-NLS-1$
	public static final String PROPERTIES_CCF_PASSWORD = "ccf.db.password"; //$NON-NLS-1$
	public static final String PROPERTIES_CCF_LOG_MESSAGE_TEMPLATE = "ccf.logMessageTemplate"; //$NON-NLS-1$
	public static final String PROPERTIES_CCF_JMX_PORT = "ccf.jmxPort"; //$NON-NLS-1$
	public static final String PROPERTIES_CCF_HOST_NAME = "ccf.ccfHostName"; //$NON-NLS-1$

	public static final String PROPERTIES_SYSTEM_TIMEZONE = "gui.system.timezone"; //$NON-NLS-1$
	public static final String PROPERTIES_SYSTEM_ENCODING = "gui.system.encoding"; //$NON-NLS-1$
	
	// QC Properties
	public static final String PROPERTIES_QC_URL = "qc.system.1.url"; //$NON-NLS-1$
	public static final String PROPERTIES_QC_USER = "qc.system.1.username"; //$NON-NLS-1$
	public static final String PROPERTIES_QC_PASSWORD = "qc.system.1.password"; //$NON-NLS-1$
	public static final String PROPERTIES_QC_RESYNC_USER = "qc.system.1.resync.username"; //$NON-NLS-1$
	public static final String PROPERTIES_QC_RESYNC_PASSWORD = "qc.system.1.resync.password"; //$NON-NLS-1$
	public static final String PROPERTIES_QC_ATTACHMENT_SIZE = "qc.max.attachmentsize.per.artifact"; //$NON-NLS-1$
	
	// SFEE Properties
	public static final String PROPERTIES_SFEE_URL = "sfee.server.1.url"; //$NON-NLS-1$
	public static final String PROPERTIES_SFEE_USER = "sfee.server.1.username"; //$NON-NLS-1$
	public static final String PROPERTIES_SFEE_PASSWORD = "sfee.server.1.password"; //$NON-NLS-1$
	public static final String PROPERTIES_SFEE_RESYNC_USER = "sfee.server.1.resync.username"; //$NON-NLS-1$
	public static final String PROPERTIES_SFEE_RESYNC_PASSWORD = "sfee.server.1.resync.password"; //$NON-NLS-1$
	public static final String PROPERTIES_SFEE_ATTACHMENT_SIZE = "sfee.max.attachmentsize.per.artifact"; //$NON-NLS-1$
	
	// CEE Properties
	public static final String PROPERTIES_CEE_URL = "cee.server.1.url"; //$NON-NLS-1$
	public static final String PROPERTIES_CEE_USER = "cee.server.1.username"; //$NON-NLS-1$
	public static final String PROPERTIES_CEE_DISPLAY_NAME = "cee.server.1.connector.user.displayName"; //$NON-NLS-1$
	public static final String PROPERTIES_CEE_PASSWORD = "cee.server.1.password"; //$NON-NLS-1$
	public static final String PROPERTIES_CEE_RESYNC_USER = "cee.server.1.resync.username"; //$NON-NLS-1$
	public static final String PROPERTIES_CEE_RESYNC_DISPLAY_NAME = "cee.server.1.resync.user.displayName"; //$NON-NLS-1$
	public static final String PROPERTIES_CEE_RESYNC_PASSWORD = "cee.server.1.resync.password"; //$NON-NLS-1$
	public static final String PROPERTIES_CEE_ATTACHMENT_SIZE = "cee.max.attachmentsize.per.artifact";	 //$NON-NLS-1$
	
	// Default database
	public static final String DATABASE_DEFAULT_DESCRIPTION = "Default"; //$NON-NLS-1$
	public static final String DATABASE_DEFAULT_DRIVER = "org.hsqldb.jdbcDriver"; //$NON-NLS-1$
	public static final String DATABASE_DEFAULT_URL = "jdbc:hsqldb:hsql://localhost/xdb"; //$NON-NLS-1$
	public static final String DATABASE_DEFAULT_USER = "sa"; //$NON-NLS-1$
	public static final String DATABASE_DEFAULT_PASSWORD = ""; //$NON-NLS-1$
	public static final boolean DEFAULT_AUTOCONNECT = false;
	public static final String DEFAULT_HOSPITAL_COLUMNS = CcfDataProvider.DEFAULT_HOSPITAL_COLUMNS;
	public static final String DEFAULT_IDENTITY_MAPPING_COLUMNS = CcfDataProvider.DEFAULT_IDENTITY_MAPPING_COLUMNS;
	
	public static final String DEFAULT_JMX_PORT_PT2QC = "8082";
	public static final String DEFAULT_JMX_PORT_QC2PT = "8083";
	public static final String DEFAULT_JMX_PORT_QC2SFEE = "8084";
	public static final String DEFAULT_JMX_PORT_SFEE2QC = "8085";
	public static final String DEFAULT_LOG_MESSAGE_TEMPLATE = "An Artifact has been quarantined.\n\nSOURCE_SYSTEM_ID: <SOURCE_SYSTEM_ID> \nSOURCE_REPOSITORY_ID: <SOURCE_REPOSITORY_ID> \nSOURCE_ARTIFACT_ID: <SOURCE_ARTIFACT_ID> \nTARGET_SYSTEM_ID: <TARGET_SYSTEM_ID> \nTARGET_REPOSITORY_ID: <TARGET_REPOSITORY_ID> \nTARGET_ARTIFACT_ID: <TARGET_ARTIFACT_ID> \nERROR_CODE: <ERROR_CODE> \nTIMESTAMP: <TIMESTAMP> \nEXCEPTION_CLASS_NAME: <EXCEPTION_CLASS_NAME> \nEXCEPTION_MESSAGE: <EXCEPTION_MESSAGE> \nCAUSE_EXCEPTION_CLASS_NAME: <CAUSE_EXCEPTION_CLASS_NAME> \nCAUSE_EXCEPTION_MESSAGE: <CAUSE_EXCEPTION_MESSAGE> \nSTACK_TRACE: <STACK_TRACE> \nADAPTOR_NAME: <ADAPTOR_NAME> \nORIGINATING_COMPONENT: <ORIGINATING_COMPONENT> \nDATA_TYPE: <DATA_TYPE> \nDATA: <DATA> \nTHREAD_NAME: <THREAD_NAME> \nFIXED: <FIXED> \nREPROCESSED: <REPROCESSED> \nSOURCE_SYSTEM_KIND: <SOURCE_SYSTEM_KIND> \nSOURCE_REPOSITORY_KIND: <SOURCE_REPOSITORY_KIND> \nTARGET_SYSTEM_KIND: <TARGET_SYSTEM_KIND> \nTARGET_REPOSITORY_KIND: <TARGET_REPOSITORY_KIND> \nSOURCE_LAST_MODIFICATION_TIME: <SOURCE_LAST_MODIFICATION_TIME> \nTARGET_LAST_MODIFICATION_TIME: <TARGET_LAST_MODIFICATION_TIME> \nSOURCE_ARTIFACT_VERSION: <SOURCE_ARTIFACT_VERSION> \nTARGET_ARTIFACT_VERSION: <TARGET_ARTIFACT_VERSION> \nARTIFACT_TYPE: <ARTIFACT_TYPE> \nGENERIC_ARTIFACT: <GENERIC_ARTIFACT>"; //$NON-NLS-1$
	public static final int DEFAULT_RESET_DELAY = 10;
	
	// The shared instance
	private static Activator plugin;
	
	private static List<Image> landscapeContributorImages = new ArrayList<Image>();
	
	public static final String PREF_CCF_LANDSCAPES_NODE = "ccfLandscapes"; //$NON-NLS-1$

	public static final String SAMPLE_XSL_FILE_NAME = "sample.xsl"; //$NON-NLS-1$
	
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
	
	public static void addChangeListener(IProjectMappingsChangeListener listener) {
		changeListeners.add(listener);
	}
	
	public static void removeChangeListener(IProjectMappingsChangeListener listener) {
		changeListeners.remove(listener);
	}
	
	public static void notifyChanged(ProjectMappings projectMappings) {
	for (IProjectMappingsChangeListener listener : Activator.changeListeners) {
		listener.changed(projectMappings);
	}
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
				landscapeContributor.setId(configurationElement.getAttribute("id")); //$NON-NLS-1$
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
	
	public static ILandscapeContributor getLandscapeContributor(Landscape landscape) throws Exception {
		ILandscapeContributor landscapeContributor = null;
		landscapeContributors = getLandscapeContributors();
		for (int i = 0; i < landscapeContributors.length; i++) {
			if (landscapeContributors[i].getId().equals(landscape.getContributorId())) {
				landscapeContributor = landscapeContributors[i];
				break;
			}
		}
		return landscapeContributor;
	}
	
	public boolean storeLandscape(String description, ILandscapeContributor landscapeContributor) {
		Landscape landscape = new Landscape();
		landscape.setDescription(description);
		landscape.setType1(landscapeContributor.getType1());
		landscape.setType2(landscapeContributor.getType2());
		landscape.setConfigurationFolder1(landscapeContributor.getConfigurationFolder1());
		landscape.setConfigurationFolder2(landscapeContributor.getConfigurationFolder2());
		landscape.setContributorId(landscapeContributor.getId());
		return storeLandscape(landscape);
	}
	
	public boolean storeLandscape(Landscape landscape) {
		Preferences prefs = getInstancePreferences().node(PREF_CCF_LANDSCAPES_NODE).node(landscape.getDescription().replaceAll("/", "%slash%")); //$NON-NLS-1$ //$NON-NLS-2$
		prefs.put("type1", landscape.getType1()); //$NON-NLS-1$
		prefs.put("type2", landscape.getType2()); //$NON-NLS-1$
		prefs.put("configFolder1", landscape.getConfigurationFolder1()); //$NON-NLS-1$
		if (landscape.getConfigurationFolder2() != null) prefs.put("configFolder2", landscape.getConfigurationFolder2()); //$NON-NLS-1$
		prefs.put("contributorId", landscape.getContributorId()); //$NON-NLS-1$
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
	
	public Landscape getLandscape(String description) {
		Landscape[] landscapes = getLandscapes();
		for (int i = 0; i < landscapes.length; i++) {
			if (landscapes[i].getDescription().equals(description)) return landscapes[i];
		}
		return null;
	}
	
	public Landscape[] getLandscapes() {
		List<Landscape> landscapes = new ArrayList<Landscape>();
		try {
			String[] childrenNames = getInstancePreferences().node(PREF_CCF_LANDSCAPES_NODE).childrenNames();
			for (int i = 0; i < childrenNames.length; i++) {
				Preferences node = getInstancePreferences().node(PREF_CCF_LANDSCAPES_NODE).node(childrenNames[i]); //$NON-NLS-1$
				Landscape landscape = new Landscape();
				landscape.setDescription(childrenNames[i].replaceAll("%slash%", "/")); //$NON-NLS-1$ //$NON-NLS-2$
				landscape.setType1(node.get("type1", "")); //$NON-NLS-1$ //$NON-NLS-2$
				landscape.setType2(node.get("type2", "")); //$NON-NLS-1$ //$NON-NLS-2$
				landscape.setConfigurationFolder1(node.get("configFolder1", "")); //$NON-NLS-1$ //$NON-NLS-2$
				landscape.setConfigurationFolder2(node.get("configFolder2", "")); //$NON-NLS-1$ //$NON-NLS-2$
				landscape.setNode(node);
				landscape.setContributorId(node.get("contributorId", "")); //$NON-NLS-1$ //$NON-NLS-2$
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
		Image image = getImage("landscape_" + landscape.getType1() + "_" + landscape.getType2() + ".gif"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (image == null) image = getImage("landscape.gif"); //$NON-NLS-1$
		return image;
	}
	
	private void initializeImages() {
		imageDescriptors = new Hashtable<String, ImageDescriptor>(40);
		createImageDescriptor(IMAGE_REFRESH);
		createImageDescriptor(IMAGE_FILTERS);
		createImageDescriptor(IMAGE_FORWARD);
		createImageDescriptor(IMAGE_BACKWARD);
		createImageDescriptor(IMAGE_DATABASE_CONNECTION);
		createImageDescriptor(IMAGE_HOSPITAL_ENTRY);
		createImageDescriptor(IMAGE_HOSPITAL_ENTRY_FIXED);
		createImageDescriptor(IMAGE_HOSPITAL_ENTRY_REPLAY);
		createImageDescriptor(IMAGE_SYNC_STATUS_ENTRY);
		createImageDescriptor(IMAGE_SYNC_STATUS_ENTRY_PAUSED);
		createImageDescriptor(IMAGE_NEW_LANDSCAPE);
		createImageDescriptor(IMAGE_NEW_LANDSCAPE_WIZBAN);
		createImageDescriptor(IMAGE_LANDSCAPE);
		createImageDescriptor(IMAGE_LANDSCAPE_QC_PT);
		createImageDescriptor(IMAGE_LANDSCAPE_QC_TF);
		createImageDescriptor(IMAGE_PROJECT_MAPPINGS);
		createImageDescriptor(IMAGE_LOGS);
		createImageDescriptor(IMAGE_LOG);
		createImageDescriptor(IMAGE_ERROR);
		createImageDescriptor(IMAGE_IDENTITY_MAPPING);
	}
	
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(IMAGE_REFRESH, getImageDescriptor(IMAGE_REFRESH));
		reg.put(IMAGE_FILTERS, getImageDescriptor(IMAGE_FILTERS));
		reg.put(IMAGE_FORWARD, getImageDescriptor(IMAGE_FORWARD));
		reg.put(IMAGE_BACKWARD, getImageDescriptor(IMAGE_BACKWARD));
		reg.put(IMAGE_DATABASE_CONNECTION, getImageDescriptor(IMAGE_DATABASE_CONNECTION));
		reg.put(IMAGE_HOSPITAL_ENTRY, getImageDescriptor(IMAGE_HOSPITAL_ENTRY));
		reg.put(IMAGE_HOSPITAL_ENTRY_FIXED, getImageDescriptor(IMAGE_HOSPITAL_ENTRY_FIXED));
		reg.put(IMAGE_HOSPITAL_ENTRY_REPLAY, getImageDescriptor(IMAGE_HOSPITAL_ENTRY_REPLAY));
		reg.put(IMAGE_SYNC_STATUS_ENTRY, getImageDescriptor(IMAGE_SYNC_STATUS_ENTRY));
		reg.put(IMAGE_SYNC_STATUS_ENTRY_PAUSED, getImageDescriptor(IMAGE_SYNC_STATUS_ENTRY_PAUSED));
		reg.put(IMAGE_NEW_LANDSCAPE, getImageDescriptor(IMAGE_NEW_LANDSCAPE));
		reg.put(IMAGE_NEW_LANDSCAPE_WIZBAN, getImageDescriptor(IMAGE_NEW_LANDSCAPE_WIZBAN));
		reg.put(IMAGE_LANDSCAPE, getImageDescriptor(IMAGE_LANDSCAPE));
		reg.put(IMAGE_LANDSCAPE_QC_PT, getImageDescriptor(IMAGE_LANDSCAPE_QC_PT));
		reg.put(IMAGE_LANDSCAPE_QC_TF, getImageDescriptor(IMAGE_LANDSCAPE_QC_TF));
		reg.put(IMAGE_PROJECT_MAPPINGS, getImageDescriptor(IMAGE_PROJECT_MAPPINGS));
		reg.put(IMAGE_LOGS, getImageDescriptor(IMAGE_LOGS));
		reg.put(IMAGE_LOG, getImageDescriptor(IMAGE_LOG));
		reg.put(IMAGE_ERROR, getImageDescriptor(IMAGE_ERROR));
		reg.put(IMAGE_IDENTITY_MAPPING, getImageDescriptor(IMAGE_IDENTITY_MAPPING));
	}
}

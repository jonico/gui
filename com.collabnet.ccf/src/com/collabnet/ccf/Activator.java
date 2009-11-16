package com.collabnet.ccf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
import org.osgi.util.tracker.ServiceTracker;

import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.model.AdministratorLandscape;
import com.collabnet.ccf.model.Database;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.OperatorLandscape;
import com.collabnet.ccf.model.Patient;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.Role;
import com.collabnet.ccf.schemageneration.Proxy;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	private ServiceTracker proxyServiceTracker;
	
	private Hashtable<String, ImageDescriptor> imageDescriptors;

	// The plug-in ID
	public static final String PLUGIN_ID = "com.collabnet.ccf"; //$NON-NLS-1$
	
	// Landscape contributor extension point ID
	public static final String LANDSCAPE_CONTRIBUTORS = "com.collabnet.ccf.landscapeContributors"; //$NON-NLS-1$	
	
	private static ILandscapeContributor[] landscapeContributors;
	private static List<IProjectMappingsChangeListener> changeListeners = new ArrayList<IProjectMappingsChangeListener>();
	private static List<IRoleChangedListener> roleChangedListeners = new ArrayList<IRoleChangedListener>();
	
	// Images
	public static final String IMAGE_ERROR = "error.gif"; //$NON-NLS-1$
	public static final String IMAGE_NEW_LANDSCAPE = "new_landscape.gif"; //$NON-NLS-1$
	public static final String IMAGE_NEW_LANDSCAPE_WIZBAN = "new_landscape_wizban.png"; //$NON-NLS-1$
	public static final String IMAGE_NEW_PROJECT_MAPPING_WIZBAN = "new_project_mapping_wizban.png"; //$NON-NLS-1$
	public static final String IMAGE_EDIT_FIELD_MAPPINGS_WIZBAN = "edit_field_mappings_wizban.png"; //$NON-NLS-1$
	public static final String IMAGE_LANDSCAPE = "landscape.gif"; //$NON-NLS-1$
	public static final String IMAGE_PROJECT_MAPPINGS = "project_mappings.gif"; //$NON-NLS-1$
	public static final String IMAGE_LOGS = "logs.gif"; //$NON-NLS-1$
	public static final String IMAGE_LOG = "log.gif"; //$NON-NLS-1$
	public static final String IMAGE_LANDSCAPE_QC_PT = "landscape_QC_PT.gif"; //$NON-NLS-1$
	public static final String IMAGE_LANDSCAPE_QC_TF = "landscape_QC_TF.gif"; //$NON-NLS-1$
	public static final String IMAGE_LANDSCAPE_OPERATOR = "landscape_operator.gif"; //$NON-NLS-1$
	public static final String IMAGE_REFRESH = "refresh.gif"; //$NON-NLS-1$
	public static final String IMAGE_FILTERS = "filters.gif"; //$NON-NLS-1$
	public static final String IMAGE_FORWARD = "forward_nav.gif"; //$NON-NLS-1$
	public static final String IMAGE_BACKWARD = "nav_backward.gif"; //$NON-NLS-1$
	public static final String IMAGE_DATABASE_CONNECTION = "dbConnection.gif"; //$NON-NLS-1$
	public static final String IMAGE_HOSPITAL_ENTRY = "hospitalEntry.gif"; //$NON-NLS-1$
	public static final String IMAGE_HOSPITAL_ENTRY_OUTDATED = "hospitalEntryOutdated.gif"; //$NON-NLS-1$
	public static final String IMAGE_HOSPITAL_ENTRY_FIXED = "hospitalEntryFixed.gif"; //$NON-NLS-1$
	public static final String IMAGE_HOSPITAL_ENTRY_REPLAY = "hospitalEntryReplay.gif"; //$NON-NLS-1$
	public static final String IMAGE_HOSPITAL_ENTRY_REPLAY_FAILED = "hospitalEntryReplayFailed.gif"; //$NON-NLS-1$
	public static final String IMAGE_SYNC_STATUS_ENTRY = "sync_status_entry.gif"; //$NON-NLS-1$
	public static final String IMAGE_SYNC_STATUS_ENTRY_PAUSED = "sync_status_entry_paused.gif"; //$NON-NLS-1$
	public static final String IMAGE_SYNC_STATUS_ENTRY_WITH_HOSPITAL_ENTRIES = "sync_status_entry_with_hospital_entries.gif"; //$NON-NLS-1$
	public static final String IMAGE_IDENTITY_MAPPING = "identityMappingView.gif"; //$NON-NLS-1$
	public static final String IMAGE_MULTIPLE_SOURCE = "multiple_source.gif"; //$NON-NLS-1$
	public static final String IMAGE_MULTIPLE_TARGET = "multiple_target.gif"; //$NON-NLS-1$
	public static final String IMAGE_ONE_WAY = "one_way.gif"; //$NON-NLS-1$
	public static final String IMAGE_NO_INCONSISTENCIES = "no_inconsistencies.gif"; //$NON-NLS-1$
	public static final String IMAGE_MONITOR = "monitor.gif"; //$NON-NLS-1$
	
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
	public static final String PREFERENCES_SHOW_HOSPITAL_COUNT = "pref_show_hospital_count"; //$NON-NLS-1$	
	public static final String PREFERENCES_ACTIVE_ROLE = "pref_active_role"; //$NON-NLS-1$
	public static final String PREFERENCES_GRAPHICAL_MAPPING_AVAILABLE = "pref_graphical_mapping_available"; //$NON-NLS-1$
	public static final String PREFERENCES_MAPFORCE_PATH = "pref_mapforce_path"; //$NON-NLS-1$
	
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
	public static final boolean DEFAULT_SHOW_HOSPITAL_COUNT = true;
	public static final boolean DEFAULT_GRAPHICAL_MAPPING_AVAILABLE = true;
	public static final String DEFAULT_MAPFORCE_PATH = "C:\\Program Files\\Altova\\Mapforce2009\\MapForce.exe";
	
	public static final String DEFAULT_CCF_HOST = "http://localhost"; //$NON-NLS-1$
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
	public static final String PREF_CCF_ROLES_NODE = "ccfRoles"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_PASSWORD = "password"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_ADD_LANDSCAPE = "addLandscape"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_EDIT_LANDSCAPE = "editLandscape"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_DELETE_LANDSCAPE = "deleteLandscape"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_ADD_PROJECT_MAPPING = "addProjectMapping"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_CHANGE_PROJECT_MAPPING = "changeProjectMapping"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_DELETE_PROJECT_MAPPING = "deleteProjectMapping"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_EDIT_FIELD_MAPPINGS = "editFieldMappings"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_PAUSE_SYNCHRONIZATION = "pauseSynchronization"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_RESUME_SYNCHRONIZATION = "resumeSynchronization"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_RESET_SYNCHRONIZATION_STATUS = "resetSynchronizationStatus"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_DELETE_PROJECT_MAPPING_IDENTITY_MAPPINGS = "deleteProjectMappingIdentityMappings"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_EDIT_QUARANTINED_ARTIFACT = "editQuarantinedArtifact"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_MARK_AS_FIXED = "markAsFixed"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_REOPEN = "reopen"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_REPLAY = "replay"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_CANCEL_REPLAY = "cancelReplay"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_DELETE_HOSPITAL_ENTRY = "deleteHospitalEntry"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_CREATE_REVERSE_IDENTITY_MAPPING = "createReverseIdentityMapping"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_DELETE_IDENTITY_MAPPING = "deleteIdentityMapping"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_EDIT_IDENTITY_MAPPING = "editIdentityMapping"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_EDIT_LOG_SETTINGS = "editLogSettings"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_CONSISTENCY_CHECK = "consistencyCheck"; //$NON-NLS-1$
	public static final String PREF_CCF_ROLES_MAINTAIN_ROLES = "maintainRoles"; //$NON-NLS-1$
	
	public static final String SAMPLE_XSL_FILE_NAME = "sample.xsl"; //$NON-NLS-1$
	public static final String CREATE_INITIAL_MFD_FILE_NAME = "CreateInitialMFD.xsl"; //$NON-NLS-1$
	
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
	
	public static void addRoleChangedListener(IRoleChangedListener listener) {
		roleChangedListeners.add(listener);
	}
	
	public static void removeRoleChangedListener(IRoleChangedListener listener) {
		roleChangedListeners.remove(listener);
	}
	
	public static void notifyChanged(ProjectMappings projectMappings) {
		for (IProjectMappingsChangeListener listener : Activator.changeListeners) {
			listener.changed(projectMappings);
		}
	}
	
	public static void notifyRoleChanged(Role activeRole) {
		for (IRoleChangedListener listener : Activator.roleChangedListeners) {
			listener.roleChanged(activeRole);
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
	
	public boolean storeLandscape(String description, int role, Database database, ILandscapeContributor landscapeContributor) {
		Landscape landscape = new Landscape();
		landscape.setDescription(description);
		landscape.setRole(role);
		if (database != null) {
			landscape.setDatabaseUrl(database.getUrl());
			landscape.setDatabaseDriver(database.getDriver());
			landscape.setDatabaseUser(database.getUser());
			landscape.setDatabasePassword(database.getPassword());
		}
		landscape.setType1(landscapeContributor.getType1());
		landscape.setType2(landscapeContributor.getType2());
		landscape.setConfigurationFolder1(landscapeContributor.getConfigurationFolder1());
		landscape.setConfigurationFolder2(landscapeContributor.getConfigurationFolder2());
		landscape.setContributorId(landscapeContributor.getId());
		return storeLandscape(landscape);
	}
	
	public boolean storeRole(Role role) {
		Preferences prefs = getInstancePreferences().node(PREF_CCF_ROLES_NODE).node(role.getName());
		prefs.put(PREF_CCF_ROLES_PASSWORD, encode(role.getPassword()));
		prefs.putBoolean(PREF_CCF_ROLES_ADD_LANDSCAPE, role.isAddLandscape());
		prefs.putBoolean(PREF_CCF_ROLES_EDIT_LANDSCAPE, role.isEditLandscape());
		prefs.putBoolean(PREF_CCF_ROLES_DELETE_LANDSCAPE, role.isDeleteLandscape());
		prefs.putBoolean(PREF_CCF_ROLES_ADD_PROJECT_MAPPING, role.isAddProjectMapping());
		prefs.putBoolean(PREF_CCF_ROLES_CHANGE_PROJECT_MAPPING, role.isChangeProjectMapping());
		prefs.putBoolean(PREF_CCF_ROLES_DELETE_PROJECT_MAPPING, role.isDeleteProjectMapping());
		prefs.putBoolean(PREF_CCF_ROLES_EDIT_FIELD_MAPPINGS, role.isEditFieldMappings());
		prefs.putBoolean(PREF_CCF_ROLES_PAUSE_SYNCHRONIZATION, role.isPauseSynchronization());
		prefs.putBoolean(PREF_CCF_ROLES_RESUME_SYNCHRONIZATION, role.isResumeSynchronization());
		prefs.putBoolean(PREF_CCF_ROLES_RESET_SYNCHRONIZATION_STATUS, role.isResetSynchronizationStatus());
		prefs.putBoolean(PREF_CCF_ROLES_DELETE_PROJECT_MAPPING_IDENTITY_MAPPINGS, role.isDeleteProjectMappingIdentityMappings());
		prefs.putBoolean(PREF_CCF_ROLES_EDIT_QUARANTINED_ARTIFACT, role.isEditQuarantinedArtifact());
		prefs.putBoolean(PREF_CCF_ROLES_MARK_AS_FIXED, role.isMarkAsFixed());
		prefs.putBoolean(PREF_CCF_ROLES_REOPEN, role.isReopen());
		prefs.putBoolean(PREF_CCF_ROLES_REPLAY, role.isReplay());
		prefs.putBoolean(PREF_CCF_ROLES_CANCEL_REPLAY, role.isCancelReplay());
		prefs.putBoolean(PREF_CCF_ROLES_DELETE_HOSPITAL_ENTRY, role.isDeleteHospitalEntry());
		prefs.putBoolean(PREF_CCF_ROLES_CREATE_REVERSE_IDENTITY_MAPPING, role.isCreateReverseIdentityMapping());
		prefs.putBoolean(PREF_CCF_ROLES_DELETE_IDENTITY_MAPPING, role.isDeleteProjectMapping());
		prefs.putBoolean(PREF_CCF_ROLES_EDIT_IDENTITY_MAPPING, role.isEditIdentityMapping());
		prefs.putBoolean(PREF_CCF_ROLES_EDIT_LOG_SETTINGS, role.isEditLogSettings());
		prefs.putBoolean(PREF_CCF_ROLES_CONSISTENCY_CHECK, role.isConsistencyCheck());
		prefs.putBoolean(PREF_CCF_ROLES_MAINTAIN_ROLES, role.isMaintainRoles());
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			handleError(e);
			return false;
		}		
		return true;
	}
	
	public boolean storeLandscape(Landscape landscape) {
		Preferences prefs = getInstancePreferences().node(PREF_CCF_LANDSCAPES_NODE).node(landscape.getDescription().replaceAll("/", "%slash%")); //$NON-NLS-1$ //$NON-NLS-2$
		prefs.putInt("role", landscape.getRole());
		prefs.put("type1", landscape.getType1()); //$NON-NLS-1$
		prefs.put("type2", landscape.getType2()); //$NON-NLS-1$
		if (landscape.getRole() == Landscape.ROLE_ADMINISTRATOR) {
			prefs.put("configFolder1", landscape.getConfigurationFolder1()); //$NON-NLS-1$
			if (landscape.getConfigurationFolder2() != null) prefs.put("configFolder2", landscape.getConfigurationFolder2()); //$NON-NLS-1$
		} else {
			prefs.put("databaseUrl", landscape.getDatabaseUrl()); //$NON-NLS-1$
			prefs.put("databaseDriver", landscape.getDatabaseDriver()); //$NON-NLS-1$
			prefs.put("databaseUser", landscape.getDatabaseUser()); //$NON-NLS-1$
			prefs.put("databasePassword", landscape.getDatabasePassword()); //$NON-NLS-1$			
			if (landscape.getCcfHost1() != null) prefs.put("ccfHost1", landscape.getCcfHost1()); //$NON-NLS-1$
			if (landscape.getCcfHost2() != null) prefs.put("ccfHost2", landscape.getCcfHost2()); //$NON-NLS-1$
			if (landscape.getLogsPath1() != null) prefs.put("logsPath1", landscape.getLogsPath1()); //$NON-NLS-1$
			if (landscape.getLogsPath2() != null) prefs.put("logsPath2", landscape.getLogsPath2()); //$NON-NLS-1$
			if (landscape.getJmxPort1() != null) prefs.put("jmxPort1", landscape.getJmxPort1()); //$NON-NLS-1$
			if (landscape.getJmxPort2() != null) prefs.put("jmxPort2", landscape.getJmxPort2()); //$NON-NLS-1$
		}
		prefs.put("contributorId", landscape.getContributorId()); //$NON-NLS-1$
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			handleError(e);
			return false;
		}
		return true;		
	}
	
	public boolean deleteRole(Role role) {
		Preferences node = role.getNode();
		if (node != null) {
			try {
				node.removeNode();
				getInstancePreferences().node(PREF_CCF_ROLES_NODE).flush();
			} catch (BackingStoreException e) {
				handleError(e);
				return false;
			}
			return true;
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
	
	public Role getActiveRole() {
		String activeRole = getPreferenceStore().getString(Activator.PREFERENCES_ACTIVE_ROLE);
		if (activeRole != null) {
			Preferences node = getInstancePreferences().node(PREF_CCF_ROLES_NODE).node(activeRole);
			if (node != null) {
				return getRole(activeRole, node);
			}
		}
		return new Role("Default");
	}
	
	private Role getRole(String name, Preferences node) {
		Role role = new Role(name);
		role.setPassword(decode(node.get(PREF_CCF_ROLES_PASSWORD, "")));
		role.setAddLandscape(node.getBoolean(PREF_CCF_ROLES_ADD_LANDSCAPE, true));
		role.setEditLandscape(node.getBoolean(PREF_CCF_ROLES_EDIT_LANDSCAPE, true));
		role.setDeleteLandscape(node.getBoolean(PREF_CCF_ROLES_DELETE_LANDSCAPE, true));
		role.setAddProjectMapping(node.getBoolean(PREF_CCF_ROLES_ADD_PROJECT_MAPPING, true));
		role.setChangeProjectMapping(node.getBoolean(PREF_CCF_ROLES_CHANGE_PROJECT_MAPPING, true));
		role.setDeleteProjectMapping(node.getBoolean(PREF_CCF_ROLES_DELETE_PROJECT_MAPPING, true));
		role.setEditFieldMappings(node.getBoolean(PREF_CCF_ROLES_EDIT_FIELD_MAPPINGS, true));
		role.setPauseSynchronization(node.getBoolean(PREF_CCF_ROLES_PAUSE_SYNCHRONIZATION, true));
		role.setResumeSynchronization(node.getBoolean(PREF_CCF_ROLES_RESUME_SYNCHRONIZATION, true));
		role.setResetSynchronizationStatus(node.getBoolean(PREF_CCF_ROLES_RESET_SYNCHRONIZATION_STATUS, true));
		role.setDeleteProjectMappingIdentityMappings(node.getBoolean(PREF_CCF_ROLES_DELETE_PROJECT_MAPPING_IDENTITY_MAPPINGS, true));
		role.setEditQuarantinedArtifact(node.getBoolean(PREF_CCF_ROLES_EDIT_QUARANTINED_ARTIFACT, true));
		role.setMarkAsFixed(node.getBoolean(PREF_CCF_ROLES_MARK_AS_FIXED, true));
		role.setReopen(node.getBoolean(PREF_CCF_ROLES_REOPEN, true));
		role.setReplay(node.getBoolean(PREF_CCF_ROLES_REPLAY, true));
		role.setCancelReplay(node.getBoolean(PREF_CCF_ROLES_CANCEL_REPLAY, true));
		role.setDeleteHospitalEntry(node.getBoolean(PREF_CCF_ROLES_DELETE_HOSPITAL_ENTRY, true));
		role.setCreateReverseIdentityMapping(node.getBoolean(PREF_CCF_ROLES_CREATE_REVERSE_IDENTITY_MAPPING, true));
		role.setDeleteIdentityMapping(node.getBoolean(PREF_CCF_ROLES_DELETE_IDENTITY_MAPPING, true));
		role.setEditIdentityMapping(node.getBoolean(PREF_CCF_ROLES_EDIT_IDENTITY_MAPPING, true));
		role.setEditLogSettings(node.getBoolean(PREF_CCF_ROLES_EDIT_LOG_SETTINGS, true));
		role.setConsistencyCheck(node.getBoolean(PREF_CCF_ROLES_CONSISTENCY_CHECK, true));
		role.setMaintainRoles(node.getBoolean(PREF_CCF_ROLES_MAINTAIN_ROLES, true));
		role.setNode(node);	
		return role;
	}
	
	public Role[] getRoles() {
		List<Role> roles = new ArrayList<Role>();
		try {
			String[] childrenNames = getInstancePreferences().node(PREF_CCF_ROLES_NODE).childrenNames();
			for (int i = 0; i < childrenNames.length; i++) {
				Preferences node = getInstancePreferences().node(PREF_CCF_ROLES_NODE).node(childrenNames[i]);
				Role role = getRole(childrenNames[i], node);
				roles.add(role);
			}
			if (roles.size() == 0) {
				Role role = new Role("Default");
				roles.add(role);
				getPreferenceStore().setValue(PREFERENCES_ACTIVE_ROLE, "Default");
			}
			Role[] roleArray = new Role[roles.size()];
			roles.toArray(roleArray);
			Arrays.sort(roleArray);
			return roleArray;
		} catch (Exception e) {
			handleError(e);
		}
		return new Role[0];
	}
	
	public Landscape[] getLandscapes() {
		List<Landscape> landscapes = new ArrayList<Landscape>();
		try {
			String[] childrenNames = getInstancePreferences().node(PREF_CCF_LANDSCAPES_NODE).childrenNames();
			for (int i = 0; i < childrenNames.length; i++) {
				Preferences node = getInstancePreferences().node(PREF_CCF_LANDSCAPES_NODE).node(childrenNames[i]); //$NON-NLS-1$
				Landscape landscape = null;
				if (node.getInt("role", Landscape.ROLE_ADMINISTRATOR) == Landscape.ROLE_OPERATOR) {
					landscape = new OperatorLandscape();
				} else {
					landscape = new AdministratorLandscape();
				}
				landscape.setDescription(childrenNames[i].replaceAll("%slash%", "/")); //$NON-NLS-1$ //$NON-NLS-2$
				landscape.setType1(node.get("type1", "")); //$NON-NLS-1$ //$NON-NLS-2$
				landscape.setType2(node.get("type2", "")); //$NON-NLS-1$ //$NON-NLS-2$				
				if (landscape.getRole() == Landscape.ROLE_OPERATOR) {
					landscape.setDatabaseUrl(node.get("databaseUrl", DATABASE_DEFAULT_URL)); //$NON-NLS-1$
					landscape.setDatabaseDriver(node.get("databaseDriver", DATABASE_DEFAULT_DRIVER)); //$NON-NLS-1$
					landscape.setDatabaseUser(node.get("databaseUser", DATABASE_DEFAULT_USER)); //$NON-NLS-1$
					landscape.setDatabasePassword(node.get("databasePassword", DATABASE_DEFAULT_PASSWORD)); //$NON-NLS-1$
					
					landscape.setLogsPath1(node.get("logsPath1", "")); //$NON-NLS-1$ //$NON-NLS-2$
					landscape.setLogsPath2(node.get("logsPath2", "")); //$NON-NLS-1$ //$NON-NLS-2$
					
					landscape.setCcfHost1(node.get("ccfHost1", DEFAULT_CCF_HOST)); //$NON-NLS-1$
					landscape.setCcfHost2(node.get("ccfHost2", DEFAULT_CCF_HOST)); //$NON-NLS-1$
				
					String defaultJmxPort1 = null;
					String defaultJmxPort2 = null;
					
					if (landscape.getType2().equals(Landscape.TYPE_PT)) {
						defaultJmxPort1 = DEFAULT_JMX_PORT_PT2QC;
						defaultJmxPort2 = DEFAULT_JMX_PORT_QC2PT;
					}
					if (landscape.getType2().equals(Landscape.TYPE_TF)) {
						defaultJmxPort1 = DEFAULT_JMX_PORT_SFEE2QC;
						defaultJmxPort2 = DEFAULT_JMX_PORT_QC2SFEE;
					}
					
					landscape.setJmxPort1(node.get("jmxPort1", defaultJmxPort1)); //$NON-NLS-1$
					landscape.setJmxPort2(node.get("jmxPort2", defaultJmxPort2)); //$NON-NLS-1$
				}
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
		if (landscape.getRole() == Landscape.ROLE_OPERATOR) return getImage(IMAGE_LANDSCAPE_OPERATOR);
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
		createImageDescriptor(IMAGE_HOSPITAL_ENTRY_OUTDATED);
		createImageDescriptor(IMAGE_HOSPITAL_ENTRY_REPLAY);
		createImageDescriptor(IMAGE_HOSPITAL_ENTRY_REPLAY_FAILED);
		createImageDescriptor(IMAGE_SYNC_STATUS_ENTRY);
		createImageDescriptor(IMAGE_SYNC_STATUS_ENTRY_PAUSED);
		createImageDescriptor(IMAGE_SYNC_STATUS_ENTRY_WITH_HOSPITAL_ENTRIES);
		createImageDescriptor(IMAGE_NEW_LANDSCAPE);
		createImageDescriptor(IMAGE_NEW_LANDSCAPE_WIZBAN);
		createImageDescriptor(IMAGE_NEW_PROJECT_MAPPING_WIZBAN);
		createImageDescriptor(IMAGE_EDIT_FIELD_MAPPINGS_WIZBAN);
		createImageDescriptor(IMAGE_LANDSCAPE);
		createImageDescriptor(IMAGE_LANDSCAPE_QC_PT);
		createImageDescriptor(IMAGE_LANDSCAPE_QC_TF);
		createImageDescriptor(IMAGE_LANDSCAPE_OPERATOR);
		createImageDescriptor(IMAGE_PROJECT_MAPPINGS);
		createImageDescriptor(IMAGE_LOGS);
		createImageDescriptor(IMAGE_LOG);
		createImageDescriptor(IMAGE_ERROR);
		createImageDescriptor(IMAGE_IDENTITY_MAPPING);
		createImageDescriptor(IMAGE_MULTIPLE_SOURCE);
		createImageDescriptor(IMAGE_MULTIPLE_TARGET);
		createImageDescriptor(IMAGE_ONE_WAY);
		createImageDescriptor(IMAGE_NO_INCONSISTENCIES);
		createImageDescriptor(IMAGE_MONITOR);
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
		reg.put(IMAGE_HOSPITAL_ENTRY_REPLAY_FAILED, getImageDescriptor(IMAGE_HOSPITAL_ENTRY_REPLAY_FAILED));
		reg.put(IMAGE_HOSPITAL_ENTRY_OUTDATED, getImageDescriptor(IMAGE_HOSPITAL_ENTRY_OUTDATED));
		reg.put(IMAGE_SYNC_STATUS_ENTRY, getImageDescriptor(IMAGE_SYNC_STATUS_ENTRY));
		reg.put(IMAGE_SYNC_STATUS_ENTRY_PAUSED, getImageDescriptor(IMAGE_SYNC_STATUS_ENTRY_PAUSED));
		reg.put(IMAGE_SYNC_STATUS_ENTRY_WITH_HOSPITAL_ENTRIES, getImageDescriptor(IMAGE_SYNC_STATUS_ENTRY_WITH_HOSPITAL_ENTRIES));
		reg.put(IMAGE_NEW_LANDSCAPE, getImageDescriptor(IMAGE_NEW_LANDSCAPE));
		reg.put(IMAGE_NEW_LANDSCAPE_WIZBAN, getImageDescriptor(IMAGE_NEW_LANDSCAPE_WIZBAN));
		reg.put(IMAGE_NEW_PROJECT_MAPPING_WIZBAN, getImageDescriptor(IMAGE_NEW_PROJECT_MAPPING_WIZBAN));
		reg.put(IMAGE_EDIT_FIELD_MAPPINGS_WIZBAN, getImageDescriptor(IMAGE_EDIT_FIELD_MAPPINGS_WIZBAN));
		reg.put(IMAGE_LANDSCAPE, getImageDescriptor(IMAGE_LANDSCAPE));
		reg.put(IMAGE_LANDSCAPE_QC_PT, getImageDescriptor(IMAGE_LANDSCAPE_QC_PT));
		reg.put(IMAGE_LANDSCAPE_QC_TF, getImageDescriptor(IMAGE_LANDSCAPE_QC_TF));
		reg.put(IMAGE_LANDSCAPE_OPERATOR, getImageDescriptor(IMAGE_LANDSCAPE_OPERATOR));
		reg.put(IMAGE_PROJECT_MAPPINGS, getImageDescriptor(IMAGE_PROJECT_MAPPINGS));
		reg.put(IMAGE_LOGS, getImageDescriptor(IMAGE_LOGS));
		reg.put(IMAGE_LOG, getImageDescriptor(IMAGE_LOG));
		reg.put(IMAGE_ERROR, getImageDescriptor(IMAGE_ERROR));
		reg.put(IMAGE_IDENTITY_MAPPING, getImageDescriptor(IMAGE_IDENTITY_MAPPING));
		reg.put(IMAGE_MULTIPLE_SOURCE, getImageDescriptor(IMAGE_MULTIPLE_SOURCE));
		reg.put(IMAGE_MULTIPLE_TARGET, getImageDescriptor(IMAGE_MULTIPLE_TARGET));
		reg.put(IMAGE_ONE_WAY, getImageDescriptor(IMAGE_ONE_WAY));
		reg.put(IMAGE_NO_INCONSISTENCIES, getImageDescriptor(IMAGE_NO_INCONSISTENCIES));
		reg.put(IMAGE_MONITOR, getImageDescriptor(IMAGE_MONITOR));
	}
	
	public static IProject getTemporaryFilesProject() throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project  = root.getProject("Temporary CCF Files");	
		if (!project.exists()) {
			project.create(null);
		}
		if (!project.isOpen()) {
			project.open(null);
		}
		return project;
	}
	
	public static IFolder getQuarantinedArtifactFolder() throws CoreException {
		IProject project = getTemporaryFilesProject();
		IFolder folder = project.getFolder("Quarantined Artifacts");
		if (!folder.exists()) {
			folder.create(IResource.NONE, true, null);
		}
		return folder;
	}
	
	public static IFile getQuarantinedArtifactFile(Patient patient, boolean createFile) throws CoreException {
		IFolder folder = getQuarantinedArtifactFolder();
		IFile file = folder.getFile("Payload" + patient.getId() + ".xml");
		if (createFile) {
			if (file.exists()) {
				file.delete(true, null);
			}
			byte[] bytes = patient.getGenericArtifact().getBytes();
		    InputStream source = new ByteArrayInputStream(bytes);
		    file.create(source, IResource.NONE, null);
		}
		return file;
	}
	
	private static String encode(String string) {
		return Obfuscator.obfuscateString(string);
	}
	
	private static String decode(String string) {
		return Obfuscator.deObfuscateString(string);
	}
	
	public IProxyService getProxyService() {
		return (IProxyService) proxyServiceTracker.getService();
	}    
	
	public static Proxy getPlatformProxy(String url) {
		IProxyService service = getDefault().getProxyService();
		if (service != null && service.isProxiesEnabled()) {
			String host = Proxy.getDomain(url);
			IProxyData data = null;
			if (url.toLowerCase().startsWith("https://")) //$NON-NLS-1$
				data = service.getProxyDataForHost(host, IProxyData.HTTPS_PROXY_TYPE);
			else
				data = service.getProxyDataForHost(host, IProxyData.HTTP_PROXY_TYPE);
			if (data != null && data.getHost() != null) {
				return new Proxy(data.getHost(), data.getPort(), data.isRequiresAuthentication(),
						data.getUserId(), data.getPassword());
			}
		}
		return null;
	}	
}

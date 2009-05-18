package com.collabnet.ccf.db;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.IdentityMapping;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.Patient;
import com.collabnet.ccf.model.ProjectMappings;
import com.collabnet.ccf.model.SynchronizationStatus;

public class CcfDataProvider {	
	private IPreferenceStore store = Activator.getDefault().getPreferenceStore();
	
	// HOSPITAL Columns
	public final static String HOSPITAL_ID = "ID";
	public final static String HOSPITAL_TIMESTAMP = "TIMESTAMP";
	public final static String HOSPITAL_EXCEPTION_CLASS_NAME = "EXCEPTION_CLASS_NAME";
	public final static String HOSPITAL_EXCEPTION_MESSAGE = "EXCEPTION_MESSAGE";
	public final static String HOSPITAL_CAUSE_EXCEPTION_CLASS_NAME = "CAUSE_EXCEPTION_CLASS_NAME";
	public final static String HOSPITAL_CAUSE_EXCEPTION_MESSAGE = "CAUSE_EXCEPTION_MESSAGE";
	public final static String HOSPITAL_STACK_TRACE = "STACK_TRACE";
	public final static String HOSPITAL_ADAPTOR_NAME = "ADAPTOR_NAME";
	public final static String HOSPITAL_ORIGINATING_COMPONENT = "ORIGINATING_COMPONENT";
	public final static String HOSPITAL_DATA_TYPE = "DATA_TYPE";
	public final static String HOSPITAL_DATA = "DATA";
	public final static String HOSPITAL_FIXED = "FIXED";
	public final static String HOSPITAL_REPROCESSED = "REPROCESSED";
	public final static String HOSPITAL_SOURCE_SYSTEM_ID = "SOURCE_SYSTEM_ID";
	public final static String HOSPITAL_SOURCE_REPOSITORY_ID = "SOURCE_REPOSITORY_ID";
	public final static String HOSPITAL_TARGET_SYSTEM_ID = "TARGET_SYSTEM_ID";
	public final static String HOSPITAL_TARGET_REPOSITORY_ID = "TARGET_REPOSITORY_ID";
	public final static String HOSPITAL_SOURCE_SYSTEM_KIND = "SOURCE_SYSTEM_KIND";
	public final static String HOSPITAL_SOURCE_REPOSITORY_KIND = "SOURCE_REPOSITORY_KIND";
	public final static String HOSPITAL_TARGET_SYSTEM_KIND = "TARGET_SYSTEM_KIND";
	public final static String HOSPITAL_TARGET_REPOSITORY_KIND = "TARGET_REPOSITORY_KIND";
	public final static String HOSPITAL_SOURCE_ARTIFACT_ID = "SOURCE_ARTIFACT_ID";
	public final static String HOSPITAL_TARGET_ARTIFACT_ID = "TARGET_ARTIFACT_ID";
	public final static String HOSPITAL_ERROR_CODE = "ERROR_CODE";
	public final static String HOSPITAL_SOURCE_LAST_MODIFICATION_TIME = "SOURCE_LAST_MODIFICATION_TIME";
	public final static String HOSPITAL_TARGET_LAST_MODIFICATION_TIME = "TARGET_LAST_MODIFICATION_TIME";
	public final static String HOSPITAL_SOURCE_ARTIFACT_VERSION = "SOURCE_ARTIFACT_VERSION";
	public final static String HOSPITAL_TARGET_ARTIFACT_VERSION = "TARGET_ARTIFACT_VERSION";
	public final static String HOSPITAL_ARTIFACT_TYPE = "ARTIFACT_TYPE";
	public final static String HOSPITAL_GENERIC_ARTIFACT = "GENERIC_ARTIFACT";
	
	// SYNCHRONIZATION_STATUS Columns
	public final static String SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_ID = "SOURCE_SYSTEM_ID";
	public final static String SYNCHRONIZATION_STATUS_SOURCE_REPOSITORY_ID = "SOURCE_REPOSITORY_ID";
	public final static String SYNCHRONIZATION_STATUS_TARGET_SYSTEM_ID = "TARGET_SYSTEM_ID";
	public final static String SYNCHRONIZATION_STATUS_TARGET_REPOSITORY_ID = "TARGET_REPOSITORY_ID";
	public final static String SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_KIND = "SOURCE_SYSTEM_KIND";
	public final static String SYNCHRONIZATION_STATUS_SOURCE_REPOSITORY_KIND = "SOURCE_REPOSITORY_KIND";
	public final static String SYNCHRONIZATION_STATUS_TARGET_SYSTEM_KIND = "TARGET_SYSTEM_KIND";
	public final static String SYNCHRONIZATION_STATUS_TARGET_REPOSITORY_KIND = "TARGET_REPOSITORY_KIND";
	public final static String SYNCHRONIZATION_STATUS_LAST_SOURCE_ARTIFACT_MODIFICATION_DATE = "LAST_SOURCE_ARTIFACT_MODIFICATION_DATE";
	public final static String SYNCHRONIZATION_STATUS_LAST_SOURCE_ARTIFACT_VERSION = "LAST_SOURCE_ARTIFACT_VERSION";
	public final static String SYNCHRONIZATION_STATUS_LAST_SOURCE_ARTIFACT_ID = "LAST_SOURCE_ARTIFACT_ID";
	public final static String SYNCHRONIZATION_STATUS_CONFLICT_RESOLUTION_PRIORITY = "CONFLICT_RESOLUTION_PRIORITY";
	public final static String SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_TIMEZONE = "SOURCE_SYSTEM_TIMEZONE";
	public final static String SYNCHRONIZATION_STATUS_TARGET_SYSTEM_TIMEZONE = "TARGET_SYSTEM_TIMEZONE";
	public final static String SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_ENCODING = "SOURCE_SYSTEM_ENCODING";
	public final static String SYNCHRONIZATION_STATUS_TARGET_SYSTEM_ENCODING = "TARGET_SYSTEM_ENCODING";

	// IDENTITY_MAPPING Columns
	public final static String IDENTITY_MAPPING_SOURCE_SYSTEM_ID = "SOURCE_SYSTEM_ID";
	public final static String IDENTITY_MAPPING_SOURCE_REPOSITORY_ID = "SOURCE_REPOSITORY_ID";
	public final static String IDENTITY_MAPPING_TARGET_SYSTEM_ID = "TARGET_SYSTEM_ID";
	public final static String IDENTITY_MAPPING_TARGET_REPOSITORY_ID = "TARGET_REPOSITORY_ID";
	public final static String IDENTITY_MAPPING_SOURCE_SYSTEM_KIND = "SOURCE_SYSTEM_KIND";
	public final static String IDENTITY_MAPPING_SOURCE_REPOSITORY_KIND = "SOURCE_REPOSITORY_KIND";
	public final static String IDENTITY_MAPPING_TARGET_SYSTEM_KIND = "TARGET_SYSTEM_KIND";
	public final static String IDENTITY_MAPPING_TARGET_REPOSITORY_KIND = "TARGET_REPOSITORY_KIND";
	public final static String IDENTITY_MAPPING_SOURCE_ARTIFACT_ID = "SOURCE_ARTIFACT_ID";
	public final static String IDENTITY_MAPPING_TARGET_ARTIFACT_ID = "TARGET_ARTIFACT_ID";
	public final static String IDENTITY_MAPPING_SOURCE_LAST_MODIFICATION_TIME = "SOURCE_LAST_MODIFICATION_TIME";
	public final static String IDENTITY_MAPPING_TARGET_LAST_MODIFICATION_TIME = "TARGET_LAST_MODIFICATION_TIME";
	public final static String IDENTITY_MAPPING_SOURCE_ARTIFACT_VERSION = "SOURCE_ARTIFACT_VERSION";
	public final static String IDENTITY_MAPPING_TARGET_ARTIFACT_VERSION = "TARGET_ARTIFACT_VERSION";
	public final static String IDENTITY_MAPPING_ARTIFACT_TYPE = "ARTIFACT_TYPE";	
	public final static String IDENTITY_MAPPING_DEP_CHILD_SOURCE_ARTIFACT_ID = "DEP_CHILD_SOURCE_ARTIFACT_ID";	
	public final static String IDENTITY_MAPPING_DEP_CHILD_SOURCE_REPOSITORY_ID = "DEP_CHILD_SOURCE_REPOSITORY_ID";
	public final static String IDENTITY_MAPPING_DEP_CHILD_SOURCE_REPOSITORY_KIND = "DEP_CHILD_SOURCE_REPOSITORY_KIND";
	public final static String IDENTITY_MAPPING_DEP_CHILD_TARGET_ARTIFACT_ID = "DEP_CHILD_TARGET_ARTIFACT_ID";	
	public final static String IDENTITY_MAPPING_DEP_CHILD_TARGET_REPOSITORY_ID = "DEP_CHILD_TARGET_REPOSITORY_ID";
	public final static String IDENTITY_MAPPING_DEP_CHILD_TARGET_REPOSITORY_KIND = "DEP_CHILD_TARGET_REPOSITORY_KIND";	
	public final static String IDENTITY_MAPPING_DEP_PARENT_SOURCE_ARTIFACT_ID = "DEP_PARENT_SOURCE_ARTIFACT_ID";	
	public final static String IDENTITY_MAPPING_DEP_PARENT_SOURCE_REPOSITORY_ID = "DEP_PARENT_SOURCE_REPOSITORY_ID";
	public final static String IDENTITY_MAPPING_DEP_PARENT_SOURCE_REPOSITORY_KIND = "DEP_PARENT_SOURCE_REPOSITORY_KIND";
	public final static String IDENTITY_MAPPING_DEP_PARENT_TARGET_ARTIFACT_ID = "DEP_PARENT_TARGET_ARTIFACT_ID";	
	public final static String IDENTITY_MAPPING_DEP_PARENT_TARGET_REPOSITORY_ID = "DEP_PARENT_TARGET_REPOSITORY_ID";
	public final static String IDENTITY_MAPPING_DEP_PARENT_TARGET_REPOSITORY_KIND = "DEP_PARENT_TARGET_REPOSITORY_KIND";
	
	public final static String DEFAULT_HOSPITAL_COLUMNS = HOSPITAL_TIMESTAMP + "," +
	HOSPITAL_ADAPTOR_NAME + "," +
	HOSPITAL_ORIGINATING_COMPONENT + "," +
	HOSPITAL_SOURCE_ARTIFACT_ID + "," +
	HOSPITAL_TARGET_ARTIFACT_ID + "," +
	HOSPITAL_ERROR_CODE + "," +
	HOSPITAL_EXCEPTION_MESSAGE + "," +
	HOSPITAL_CAUSE_EXCEPTION_MESSAGE;
	
	public final static String DEFAULT_IDENTITY_MAPPING_COLUMNS = IDENTITY_MAPPING_SOURCE_ARTIFACT_ID + "," +
	IDENTITY_MAPPING_TARGET_ARTIFACT_ID + "," +
	IDENTITY_MAPPING_SOURCE_LAST_MODIFICATION_TIME + "," +
	IDENTITY_MAPPING_TARGET_LAST_MODIFICATION_TIME + "," +
	IDENTITY_MAPPING_SOURCE_ARTIFACT_VERSION + "," +
	IDENTITY_MAPPING_TARGET_ARTIFACT_VERSION;
	
	public final static String HOSPITAL_COLUMNS = HOSPITAL_ID + "," +
	                                              HOSPITAL_TIMESTAMP + "," +
	                                              HOSPITAL_EXCEPTION_CLASS_NAME + "," +
	                                              HOSPITAL_EXCEPTION_MESSAGE + "," +
	                                              HOSPITAL_CAUSE_EXCEPTION_CLASS_NAME + "," +
	                                              HOSPITAL_CAUSE_EXCEPTION_MESSAGE + "," +	 
	                                              HOSPITAL_STACK_TRACE + "," +
	                                              HOSPITAL_ADAPTOR_NAME + "," +
	                                              HOSPITAL_ORIGINATING_COMPONENT + "," +
	                                              HOSPITAL_DATA_TYPE + "," +
	                                              HOSPITAL_DATA + "," +
	                                              HOSPITAL_FIXED + "," +
	                                              HOSPITAL_REPROCESSED + "," +
	                                              HOSPITAL_SOURCE_SYSTEM_ID + "," +
	                                              HOSPITAL_SOURCE_REPOSITORY_ID + "," +
	                                              HOSPITAL_TARGET_SYSTEM_ID + "," +
	                                              HOSPITAL_TARGET_REPOSITORY_ID + "," +
	                                              HOSPITAL_SOURCE_SYSTEM_KIND + "," +
	                                              HOSPITAL_SOURCE_REPOSITORY_KIND + "," +
	                                              HOSPITAL_TARGET_SYSTEM_KIND + "," +
	                                              HOSPITAL_TARGET_REPOSITORY_KIND + "," +
	                                              HOSPITAL_SOURCE_ARTIFACT_ID + "," +
	                                              HOSPITAL_TARGET_ARTIFACT_ID + "," +
	                                              HOSPITAL_ERROR_CODE + "," +
	                                              HOSPITAL_SOURCE_LAST_MODIFICATION_TIME + "," +
	                                              HOSPITAL_TARGET_LAST_MODIFICATION_TIME + "," +
	                                              HOSPITAL_SOURCE_ARTIFACT_VERSION + "," +
	                                              HOSPITAL_TARGET_ARTIFACT_VERSION + "," +
	                                              HOSPITAL_ARTIFACT_TYPE + "," +
	                                              HOSPITAL_GENERIC_ARTIFACT;
	
	public final static String IDENTITY_MAPPING_COLUMNS = IDENTITY_MAPPING_SOURCE_SYSTEM_ID + "," +
    IDENTITY_MAPPING_SOURCE_REPOSITORY_ID + "," +
    IDENTITY_MAPPING_TARGET_SYSTEM_ID + "," +
    IDENTITY_MAPPING_TARGET_REPOSITORY_ID + "," +
    IDENTITY_MAPPING_SOURCE_SYSTEM_KIND + "," +
    IDENTITY_MAPPING_SOURCE_REPOSITORY_KIND + "," +	 
    IDENTITY_MAPPING_TARGET_SYSTEM_KIND + "," +
    IDENTITY_MAPPING_TARGET_REPOSITORY_KIND + "," +	 
    IDENTITY_MAPPING_SOURCE_ARTIFACT_ID + "," +
    IDENTITY_MAPPING_TARGET_ARTIFACT_ID + "," +
    IDENTITY_MAPPING_SOURCE_LAST_MODIFICATION_TIME + "," +
    IDENTITY_MAPPING_TARGET_LAST_MODIFICATION_TIME + "," +
    IDENTITY_MAPPING_SOURCE_ARTIFACT_VERSION + "," +
    IDENTITY_MAPPING_TARGET_ARTIFACT_VERSION + "," +
    IDENTITY_MAPPING_ARTIFACT_TYPE + "," +
    IDENTITY_MAPPING_DEP_CHILD_SOURCE_ARTIFACT_ID + "," +
    IDENTITY_MAPPING_DEP_CHILD_SOURCE_REPOSITORY_ID + "," +
    IDENTITY_MAPPING_DEP_CHILD_SOURCE_REPOSITORY_KIND + "," +
    IDENTITY_MAPPING_DEP_CHILD_TARGET_ARTIFACT_ID + "," +
    IDENTITY_MAPPING_DEP_CHILD_TARGET_REPOSITORY_ID + "," +
    IDENTITY_MAPPING_DEP_CHILD_TARGET_REPOSITORY_KIND + "," +
    IDENTITY_MAPPING_DEP_PARENT_SOURCE_ARTIFACT_ID + "," +
    IDENTITY_MAPPING_DEP_PARENT_SOURCE_REPOSITORY_ID + "," +
    IDENTITY_MAPPING_DEP_PARENT_SOURCE_REPOSITORY_KIND + "," +
    IDENTITY_MAPPING_DEP_PARENT_TARGET_ARTIFACT_ID + "," +
    IDENTITY_MAPPING_DEP_PARENT_TARGET_REPOSITORY_ID + "," +
    IDENTITY_MAPPING_DEP_PARENT_TARGET_REPOSITORY_KIND;
	
	public final static String HOSPITAL_REPLAY = "replay";
	
	private final static String SQL_HOSPITAL_SELECT = "SELECT * FROM HOSPITAL";
	private final static String SQL_HOSPITAL_UPDATE = "UPDATE HOSPITAL";
	private final static String SQL_HOSPITAL_DELETE = "DELETE FROM HOSPITAL";
	
	private final static String SQL_SYNCHRONIZATION_STATUS_SELECT = "SELECT * FROM SYNCHRONIZATION_STATUS";
	private final static String SQL_SYNCHRONIZATION_STATUS_UPDATE = "UPDATE SYNCHRONIZATION_STATUS";
	private final static String SQL_SYNCHRONIZATION_STATUS_DELETE = "DELETE FROM SYNCHRONIZATION_STATUS";
	private final static String SQL_SYNCHRONIZATION_STATUS_INSERT = "INSERT INTO SYNCHRONIZATION_STATUS";
	
	private final static String SQL_IDENTITY_MAPPING_SELECT = "SELECT * FROM IDENTITY_MAPPING";
	private final static String SQL_IDENTITY_MAPPING_UPDATE = "UPDATE IDENTITY_MAPPING";
	private final static String SQL_IDENTITY_MAPPING_DELETE = "DELETE FROM IDENTITY_MAPPING";

	public Patient[] getPatients(Landscape landscape, Filter[][] filters) throws Exception {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		Patient[] patients = null;
		try {
			connection = getConnection(landscape);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(Filter.getQuery(SQL_HOSPITAL_SELECT, filters));
			patients = getPatients(rs, landscape);
		}
		catch (Exception e) {
			Activator.handleError(e);
			throw e;
		}
		finally {
	        try
	        {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e)
	        {
	            Activator.handleError("Could not close ResultSet" ,e);
	        }
	        try
	        {
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e)
	        {
	        	 Activator.handleError("Could not close Statement" ,e);
	        }
	        try
	        {
	            if (connection  != null)
	                connection.close();
	        }
	        catch (SQLException e)
	        {
	        	 Activator.handleError("Could not close Connection" ,e);
	        }			
		}
		return patients;
	}
	
	public Patient[] getPatients(Landscape landscape, Filter[] filters) throws Exception {
		Filter[][] filterGroups = { filters };
		return getPatients(landscape, filterGroups);
	}
	
	public IdentityMapping getIdentityMapping(IdentityMapping identityMapping) throws Exception  {
		Filter sourceRepositoryIdFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_REPOSITORY_ID, identityMapping.getSourceRepositoryId(), true);
		Filter targetRepositoryIdFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_TARGET_REPOSITORY_ID, identityMapping.getTargetRepositoryId(), true);
		Filter sourceArtifactIdFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_SOURCE_ARTIFACT_ID, identityMapping.getSourceArtifactId(), true);
		Filter artifactTypeFilter = new Filter(CcfDataProvider.IDENTITY_MAPPING_ARTIFACT_TYPE, identityMapping.getArtifactType(), true);
		Filter[] filters = { sourceRepositoryIdFilter, targetRepositoryIdFilter, sourceArtifactIdFilter, artifactTypeFilter };
		
		IdentityMapping[] identityMappings = getIdentityMappings(identityMapping.getLandscape(), filters);
		if (identityMappings != null && identityMappings.length == 1) {
			return identityMappings[0];
		}
		
		return identityMapping;
	}
	
	public IdentityMapping[] getIdentityMappings(Landscape landscape, Filter[][] filters) throws Exception {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		IdentityMapping[] identityMappings = null;
		try {
			connection = getConnection(landscape);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(Filter.getQuery(SQL_IDENTITY_MAPPING_SELECT, filters));
			identityMappings = getIdentityMappings(rs, landscape);
		}
		catch (Exception e) {
			Activator.handleError(e);
			throw e;
		}
		finally {
	        try
	        {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e)
	        {
	            Activator.handleError("Could not close ResultSet" ,e);
	        }
	        try
	        {
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e)
	        {
	        	 Activator.handleError("Could not close Statement" ,e);
	        }
	        try
	        {
	            if (connection  != null)
	                connection.close();
	        }
	        catch (SQLException e)
	        {
	        	 Activator.handleError("Could not close Connection" ,e);
	        }			
		}
		return identityMappings;
	}
	
	public IdentityMapping[] getIdentityMappings(Landscape landscape, Filter[] filters) throws Exception {
		Filter[][] filterGroups = { filters };
		return getIdentityMappings(landscape, filterGroups);
	}
	
	public void addSynchronizationStatus(ProjectMappings projectMappings, SynchronizationStatus synchronizationStatus) throws Exception {
		Connection connection = null;
		Statement stmt = null;	
		try {
			Landscape landscape = projectMappings.getLandscape();
			connection = getConnection(landscape);
			stmt = connection.createStatement();
			String version;
			if (synchronizationStatus.getSourceSystemKind().startsWith(Landscape.TYPE_PT)) {
				version = Long.toString(Timestamp.valueOf("1999-01-01 00:00:00.0").getTime());	
			} else {
				version = "0";
			}
			StringBuffer insertStatement = new StringBuffer(SQL_SYNCHRONIZATION_STATUS_INSERT +
			" VALUES('" + synchronizationStatus.getSourceSystemId() + "','" +
			synchronizationStatus.getSourceRepositoryId() + "','" +
			synchronizationStatus.getTargetSystemId() + "','" +
			synchronizationStatus.getTargetRepositoryId() + "','" +
			synchronizationStatus.getSourceSystemKind() + "','" +
			synchronizationStatus.getSourceRepositoryKind() + "','" +
			synchronizationStatus.getTargetSystemKind() + "','" +
			synchronizationStatus.getTargetRepositoryKind() + "','1999-01-01 00:00:00.0','" + version + "','0','" +
			synchronizationStatus.getConflictResolutionPriority() + "','" +
			synchronizationStatus.getSourceSystemTimezone() + "','" +
			synchronizationStatus.getTargetSystemTimezone() + "',");
			if (synchronizationStatus.getSourceSystemEncoding() == null) {
				insertStatement.append("NULL,");
			} else {
				insertStatement.append("'" + synchronizationStatus.getSourceSystemEncoding() + "',");
			}
			if (synchronizationStatus.getTargetSystemEncoding() == null) {
				insertStatement.append("NULL)");
			} else {
				insertStatement.append("'" + synchronizationStatus.getTargetSystemEncoding() + "')");
			}	
			stmt.executeUpdate(insertStatement.toString());
		}
		catch (Exception e) {
			Activator.handleError(e);
			throw e;
		}
		finally {
	        try
	        {
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e)
	        {
	        	 Activator.handleError("Could not close Statement" ,e);
	        }
	        try
	        {
	            if (connection  != null)
	                connection.close();
	        }
	        catch (SQLException e)
	        {
	        	 Activator.handleError("Could not close Connection" ,e);
	        }			
		}	
	}
	
	public SynchronizationStatus[] getSynchronizationStatuses(Landscape landscape, ProjectMappings projectMappings)  throws Exception {
		Filter filter1 = new Filter(SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_KIND, landscape.getType1(), true, Filter.FILTER_TYPE_LIKE);
		Filter filter2 = new Filter(SYNCHRONIZATION_STATUS_TARGET_SYSTEM_KIND, landscape.getType2(), true, Filter.FILTER_TYPE_LIKE);
		Filter[] orGroup1 = { filter1, filter2 };
		Filter filter3 = new Filter(SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_KIND, landscape.getType2(), true, Filter.FILTER_TYPE_LIKE);
		Filter filter4 = new Filter(SYNCHRONIZATION_STATUS_TARGET_SYSTEM_KIND, landscape.getType1(), true, Filter.FILTER_TYPE_LIKE);
		Filter[] orGroup2 = { filter3, filter4 };

		Filter[][] filters = { orGroup1, orGroup2 };

		SynchronizationStatus[] statuses = getSynchronizationStatuses(landscape, projectMappings, filters);
		Arrays.sort(statuses);
		return statuses;
	}
	
	public SynchronizationStatus[] getSynchronizationStatuses(Landscape landscape, ProjectMappings projectMappings, Filter[][] filters) throws Exception {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		SynchronizationStatus[] statuses = null;
		try {
			connection = getConnection(landscape);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(Filter.getQuery(SQL_SYNCHRONIZATION_STATUS_SELECT, filters));
			statuses = getSynchronizationStatuses(rs, landscape, projectMappings);
		}
		catch (Exception e) {
			Activator.handleError(e);
			throw e;
		}
		finally {
	        try
	        {
	            if (rs != null)
	                rs.close();
	        }
	        catch (Exception e)
	        {
	            Activator.handleError("Could not close ResultSet" ,e);
	        }
	        try
	        {
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e)
	        {
	        	 Activator.handleError("Could not close Statement" ,e);
	        }
	        try
	        {
	            if (connection  != null)
	                connection.close();
	        }
	        catch (SQLException e)
	        {
	        	 Activator.handleError("Could not close Connection" ,e);
	        }			
		}
		return statuses;
	}
	
	public void deletePatients(Landscape landscape, Filter[] filters) throws  Exception {
		delete(SQL_HOSPITAL_DELETE, landscape, filters);
	}
	
	public void deleteSynchronizationStatuses(Landscape landscape, Filter[] filters) throws Exception {
		delete(SQL_SYNCHRONIZATION_STATUS_DELETE, landscape, filters);
	}
	
	public void deleteIdentityMappings(Landscape landscape, Filter[] filters) throws Exception {
		delete(SQL_IDENTITY_MAPPING_DELETE, landscape, filters);
	}
	
	private void delete(String sql, Landscape landscape, Filter[] filters) throws  Exception {
		Connection connection = null;
		Statement stmt = null;	
		try {
			connection = getConnection(landscape);
			stmt = connection.createStatement();
			String deleteStatement = Filter.getQuery(sql, filters);
			stmt.executeUpdate(deleteStatement);
		}
		catch (Exception e) {
			Activator.handleError(e);
			throw e;
		}
		finally {
	        try
	        {
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e)
	        {
	        	 Activator.handleError("Could not close Statement" ,e);
	        }
	        try
	        {
	            if (connection  != null)
	                connection.close();
	        }
	        catch (SQLException e)
	        {
	        	 Activator.handleError("Could not close Connection" ,e);
	        }			
		}
	}
	
	public int updatePatients(Landscape landscape, Update[] updates, Filter[] filters) throws Exception {
		return update(SQL_HOSPITAL_UPDATE, landscape, updates, filters);
	}
	
	public int updateIdentityMappings(Landscape landscape, Update[] updates, Filter[] filters) throws Exception {
		return update(SQL_IDENTITY_MAPPING_UPDATE, landscape, updates, filters);
	}
	
	public int updateSynchronizationStatuses(Landscape landscape, Update[] updates, Filter[] filters) throws Exception {
		return update(SQL_SYNCHRONIZATION_STATUS_UPDATE, landscape, updates, filters);
	}
	
	public void pauseSynchronization(SynchronizationStatus status) throws Exception {
		Filter sourceSystemFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_ID, status.getSourceSystemId(), true);
		Filter sourceRepositoryFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_REPOSITORY_ID, status.getSourceRepositoryId(), true);
		Filter targetSystemFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_TARGET_SYSTEM_ID, status.getTargetSystemId(), true);
		Filter targetRepositoryFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_TARGET_REPOSITORY_ID, status.getTargetRepositoryId(), true);
		Filter[] filters = { sourceSystemFilter, sourceRepositoryFilter, targetSystemFilter, targetRepositoryFilter };
		Update update = new Update(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_KIND, status.getSourceSystemKind() + "_paused");
		Update[] updates = { update };	
		updateSynchronizationStatuses(status.getLandscape(), updates, filters);
		status.setSourceSystemKind(status.getSourceSystemKind() + "_paused");
	}
	
	public void resumeSynchronization(SynchronizationStatus status) throws Exception {
		int index = status.getSourceSystemKind().indexOf("_paused");
		if (index != -1) {
			Filter sourceSystemFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_ID, status.getSourceSystemId(), true);
			Filter sourceRepositoryFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_REPOSITORY_ID, status.getSourceRepositoryId(), true);
			Filter targetSystemFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_TARGET_SYSTEM_ID, status.getTargetSystemId(), true);
			Filter targetRepositoryFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_TARGET_REPOSITORY_ID, status.getTargetRepositoryId(), true);
			Filter[] filters = { sourceSystemFilter, sourceRepositoryFilter, targetSystemFilter, targetRepositoryFilter };			
			Update update = new Update(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_KIND, status.getSourceSystemKind().substring(0, index));
			Update[] updates = { update };	
			updateSynchronizationStatuses(status.getLandscape(), updates, filters);
			status.setSourceSystemKind(status.getSourceSystemKind().substring(0, index));
		}
	}
	
	public void resetSynchronizationStatus(SynchronizationStatus status) throws Exception {
		resetSynchronizationStatus(status, Timestamp.valueOf("1999-01-01 00:00:00.0"));
	}
	
	public void resetSynchronizationStatus(final SynchronizationStatus status, final Timestamp timestamp) throws Exception {
		// Pause first so that changes are not overlaid.
		final boolean pausedAlready = status.isPaused();
		if (!pausedAlready) pauseSynchronization(status);
		
		Runnable runnable = new Runnable() {
			public void run() {
				Filter sourceSystemFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_ID, status.getSourceSystemId(), true);
				Filter sourceRepositoryFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_REPOSITORY_ID, status.getSourceRepositoryId(), true);
				Filter targetSystemFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_TARGET_SYSTEM_ID, status.getTargetSystemId(), true);
				Filter targetRepositoryFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_TARGET_REPOSITORY_ID, status.getTargetRepositoryId(), true);
				Filter[] filters = { sourceSystemFilter, sourceRepositoryFilter, targetSystemFilter, targetRepositoryFilter };
				Update dateUpdate = new Update(CcfDataProvider.SYNCHRONIZATION_STATUS_LAST_SOURCE_ARTIFACT_MODIFICATION_DATE, timestamp.toString());
				String version;
				if (status.getSourceSystemKind().startsWith(Landscape.TYPE_PT)) {
					version = Long.toString(timestamp.getTime());	
				} else {
					version = "0";
				}
				Update versionUpdate = new Update(CcfDataProvider.SYNCHRONIZATION_STATUS_LAST_SOURCE_ARTIFACT_VERSION, version);
				
				Update idUpdate = new Update(CcfDataProvider.SYNCHRONIZATION_STATUS_LAST_SOURCE_ARTIFACT_ID, "0");
				Update[] updates = { dateUpdate, versionUpdate, idUpdate };
				try {
					updateSynchronizationStatuses(status.getLandscape(), updates, filters);
					// Resume
					if (!pausedAlready) resumeSynchronization(status);				

					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							Activator.notifyChanged(status.getProjectMappings());
						}						
					});

				} catch (Exception e) {
					Activator.handleError(e);
				}
			}			
		};
		int delay;
		if (pausedAlready) delay = 0;
		else delay = Activator.getDefault().getPreferenceStore().getInt(Activator.PREFERENCES_RESET_DELAY);
		runAfterDelay(runnable, delay);
	}
	
	private void runAfterDelay(final Runnable runnable, final int delaySeconds) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(delaySeconds * 1000);
					runnable.run();
				} catch (InterruptedException e) {
					Activator.handleError(e);
				}				
			}			
		};
		thread.start();
	}
	
	public void deleteIdentityMappings(SynchronizationStatus status) throws Exception {
		pauseSynchronization(status);
		
		Filter sourceSystemFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_ID, status.getSourceSystemId(), true);
		Filter sourceRepositoryFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_SOURCE_REPOSITORY_ID, status.getSourceRepositoryId(), true);
		Filter targetSystemFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_TARGET_SYSTEM_ID, status.getTargetSystemId(), true);
		Filter targetRepositoryFilter = new Filter(CcfDataProvider.SYNCHRONIZATION_STATUS_TARGET_REPOSITORY_ID, status.getTargetRepositoryId(), true);
		Filter[] filters = { sourceSystemFilter, sourceRepositoryFilter, targetSystemFilter, targetRepositoryFilter };		
		deleteIdentityMappings(status.getLandscape(), filters);
		
		resumeSynchronization(status);
	}
	
	private int update(String sql, Landscape landscape, Update[] updates, Filter[] filters) throws Exception {
		Connection connection = null;
		Statement stmt = null;
		int rowsUpdated = 0;
		try {
			connection = getConnection(landscape);
			stmt = connection.createStatement();
			String updateStatement = Update.getUpdate(sql, updates);
			updateStatement = Filter.getQuery(updateStatement, filters);
			rowsUpdated = stmt.executeUpdate(updateStatement);
		}
		catch (Exception e) {
			Activator.handleError(e);
			throw e;
		}
		finally {
	        try
	        {
	            if (stmt != null)
	                stmt.close();
	        }
	        catch (Exception e)
	        {
	        	 Activator.handleError("Could not close Statement" ,e);
	        }
	        try
	        {
	            if (connection  != null)
	                connection.close();
	        }
	        catch (SQLException e)
	        {
	        	 Activator.handleError("Could not close Connection" ,e);
	        }			
		}
		return rowsUpdated;
	}
	
	private Connection getConnection(Landscape landscape) throws Exception {
		if (landscape == null) return getConnection();
//		Connection connection = null;
		String configurationFolder = landscape.getConfigurationFolder();
		File folder = new File(configurationFolder);
		File propertiesFile = new File(folder, "ccf.properties");
		try {
			FileInputStream inputStream = new FileInputStream(propertiesFile);
			Properties properties = new Properties();
			properties.load(inputStream);
			inputStream.close();
			String url = properties.getProperty(Activator.PROPERTIES_CCF_URL);
			String driver = properties.getProperty(Activator.PROPERTIES_CCF_DRIVER);
			String user = properties.getProperty(Activator.PROPERTIES_CCF_USER);
			String password = properties.getProperty(Activator.PROPERTIES_CCF_PASSWORD);			
			store.setValue(Activator.PREFERENCES_DATABASE_URL, url);
			store.setValue(Activator.PREFERENCES_DATABASE_DRIVER, driver);
			store.setValue(Activator.PREFERENCES_DATABASE_USER, user);
			store.setValue(Activator.PREFERENCES_DATABASE_PASSWORD, password);			
			return getConnection(driver, url, user, password);
		} catch (Exception e) {
			Activator.handleError(e);
			throw new Exception("Unable to connect to database", e);
		}
//		return connection;
	}

	private Connection getConnection() throws ClassNotFoundException, SQLException {
		return getConnection(store.getString(Activator.PREFERENCES_DATABASE_DRIVER), store.getString(Activator.PREFERENCES_DATABASE_URL), store.getString(Activator.PREFERENCES_DATABASE_USER), store.getString(Activator.PREFERENCES_DATABASE_PASSWORD));
	}
	
	private Connection getConnection(String driver, String url, String user, String password) throws ClassNotFoundException, SQLException {
		Class.forName(driver);
		return DriverManager.getConnection(url, user, password);	
	}	
	
	private Patient[] getPatients(ResultSet rs, Landscape landscape) throws SQLException {
		List<Patient> patients = new ArrayList<Patient>();
		while (rs.next()) {
			Patient patient = new Patient();
			patient.setId(rs.getInt(HOSPITAL_ID));
			patient.setTimeStamp(rs.getString(HOSPITAL_TIMESTAMP));
			patient.setExceptionClassName(rs.getString(HOSPITAL_EXCEPTION_CLASS_NAME));
			patient.setExceptionMessage(rs.getString(HOSPITAL_EXCEPTION_MESSAGE));
			patient.setCauseExceptionClassName(rs.getString(HOSPITAL_CAUSE_EXCEPTION_CLASS_NAME));
			patient.setCauseExceptionMessage(rs.getString(HOSPITAL_CAUSE_EXCEPTION_MESSAGE));
			patient.setStackTrace(rs.getString(HOSPITAL_STACK_TRACE));
			patient.setAdaptorName(rs.getString(HOSPITAL_ADAPTOR_NAME));
			patient.setOriginatingComponent(rs.getString(HOSPITAL_ORIGINATING_COMPONENT));
			patient.setDataType(rs.getString(HOSPITAL_DATA_TYPE));
			patient.setData(rs.getString(HOSPITAL_DATA));
			patient.setFixed(rs.getBoolean(HOSPITAL_FIXED));
			patient.setReprocessed(rs.getBoolean(HOSPITAL_REPROCESSED));
			patient.setSourceSystemId(rs.getString(HOSPITAL_SOURCE_SYSTEM_ID));
			patient.setSourceRepositoryId(rs.getString(HOSPITAL_SOURCE_REPOSITORY_ID));
			patient.setTargetSystemId(rs.getString(HOSPITAL_TARGET_SYSTEM_ID));
			patient.setTargetRepositoryId(rs.getString(HOSPITAL_TARGET_REPOSITORY_ID));
			patient.setSourceSystemKind(rs.getString(HOSPITAL_SOURCE_SYSTEM_KIND));
			patient.setSourceRepositoryKind(rs.getString(HOSPITAL_SOURCE_REPOSITORY_KIND));
			patient.setTargetSystemKind(rs.getString(HOSPITAL_TARGET_SYSTEM_KIND));
			patient.setTargetRepositoryKind(rs.getString(HOSPITAL_TARGET_REPOSITORY_KIND));
			patient.setSourceArtifactId(rs.getString(HOSPITAL_SOURCE_ARTIFACT_ID));
			patient.setTargetArtifactId(rs.getString(HOSPITAL_TARGET_ARTIFACT_ID));
			patient.setErrorCode(rs.getString(HOSPITAL_ERROR_CODE));
			patient.setSourceLastModificationTime(rs.getTimestamp(HOSPITAL_SOURCE_LAST_MODIFICATION_TIME));
			patient.setTargetLastModificationTime(rs.getTimestamp(HOSPITAL_TARGET_LAST_MODIFICATION_TIME));
			patient.setSourceArtifactVersion(rs.getString(HOSPITAL_SOURCE_ARTIFACT_VERSION));
			patient.setTargetArtifactVersion(rs.getString(HOSPITAL_TARGET_ARTIFACT_VERSION));
			patient.setArtifactType(rs.getString(HOSPITAL_ARTIFACT_TYPE));
			patient.setGenericArtifact(rs.getString(HOSPITAL_GENERIC_ARTIFACT));
			patient.setLandscape(landscape);
			patients.add(patient);
		}
		Patient[] patientArray = new Patient[patients.size()];
		patients.toArray(patientArray);
		return patientArray;
	}
	
	private SynchronizationStatus[] getSynchronizationStatuses(ResultSet rs, Landscape landscape, ProjectMappings projectMappings) throws SQLException {
		List<SynchronizationStatus> synchonizationStatuses = new ArrayList<SynchronizationStatus>();
		while (rs.next()) {
			SynchronizationStatus status = new SynchronizationStatus();
			status.setSourceSystemId(rs.getString(SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_ID));
			status.setSourceRepositoryId(rs.getString(SYNCHRONIZATION_STATUS_SOURCE_REPOSITORY_ID));
			status.setTargetSystemId(rs.getString(SYNCHRONIZATION_STATUS_TARGET_SYSTEM_ID));
			status.setTargetRepositoryId(rs.getString(SYNCHRONIZATION_STATUS_TARGET_REPOSITORY_ID));
			status.setSourceSystemKind(rs.getString(SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_KIND));
			status.setSourceRepositoryKind(rs.getString(SYNCHRONIZATION_STATUS_SOURCE_REPOSITORY_KIND));
			status.setTargetSystemKind(rs.getString(SYNCHRONIZATION_STATUS_TARGET_SYSTEM_KIND));
			status.setTargetRepositoryKind(rs.getString(SYNCHRONIZATION_STATUS_TARGET_REPOSITORY_KIND));
			status.setSourceLastModificationTime(rs.getTimestamp(SYNCHRONIZATION_STATUS_LAST_SOURCE_ARTIFACT_MODIFICATION_DATE));
			status.setSourceLastArtifactVersion(rs.getString(SYNCHRONIZATION_STATUS_LAST_SOURCE_ARTIFACT_VERSION));
			status.setSourceLastArtifactId(rs.getString(SYNCHRONIZATION_STATUS_LAST_SOURCE_ARTIFACT_ID));
			status.setConflictResolutionPriority(rs.getString(SYNCHRONIZATION_STATUS_CONFLICT_RESOLUTION_PRIORITY));
			status.setSourceSystemTimezone(rs.getString(SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_TIMEZONE));
			status.setSourceSystemEncoding(rs.getString(SYNCHRONIZATION_STATUS_SOURCE_SYSTEM_ENCODING));
			status.setTargetSystemTimezone(rs.getString(SYNCHRONIZATION_STATUS_TARGET_SYSTEM_TIMEZONE));
			status.setTargetSystemEncoding(rs.getString(SYNCHRONIZATION_STATUS_TARGET_SYSTEM_ENCODING));			
			status.setProjectMappings(projectMappings);
			status.setLandscape(landscape);
			synchonizationStatuses.add(status);
		}
		SynchronizationStatus[] statusArray = new SynchronizationStatus[synchonizationStatuses.size()];
		synchonizationStatuses.toArray(statusArray);
		return statusArray;
	}
	
	private IdentityMapping[] getIdentityMappings(ResultSet rs, Landscape landscape) throws SQLException {
		List<IdentityMapping> identityMappings = new ArrayList<IdentityMapping>();
		while (rs.next()) {
			IdentityMapping identityMapping = new IdentityMapping();
			identityMapping.setSourceSystemId(rs.getString(IDENTITY_MAPPING_SOURCE_SYSTEM_ID));
			identityMapping.setSourceRepositoryId(rs.getString(IDENTITY_MAPPING_SOURCE_REPOSITORY_ID));
			identityMapping.setTargetSystemId(rs.getString(IDENTITY_MAPPING_TARGET_SYSTEM_ID));
			identityMapping.setTargetRepositoryId(rs.getString(IDENTITY_MAPPING_TARGET_REPOSITORY_ID));
			identityMapping.setSourceSystemKind(rs.getString(IDENTITY_MAPPING_SOURCE_SYSTEM_KIND));
			identityMapping.setSourceRepositoryKind(rs.getString(IDENTITY_MAPPING_SOURCE_REPOSITORY_KIND));
			identityMapping.setTargetSystemKind(rs.getString(IDENTITY_MAPPING_TARGET_SYSTEM_KIND));
			identityMapping.setTargetRepositoryKind(rs.getString(IDENTITY_MAPPING_TARGET_REPOSITORY_KIND));
			identityMapping.setSourceArtifactId(rs.getString(IDENTITY_MAPPING_SOURCE_ARTIFACT_ID));
			identityMapping.setTargetArtifactId(rs.getString(IDENTITY_MAPPING_TARGET_ARTIFACT_ID));
			identityMapping.setSourceLastModificationTime(rs.getTimestamp(IDENTITY_MAPPING_SOURCE_LAST_MODIFICATION_TIME));
			identityMapping.setTargetLastModificationTime(rs.getTimestamp(IDENTITY_MAPPING_TARGET_LAST_MODIFICATION_TIME));
			identityMapping.setSourceArtifactVersion(rs.getString(IDENTITY_MAPPING_SOURCE_ARTIFACT_VERSION));
			identityMapping.setTargetArtifactVersion(rs.getString(IDENTITY_MAPPING_TARGET_ARTIFACT_VERSION));
			identityMapping.setArtifactType(rs.getString(IDENTITY_MAPPING_ARTIFACT_TYPE));
			identityMapping.setChildSourceArtifactId(rs.getString(IDENTITY_MAPPING_DEP_CHILD_SOURCE_ARTIFACT_ID));
			identityMapping.setChildSourceRepositoryId(rs.getString(IDENTITY_MAPPING_DEP_CHILD_SOURCE_REPOSITORY_ID));
			identityMapping.setChildSourceRepositoryKind(rs.getString(IDENTITY_MAPPING_DEP_CHILD_SOURCE_REPOSITORY_KIND));
			identityMapping.setChildTargetArtifactId(rs.getString(IDENTITY_MAPPING_DEP_CHILD_TARGET_ARTIFACT_ID));
			identityMapping.setChildTargetRepositoryId(rs.getString(IDENTITY_MAPPING_DEP_CHILD_TARGET_REPOSITORY_ID));
			identityMapping.setChildTargetRepositoryKind(rs.getString(IDENTITY_MAPPING_DEP_CHILD_TARGET_REPOSITORY_KIND));			
			identityMapping.setParentSourceArtifactId(rs.getString(IDENTITY_MAPPING_DEP_PARENT_SOURCE_ARTIFACT_ID));
			identityMapping.setParentSourceRepositoryId(rs.getString(IDENTITY_MAPPING_DEP_PARENT_SOURCE_REPOSITORY_ID));
			identityMapping.setParentSourceRepositoryKind(rs.getString(IDENTITY_MAPPING_DEP_PARENT_SOURCE_REPOSITORY_KIND));
			identityMapping.setParentTargetArtifactId(rs.getString(IDENTITY_MAPPING_DEP_PARENT_TARGET_ARTIFACT_ID));
			identityMapping.setParentTargetRepositoryId(rs.getString(IDENTITY_MAPPING_DEP_PARENT_TARGET_REPOSITORY_ID));
			identityMapping.setParentTargetRepositoryKind(rs.getString(IDENTITY_MAPPING_DEP_PARENT_TARGET_REPOSITORY_KIND));			
			identityMapping.setLandscape(landscape);
			identityMappings.add(identityMapping);
		}
		IdentityMapping[] identityMappingArray = new IdentityMapping[identityMappings.size()];
		identityMappings.toArray(identityMappingArray);
		return identityMappingArray;
	}

}

package com.collabnet.ccf.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.Patient;

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
	
	private final static String SQL_HOSPITAL_SELECT = "SELECT * FROM HOSPITAL";
	private final static String SQL_HOSPITAL_UPDATE = "UPDATE HOSPITAL";

	public Patient[] getPatients(Filter[] filters) throws SQLException, ClassNotFoundException {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		Patient[] patients = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(Filter.getQuery(SQL_HOSPITAL_SELECT, filters));
			patients = getPatients(rs);
		}
		catch (SQLException e) {
			Activator.handleError(e);
			throw e;
		}
		catch (ClassNotFoundException e) {
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
	
	public int updatePatients(Update[] updates, Filter[] filters) throws SQLException, ClassNotFoundException {
		Connection connection = null;
		Statement stmt = null;
		int rowsUpdated = 0;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			String updateStatement = Update.getUpdate(SQL_HOSPITAL_UPDATE, updates);
			updateStatement = Filter.getQuery(updateStatement, filters);
			rowsUpdated = stmt.executeUpdate(updateStatement);
		}
		catch (SQLException e) {
			Activator.handleError(e);
			throw e;
		}
		catch (ClassNotFoundException e) {
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

	private Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName(store.getString(Activator.PREFERENCES_DATABASE_DRIVER));
		return DriverManager.getConnection(store.getString(Activator.PREFERENCES_DATABASE_URL), store.getString(Activator.PREFERENCES_DATABASE_USER), store.getString(Activator.PREFERENCES_DATABASE_PASSWORD));	
	}
	
	private Patient[] getPatients(ResultSet rs) throws SQLException {
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
			patients.add(patient);
		}
		Patient[] patientArray = new Patient[patients.size()];
		patients.toArray(patientArray);
		return patientArray;
	}

}

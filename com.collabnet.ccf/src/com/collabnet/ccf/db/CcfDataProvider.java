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
import com.collabnet.ccf.model.Hospital;

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
	
	private final static String SQL = "SELECT * FROM HOSPITAL";

	public Hospital[] getHospitals(Filter[] filters) throws SQLException, ClassNotFoundException {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		Hospital[] hospitals = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(Filter.getQuery(SQL, filters));
			hospitals = getHospitals(rs);
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
		return hospitals;
	}

	private Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName(store.getString(Activator.PREFERENCES_DATABASE_DRIVER));
		return DriverManager.getConnection(store.getString(Activator.PREFERENCES_DATABASE_URL), store.getString(Activator.PREFERENCES_DATABASE_USER), store.getString(Activator.PREFERENCES_DATABASE_PASSWORD));	
	}
	
	private Hospital[] getHospitals(ResultSet rs) throws SQLException {
		List<Hospital> hospitals = new ArrayList<Hospital>();
		while (rs.next()) {
			Hospital hospital = new Hospital();
			hospital.setId(rs.getInt(HOSPITAL_ID));
			hospital.setTimeStamp(rs.getString(HOSPITAL_TIMESTAMP));
			hospital.setExceptionClassName(rs.getString(HOSPITAL_EXCEPTION_CLASS_NAME));
			hospital.setExceptionMessage(rs.getString(HOSPITAL_EXCEPTION_MESSAGE));
			hospital.setCauseExceptionClassName(rs.getString(HOSPITAL_CAUSE_EXCEPTION_CLASS_NAME));
			hospital.setCauseExceptionMessage(rs.getString(HOSPITAL_CAUSE_EXCEPTION_MESSAGE));
			hospital.setStackTrace(rs.getString(HOSPITAL_STACK_TRACE));
			hospital.setAdaptorName(rs.getString(HOSPITAL_ADAPTOR_NAME));
			hospital.setOriginatingComponent(rs.getString(HOSPITAL_ORIGINATING_COMPONENT));
			hospital.setDataType(rs.getString(HOSPITAL_DATA_TYPE));
			hospital.setData(rs.getString(HOSPITAL_DATA));
			hospital.setFixed(rs.getBoolean(HOSPITAL_FIXED));
			hospital.setReprocessed(rs.getBoolean(HOSPITAL_REPROCESSED));
			hospital.setSourceSystemId(rs.getString(HOSPITAL_SOURCE_SYSTEM_ID));
			hospital.setSourceRepositoryId(rs.getString(HOSPITAL_SOURCE_REPOSITORY_ID));
			hospital.setTargetSystemId(rs.getString(HOSPITAL_TARGET_SYSTEM_ID));
			hospital.setTargetRepositoryId(rs.getString(HOSPITAL_TARGET_REPOSITORY_ID));
			hospital.setSourceSystemKind(rs.getString(HOSPITAL_SOURCE_SYSTEM_KIND));
			hospital.setSourceRepositoryKind(rs.getString(HOSPITAL_SOURCE_REPOSITORY_KIND));
			hospital.setTargetSystemKind(rs.getString(HOSPITAL_TARGET_SYSTEM_KIND));
			hospital.setTargetRepositoryKind(rs.getString(HOSPITAL_TARGET_REPOSITORY_KIND));
			hospital.setSourceArtifactId(rs.getString(HOSPITAL_SOURCE_ARTIFACT_ID));
			hospital.setTargetArtifactId(rs.getString(HOSPITAL_TARGET_ARTIFACT_ID));
			hospital.setErrorCode(rs.getString(HOSPITAL_ERROR_CODE));
			hospital.setSourceLastModificationTime(rs.getTimestamp(HOSPITAL_SOURCE_LAST_MODIFICATION_TIME));
			hospital.setTargetLastModificationTime(rs.getTimestamp(HOSPITAL_TARGET_LAST_MODIFICATION_TIME));
			hospital.setSourceArtifactVersion(rs.getString(HOSPITAL_SOURCE_ARTIFACT_VERSION));
			hospital.setTargetArtifactVersion(rs.getString(HOSPITAL_TARGET_ARTIFACT_VERSION));
			hospital.setArtifactType(rs.getString(HOSPITAL_ARTIFACT_TYPE));
			hospital.setGenericArtifact(rs.getString(HOSPITAL_GENERIC_ARTIFACT));
			hospitals.add(hospital);
		}
		Hospital[] hospitalArray = new Hospital[hospitals.size()];
		hospitals.toArray(hospitalArray);
		return hospitalArray;
	}

}

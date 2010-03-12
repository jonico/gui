/*
 * Copyright 2009 CollabNet, Inc. ("CollabNet")
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **/
package com.collabnet.ccf.qc.schemageneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.collabnet.ccf.core.CCFRuntimeException;
import com.collabnet.ccf.core.GenericArtifact;
import com.collabnet.ccf.core.GenericArtifactField;
import com.collabnet.ccf.core.GenericArtifact.ArtifactActionValue;
import com.collabnet.ccf.core.GenericArtifact.ArtifactModeValue;
import com.collabnet.ccf.core.GenericArtifact.ArtifactTypeValue;
import com.collabnet.ccf.core.GenericArtifact.IncludesFieldMetaDataValue;
import com.collabnet.ccf.core.GenericArtifactField.FieldValueTypeValue;
import com.collabnet.ccf.pi.qc.v90.api.ICommand;
import com.collabnet.ccf.pi.qc.v90.api.IConnection;
import com.collabnet.ccf.pi.qc.v90.api.IRecordSet;
import com.collabnet.ccf.pi.qc.v90.api.dcom.Connection;
import com.collabnet.ccf.schemageneration.RepositoryLayoutExtractor;

/**
 * The QCSchemaGenerator is responsible for generating a generic artifact that
 * fully describes the schema of a repository
 * 
 * @author jnicolai
 * 
 */
public class QCLayoutExtractor implements RepositoryLayoutExtractor {

	static final String sfColumnName = "SF_COLUMN_NAME";
	static final String sfColumnType = "SF_COLUMN_TYPE";
	static final String sfUserLabel = "SF_USER_LABEL";
	static final String sfEditStyle = "SF_EDIT_STYLE";
	static final String sfIsMultiValue = "SF_IS_MULTIVALUE";

	static final String sfRootId = "SF_ROOT_ID";
	static final String alDescription = "AL_DESCRIPTION";
	static final String usUsername = "US_USERNAME";

	static final String sfListComboValue = "ListCombo";
	static final String sfUserComboValue = "UserCombo";

	static final String numberDataType = "number";
	static final String charDataType = "char";
	static final String memoDataType = "memo";
	static final String dateDataType = "DATE";

	static final String bgBugIdFieldName = "BG_BUG_ID";
	static final String auActionIdFieldName = "AU_ACTION_ID";
	static final String apFieldNameFieldName = "AP_FIELD_NAME";

	static final String apOldValueFieldName = "AP_OLD_VALUE";
	static final String apOldLongValueFieldName = "AP_OLD_LONG_VALUE";

	static final String CARDINALITY_GREATER_THAN_ONE = "GT1";
	public static final String PARAM_DELIMITER = "-";

	private String serverUrl;
	private String userName;
	private String password;
	private boolean comInitialized = false;
	
	/**
	 * This data structure maps the repository id to the corresponding requirements type's technical id
	 */
	static Map <String, String> repositoryIdToTechnicalRequirementsTypeIdMap = new HashMap<String, String>();

	private void closeConnection(IConnection connection) {
		try {
			connection.logout();
			connection.disconnect();
		} catch (Exception e) {
			// log.warn(
			// "Could not close QC connection. So releasing the Connection COM dispatch"
			// , e);
		}
		connection.safeRelease();
		connection = null;
	}

	private void initCOM() {
		if (!comInitialized) {
			ComHandle.initCOM();
			comInitialized = true;
		}
	}

	private void tearDownCOM() {
		if (comInitialized) {
			ComHandle.tearDownCOM();
			comInitialized = false;
		}
	}

	private IConnection createConnection(String serverUrl, String repositoryId,
			String username, String password) {
		String domain = null;
		String project = null;
		if (repositoryId != null) {
			String[] splitRepoId = repositoryId.split(PARAM_DELIMITER);
			if (splitRepoId != null) {
				if (splitRepoId.length == 2 || splitRepoId.length == 3) {
					domain = splitRepoId[0];
					project = splitRepoId[1];
				} else {
					throw new IllegalArgumentException("Repository Id "
							+ repositoryId + " is invalid.");
				}
			}
		} else {
			throw new IllegalArgumentException("Repository Id cannot be null");
		}

		IConnection connection = new Connection(serverUrl, domain, project,
				username, password);
		return connection;
	}

	/**
	 * Returns the server URL of the source HP QC system that is configured in
	 * the wiring file.
	 * 
	 * @return
	 */
	public String getServerUrl() {
		return serverUrl;
	}

	/**
	 * Sets the source HP QC system's server URL.
	 * 
	 * @param serverUrl
	 *            - the URL of the source HP QC system.
	 */
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	/**
	 * Sets the password that belongs to the username
	 * 
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets the mandatory username
	 * 
	 * The user name is used to login into the HP QC instance whenever an
	 * artifact should be updated or extracted. This user has to differ from the
	 * resync user in order to force initial resyncs with the source system once
	 * a new artifact has been created.
	 * 
	 * @param userName
	 *            the user name to set
	 */
	public void setUserName(String username) {
		this.userName = username;
	}

	public GenericArtifact getProjectSchema(String repositoryId) {
		IConnection qcConnection = null;
		try {
			initCOM();
			qcConnection = createConnection(serverUrl, repositoryId, userName,
					password);
			if (isDefectRepository(repositoryId)) {
				return getSchemaFieldsForDefect(qcConnection);
			} else {
				String technicalReleaseId = extractTechnicalRequirementsType(repositoryId, qcConnection);
				return getSchemaFieldsForRequirement(qcConnection, technicalReleaseId);
			}
		} finally {
			if (qcConnection != null) {
				closeConnection(qcConnection);
			}
			tearDownCOM();
		}
	}
	
	/**
	 * Validates, whether given QC domain and project (will throw an exception otherwise) 
	 * @param domain
	 * @param project
	 */
	public void validateQCDomainAndProject(String domain, String project) {
		IConnection qcConnection = null;
		try {
			initCOM();
			qcConnection = createConnection(serverUrl, domain + PARAM_DELIMITER + project, userName,
					password);
		} finally {
			if (qcConnection != null) {
				closeConnection(qcConnection);
			}
			tearDownCOM();
		}
	}
	
	/**
	 * Get the available requirement types
	 * @param domain
	 * @param project
	 * @return list of available requirement types
	 */
	public List<String> getRequirementTypes(String domain, String project) {
		List<String> requirementTypes = new ArrayList<String>();
		IConnection qcConnection = null;
		try {
			initCOM();
			qcConnection = createConnection(serverUrl, domain + PARAM_DELIMITER + project, userName,
					password);
			String sql = "SELECT TPR_NAME FROM REQ_TYPE";
			IRecordSet rs = null;
			try {
				rs = executeSQL(qcConnection, sql);
				int rc = rs.getRecordCount();
				for (int cnt = 0; cnt < rc; cnt++, rs.next()) {
					String reqType = rs.getFieldValueAsString("TPR_NAME");
					requirementTypes.add(reqType);
				}
			} finally {
				if (rs != null) {
					rs.safeRelease();
					rs = null;
				}
			}
		} finally {
			if (qcConnection != null) {
				closeConnection(qcConnection);
			}
			tearDownCOM();
		}
		return requirementTypes;
	}
	
	public static GenericArtifact getSchemaFieldsForRequirement(IConnection qcc,
			String technicalReleaseTypeId) {

		// Get all the fields in the project represented
		// by qcc
		String sql = "SELECT * FROM REQ_TYPE_FIELD rf, system_field sf where rf.rtf_type_id = '"
				+ technicalReleaseTypeId
				+ "' and rf.rtf_sf_column_name = sf.sf_column_name";
		GenericArtifact genericArtifact = null;
		IRecordSet rs = null;
		try {
			rs = executeSQL(qcc, sql);
			int rc = rs.getRecordCount();
			genericArtifact = new GenericArtifact();
			genericArtifact.setArtifactAction(ArtifactActionValue.CREATE);
			genericArtifact.setArtifactMode(ArtifactModeValue.COMPLETE);
			genericArtifact.setArtifactType(ArtifactTypeValue.PLAINARTIFACT);
			genericArtifact
					.setIncludesFieldMetaData(IncludesFieldMetaDataValue.TRUE);
			
			for (int cnt = 0; cnt < rc; cnt++, rs.next()) {
				String columnName = rs.getFieldValueAsString(sfColumnName);
				
				String columnType = rs.getFieldValueAsString(sfColumnType);

				String fieldDisplayName = rs.getFieldValueAsString(sfUserLabel);
				if (fieldDisplayName == null) {
					continue;
				}

				List<String> fieldValueOptions = new ArrayList<String>();
				
				String editStyle = rs.getFieldValueAsString(sfEditStyle);
				// String isMultiValue =
				// rs.getFieldValueAsString(sfIsMultiValue);
				GenericArtifactField field;

				// obtain the GenericArtifactField datatype from the columnType
				// and editStyle
				GenericArtifactField.FieldValueTypeValue fieldValueType = convertQCDataTypeToGADatatype(
						columnType, editStyle, columnName);

				boolean isMultiSelectField = false;
				if (fieldValueType
						.equals(GenericArtifactField.FieldValueTypeValue.STRING)) {
					String isMultiValue = rs
							.getFieldValueAsString(sfIsMultiValue);
					
					String rootId = rs.getFieldValueAsString(sfRootId);
					addFieldOptionValue(qcc, rootId, fieldValueOptions);

					if (columnType.equals("char") && editStyle != null
							&& isMultiValue != null
							&& isMultiValue.trim().length() > 0
//							&& !StringUtils.isEmpty(isMultiValue)
							&& isMultiValue.equals("Y")) {
						if (editStyle.equals("ListCombo")
								|| editStyle.equals("TreeCombo")
								|| editStyle.equals("ReqTreeCombo")) {
							isMultiSelectField = true;
						}
					}
				}

				field = genericArtifact.addNewField(columnName,
						GenericArtifactField.VALUE_FIELD_TYPE_FLEX_FIELD);
				field.setFieldValueType(fieldValueType);
				field
						.setMaxOccursValue(isMultiSelectField ? GenericArtifactField.UNBOUNDED
								: "1");
				field.setMinOccurs(0);
				field.setNullValueSupported("true");
				field.setAlternativeFieldName(columnName);
				
				// special treatment for some fields (have to find out whether this is only due to the agile accelarator)
				if (columnName.equals("RQ_TYPE_ID")) {
					fieldValueType = FieldValueTypeValue.STRING;
					field.setFieldValueType(FieldValueTypeValue.STRING);
				}
				else if (columnName.equals("RQ_REQ_TIME")) {
					fieldValueType = FieldValueTypeValue.STRING;
					field.setFieldValueType(FieldValueTypeValue.STRING);
				}
				else if (columnName.equals("RQ_VC_CHECKOUT_TIME")) {
					fieldValueType = FieldValueTypeValue.STRING;
					field.setFieldValueType(FieldValueTypeValue.STRING);
				}
				if (columnName.equals("RQ_DEV_COMMENTS")) {
					field
						.setFieldAction(GenericArtifactField.FieldActionValue.APPEND);
				}
				else {
					field
						.setFieldAction(GenericArtifactField.FieldActionValue.REPLACE);
				}
				field.setFieldValue(generateFieldDocumentation(
						fieldDisplayName, fieldValueType, fieldValueOptions));
			}
		} finally {
			if (rs != null) {
				rs.safeRelease();
				rs = null;
			}
		}

		return genericArtifact;
	}
	
	/**
	 * Returns whether this repository id belongs to a defect repository
	 * If not, it belongs to a requirements type
	 * @param repositoryId repositoryId
	 * @return true if repository id belongs to a defect repository
	 */
	public static boolean isDefectRepository(String repositoryId) {
		String[] splitRepoId = repositoryId.split(PARAM_DELIMITER);
		if(splitRepoId != null){
			// we now also accept a double hyphen to synchronize requirement types as well
			if(splitRepoId.length == 2){
				return true;
			}
			else if (splitRepoId.length == 3) {
				return false;
			}
			else {
				throw new IllegalArgumentException("Repository Id "+repositoryId+" is invalid.");
			}
		}
		throw new IllegalArgumentException("Repository Id "+repositoryId+" is invalid.");
	}

	private static GenericArtifactField.FieldValueTypeValue convertQCDataTypeToGADatatype(String dataType, String editStyle, String columnName) {

		// TODO: Convert the datatype, editStyle pair to a valid GA type
		if(dataType.equals("char") && (editStyle==null || ( editStyle!=null && editStyle.equals(""))) )
			return GenericArtifactField.FieldValueTypeValue.STRING;
		if(dataType.equals("char") && ( editStyle!=null && editStyle.equals("UserCombo")) )
			return GenericArtifactField.FieldValueTypeValue.USER;
		if(dataType.equals("char") && ( editStyle!=null && editStyle.equals("DateCombo")) )
			return GenericArtifactField.FieldValueTypeValue.DATE;
		if(dataType.equals("char") && ( editStyle!=null && editStyle.equals("ListCombo")) ) {
			//if(isMultiValue.equals("N"))
				return GenericArtifactField.FieldValueTypeValue.STRING;
			//if(isMultiValue.equals("Y")) // MULTI_SELECT_LIST
			//	return GenericArtifactField.FieldValueTypeValue.STRING;
		}
		if(dataType.equals("memo"))
			return GenericArtifactField.FieldValueTypeValue.HTMLSTRING;
		if(dataType.equals("char") && ( editStyle!=null && editStyle.equals("TreeCombo")) ) {
			//if(isMultiValue.equals("N"))
				return GenericArtifactField.FieldValueTypeValue.STRING;
			//if(isMultiValue.equals("Y")) // MULTI_SELECT_LIST
			//	return GenericArtifactField.FieldValueTypeValue.STRING;
		}
		if(dataType.equals("char") && ( editStyle!=null && editStyle.equals("ReqTreeCombo")) ) {
			//if(isMultiValue.equals("N"))
				return GenericArtifactField.FieldValueTypeValue.STRING;
			//if(isMultiValue.equals("Y")) // MULTI_SELECT_LIST
			//	return GenericArtifactField.FieldValueTypeValue.STRING;
		}

		if(dataType.equals("number"))
			return GenericArtifactField.FieldValueTypeValue.INTEGER;
		if(dataType.equals("DATE") && (editStyle!=null && editStyle.equals("DateCombo")) )
			return GenericArtifactField.FieldValueTypeValue.DATE;
		if(dataType.equals("DATE") && editStyle==null)
			return GenericArtifactField.FieldValueTypeValue.DATE;
		if(dataType.equals("time"))
			return GenericArtifactField.FieldValueTypeValue.DATETIME;

		return GenericArtifactField.FieldValueTypeValue.STRING;
	}

	
	private static Object generateFieldDocumentation(String fieldDisplayName,
			FieldValueTypeValue fieldValueType, List<String> fieldValues) {
		StringBuffer documentation = new StringBuffer();
		documentation.append(fieldDisplayName + " (" + fieldValueType + ")\n");
		if (fieldValues != null && fieldValues.size() != 0) {
			Collections.sort(fieldValues);
			documentation.append(" Values: [");
			for (String fieldValue : fieldValues) {
				documentation.append(" '" + fieldValue + "',");
			}
			/*
			 * if (fieldValues.length == 0) { documentation.append("<empty>"); }
			 */
			documentation.deleteCharAt(documentation.length() - 1);
			documentation.append(" ]");
		}
		return documentation.toString();
	}

	private static GenericArtifact getSchemaFieldsForDefect(IConnection qcc) {

		// Get all the fields in the project represented
		// by qcc
		String sql = "SELECT * FROM SYSTEM_FIELD WHERE SF_TABLE_NAME='BUG'";
		GenericArtifact genericArtifact = null;
		IRecordSet rs = null;
		try {
			rs = executeSQL(qcc, sql);
			int rc = rs.getRecordCount();
			genericArtifact = new GenericArtifact();
			genericArtifact.setArtifactAction(ArtifactActionValue.CREATE);
			genericArtifact.setArtifactMode(ArtifactModeValue.COMPLETE);
			genericArtifact.setArtifactType(ArtifactTypeValue.PLAINARTIFACT);
			genericArtifact
					.setIncludesFieldMetaData(IncludesFieldMetaDataValue.TRUE);
			for (int cnt = 0; cnt < rc; cnt++, rs.next()) {
				String columnName = rs.getFieldValueAsString(sfColumnName);

				String columnType = rs.getFieldValueAsString(sfColumnType);
				String fieldDisplayName = rs.getFieldValueAsString(sfUserLabel);
				if (fieldDisplayName == null) {
					continue;
				}
				List<String> fieldValueOptions = new ArrayList<String>();

				String editStyle = rs.getFieldValueAsString(sfEditStyle);
				// String isMultiValue =
				// rs.getFieldValueAsString(sfIsMultiValue);
				GenericArtifactField field;

				// obtain the GenericArtifactField datatype from the columnType
				// and editStyle
				GenericArtifactField.FieldValueTypeValue fieldValueType = convertQCDataTypeToGADatatype(
						columnType, editStyle, columnName);

				boolean isMultiSelectField = false;
				if (fieldValueType
						.equals(GenericArtifactField.FieldValueTypeValue.STRING)) {

					String rootId = rs.getFieldValueAsString(sfRootId);
					addFieldOptionValue(qcc, rootId, fieldValueOptions);
					String isMultiValue = rs
							.getFieldValueAsString(sfIsMultiValue);

					if (columnType.equals("char") && editStyle != null
							&& isMultiValue != null
							&& isMultiValue.trim().length() > 0
//							&& !StringUtils.isEmpty(isMultiValue)
							&& isMultiValue.equals("Y")) {
						if (editStyle.equals("ListCombo")
								|| editStyle.equals("TreeCombo")) {
							isMultiSelectField = true;
						}
					}
				}

				field = genericArtifact.addNewField(columnName,
						GenericArtifactField.VALUE_FIELD_TYPE_FLEX_FIELD);
				field.setFieldValueType(fieldValueType);
				field
						.setMaxOccursValue(isMultiSelectField ? GenericArtifactField.UNBOUNDED
								: "1");
				field.setMinOccurs(0);
				field.setNullValueSupported("true");
				field.setAlternativeFieldName(columnName);

				// Only for the Comments field, the action value of the
				// GenericArtifactField is set to APPEND. Later, this feature
				// can be upgraded.
				if (columnName != null && columnName.equals("BG_DEV_COMMENTS"))
					field
							.setFieldAction(GenericArtifactField.FieldActionValue.APPEND);
				if (columnName != null
						&& !(columnName.equals("BG_DEV_COMMENTS")))
					field
							.setFieldAction(GenericArtifactField.FieldActionValue.REPLACE);
				
				if (columnName.equals("BG_VC_CHECKOUT_TIME")) {
					fieldValueType = FieldValueTypeValue.STRING;
					field.setFieldValueType(FieldValueTypeValue.STRING);
				}
				
				field.setFieldValue(generateFieldDocumentation(
						fieldDisplayName, fieldValueType, fieldValueOptions));
			}
		} finally {
			if (rs != null) {
				rs.safeRelease();
				rs = null;
			}
		}

		return genericArtifact;
	}

	private static void addFieldOptionValue(IConnection qcc, String rootId,
			List<String> fieldValueOptions) {
		if ("0".equals(rootId)) {
			return;
		}
		IRecordSet rsDetails = null;
		try {
			String detailSQL = "SELECT al_description as VALUE, al_no_of_sons AS SONS, al_item_id AS ROOT_ID FROM ALL_LISTS where al_father_id = "
					+ rootId;
			rsDetails = executeSQL(qcc, detailSQL);
			int detailRowCount = rsDetails.getRecordCount();

			for (int i = 0; i < detailRowCount; ++i) {
				int noOfSons = Integer.parseInt(rsDetails
						.getFieldValueAsString("SONS"));
				if (noOfSons == 0) {
					fieldValueOptions.add(rsDetails
							.getFieldValueAsString("VALUE"));
				} else {
					addFieldOptionValue(qcc, rsDetails
							.getFieldValueAsString("ROOT_ID"),
							fieldValueOptions);
				}
				rsDetails.next();
			}
		} finally {
			if (rsDetails != null) {
				rsDetails.safeRelease();
				rsDetails = null;
			}
		}

	}
	
	public static IRecordSet executeSQL(IConnection qcc, String sql) {
		ICommand command = null;
		try {
			command = qcc.getCommand();
			command.setCommandText(sql);
			IRecordSet rs = command.execute();
			return rs;
		} finally {
			command = null;
		}
	}

	public GenericArtifact getRepositoryLayout(String repositoryId) {
		return getProjectSchema(repositoryId);
	}
	
	/**
	 * Extracts the technical requirements type from the repository id (does a lookup and retrieve technical id for it)
	 * @param repositoryId repository id
	 * @param qcc HP QC connection
	 * @return technical requirements type
	 */
	public static String extractTechnicalRequirementsType (String repositoryId, IConnection qcc) {
		// first lookup the map
		String requirementsType = repositoryIdToTechnicalRequirementsTypeIdMap.get(repositoryId);
		if (requirementsType == null) {
			// we have to extract the requirements type now
			String[] splitRepoId = repositoryId.split(PARAM_DELIMITER);
			if(splitRepoId != null){
				// we now also accept a double hyphen to synchronize requirement types as well
				if(splitRepoId.length == 3){
					requirementsType = splitRepoId[2];
					// now we have to retrieve the technical id for the requirements type
					String technicalId = getRequirementTypeTechnicalId(qcc, requirementsType);
					repositoryIdToTechnicalRequirementsTypeIdMap.put(repositoryId, technicalId);
					return technicalId;
				}
				else {
					throw new IllegalArgumentException("Repository Id "+repositoryId+" is invalid.");
				}
			} else {
				throw new IllegalArgumentException("Repository Id "+repositoryId+" is invalid.");
			}
		} else {
			return requirementsType;
		}
	}
	
	public static String getRequirementTypeTechnicalId(IConnection qcc,
			String requirementTypeName) {
		IRecordSet rs = null;
		try {
			rs = executeSQL(qcc,
					"SELECT TPR_TYPE_ID FROM REQ_TYPE WHERE TPR_NAME = '"
							+ requirementTypeName + "'");
			if (rs.getRecordCount() != 1) {
				throw new CCFRuntimeException(
						"Could not retrieve technical id for requirements type "
								+ requirementTypeName);
			} else {
				return rs.getFieldValueAsString("TPR_TYPE_ID");
			}
		} finally {
			if (rs != null) {
				rs.safeRelease();
				rs = null;
			}
		}
	}

}
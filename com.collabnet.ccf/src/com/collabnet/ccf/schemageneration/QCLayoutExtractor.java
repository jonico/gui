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
package com.collabnet.ccf.schemageneration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.collabnet.ccf.pi.qc.v90.api.ICommand;
import com.collabnet.ccf.pi.qc.v90.api.IConnection;
import com.collabnet.ccf.pi.qc.v90.api.IRecordSet;
import com.collabnet.ccf.pi.qc.v90.api.dcom.Connection;
import com.collabnet.ccf.core.GenericArtifact;
import com.collabnet.ccf.core.GenericArtifactField;
import com.collabnet.ccf.core.GenericArtifact.ArtifactActionValue;
import com.collabnet.ccf.core.GenericArtifact.ArtifactModeValue;
import com.collabnet.ccf.core.GenericArtifact.ArtifactTypeValue;
import com.collabnet.ccf.core.GenericArtifact.IncludesFieldMetaDataValue;
import com.collabnet.ccf.core.GenericArtifactField.FieldValueTypeValue;

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
				if (splitRepoId.length == 2) {
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
			return getSchemaFields(qcConnection);
		} finally {
			if (qcConnection != null) {
				closeConnection(qcConnection);
			}
			tearDownCOM();
		}
	}

	private static GenericArtifactField.FieldValueTypeValue convertQCDataTypeToGADatatype(
			String dataType, String editStyle, String columnName) {

		// TODO: Convert the datatype, editStyle pair to a valid GA type
		if (dataType.equals("char")
				&& (editStyle == null || (editStyle != null && editStyle
						.equals(""))))
			return GenericArtifactField.FieldValueTypeValue.STRING;
		if (dataType.equals("char")
				&& (editStyle != null && editStyle.equals("UserCombo")))
			return GenericArtifactField.FieldValueTypeValue.USER;
		if (dataType.equals("char")
				&& (editStyle != null && editStyle.equals("DateCombo")))
			return GenericArtifactField.FieldValueTypeValue.DATE;
		if (dataType.equals("char")
				&& (editStyle != null && editStyle.equals("ListCombo"))) {
			// if(isMultiValue.equals("N"))
			return GenericArtifactField.FieldValueTypeValue.STRING;
			// if(isMultiValue.equals("Y")) // MULTI_SELECT_LIST
			// return GenericArtifactField.FieldValueTypeValue.STRING;
		}
		if (dataType.equals("memo"))
			return GenericArtifactField.FieldValueTypeValue.HTMLSTRING;
		if (dataType.equals("char")
				&& (editStyle != null && editStyle.equals("TreeCombo"))) {
			// if(isMultiValue.equals("N"))
			return GenericArtifactField.FieldValueTypeValue.STRING;
			// if(isMultiValue.equals("Y")) // MULTI_SELECT_LIST
			// return GenericArtifactField.FieldValueTypeValue.STRING;
		}

		if (dataType.equals("number"))
			return GenericArtifactField.FieldValueTypeValue.INTEGER;
		if (dataType.equals("DATE")
				&& (editStyle != null && editStyle.equals("DateCombo")))
			return GenericArtifactField.FieldValueTypeValue.DATE;

		// log.debug("Unknown QC data type "+dataType +
		// " of field "+columnName+" defaulting to STRING");
		return GenericArtifactField.FieldValueTypeValue.STRING;
	}

	// public static GenericArtifact getSchemaFieldsAndValues(IConnection qcc) {
	//
	// // Get all the fields in the project represented
	// // by qcc
	// String sql = "SELECT * FROM SYSTEM_FIELD WHERE SF_TABLE_NAME='BUG'";
	//
	// IRecordSet rs = null;
	// GenericArtifact genericArtifact = null;
	// try {
	// rs = QCDefectHandler.executeSQL(qcc, sql);
	// int rc = rs.getRecordCount();
	// genericArtifact = new GenericArtifact();
	// for(int cnt = 0 ; cnt < rc ; cnt++, rs.next())
	// {
	// String columnName = rs.getFieldValue(sfColumnName);
	// String columnType = rs.getFieldValue(sfColumnType);
	// //String fieldDisplayName = rs.getFieldValue(sfUserLabel);
	// String editStyle = rs.getFieldValue(sfEditStyle);
	// String isMultiValue = rs.getFieldValue(sfIsMultiValue);
	// GenericArtifactField field;
	//
	// // obtain the GenericArtifactField datatype from the columnType and
	// editStyle
	// GenericArtifactField.FieldValueTypeValue fieldValueTypeValue =
	// convertQCDataTypeToGADatatype(columnType, editStyle, isMultiValue);
	// field = genericArtifact.addNewField(columnName, columnType);
	// field.setFieldValueType(fieldValueTypeValue);
	//
	// // Only for the Comments field, the action value of the
	// GenericArtifactField is set to APPEND. Later, this feature can be
	// upgraded.
	// if(columnName!=null && columnName.equals("BG_DEV_COMMENTS"))
	// field.setFieldAction(GenericArtifactField.FieldActionValue.APPEND);
	// if(columnName!=null && !(columnName.equals("BG_DEV_COMMENTS")) )
	// field.setFieldAction(GenericArtifactField.FieldActionValue.REPLACE);
	//
	// // Obtain the value to set in the field
	// if (editStyle == sfListComboValue ) {
	// // Get the list values
	// String rootId = rs.getFieldValue(sfRootId);
	//
	// // Obtain the list values
	// String subSql = "SELECT * FROM ALL_LISTS WHERE AL_RATHER_ID = " + rootId;
	// IRecordSet subRs = null;
	// try {
	// subRs = QCDefectHandler.executeSQL(qcc, subSql);
	// int rsRc = subRs.getRecordCount();
	//
	// if ( rsRc > 0 ) {
	// List<String> values = new ArrayList<String>();
	// for (int rsCnt = 0 ; rsCnt < rsRc ; subRs.next()) {
	// String listValue = subRs.getFieldValue(alDescription);
	// values.add(listValue);
	// }
	// field.setFieldValue(values);
	// }
	// }
	// finally {
	// if(subRs != null){
	// subRs.safeRelease();
	// subRs = null;
	// }
	// }
	// }
	// else if (editStyle == sfUserComboValue ) {
	// // get the user values
	// String subSql = "SELECT * FROM USERS";
	// IRecordSet subRs = null;
	// try {
	// subRs = QCDefectHandler.executeSQL(qcc, subSql);
	// int rsRc = subRs.getRecordCount();
	//
	// if ( rsRc > 0 ) {
	// List<String> values = new ArrayList<String>();
	// for (int rsCnt = 0 ; rsCnt < rsRc ; subRs.next()) {
	// String userValue = subRs.getFieldValue(usUsername);
	// values.add(userValue);
	// }
	// field.setFieldValue(values);
	// }
	// }
	// finally {
	// if(subRs != null) {
	// subRs.safeRelease();
	// subRs = null;
	// }
	// }
	// }
	// }
	// }
	// finally {
	// if(rs != null){
	// rs.safeRelease();
	// rs = null;
	// }
	// }
	//
	// return genericArtifact;
	// }

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

	private static GenericArtifact getSchemaFields(IConnection qcc) {

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
							&& !StringUtils.isEmpty(isMultiValue)
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

	/*
	 * public static GenericArtifact getSchemaAttachments(IConnection qcc,
	 * GenericArtifact genericArtifact, String actionId, String entityId, String
	 * attachmentName) {
	 * 
	 * GenericArtifactAttachment attachment; List<String> attachmentIdAndType =
	 * QCDefectHandler.getFromTable(qcc, entityId, attachmentName);
	 * if(attachmentIdAndType!=null) { String attachmentId =
	 * attachmentIdAndType.get(0); // CR_REF_ID String attachmentContentType =
	 * attachmentIdAndType.get(1); // CR_REF_TYPE String attachmentDescription =
	 * attachmentIdAndType.get(2); // CR_DESCRIPTION
	 * 
	 * if(attachmentContentType.equals("File")) { attachment =
	 * genericArtifact.addNewAttachment(attachmentName, attachmentId,
	 * attachmentDescription);
	 * attachment.setAttachmentContentType(GenericArtifactAttachment
	 * .AttachmentContentTypeValue.DATA); } else { attachment =
	 * genericArtifact.addNewAttachment(attachmentId, attachmentId,
	 * attachmentDescription);
	 * attachment.setAttachmentContentType(GenericArtifactAttachment
	 * .AttachmentContentTypeValue.LINK); }
	 * attachment.setAttachmentAction(GenericArtifactAttachment
	 * .AttachmentActionValue.CREATE); }
	 * 
	 * return genericArtifact;
	 * 
	 * }
	 * 
	 * public static GenericArtifact getCompleteSchemaAttachments(IConnection
	 * qcc, String actionId, String entityId, List<String> attachmentNames) {
	 * 
	 * GenericArtifact genericArtifact = new GenericArtifact();
	 * if(attachmentNames!=null) { for(int cnt=0; cnt < attachmentNames.size();
	 * cnt++) {
	 * 
	 * GenericArtifactAttachment attachment; List<String> attachmentIdAndType =
	 * QCDefectHandler.getFromTable(qcc, entityId, attachmentNames.get(cnt));
	 * if(attachmentIdAndType!=null) { String attachmentId =
	 * attachmentIdAndType.get(0); // CR_REF_ID String attachmentContentType =
	 * attachmentIdAndType.get(1); // CR_REF_TYPE String attachmentDescription =
	 * attachmentIdAndType.get(2); // CR_DESCRIPTION
	 * 
	 * if(attachmentContentType.equals("File")) { attachment =
	 * genericArtifact.addNewAttachment(attachmentNames.get(cnt), attachmentId,
	 * attachmentDescription);
	 * attachment.setAttachmentContentType(GenericArtifactAttachment
	 * .AttachmentContentTypeValue.DATA); } else { attachment =
	 * genericArtifact.addNewAttachment(attachmentId, attachmentId,
	 * attachmentDescription);
	 * attachment.setAttachmentContentType(GenericArtifactAttachment
	 * .AttachmentContentTypeValue.LINK); }
	 * attachment.setAttachmentAction(GenericArtifactAttachment
	 * .AttachmentActionValue.CREATE); }
	 * 
	 * } } return genericArtifact;
	 * 
	 * }
	 */

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

}
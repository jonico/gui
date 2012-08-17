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
package com.collabnet.ccf.tfs.schemageneration;

import java.util.List;

import com.collabnet.ccf.core.GenericArtifact;
import com.collabnet.ccf.schemageneration.RepositoryLayoutExtractor;

/**
 * The QCSchemaGenerator is responsible for generating a generic artifact that
 * fully describes the schema of a repository
 * 
 * @author jnicolai
 * 
 */
public class TFSLayoutExtractor implements RepositoryLayoutExtractor {

	public static final String PARAM_DELIMITER = "-";

	private String serverUrl;
	private String userName;
	private String password;

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

	public List<String> getDomains () {
		
		TFSConnection connection = new TFSConnection(serverUrl, userName, password);
		return connection.getCollectionsNames();
		
	}
	
	public List<String> getProjects (String collectionName) {
		
		TFSConnection connection = new TFSConnection(serverUrl, userName, password);
		return connection.getProyectsNames(collectionName);
	}

	public GenericArtifact getRepositoryLayout(String repositoryId) {
		return getProjectSchema(repositoryId);
	}
	
	public GenericArtifact getProjectSchema(String repositoryId) {
		// TODO: See what is it for
//		IConnection qcConnection = null;
//		try {
//			initCOM();
//			qcConnection = createConnection(serverUrl, repositoryId, userName,
//					password);
//			if (isDefectRepository(repositoryId)) {
//				return getSchemaFieldsForDefect(qcConnection);
//			} else {
//				String technicalReleaseId = extractTechnicalRequirementsType(repositoryId, qcConnection);
//				return getSchemaFieldsForRequirement(qcConnection, technicalReleaseId);
//			}
//		} finally {
//			if (qcConnection != null) {
//				closeConnection(qcConnection);
//			}
//			tearDownCOM();
//		}
		return null;
	}
	
	public void validateTFSDomainAndProject(String collection, String project) {

		TFSConnection connection = null;
		connection = new TFSConnection(serverUrl, userName, password);
		connection.getWorkItemsTypesNames(collection, project);
		
	}
}
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
package com.collabnet.ccf.rqp.schemageneration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.collabnet.ccf.core.GenericArtifact;
import com.collabnet.ccf.schemageneration.RepositoryLayoutExtractor;
import com.rational.reqpro.rpx._Package;
import com.rational.reqpro.rpx._ReqType;
import com.rational.reqpro.rpx._ReqTypes;
import com.rational.reqpro.rpx._RootPackage;
import com.rational.reqpro.rpx.enumPackageLookups;
import com.rational.reqpro.rpx.enumRequirementsLookups;

/**
 * The RQPSchemaGenerator is responsible for generating a generic artifact that
 * fully describes the schema of a repository
 * 
 * @author jnicolai
 * 
 */
public class RQPLayoutExtractor implements RepositoryLayoutExtractor {

	public static final String PARAM_DELIMITER = "-";
	public static final String PARAM_DELIMITER_CREDENTIALS = ":";
	public static final String CONNECTION_INFO_DELIMITER = "\\";
	public static final String PROJECT_EXTENSION = ".rqs";
	
	private final String[] REQUIREMENT_TYPES = {"FNR","URS","PACKAGE"};

	private String serverUrl;
	private String userName;
	private String password;
	
	/**
	 * Returns the server URL of the source  RQP system that is configured in
	 * the wiring file.
	 * 
	 * @return
	 */
	public String getServerUrl() {
		return serverUrl;
	}

	/**
	 * Sets the source  RQP system's server URL.
	 * 
	 * @param serverUrl
	 *            - the URL of the source  RQP system.
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
	 * The user name is used to login into the  RQP instance whenever an
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
		return null;
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

	public GenericArtifact getRepositoryLayout(String repositoryId) {
		return getProjectSchema(repositoryId);
	}
	

	public List<String> getPackages(String rqp_package) {
		RQPConnection rqpConnection = new RQPConnection(userName +PARAM_DELIMITER_CREDENTIALS +password);
		rqp_package = rqp_package.replaceAll("@", Matcher.quoteReplacement(CONNECTION_INFO_DELIMITER));
		String connectionInfo = serverUrl + CONNECTION_INFO_DELIMITER + rqp_package + PROJECT_EXTENSION;
		rqpConnection.openProject(connectionInfo);
		ArrayList<String> folderNames = new ArrayList<String>();
		try {
			_RootPackage rootPackage = rqpConnection.getProjectConnection().GetRootPackage(true);
			int count = rootPackage.getCountWithChildren(enumPackageLookups.ePackageLookup_Key, true) + 1;
			int i =2, foundPackages=0;
			
			while(foundPackages != count){
				_Package childPackage = rqpConnection.getProjectConnection().GetPackage(i, enumPackageLookups.ePackageLookup_Key);
				
				if(childPackage != null){
					folderNames.add(childPackage.getName());
					foundPackages++;
				}
				
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return folderNames;
	}
	
	/**
	 * Retrieves all requirement types managed in RequisitePro
	 * 
	 * @param project
	 * @return
	 */
	public List<String> getRequirementTypes(String project) {
		RQPConnection rqpConnection = new RQPConnection(userName +PARAM_DELIMITER_CREDENTIALS +password);
		project = project.replaceAll("@", Matcher.quoteReplacement(CONNECTION_INFO_DELIMITER));
		String connectionInfo = serverUrl + CONNECTION_INFO_DELIMITER + project + PROJECT_EXTENSION;
		rqpConnection.openProject(connectionInfo);
		List<String> requirementTypeNames = new ArrayList<String>();
		try {
			_ReqTypes requirementTypes = rqpConnection.getProjectConnection().getReqTypes();
			
			for(int i=1;i<=requirementTypes.getCount();i++){
				_ReqType requirement = requirementTypes.getItem(i, enumRequirementsLookups.eReqsLookup_ReqTypeKey);
				requirementTypeNames.add(requirement.getName());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return requirementTypeNames;
	}
	
	/**
	 * Retrieves requirement types acronyms currently supported in RequisitePro synchronization process
	 * 
	 * @param project
	 * @return
	 */
	public List<String> getRequirementNames(String project) {
		List<String> requirementTypeNames = new ArrayList<String>();
		for (String requirement : REQUIREMENT_TYPES) {
			requirementTypeNames.add(requirement);
		}
		return requirementTypeNames;
	}	

	public void validateRQPProjectAndPackage(String project) {
		getRequirementTypes(project);
	}	

}
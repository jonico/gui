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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.atlassian.jira.rest.client.GetCreateIssueMetadataOptionsBuilder;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.domain.BasicComponent;
import com.atlassian.jira.rest.client.domain.BasicPriority;
import com.atlassian.jira.rest.client.domain.CimFieldInfo;
import com.atlassian.jira.rest.client.domain.CimIssueType;
import com.atlassian.jira.rest.client.domain.CimProject;
import com.atlassian.jira.rest.client.domain.EntityHelper;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;
import com.collabnet.ccf.core.GenericArtifact;
import com.collabnet.ccf.core.GenericArtifact.ArtifactActionValue;
import com.collabnet.ccf.core.GenericArtifact.ArtifactModeValue;
import com.collabnet.ccf.core.GenericArtifact.ArtifactTypeValue;
import com.collabnet.ccf.core.GenericArtifact.IncludesFieldMetaDataValue;
import com.collabnet.ccf.core.GenericArtifactField;
import com.collabnet.ccf.core.GenericArtifactField.FieldValueTypeValue;
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
	static final NullProgressMonitor pm = new NullProgressMonitor();

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
		String projectKey = JIRAMetaData.extractProjectKeyFromRepositoryId(repositoryId);
		String issueTypeString = JIRAMetaData.extractIssueTypeFromRepositoryId(repositoryId);
		GenericArtifact genericArtifact = new GenericArtifact();
		genericArtifact.setArtifactAction(ArtifactActionValue.CREATE);
		genericArtifact.setArtifactMode(ArtifactModeValue.COMPLETE);
		genericArtifact.setArtifactType(ArtifactTypeValue.PLAINARTIFACT);
		genericArtifact.setIncludesFieldMetaData(IncludesFieldMetaDataValue.TRUE);
		JiraRestClient jiraRestClient = getJiraConnection();
		if(jiraRestClient != null){
			final Iterable<CimProject> metadataProjects = jiraRestClient.getIssueClient().getCreateIssueMetadata(
					new GetCreateIssueMetadataOptionsBuilder().withProjectKeys(projectKey).withIssueTypeNames(issueTypeString).withExpandedIssueTypesFields().build(), pm);
			final CimProject project = metadataProjects.iterator().next();
			final CimIssueType createIssueType = EntityHelper.findEntityByName(project.getIssueTypes(), issueTypeString);
			Iterator<Entry<String, CimFieldInfo>> fieldsit = createIssueType.getFields().entrySet().iterator();
			while(fieldsit.hasNext()){
				 Entry<String, CimFieldInfo> field = fieldsit.next();
				CimFieldInfo fieldInfo = field.getValue();
				createGenericArtifactField(fieldInfo, genericArtifact);
				
			}
			//FIXME: Need to explore JIRA api to find status allowed values
			Set<Object> statusAllowedValues = new HashSet<Object>();
			statusAllowedValues.add("Open");
			statusAllowedValues.add("Closed");
			createGenericArtifactField("status", "status", FieldValueTypeValue.STRING.toString(), statusAllowedValues, genericArtifact);

			//comments for jira for layout extractor
			createGenericArtifactField("comment", "comment", FieldValueTypeValue.STRING.toString(), Collections.emptySet(), genericArtifact);
		}
		
		return genericArtifact;
	}
	
	private GenericArtifactField createGenericArtifactField(CimFieldInfo jiraFieldInfo,	GenericArtifact genericArtifact) {
		return createGenericArtifactField(jiraFieldInfo.getId(),  jiraFieldInfo.getId(), jiraFieldInfo.getSchema().getType(),
				jiraFieldInfo.getAllowedValues(), genericArtifact);
	}
	
	private GenericArtifactField createGenericArtifactField(String fieldName,String alternativeFieldName,String type,Iterable<Object> allowedValues,GenericArtifact genericArtifact){
		GenericArtifactField field = genericArtifact.addNewField(fieldName,	GenericArtifactField.VALUE_FIELD_TYPE_MANDATORY_FIELD);
		field.setFieldAction(GenericArtifactField.FieldActionValue.REPLACE);
		//FIXME : determine jira supported fieldvalue type
		field.setFieldValueType(FieldValueTypeValue.STRING);
		field.setFieldValueHasChanged(true);
		field.setAlternativeFieldName(alternativeFieldName);
		//FIXME: determine whether field supports null or not
		field.setNullValueSupported("true");
		field.setMinOccurs(0);
		//FIXME: determine max occur value for field
		field.setMaxOccurs(1);
		field.setFieldValue(TFSLayoutExtractor.generateFieldDocumentation(fieldName, alternativeFieldName, type,
				GenericArtifactField.VALUE_FIELD_TYPE_MANDATORY_FIELD, "true", allowedValues));
		return field;
	}

	private static Object generateFieldDocumentation(String fieldName,
			String alternativeFieldName, String fieldValueType,
			String fieldType, String isNullValueSupported,Iterable<Object> allowedValues) {
		StringBuffer documentation = new StringBuffer();
		documentation.append(fieldName + " (" + fieldType + " / "
				+ fieldValueType + ")\n");
		if(allowedValues != null){
			Iterator<Object> it = allowedValues.iterator();
			Set<String> sortedValues = new TreeSet<String>();
			while(it.hasNext()){
				Object obj = it.next();
				if(obj instanceof BasicPriority){
					sortedValues.add(String.valueOf(((BasicPriority)obj).getId())); // setting priority ids
				}else if(obj instanceof BasicComponent){
					sortedValues.add(((BasicComponent)obj).getName());
				}else if(obj instanceof String){
					sortedValues.add(it.next().toString());
				}
				
			}
			if(!sortedValues.isEmpty()){
				documentation.append(" Values: [");
				for (String fieldValueOption : sortedValues) {
					documentation.append(" '" + fieldValueOption + "',");
				}
				documentation.deleteCharAt(documentation.length() - 1);
				documentation.append(" ]");
			}
		}
		return documentation.toString();
	}
	
	public JiraRestClient getJiraConnection(){
		JiraRestClient restClient = null;
		try {
			URI jiraServerUri = new URI(serverUrl);
	    	JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
	    	restClient= factory.createWithBasicHttpAuthentication(jiraServerUri, userName,password);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return restClient;
	}
	
	public void validateTFSDomainAndProject(String collection, String project) {

		TFSConnection connection = null;
		connection = new TFSConnection(serverUrl, userName, password);
		connection.getWorkItemsTypesNames(collection, project);
		
	}
}
package com.collabnet.ccf.teamforge.schemageneration;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.EngineConfiguration;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.configuration.SimpleProvider;

import com.collabnet.teamforge.api.Connection;
import com.collabnet.teamforge.api.Filter;
import com.collabnet.teamforge.api.main.ProjectList;
import com.collabnet.teamforge.api.main.ProjectMemberList;
import com.collabnet.teamforge.api.main.ProjectMemberRow;
import com.collabnet.teamforge.api.main.ProjectRow;
import com.collabnet.teamforge.api.tracker.ArtifactDependencyRow;
import com.collabnet.teamforge.api.tracker.ArtifactDetailList;
import com.collabnet.teamforge.api.tracker.TrackerDO;
import com.collabnet.teamforge.api.tracker.TrackerFieldDO;
import com.collabnet.teamforge.api.tracker.TrackerFieldValueDO;
import com.collabnet.teamforge.api.tracker.TrackerList;
import com.collabnet.teamforge.api.tracker.TrackerRow;
import com.collabnet.teamforge.api.tracker.WorkflowTransitionRow;

public class TFSoapClient {
	
	private Connection connection;
	
	private static Map<String, TFSoapClient> clients = new HashMap<String, TFSoapClient>();

	public TFSoapClient(String serverUrl, String userId, String password) {
		connection = Connection.getConnection(serverUrl, userId, password, null, null, null, false);
		Connection.setEngineConfiguration(getEngineConfiguration());
		clients.put(serverUrl + userId + password, this);
	}
	
	public static TFSoapClient getSoapClient(String serverUrl, String userId, String password) {
		TFSoapClient client = clients.get(serverUrl + userId + password);
		if (client == null) client = new TFSoapClient(serverUrl, userId, password);
		return client;
	}
	
	public TrackerDO createTracker(String projectId, String trackerName, String trackerTitle, String trackerDescription, String icon) throws RemoteException {
		TrackerDO trackerDO = connection.getTrackerClient().createTracker(projectId, trackerName, trackerTitle, trackerDescription, icon);
		return trackerDO;
	}
	
	public TrackerFieldDO[] getFields(String trackerId) throws RemoteException {
		TrackerFieldDO[] fields = connection.getTrackerClient().getFields(trackerId);
		return fields;
	}
	
	public void setField(String objectId, TrackerFieldDO fieldData) throws RemoteException {
		connection.getTrackerClient().setField(objectId, fieldData);
	}
	
	public void addTextField(String trackerId, String fieldName, int displayColumns, int displayLines, boolean isRequired, boolean isDisabled, boolean isHiddenOnCreate, String defaultValue) throws RemoteException {
		connection.getTrackerClient().addTextField(trackerId, fieldName, displayColumns, displayLines, isRequired, isDisabled, isHiddenOnCreate, defaultValue);
	}
	
	public void addDateField(String trackerId, String fieldName, boolean isRequired, boolean isDisabled, boolean isHiddenOnCreate) throws RemoteException {
		connection.getTrackerClient().addDateField(trackerId, fieldName, isRequired, isDisabled, isHiddenOnCreate);
	}
	
	public void addMultiSelectField(String trackerId, String fieldName, int displayLines, boolean isRequired, boolean isDisabled, boolean isHiddenOnCreate, String[] fieldValues, String[] defaultValues) throws RemoteException {
		connection.getTrackerClient().addMultiSelectField(trackerId, fieldName, displayLines, isRequired, isDisabled, isHiddenOnCreate, fieldValues, defaultValues);
	}
	
	public boolean isFieldValueUsed(String trackerId, String fieldName, TrackerFieldValueDO fieldValue) throws RemoteException {
		Filter filter = new Filter(fieldName, fieldValue.getValue());
		Filter[] filters = { filter };
		String[] selectedColumns = { "COLUMN_ID" };
		ArtifactDetailList artifactList = connection.getTrackerClient().getArtifactDetailList(
				trackerId, 
				selectedColumns, 
				filters, 
				null, 
				0, 
				1, 
				false, 
				true);
		return artifactList.getDataRows().length > 0;
	}

	/**
	 * Get children dependencies of a given artifact
	 * 
	 * @param artifactId
	 *            artifact id
	 * @return list with children artifact dependencies
	 * @throws RemoteException
	 *             thrown if an errors occurs within SFEE
	 */
	public ArtifactDependencyRow[] getArtifactChildDependencies(String artifactId) throws RemoteException {
		ArtifactDependencyRow[] result = connection.getTrackerClient().getChildDependencyList(artifactId).getDataRows();
		return result;
	}
	
	/**
	 * Returns all trackers within the specified project
	 * 
	 * @param projectId
	 *            id of the project in question
	 * @return array with all requested trackers
	 * @throws RemoteException
	 *             when an error is encountered in listing trackers.
	 */
	public TrackerRow[] getAllTrackersOfProject(String projectId) throws RemoteException {
		TrackerList trackerList = connection.getTrackerClient().getTrackerList(projectId);
		return trackerList.getDataRows();
	}
	
	/**
	 * Returns basic tracker information
	 * 
	 * @param trackerId
	 *            id of the tracker in question
	 * @return tracker info
	 * @throws RemoteException
	 *             thrown if an errors occurs within SFEE
	 */
	public TrackerDO getTrackerInformation(String trackerId) throws RemoteException {
		return connection.getTrackerClient().getTrackerData(trackerId);
	}	
	
	/**
	 * Returns all workflow transitions possible for this tracker
	 * 
	 * @param trackerId
	 *            trackerId id of the tracker in question
	 * @return array with all possible transitions
	 * @throws RemoteException
	 */
	public WorkflowTransitionRow[] getWorkflowTransitions(String trackerId) throws RemoteException {
		return connection.getTrackerClient().getAllowedWorkflowTransitionList(trackerId).getDataRows();
	}
	
	/**
	 * Returns the custom or flex fields for a particular tracker
	 * 
	 * @param trackerId
	 * @return
	 * @throws RemoteException
	 */
	public TrackerFieldDO[] getFlexFields(String trackerId) throws RemoteException {
		TrackerFieldDO[] rows = connection.getTrackerClient().getFields(trackerId);
		return rows;
	}
	
	/**
	 * Retrieves the field meta-information of the tracker
	 * 
	 * @param trackerId
	 *            id of the tracker in question
	 * @return field meta data
	 * @throws RemoteException
	 *             thrown if an errors occurs within SFEE
	 */
	public TrackerFieldDO[] getSupportedFields(String trackerId) throws RemoteException {
		return connection.getTrackerClient().getFields(trackerId);
	}
	
	/**
	 * Returns all projects within the SFEE site currently logged in
	 * 
	 * @return project descriptions
	 * @throws RemoteException
	 *             when error occurs during project retrieval
	 */
	public ProjectRow[] getAllProjects() throws RemoteException {
		ProjectList projectList = connection.getTeamForgeClient().getProjectList();
		return projectList.getDataRows();
	}
	
	/**
	 * Returns all users belonging to the project
	 * @param projectId id of the project in question
	 * @return users within projects
	 * @throws RemoteException when error occurs during user list retrieval
	 */
	public ProjectMemberRow[] getAllProjectMembers(String projectId) throws RemoteException {
		 ProjectMemberList projectMemberList = connection.getTeamForgeClient().getProjectMemberList(projectId);
		return projectMemberList.getDataRows();
	}
	
	public boolean supports50() {
		return connection.supports50();
	}
	
	public boolean supports53() {
		return connection.supports53();
	}
	
	public static EngineConfiguration getEngineConfiguration() {
		SimpleProvider config = new SimpleProvider();
		config.deployTransport("http", new SimpleTargetedChain(new TeamForgeHTTPSender())); //$NON-NLS-1$
		config.deployTransport("https", new SimpleTargetedChain(new TeamForgeHTTPSender())); //$NON-NLS-1$
		return config;
	}		
}

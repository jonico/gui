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

import com.vasoftware.sf.soap44.webservices.sfmain.TrackerFieldSoapDO;
import com.vasoftware.sf.soap44.webservices.tracker.ArtifactDependencySoapRow;
import com.vasoftware.sf.soap44.webservices.tracker.ITrackerAppSoap;
import com.vasoftware.sf.soap44.webservices.tracker.TrackerSoapDO;
import com.vasoftware.sf.soap44.webservices.tracker.TrackerSoapList;
import com.vasoftware.sf.soap44.webservices.tracker.TrackerSoapRow;
import com.vasoftware.sf.soap44.webservices.tracker.WorkflowTransitionSoapRow;
import com.vasoftware.sf.soap44.webservices.ClientSoapStubFactory;
import java.rmi.RemoteException;

/**
 * The tracker handler class provides support for all webservices methods in the
 * ITrackerAppSoap interface
 */
public class TFTrackerHandler {
	/**
	 * Tracker Soap API handle
	 */
	private ITrackerAppSoap trackerHandler;

	/**
	 * Class constructor.
	 * 
	 * @param serverUrl
	 *            Soap server URL.
	 */
	public TFTrackerHandler(String serverUrl) {
		trackerHandler = (ITrackerAppSoap) ClientSoapStubFactory.getSoapStub(
				ITrackerAppSoap.class, serverUrl);
	}

	/**
	 * Get children dependencies of a given artifact
	 * 
	 * @param artifactId
	 *            artifact id
	 * @param sessionId
	 *            SFEE SOAP session id
	 * @return list with children artifact dependencies
	 * @throws RemoteException
	 *             thrown if an errors occurs within SFEE
	 */
	public ArtifactDependencySoapRow[] getArtifactChildDependencies(
			String sessionId, String artifactId) throws RemoteException {
		ArtifactDependencySoapRow[] result = trackerHandler
				.getChildDependencyList(sessionId, artifactId).getDataRows();
		return result;
	}

	/**
	 * Returns all trackers within the specified project
	 * 
	 * @param projectId
	 *            id of the project in question
	 * @param sessionId
	 *            User session id.
	 * @return array with all requested trackers
	 * @throws RemoteException
	 *             when an error is encountered in listing trackers.
	 */
	public TrackerSoapRow[] getAllTrackersOfProject(String sessionId,
			String projectId) throws RemoteException {

		TrackerSoapList trackerList = trackerHandler.getTrackerList(sessionId,
				projectId);
		return trackerList.getDataRows();
	}

	/**
	 * Returns basic tracker information
	 * 
	 * @param sessionId
	 *            User session id.
	 * @param trackerId
	 *            id of the tracker in question
	 * @return tracker info
	 * @throws RemoteException
	 *             thrown if an errors occurs within SFEE
	 */
	public TrackerSoapDO getTrackerInformation(String sessionId,
			String trackerId) throws RemoteException {
		return trackerHandler.getTrackerData(sessionId, trackerId);
	}

	/**
	 * Returns all workflow transitions possible for this tracker
	 * 
	 * @param sessionId
	 *            User session id.
	 * @param trackerId
	 *            trackerId id of the tracker in question
	 * @return array with all possible transitions
	 * @throws RemoteException
	 */
	public WorkflowTransitionSoapRow[] getWorkflowTransitions(String sessionId,
			String trackerId) throws RemoteException {
		return trackerHandler.getAllowedWorkflowTransitionList(sessionId,
				trackerId).getDataRows();
	}
	
	/**
	 * Returns the custom or flex fields for a particular tracker
	 * 
	 * @param sessionID
	 * @param trackerId
	 * @return
	 * @throws RemoteException
	 */
	public TrackerFieldSoapDO[] getFlexFields(String sessionID, String trackerId)
			throws RemoteException {
		TrackerFieldSoapDO[] rows = trackerHandler.getFields(sessionID, trackerId);
		return rows;
	}

	/**
	 * Retrieves the field meta-information of the tracker
	 * 
	 * @param sessionId
	 *            User session id.
	 * @param trackerId
	 *            id of the tracker in question
	 * @return field meta data
	 * @throws RemoteException
	 *             thrown if an errors occurs within SFEE
	 */
	public TrackerFieldSoapDO[] getSupportedFields(String sessionId,
			String trackerId) throws RemoteException {
		return trackerHandler.getFields(sessionId, trackerId);
	}
}

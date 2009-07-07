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

import java.io.IOException;
import java.rmi.RemoteException;
import com.vasoftware.sf.soap44.webservices.sfmain.ProjectMemberSoapList;
import com.vasoftware.sf.soap44.webservices.sfmain.ProjectMemberSoapRow;
import com.vasoftware.sf.soap44.webservices.sfmain.ProjectSoapList;
import com.vasoftware.sf.soap44.webservices.sfmain.ProjectSoapRow;
import com.vasoftware.sf.soap44.webservices.ClientSoapStubFactory;
import com.vasoftware.sf.soap44.webservices.sfmain.ISourceForgeSoap;

/**
 * This class is the base for every component that wants to access data stored
 * within a TF system. It contains the basic methods
 * to login and logout to TF via the TF web services.
 * 
 * @author jnicolai
 * 
 */
public abstract class TFConnectHelper {

	/**
	 * SourceForge Soap interface handle
	 */
	private ISourceForgeSoap mSfSoap;

	/**
	 * SFEE-SOAP-Session id
	 */
	private String sessionId;

	/**
	 * SFEE-server-URL Example: http://sfee-sap.cubit.sp.collab.net
	 */
	private String serverUrl;

	/**
	 * Password of account used to login into SFEE
	 */
	private String password;

	/**
	 * user name of account used to login into SFEE
	 */
	private String username;

	/**
	 * If this attribute is set to true, this component will not reconnect every
	 * time it has to retrieve data. If it is set to false it will reconnect to
	 * SFEE ever and ever again
	 */
	private boolean keepAlive = true;

	/**
	 * Variable that indicates whether this is the first connect to the SFEE
	 * system
	 */
	private boolean firstConnect = true;

	/**
	 * Connects to SFEE server and logs into it
	 * 
	 * @throws IOException
	 */
	public void connect() throws IOException {
		// reconnect again
		// log.debug("Connect was called");
		// TODO what will happen if connection breaks?
		if (firstConnect || !keepAlive) {
			mSfSoap = (ISourceForgeSoap) ClientSoapStubFactory.getSoapStub(
					ISourceForgeSoap.class, getServerUrl());

			login(getUsername(), getPassword());
			firstConnect = false;
		}
	}

	/**
	 * Returns all projects within the SFEE site currently logged in
	 * 
	 * @return project descriptions
	 * @throws RemoteException
	 *             when error occurs during project retrieval
	 */
	public ProjectSoapRow[] getAllProjects() throws RemoteException {
		ProjectSoapList projectList = mSfSoap.getProjectList(getSessionId());
		return projectList.getDataRows();
	}
	
	/**
	 * Returns all users belonging to the project
	 * @param projectId id of the project in question
	 * @return users within projects
	 * @throws RemoteException when error occurs during user list retrieval
	 */
	public ProjectMemberSoapRow[] getAllProjectMembers(String projectId) throws RemoteException {
		 ProjectMemberSoapList projectMemberList = mSfSoap.getProjectMemberList(getSessionId(),projectId);
		return projectMemberList.getDataRows();
	}

	/**
	 * Disconnects from the SFEE system
	 */
	public void disconnect() {
		// log.debug("Disconnect was called");
		if (keepAlive)
			return;
		try {
			logoff();
		} catch (RemoteException e) {
			// TODO Declare exception so that it can be processed by OA
			// exception handler
		//	log.error("SFEE logoff failed", e);
		}
	}

	/**
	 * Sets the SFEE server URL
	 * 
	 * @param serverUrl
	 *            see private attribute doc
	 */
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	/**
	 * Gets the SFEE server url
	 * 
	 * @return see private attribute doc
	 */
	public String getServerUrl() {
		return serverUrl;
	}

	/**
	 * Logins the user and save the session id.
	 * 
	 * @param username
	 *            User name.
	 * @param password
	 *            Password
	 * @throws RemoteException
	 *             when an error is encountered during login.
	 */
	public void login(String username, String password) throws RemoteException {
		try {
			sessionId = mSfSoap.login(username, password);
		} catch (RemoteException e) {
			sessionId = null;
			throw e;
		}
	}

	/**
	 * Logs off the user.
	 * 
	 * @throws RemoteException
	 *             when an error is encountered during logoff.
	 */
	public void logoff() throws RemoteException {
		if (sessionId != null) {
			try {
				mSfSoap.logoff(username, sessionId);
			} finally {
				sessionId = null;
			}
		}
	}

	/**
	 * Sets the SFEE user name to login
	 * 
	 * @param username
	 *            see private attribute doc
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the SFEE user name to login
	 * 
	 * @return see private attribute doc
	 */
	protected String getUsername() {
		return username;
	}

	/**
	 * Returns the user's session id.
	 * 
	 * @return User's session id.
	 */
	protected String getSessionId() {
		return sessionId;
	}

	/**
	 * Sets the password to login into SFEE
	 * 
	 * @param password
	 *            see private attribute doc
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get the password to login into SFEE
	 * 
	 * @return see private attribute doc
	 */
	protected String getPassword() {
		return password;
	}

	/**
	 * Sets keepAlive value
	 * 
	 * @param keepAlive
	 *            false or true, see private attribute doc
	 */
	public void setKeepAlive(String keepAlive) {
		if (keepAlive.equals("false"))
			this.keepAlive = false;
	}

	/**
	 * Returns keepAlive value
	 * 
	 * @return see private attribute documentation
	 */
	public String isKeepAlive() {
		return keepAlive ? "true" : "false";
	}
}
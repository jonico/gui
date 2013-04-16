package com.collabnet.ccf.rqp.schemageneration;

import java.io.IOException;

import com.rational.reqpro.rpx.Application;
import com.rational.reqpro.rpx._Application;
import com.rational.reqpro.rpx._Project;
import com.rational.reqpro.rpx.enumOpenProjectOptions;
import com.rational.reqpro.rpx.enumProjectFlags;
import com.rational.reqpro.rpx.enumProjectLookups;
import com.rational.reqpro.rpx.enumRelatedProjectOptions;

/**
 * 
 * RQP connection.
 * 
 */
public class RQPConnection {

	public static final String PARAM_DELIMITER = ":";
	
	private String username;
	private String password;
	private _Project projectConnection;
	private _Application rpxApplication;

	/**
	 * Constructor by credential info.
	 * 
	 * @param credentialInfo
	 */
	public RQPConnection(String credentialInfo) {
		if (credentialInfo != null) {
			String[] splitCredentials = credentialInfo.split(PARAM_DELIMITER);
			if (splitCredentials != null) {
				if (splitCredentials.length == 1) {
					username = splitCredentials[0];
					password = "";
				} else if (splitCredentials.length == 2) {
					username = splitCredentials[0];
					password = splitCredentials[1];
				} else {
					throw new IllegalArgumentException("Credentials info is not valid.");
				}
			}
		}
	}

	/**
	 * Establishes a connection to an RQP Project
	 * 
	 * @param connectionInfo
	 *            file path of the project.
	 */
	public void openProject(String connectionInfo) {
		try {

			if (projectConnection != null) {
				disconnect();
			}

			rpxApplication = new Application();

			int projectFlag = enumProjectFlags.eProjFlag_Normal;

			projectConnection = rpxApplication.OpenProject(connectionInfo, enumOpenProjectOptions.eOpenProjOpt_RQSFile,
					username, password, projectFlag, enumRelatedProjectOptions.eRelatedProjOption_ConnectNone);

		} catch (IOException e) {
			String cause = "Cannot connect to RQP";
			throw new IllegalArgumentException(cause);
		}
	}

	/**
	 * Disconnect the RQP Application.
	 * 
	 * @throws IOException
	 */
	public void disconnect() {
		if (projectConnection != null) {
			try {
				rpxApplication.CloseProject(projectConnection, enumProjectLookups.eProjLookup_Object);
				projectConnection = null;
				rpxApplication = null;
			} catch (IOException e) {
			}
		}
	}

	public _Project getProjectConnection() {
		return projectConnection;
	}

	public boolean isAlive() {
		return (rpxApplication != null);
	}

}


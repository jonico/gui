package com.collabnet.ccf.teamforge;

import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import com.collabnet.ccf.IConnectionTester;
import com.collabnet.ccf.teamforge.schemageneration.TFSoapClient;

public class TeamForgeConnectionTester implements IConnectionTester {
	private Exception exception;

	public Exception testConnection(final String url, final String user, final String password) {
		exception = null;
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				TFSoapClient soapClient = new TFSoapClient(url, user, password);
				try {
					soapClient.login();
				} catch (Exception e) {
					exception = e;
				}
			}			
		});
		return exception;
	}

}

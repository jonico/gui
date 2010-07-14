package com.collabnet.ccf.sw;

import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import com.collabnet.ccf.IConnectionTester;
import com.danube.scrumworks.api2.client.ScrumWorksAPIService;

public class ScrumWorksConnectionTester implements IConnectionTester {
	private Exception exception;

	@Override
	public Exception testConnection(final String url, final String user, final String password) {
		// TODO This does not work correctly because the API prompts for credentials if the
		//      provided credentials are not valid.
		exception = null;
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					ScrumWorksAPIService endpoint = Activator.getScrumWorksEndpoint(url, user, password);
					endpoint.getVersion();
				} catch (Exception e) {
					exception = e;
				}
			}			
		});
		return exception;
	}

}

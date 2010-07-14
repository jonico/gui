package com.collabnet.ccf;

public interface IConnectionTester {

	/**
	 * Test connection when Test Connection button is pressed in Landscape editor.
	 * 
	 * @param url the connection url
	 * @param user the connection user
	 * @param user the connection password
	 * 
	 * @return Exception message if connection test fails.  The exception message will be shown in a dialog.
	 *         Return null if connection test is successful.
	 */
	public Exception testConnection(String url, String user, String password);

}

/*******************************************************************************
 * Copyright (c) 2011 CollabNet.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     CollabNet - initial API and implementation
 ******************************************************************************/
package com.collabnet.ccf.migration;

public class MigrationResult {
	private String resultMessage;
	private int resultType;
	private Exception exception;
	
	public final static int INFORMATION = 0;
	public final static int ERROR = 1;
	
	public MigrationResult(String resultMessage, int resultType, Exception exception) {
		super();
		this.resultMessage = resultMessage;
		this.resultType = resultType;
		this.exception = exception;
	}
	
	public MigrationResult(String resultMessage) {
		this(resultMessage, INFORMATION, null);
	}
	
	public MigrationResult(Exception exception) {
		this(exception.getMessage(), ERROR, exception);
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public int getResultType() {
		return resultType;
	}

	public Exception getException() {
		return exception;
	}
	
}

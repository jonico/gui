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
package com.collabnet.ccf.migration.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.collabnet.ccf.dialogs.CcfDialog;

public class MigrateLandscapeErrorDialog extends CcfDialog {
	private Exception exception;

	public MigrateLandscapeErrorDialog(Shell shell, Exception exception) {
		super(shell, "MigrateLandscapeErrorDialog");
		this.exception = exception;
	}	
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("CCF 2.x Migration Error");
		
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));	
		
		Browser browser = new Browser(composite, SWT.NONE);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL);
		gd.widthHint = 500;
		gd.heightHint = 500;
		browser.setLayoutData(gd);
		
		browser.setText(exception.getMessage());
		
		return composite;
	}
	
}

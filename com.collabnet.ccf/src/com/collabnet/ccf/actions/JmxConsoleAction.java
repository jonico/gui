package com.collabnet.ccf.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

import com.collabnet.ccf.dialogs.JmxConsoleDialog;
import com.collabnet.ccf.model.Landscape;

public class JmxConsoleAction extends Action {
	private Landscape landscape;

	public JmxConsoleAction(Landscape landscape) {
		this.landscape = landscape;
		setText("Show status...");
	}

	@Override
	public void run() {
		JmxConsoleDialog dialog = new JmxConsoleDialog(Display.getDefault().getActiveShell(), landscape);
		dialog.open();
	}
}

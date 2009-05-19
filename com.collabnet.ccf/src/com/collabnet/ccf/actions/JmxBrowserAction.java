package com.collabnet.ccf.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.Landscape;

public class JmxBrowserAction extends Action {
	private Landscape landscape;
	private int direction;
	
	public static final int TO_QC = 0;
	public static final int FROM_QC = 1;

	public JmxBrowserAction(Landscape landscape, int direction) {
		super();
		this.landscape = landscape;
		this.direction = direction;
	}
	
	@Override
	public void run() {
		String urlString = null;
		switch (direction) {
		case TO_QC:
			urlString = landscape.getJmxUrl1();
			break;
		case FROM_QC:
			urlString = landscape.getJmxUrl2();
			break;
		default:
			urlString = landscape.getJmxUrl1();
			break;
		}
		try {
			URL url = new URL(urlString);
			IWebBrowser browser;		
			if (PlatformUI.getWorkbench().getBrowserSupport().isInternalWebBrowserAvailable()) {
				browser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser(
						IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.LOCATION_BAR |
							IWorkbenchBrowserSupport.NAVIGATION_BAR | IWorkbenchBrowserSupport.STATUS,
						null,
						"JMX Console",
						"JMX Console");
			} else {
				browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
			}
			browser.openURL(url);
		} catch (Exception e) {
			Activator.handleError(e);
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Open JMX Console in Browser", e.getMessage());
		}
	}

	@Override
	public String getText() {
		switch (direction) {
		case TO_QC:
			return Landscape.getTypeDescription(landscape.getType2()) + " => " + Landscape.getTypeDescription(landscape.getType1());
		case FROM_QC:
			return Landscape.getTypeDescription(landscape.getType1()) + " => " + Landscape.getTypeDescription(landscape.getType2());		
		default:
			break;
		}
		return Landscape.getTypeDescription(landscape.getType2()) + " => " + Landscape.getTypeDescription(landscape.getType1());
	}

}

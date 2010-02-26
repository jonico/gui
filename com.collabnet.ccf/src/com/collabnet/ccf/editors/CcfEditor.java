package com.collabnet.ccf.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.EditorPart;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.CCFJMXMonitorBean;
import com.collabnet.ccf.ICcfParticipant;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.views.CcfExplorerView;

public class CcfEditor extends FormEditor implements ISaveablePart2 {
	private Landscape landscape;
	
	private CCFJMXMonitorBean monitor1;
	private CCFJMXMonitorBean monitor2;
	
	private CcfCcfEditorPage ccfPage;
	private CcfEditorPage page1;
	private CcfEditorPage page2;
	private CcfProjectMappingsEditorPage mappingsPage;

	private IDialogSettings settings = Activator.getDefault().getDialogSettings();
	
	public final static String ID = "com.collabnet.ccf.editors.CcfEditor";

	public CcfEditor() {
		super();	
	}
	
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        setSite(site);
        setInput(input);
        setPartName(input.getName());
		CcfEditorInput ccfEditorInput = (CcfEditorInput)getEditorInput();
		landscape = ccfEditorInput.getLandscape();	
		if (landscape != null && landscape.getRole() == Landscape.ROLE_ADMINISTRATOR) {
			getMonitors();
		}
        setTitleImage(Activator.getImage(landscape));
    }

	private void getMonitors() {
		int port1 = 0;
		int port2 = 0;
		try {
			ICcfParticipant ccfParticipant = Activator.getCcfParticipantForType(landscape.getType2());
			port1 = ccfParticipant.getJmxMonitor1Port();
			port2 = ccfParticipant.getJmxMonitor2Port();		
		} catch (Exception e) {
			Activator.handleError(e);
		}
		if (port1 != 0) {
			monitor1 = new CCFJMXMonitorBean();
			monitor1.setHostName(landscape.getHostName1());
			monitor1.setRmiPort(port1);
			
			monitor2 = new CCFJMXMonitorBean();
			monitor2.setHostName(landscape.getHostName2());
			monitor2.setRmiPort(port2);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addPages() {
		createCcfPage();
		if (landscape.getRole() == Landscape.ROLE_ADMINISTRATOR) {
			try {
				ICcfParticipant ccfParticipant1 = Activator.getCcfParticipantForType(landscape.getType1());
				page1 = ccfParticipant1.getEditorPage1(this, getEditorInput().getName());
				if (page1 != null) {
			        int index1 = addPage(page1);
			        setPageText(index1, ccfParticipant1.getName() + " Properties");
			        pages.add(page1);
				}
				ICcfParticipant ccfParticipant2 = Activator.getCcfParticipantForType(landscape.getType2());
				page2 = ccfParticipant2.getEditorPage2(this, getEditorInput().getName());
				if (page2 != null) {
			        int index2 = addPage(page2);
			        setPageText(index2, ccfParticipant2.getName() + " Properties");
			        pages.add(page2);
				}				
			} catch (Exception e) {
				Activator.handleError(e);
			}
		}
		createMappingsPage();
		try {
			String activePage = settings.get(CcfEditorPage.ACTIVE_PAGE);
			if (activePage != null) {
				setActivePage(activePage);	
			}
		} catch (Exception e) {}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (!ccfPage.testConnection(true)) {
			return;
		}
		
		ccfPage.doSave(monitor);
		
		if (page1 != null) {
			page1.doSave(monitor);
		}
		
		if (page2 != null) {
			page2.doSave(monitor);
		}
		
		setDirty();
		
		if (CcfExplorerView.getView() != null) {
			CcfExplorerView.getView().refresh();
		}
		
		if ((monitor1 != null && monitor1.isAlive()) || (monitor1 != null && monitor1.isAlive())) {
			if (MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Landscape","Changes will take effect when CCF is restarted.\n\nDo you wish to restart now?")) {
				if (monitor1 != null && monitor1.isAlive()) {
					monitor1.restartCCFInstance();
				}
				if (monitor2 != null && monitor2.isAlive()) {
					monitor2.restartCCFInstance();
				}
			}
		}
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
    @SuppressWarnings("unchecked")
	private void createCcfPage() {
        try {
        	ccfPage = new CcfCcfEditorPage(this, "ccf", getEditorInput().getName());
	        int ccfIndex = addPage(ccfPage);
	        setPageText(ccfIndex, "CCF Properties");
	        pages.add(ccfPage);
        } catch (Exception e) { 
        	Activator.handleError(e);
        }
    }

    @SuppressWarnings("unchecked")
	private void createMappingsPage() {
        try {
        	mappingsPage = new CcfProjectMappingsEditorPage(this, "projectMappings", getEditorInput().getName());
	        int mappingsIndex = addPage(mappingsPage);
	        setPageText(mappingsIndex, "Project Mappings");
	        pages.add(mappingsPage);
        } catch (Exception e) { 
        	Activator.handleError(e);
        }
    }
 
    public void setDirty() {
    	firePropertyChange(EditorPart.PROP_DIRTY); 
    }

	public int promptToSaveOnClose() {
		String[] buttons = { "&No", "&Cancel", "&Yes" };
		MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Save Landscape", null, "'" + landscape.getDescription() + "' has been modified.  Save changes?", MessageDialog.QUESTION, buttons, 2);
		switch (dialog.open()) {
		case 0:	
			return ISaveablePart2.NO;
		case 1:	
			return ISaveablePart2.CANCEL;
		case 2:	
			return ISaveablePart2.YES;			
		default:
			return ISaveablePart2.DEFAULT;
		}
	}
	
	public IDialogSettings getDialogSettings() {
		return settings;
	}

}

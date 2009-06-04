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
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.views.CcfExplorerView;

public class CcfEditor extends FormEditor implements ISaveablePart2 {
	private Landscape landscape;
	
	private CCFJMXMonitorBean monitor1;
	private CCFJMXMonitorBean monitor2;
	
	private CcfCcfEditorPage ccfPage;
	private CcfSystemEditorPage qcPage;
	private CcfSystemEditorPage sfeePage;
	private CcfSystemEditorPage ceePage;
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
		if (landscape != null) {
			getMonitors();
		}
        setTitleImage(Activator.getImage(landscape));
    }

	private void getMonitors() {
		int port1 = 0;
		int port2 = 0;
		if (landscape.getType2().equals(Landscape.TYPE_TF)) {
			port1 = 10001;
			port2 = 10002;
		}
		if (landscape.getType2().equals(Landscape.TYPE_PT)) {
			port1 = 10000;
			port2 = 9999;
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

	@Override
	protected void addPages() {
		createCcfPage();
		createQcPage();		
		if (landscape.getType1().equals(Landscape.TYPE_TF) || landscape.getType2().equals(Landscape.TYPE_TF)) {
			createSfeePage();
		}		
		if (landscape.getType1().equals(Landscape.TYPE_PT) || landscape.getType2().equals(Landscape.TYPE_PT)) {
			createCeePage();
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
		
		if (qcPage != null) {
			qcPage.doSave(monitor);
		}
		
		if (sfeePage != null) {
			sfeePage.doSave(monitor);
		}
		
		if (ceePage != null) {
			ceePage.doSave(monitor);
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
	private void createQcPage() {
        try {
        	qcPage = new CcfSystemEditorPage(this, "qc", getEditorInput().getName(), CcfSystemEditorPage.QC);
	        int qcIndex = addPage(qcPage);
	        setPageText(qcIndex, "QC Properties");
	        pages.add(qcPage);
        } catch (Exception e) { 
        	Activator.handleError(e);
        }
    }
    
    @SuppressWarnings("unchecked")
	private void createSfeePage() {
        try {
        	sfeePage = new CcfSystemEditorPage(this, "sfee", getEditorInput().getName(), CcfSystemEditorPage.TF);
	        int sfeeIndex = addPage(sfeePage);
	        setPageText(sfeeIndex, "TeamForge Properties");
	        pages.add(sfeePage);
        } catch (Exception e) { 
        	Activator.handleError(e);
        }
    }
    
    @SuppressWarnings("unchecked")
	private void createCeePage() {
        try {
        	ceePage = new CcfSystemEditorPage(this, "cee", getEditorInput().getName(), CcfSystemEditorPage.PT);
	        int ceeIndex = addPage(ceePage);
	        setPageText(ceeIndex, "Project Tracker Properties");
	        pages.add(ceePage);
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

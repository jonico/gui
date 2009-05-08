package com.collabnet.ccf.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import com.collabnet.ccf.Activator;

public class HospitalEditor extends FormEditor {
	
	private HospitalExceptionEditorPage exceptionPage;
	
	private IDialogSettings settings = Activator.getDefault().getDialogSettings();
	
	public final static String ID = "com.collabnet.ccf.editors.HospitalEditor";
	
	public HospitalEditor() {
		super();
	}
	
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        setSite(site);
        setInput(input);
        setPartName(input.getName());
        setTitleImage(Activator.getImage(Activator.IMAGE_HOSPITAL_ENTRY));
    }

	@Override
	protected void addPages() {
		createExceptionPage();
		try {
			String activePage = settings.get(HospitalEditorPage.ACTIVE_PAGE);
			if (activePage != null) {
				setActivePage(activePage);	
			}
		} catch (Exception e) {
			Activator.handleError(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void createExceptionPage() {
        try {
        	exceptionPage = new HospitalExceptionEditorPage(this, "exception", getEditorInput().getName());
	        int index = addPage(exceptionPage);
	        setPageText(index, "Exception Details");
	        pages.add(exceptionPage);
        } catch (Exception e) { 
        	Activator.handleError(e);
        }		
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	@Override
	public void doSaveAs() {
		
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	public IDialogSettings getDialogSettings() {
		return settings;
	}

}

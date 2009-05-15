package com.collabnet.ccf.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.EditorPart;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.model.IdentityMapping;
import com.collabnet.ccf.views.IdentityMappingView;

public class IdentityMappingEditor extends FormEditor implements ISaveablePart2 {
	private IdentityMapping identityMapping;
	
	private IdentityMappingEditorPage detailsPage;
	
	public final static String ID = "com.collabnet.ccf.editors.IdentityMappingEditor";
	
	public IdentityMappingEditor() {
		super();
	}
	
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        super.init(site, input);
        setSite(site);
        setInput(input);
        setPartName(input.getName());
        setTitleImage(Activator.getImage(Activator.IMAGE_IDENTITY_MAPPING));
        identityMapping = ((IdentityMappingEditorInput)input).getIdentityMapping();
    }

	@SuppressWarnings("unchecked")
	@Override
	protected void addPages() {
       try {
        	detailsPage = new IdentityMappingEditorPage(this, "details", getEditorInput().getName());
	        int detailsIndex = addPage(detailsPage);
	        setPageText(detailsIndex, "Identity Mapping Details");
	        pages.add(detailsPage);
        } catch (Exception e) { 
        	Activator.handleError(e);
        }
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		detailsPage.doSave(monitor);
		if (detailsPage.isSaveError()) {
			monitor.setCanceled(true);
		}
		setDirty();
		if (IdentityMappingView.getView() != null) {
			IdentityMappingView.getView().refresh();
		}
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

    public void setDirty() {
    	firePropertyChange(EditorPart.PROP_DIRTY); 
    }

	public int promptToSaveOnClose() {
		String[] buttons = { "&No", "&Cancel", "&Yes" };
		MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), "Save Identity Mapping", null, "'" + identityMapping.getEditableValue() + "' has been modified.  Save changes?", MessageDialog.QUESTION, buttons, 2);
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

}

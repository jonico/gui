package com.collabnet.ccf.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionDelegate;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.editors.StringInput;
import com.collabnet.ccf.editors.StringStorage;
import com.collabnet.ccf.model.Hospital;

public class ExaminePayloadAction extends ActionDelegate {
	private IStructuredSelection fSelection;

	@SuppressWarnings("unchecked")
	public void run(IAction action) {
		Iterator iter = fSelection.iterator();
		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof Hospital) {
				IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
				Hospital hospital = (Hospital)object;
				IStorage storage = new StringStorage(hospital.getGenericArtifact(), hospital.getTimeStamp());
				IStorageEditorInput input = new StringInput(storage);
				IEditorRegistry registry = Activator.getDefault().getWorkbench().getEditorRegistry();
				IEditorDescriptor descriptor = registry.getDefaultEditor("file.xml");
				String id;
				if (descriptor == null) {
					id = "org.eclipse.ui.DefaultTextEditor"; //$NON-NLS-1$
				} else {
					id = descriptor.getId();
				}
				try {
					page.openEditor(input, id);
				} catch (PartInitException e) {
					Activator.handleError("Examine Hospital Payload", e);
					break;
				}
			}
		}
	}	
	
	public void selectionChanged(IAction action, ISelection sel) {
		if (sel instanceof IStructuredSelection) {
			fSelection= (IStructuredSelection) sel;
		}
	}	
	
}

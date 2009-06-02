package com.collabnet.ccf.actions;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionDelegate;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.editors.ExternalFileEditorInput;
import com.collabnet.ccf.model.SynchronizationStatus;

public class EditFieldMappingsAction extends ActionDelegate {
	private IStructuredSelection fSelection;
	
	@SuppressWarnings("unchecked")
	public void run(IAction action) {
		Iterator iter = fSelection.iterator();
		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof SynchronizationStatus) {
				SynchronizationStatus status = (SynchronizationStatus)object;
				File xslFile = status.getXslFile();
				if (!xslFile.exists()) {
					if (!MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Edit Field Mappings", "File " + xslFile.getName() + " does not exist.\n\nDo you wish to create it by copying sample.xsl?")) {
						return;
					}
					try {
						xslFile.createNewFile();
						File sampleFile = status.getSampleXslFile();
						if (sampleFile != null && sampleFile.exists()) {
							CcfDataProvider.copyFile(sampleFile, xslFile);
						}
					} catch (IOException e) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Edit Field Mappings", "Unable to create file " + xslFile.getName() + ":\n\n" + e.getMessage());
						Activator.handleError(e);
						return;
					}
				}
				IFileStore fileStore =  EFS.getLocalFileSystem().getStore(new Path(xslFile.getAbsolutePath()));
				final IEditorInput input = new ExternalFileEditorInput(fileStore, xslFile.getName());
				IEditorRegistry registry = Activator.getDefault().getWorkbench().getEditorRegistry();
				IEditorDescriptor descriptor = registry.getDefaultEditor(xslFile.getName());
				String id;
				if (descriptor == null) {
					id = "org.eclipse.ui.DefaultTextEditor"; //$NON-NLS-1$
				} else {
					id = descriptor.getId();
				}
				IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					page.openEditor(input, id);
				} catch (PartInitException e) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Edit Field Mappings", "Unable to open editor:\n\n" + e.getMessage());
					Activator.handleError(e);
					return;
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

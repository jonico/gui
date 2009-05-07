package com.collabnet.ccf.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import com.collabnet.ccf.editors.ExternalFileEditorInput;
import com.collabnet.ccf.model.SynchronizationStatus;

public class EditWiringsAction extends ActionDelegate {
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
					try {
						xslFile.createNewFile();
						File sampleFile = status.getSampleXslFile();
						if (sampleFile != null && sampleFile.exists()) {
							copyFile(sampleFile, xslFile);
						}
					} catch (IOException e) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Edit Wirings", "Unable to create file " + xslFile.getName() + ":\n\n" + e.getMessage());
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
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Edit Wirings", "Unable to open editor:\n\n" + e.getMessage());
					Activator.handleError(e);
					return;
				}
			}
		}	
	}
	
	private void copyFile(File fromFile, File toFile) throws IOException {
		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[4096];
			int bytes_read;
			while ((bytes_read = from.read(buffer)) != -1)
				to.write(buffer, 0, bytes_read);
		}
		finally {
			if (from != null) try { from.close(); } catch (IOException e) {}
			if (to != null) try { to.close(); } catch (IOException e) {}
		}
	}

	public void selectionChanged(IAction action, ISelection sel) {
		if (sel instanceof IStructuredSelection) {
			fSelection= (IStructuredSelection) sel;
		}
	}	
}

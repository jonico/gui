package com.collabnet.ccf.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionDelegate;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.db.CcfDataProvider;
import com.collabnet.ccf.db.Filter;
import com.collabnet.ccf.db.Update;
import com.collabnet.ccf.editors.ExternalFileEditorInput;
import com.collabnet.ccf.model.Patient;
import com.collabnet.ccf.views.HospitalView;

public class ExaminePayloadAction extends ActionDelegate {
	private IStructuredSelection fSelection;

	@SuppressWarnings("unchecked")
	public void run(IAction action) {
		Iterator iter = fSelection.iterator();
		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof Patient) {
				IWorkbenchPage page = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
				final Patient patient = (Patient)object;
				if (patient.getGenericArtifact() != null && patient.getGenericArtifact().trim().length() > 0) {
					try {
						final File tempFile = File.createTempFile("Payload" + patient.getId(), ".xml");
						BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
						out.write(patient.getGenericArtifact());
						out.close();
						IFileStore fileStore =  EFS.getLocalFileSystem().getStore(new Path(tempFile.getAbsolutePath()));
						final IEditorInput input = new ExternalFileEditorInput(fileStore, patient.getId() + " Payload");
						IEditorRegistry registry = Activator.getDefault().getWorkbench().getEditorRegistry();
						IEditorDescriptor descriptor = registry.getDefaultEditor("file.xml");
						
						String id;
						if (descriptor == null) {
							id = "org.eclipse.ui.DefaultTextEditor"; //$NON-NLS-1$
						} else {
							id = descriptor.getId();
						}
						try {
							final IEditorPart editorPart = page.openEditor(input, id);
							editorPart.addPropertyListener(new IPropertyListener() {

								public void propertyChanged(Object arg0,
										int arg1) {
									if (!editorPart.isDirty()) {
										try {
											final String updatedPayload = readFileAsString(tempFile.getAbsolutePath());
											BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
												public void run() {
													Filter filter = new Filter(CcfDataProvider.HOSPITAL_ID, Integer.toString(patient.getId()), false);
													Filter[] filters = { filter };
													Update update = new Update(CcfDataProvider.HOSPITAL_GENERIC_ARTIFACT, updatedPayload);
													Update[] updates = { update };
													CcfDataProvider dataProvider = new CcfDataProvider();
													try {
														dataProvider.updatePatients(patient.getLandscape(), updates, filters);
														if (HospitalView.getView() != null) {
															HospitalView.getView().refresh();
														}
													} catch (Exception e) {
														Activator.handleError(e);
													}
												}					
											});
										} catch (IOException e) {
											Activator.handleError(e);
										}										
									}
								}
								
							});
						} catch (PartInitException e) {
							Activator.handleError("Examine Hospital Payload", e);
							break;
						}					
						
						tempFile.deleteOnExit();
					} catch (IOException e) {
						Activator.handleError(e);
					}
				}
			}
		}
	}	
	
	public void selectionChanged(IAction action, ISelection sel) {
		if (sel instanceof IStructuredSelection) {
			fSelection= (IStructuredSelection) sel;
			if (action != null) action.setEnabled(isEnabledForSelection());
		}
	}
	
    private static String readFileAsString(String filePath)
    throws java.io.IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            fileData.append(buf, 0, numRead);
        }
        reader.close();
        return fileData.toString();
    }
    
	@SuppressWarnings("unchecked")
	private boolean isEnabledForSelection() {
		if (fSelection == null || !Activator.getDefault().getActiveRole().isEditQuarantinedArtifact()) return false;
		Iterator iter = fSelection.iterator();
		while (iter.hasNext()) {
			Object object = iter.next();
			if (object instanceof Patient) {
				Patient patient = (Patient)object;
				if (patient.getGenericArtifact() == null || patient.getGenericArtifact().trim().length() == 0)
					return false;
			}
		}
		return true;
	}	
	
}

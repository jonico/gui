package com.collabnet.ccf.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.collabnet.ccf.model.Patient;

public class HospitalEditorInput implements IEditorInput {
	private Patient patient;

	public HospitalEditorInput(Patient patient) {
		super();
		this.patient = patient;
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return patient.getEditableValue().toString();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "";
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class obj) {
		return null;
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof HospitalEditorInput)) return false;
		HospitalEditorInput compareInput = (HospitalEditorInput)obj;
		return patient.getId() == compareInput.getPatient().getId();
	}	

}

package com.collabnet.ccf;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.collabnet.ccf.model.MappingGroup;


public abstract class MappingSection implements IMappingSection {
	private IPageCompleteListener projectPage;
	private int systemNumber;

	public IPageCompleteListener getProjectPage() {
		return projectPage;
	}

	public void setProjectPage(IPageCompleteListener projectPage) {
		this.projectPage = projectPage;
	}

	public int getSystemNumber() {
		return systemNumber;
	}

	public void setSystemNumber(int systemNumber) {
		this.systemNumber = systemNumber;
	}
	
	public void showValidationErrorDialog(String errorMessage) {
		MessageDialog.openError(Display.getDefault().getActiveShell(), "New Project Mapping", errorMessage);
	}
	
	public boolean showValidationQuestionDialog(String question) {
		return MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "New Project Mapping", question);
	}
	
	public void initializeComposite(MappingGroup mappingGroup) {
		
	}

}

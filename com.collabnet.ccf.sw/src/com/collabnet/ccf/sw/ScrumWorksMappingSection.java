package com.collabnet.ccf.sw;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.collabnet.ccf.MappingSection;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.model.SynchronizationStatus;

public class ScrumWorksMappingSection extends MappingSection {
	private Text swText;

	public Composite getComposite(Composite parent, Landscape landscape) {
		Group swGroup = new Group(parent, SWT.NULL);
		GridLayout swLayout = new GridLayout();
		swLayout.numColumns = 2;
		swGroup.setLayout(swLayout);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		swGroup.setLayoutData(gd);	
		swGroup.setText("ScrumWorks:");
		
		Label swLabel = new Label(swGroup, SWT.NONE);
		swLabel.setText("Scrum:");
		
		swText = new Text(swGroup, SWT.BORDER);
		gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		swText.setLayoutData(gd);
		
		ModifyListener modifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent me) {
				if (getProjectPage() != null) {
					getProjectPage().setPageComplete();
				}
			}			
		};
		
		swText.addModifyListener(modifyListener);
		
		return swGroup;
	}

	public void initializeComposite(SynchronizationStatus projectMapping, int type) {
		// TODO Auto-generated method stub

		//  Here we will set the GUI widget values based on the projectMapping and
		//  type (IMappingSection.TYPE_SOURCE or IMappingSection.TYPE_TARGET).
	}

	public boolean isPageComplete() {
		if (swText == null) {
			return false;
		}
		if (swText.getText().trim().length() == 0) {
			return false;
		}
		return true;
	}

	public void updateSourceFields(SynchronizationStatus projectMapping) {
		// TODO Auto-generated method stub

		//  Here we will set the source repository id and source repository kind
		//  based on the values that have been entered.
	}

	public void updateTargetFields(SynchronizationStatus projectMapping) {
		// TODO Auto-generated method stub
		
		//  Here we will set the target repository id and target repository kind
		//  based on the values that have been entered.
	}

	public boolean validate(Landscape landscape) {
		showValidationErrorDialog("Feature not yet supported.");
		return false;
	}

}

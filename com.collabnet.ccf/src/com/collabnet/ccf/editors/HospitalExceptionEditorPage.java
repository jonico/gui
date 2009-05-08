package com.collabnet.ccf.editors;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class HospitalExceptionEditorPage extends HospitalEditorPage {
	private ScrolledForm form;
	private FormToolkit toolkit;
	
	public final static String STACKTRACE_SECTION_STATE = "HospitalExceptionEditorPage.stackTraceSectionExpanded";
	
	public HospitalExceptionEditorPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}
	
	public HospitalExceptionEditorPage(String id, String title) {
		super(id, title);
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		form = managedForm.getForm();
        toolkit = getEditor().getToolkit();
        TableWrapLayout formLayout = new TableWrapLayout();
        formLayout.numColumns = 1;
        form.getBody().setLayout(formLayout);
		createControls(form.getBody());
	}
	
	private void createControls(Composite composite) {
		Label headerLabel = new Label(composite, SWT.NONE);
		headerLabel.setText("Exception Details");
		headerLabel.setBackground(((FormEditor)getEditor()).getToolkit().getColors().getBackground());
		headerLabel.setFont(JFaceResources.getHeaderFont());
        TableWrapData td = new TableWrapData(TableWrapData.FILL);
        headerLabel.setLayoutData(td);
        
		Section stackTraceSection = toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        td = new TableWrapData(TableWrapData.FILL_GRAB);
        stackTraceSection.setLayoutData(td);
        stackTraceSection.setText("Stack Trace");
        Composite stackTraceSectionClient = toolkit.createComposite(stackTraceSection); 
        GridLayout stackTraceLayout = new GridLayout();
        stackTraceLayout.numColumns = 2;
        stackTraceLayout.verticalSpacing = 10;
        stackTraceSectionClient.setLayout(stackTraceLayout);
        stackTraceSection.setClient(stackTraceSectionClient);
        stackTraceSection.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
                if (e.getState()) getDialogSettings().put(STACKTRACE_SECTION_STATE, STATE_EXPANDED);
                else getDialogSettings().put(STACKTRACE_SECTION_STATE, STATE_CONTRACTED);
            }
        });
        
        toolkit.createLabel(stackTraceSectionClient, "URL:");
        
        toolkit.paintBordersFor(stackTraceSectionClient);
        
        String expansionState = getDialogSettings().get(STACKTRACE_SECTION_STATE);
        stackTraceSection.setExpanded(expansionState == null  || expansionState.equals(STATE_EXPANDED));
	}

}

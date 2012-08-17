package com.collabnet.ccf.tfs.dialogs;

import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.dialogs.CcfDialog;
import com.collabnet.ccf.dialogs.ExceptionDetailsErrorDialog;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.tfs.schemageneration.TFSConnection;

public class RequirementTypeSelectionDialog extends CcfDialog {
	private Landscape landscape;
	private String collection;
	private String project;
	private List typeList;
	private String selectedType;
	private java.util.List<String> types;
	
	private Button okButton;

	public RequirementTypeSelectionDialog(Shell shell, Landscape landscape, String collection, String project) {
		super(shell, "RequirementTypeSelectionDialog");
		this.landscape = landscape;
		this.collection = collection;
		this.project = project;
	}
	
	protected Control createDialogArea(Composite parent) {
		getTypes();
		
		getShell().setText("Select WorkItem Type");
		
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		typeList = new List(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL);
		gd.heightHint = 300;
		gd.widthHint = 300;
		typeList.setLayoutData(gd);
		
		if (types != null) {
			for (String type : types) {
				typeList.add(type);
			}
		}
		
		typeList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				okButton.setEnabled(typeList.getSelectionCount() > 0);
			}		
		});
		
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent me) {
				okPressed();
			}
		};
		
		typeList.addMouseListener(mouseListener);
		
		return composite;
	}
	
	@Override
	protected void okPressed() {
		String[] selection = typeList.getSelection();
		if (selection.length > 0) selectedType = selection[0];
		super.okPressed();
	}

	public String getType() {
		return selectedType;
	}

	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        Button button = super.createButton(parent, id, label, defaultButton);
		if (id == IDialogConstants.OK_ID) {
			okButton = button;
			okButton.setEnabled(false);
		}
        return button;
    }
	
	private void getTypes() {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					Properties properties;
					if (landscape.getType2().equals("QT")) {
						properties = landscape.getProperties2();
					} else {
						properties = landscape.getProperties1();
					}
					String url = properties.getProperty(Activator.PROPERTIES_TFS_URL, "");
					String user = properties.getProperty(Activator.PROPERTIES_TFS_USER, "");
					String password = Activator.decodePassword(properties.getProperty(Activator.PROPERTIES_TFS_PASSWORD, ""));
					
					TFSConnection tfsConnection = new TFSConnection(url, user, password);
					
					types = tfsConnection.getWorkItemsTypesNames(collection, project);
					
				} catch (Exception e) {
					Activator.handleError(e);
					ExceptionDetailsErrorDialog.openError(getShell(), "Select WorkItem Type", e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e));
				}
			}			
		});
	}

}

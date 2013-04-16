package com.collabnet.ccf.rqp.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.WorkbenchContentProvider;

import com.collabnet.ccf.Activator;
import com.collabnet.ccf.dialogs.CcfDialog;
import com.collabnet.ccf.dialogs.ExceptionDetailsErrorDialog;
import com.collabnet.ccf.model.Landscape;
import com.collabnet.ccf.rqp.schemageneration.RQPLayoutExtractor;

public class ProjectPackageSelectionDialog extends CcfDialog {
	
	private Landscape landscape;
	private int type;
	private String title;
	private RQPLayoutExtractor rqpLayoutExtractor;
	private List<RQPPackage> rqp_packages = new ArrayList<ProjectPackageSelectionDialog.RQPPackage>();
	
	private TreeViewer treeViewer;
	private Button okButton;
	
	private String project;
	private String rqp_package;
	
	private Project previouslySelectedProject;
	private RQPPackage previouslySelectedPackage;
	
	public static final int BROWSER_TYPE_PROJECT = 1;

	public ProjectPackageSelectionDialog(Shell shell, Landscape landscape, String project, String rqp_package, int type) {
		super(shell, "ProjectPackageSelectionDialog");
		this.landscape = landscape;
		this.project = project;
		this.rqp_package = rqp_package;
		this.type = type;
	}
	
	protected Control createDialogArea(Composite parent) {
		getPackages();
		title = "Select Project";
		getShell().setText(title);
		
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		treeViewer = new TreeViewer(composite);
		treeViewer.setLabelProvider(new ProjectPackageLabelProvider());
		treeViewer.setContentProvider(new ProjectPackageSelectionContentProvider());
		treeViewer.setUseHashlookup(true);
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.FILL_VERTICAL | GridData.GRAB_VERTICAL);
		layoutData.heightHint = 300;
		layoutData.widthHint = 300;
		treeViewer.getControl().setLayoutData(layoutData);
		treeViewer.setInput(this);
		
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (okButton != null) {
					okButton.setEnabled(canFinish());
				}
			}		
		});
		
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent e) {
                if (okButton.isEnabled()) okPressed();
            }
        });  
        
        if (previouslySelectedProject != null) {
        	if (type == BROWSER_TYPE_PROJECT) {
        		treeViewer.expandToLevel(previouslySelectedProject, TreeViewer.ALL_LEVELS);
        		treeViewer.reveal(previouslySelectedProject);
        	} else {
        		treeViewer.setSelection(getSelection(previouslySelectedProject));
        	}
        }
        
        if (type == BROWSER_TYPE_PROJECT && previouslySelectedPackage != null) {
        	treeViewer.setSelection(getSelection(previouslySelectedPackage));
        }
 
		return composite;
	}

	@SuppressWarnings("rawtypes")
	private ISelection getSelection(final Object object) {
		ISelection selection = new IStructuredSelection() {			
			public boolean isEmpty() {
				return false;
			}			
			@SuppressWarnings("unchecked")
			public List toList() {
				List list = new ArrayList();
				list.add(object);
				return list;
			}
			
			public Object[] toArray() {
				return toList().toArray();
			}			
			public int size() {
				return 1;
			}			
			public Iterator iterator() {
				return toList().iterator();
			}			
			public Object getFirstElement() {
				return object;
			}
		};
		return selection;
	}
	
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        Button button = super.createButton(parent, id, label, defaultButton);
		if (id == IDialogConstants.OK_ID) {
			okButton = button;
			if (type == BROWSER_TYPE_PROJECT && previouslySelectedPackage == null) {
				okButton.setEnabled(false);
			}
		}
        return button;
    }
	
	protected void okPressed() {
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		Object firstSelection = selection.getFirstElement();
		if (firstSelection instanceof RQPPackage) {
			RQPPackage selectedPackage = (RQPPackage)firstSelection;
			rqp_package = selectedPackage.toString();
		}
		super.okPressed();
	}
	
	private boolean canFinish() {
		IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		return !selection.isEmpty();
	}
	
	public String getProject() {
		return project;
	}

	public String getPackage() {
		return rqp_package;
	}

	private void getPackages() {
		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
			public void run() {
				try {
					rqpLayoutExtractor = new RQPLayoutExtractor();
					Properties properties;
					if (landscape.getType2().equals("QT")) {
						properties = landscape.getProperties2();
					} else {
						properties = landscape.getProperties1();
					}
					String url = properties.getProperty(Activator.PROPERTIES_RQP_URL, "");
					String user = properties.getProperty(Activator.PROPERTIES_RQP_USER, "");
					String password = Activator.decodePassword(properties.getProperty(
							Activator.PROPERTIES_RQP_PASSWORD, ""));
					rqpLayoutExtractor.setServerUrl(url);
					rqpLayoutExtractor.setUserName(user);
					rqpLayoutExtractor.setPassword(password);
					List<String> packages = rqpLayoutExtractor.getPackages(project);
					rqp_packages = new ArrayList<ProjectPackageSelectionDialog.RQPPackage>();
					
					for (String currentPackage : packages) {
						RQPPackage packageToAdd = new RQPPackage(currentPackage);
						if (project != null && project.equals(currentPackage)) {
							previouslySelectedPackage = packageToAdd;
						}
						rqp_packages.add(packageToAdd);
					}
					
				} catch (Exception e) {
					Activator.handleError(e);
					ExceptionDetailsErrorDialog.openError(getShell(), "Select Project", e.getMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getLocalizedMessage(), e));
				}
			}			
		});		
	}
	
	class ProjectPackageSelectionLabelProvider extends LabelProvider {
		public Image getImage(Object element) {
			return null;
		}
		public String getText(Object element) {
			return super.getText(element);
		}
	}	
	
	class ProjectPackageSelectionContentProvider extends WorkbenchContentProvider {
		public Object[] getChildren(Object element) {
			if (element instanceof ProjectPackageSelectionDialog) {
				return rqp_packages.toArray();
			}
			return new Object[0];
		}
		public Object[] getElements(Object element) {
			return getChildren(element);
		}
		public boolean hasChildren(Object element) {
			return false;
		}
	}
	
	class RQPPackage {
		private String name;
		
		public RQPPackage(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
		
	}
	
	class Project {
		private String name;
		
		public Project(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
		
	}
	
	class ProjectPackageLabelProvider extends LabelProvider {
		
	}

}
